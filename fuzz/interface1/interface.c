#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/wait.h>
#include <hiredis/hiredis.h>

// shared memeory size
#define SHM_SIZE 65536
// SHM_SIZE should be divisible by this

#define SHM_ENV_VAR "__AFL_SHM_ID"

#define LOGFILE "/tmp/afl-wrapper.log"

#define VERBOSE

#define STATUS_SUCCESS 0
#define STATUS_TIMEOUT 1
#define STATUS_CRASH 2
#define STATUS_QUEUE_FULL 3
#define STATUS_COMM_ERROR 4
#define STATUS_DONE 5

#define MAX_TRIES 400

#define DEFAULT_SERVER "127.0.0.1"
#define DEFAULT_PORT 6379

uint8_t *trace_bits;
//int prev_location = 0;

/* Stdout is piped to null when running inside AFL, so we have an option to write output to a file */
FILE *logfile;

/* Running inside AFL or standalone */
uint8_t in_afl = 0;

#define OUTPUT_STDOUT

void LOG(const char *format, ...) {
    va_list args;
#ifdef OUTPUT_STDOUT
    va_start(args, format);
    vprintf(format, args);
#endif
#ifdef OUTPUT_FILE
    va_start(args, format);
    vfprintf(logfile, format, args);
#endif
    va_end(args);
}

#define DIE(...) { LOG(__VA_ARGS__); if(!in_afl) shmdt(trace_bits); if(logfile != NULL) fclose(logfile); exit(1); }
#define LOG_AND_CLOSE(...) { LOG(__VA_ARGS__); if(logfile != NULL) fclose(logfile); }

#ifdef VERBOSE
#define LOGIFVERBOSE(...) LOG(__VA_ARGS__);
#else
#define LOGIFVERBOSE(...)
#endif

/* Set up the TCP connection */
redisContext *set_up_redis_connection(const char *hostname, const int port) {
    redisContext *conn = redisConnect(hostname, port);
    if (conn->err) {
        DIE("failed to connect to the redis server")
    }
    return conn;
}

void printUsageAndDie() {
    DIE("Usage: interface [-s <server>] [-p <port>] <filename>\n");
}

int main(int argc, char **argv) {
    /* Stdout is piped to null, so write output to a file */
#ifdef OUTPUT_FILE
    logfile = fopen(LOGFILE, "wb");
    if (logfile == NULL) {
      DIE("Error opening log file for writing\n");
    }
#endif

    /* Parameters */
    const char *filename;
    char *server = DEFAULT_SERVER;
    int port = DEFAULT_PORT;

    /* Check num of parameters */
    if (argc < 2)
        printUsageAndDie();

    /* Parse parameters */
    int curArg = 1;
    while (curArg < argc) {
        if (argv[curArg][0] == '-') {
            //flag
            if (argv[curArg][1] == 's') {
                // set server
                server = argv[curArg + 1];
                curArg += 2;
            } else if (argv[curArg][1] == 'p') {
                // set port
                port = atoi(argv[curArg + 1]);
                curArg += 2;
            } else {
                LOG("Unknown flag: %s\n", argv[curArg]);
                printUsageAndDie();
            }
        } else {
            break; // expect filename now
        }
    }
    if (curArg != argc - 1)
        printUsageAndDie();
    filename = argv[curArg];
    LOG("input file = %s\n", filename);

    /* Preamble instrumentation */
    char *shmname = getenv(SHM_ENV_VAR);
    int status = 0;
    uint8_t kelinci_status = STATUS_SUCCESS;
    if (shmname) {

        /* Running in AFL */
        in_afl = 1;

        /* Set up shared memory region */
        LOG("SHM_ID: %s\n", shmname);
        key_t key = atoi(shmname);

        /* 启动对共享内存的访问，并把共享内存连接到当前进程的地址空间
         * int shm_id 共享内存标示
         * const void *shm_addr 共享内存连接到当前进程中的地址位置，通常为空，标示让进程来选择共享内存的地址
         * int shm_flg 标识位，通常为0
         * 调用成功时，返回一个指向共享内存第一个字节的指针，如果调用失败返回-1
         */
        if ((trace_bits = shmat(key, 0, 0)) == (uint8_t * ) - 1) {
            DIE("Failed to access shared memory 2\n");
        }
        LOGIFVERBOSE("Pointer: %p\n", trace_bits);
        LOG("Shared memory attached. Value at loc 3 = %d\n", trace_bits[3]);

        /* Set up the fork server
         * Designated file descriptors for forkserver commands (the application will
         * use FORKSRV_FD and FORKSRV_FD + 1)
         * #define FORKSRV_FD          198
         */
        LOG("Starting fork server...\n");
        if (write(199, &status, 4) != 4) {
            LOG("Write failed\n");
            goto resume;
        }

        while (1) {
            if (read(198, &status, 4) != 4) {
                DIE("Read failed\n");
            }

            int child_pid = fork();
            if (child_pid < 0) {
                DIE("Fork failed\n");
            } else if (child_pid == 0) {
                LOGIFVERBOSE("Child process, continue after pork server loop\n");
                break;
            }

            LOGIFVERBOSE("Child PID: %d\n", child_pid);
            write(199, &child_pid, 4);

            LOGIFVERBOSE("Status %d \n", status);

            if (waitpid(child_pid, &status, 0) <= 0) {
                DIE("Fork crash");
            }

            LOGIFVERBOSE("Status %d \n", status);
            write(199, &status, 4);
        }

        resume:
        LOGIFVERBOSE("AFTER LOOP\n\n");
        close(198);
        close(199);

        /* Mark a location to show we are instrumented */
        trace_bits[0]++;

    } else {
        LOG("Not running within AFL. Shared memory and fork server not set up.\n");
        trace_bits = (uint8_t *) malloc(SHM_SIZE);
    }

    /* Done with initialization, now let's start the wrapper! */
    int try = 0;
    size_t nread;
    FILE *file;
    uint8_t conf = STATUS_DONE;

    redisContext *conn = set_up_redis_connection(server, port);

    // try up to MAX_TRIES time to communicate with the server
    do {
        // if this is not the first try, sleep for 0.1 seconds first
        if (try > 0)
            usleep(100000);

        // get absolute path
        char path[10000];
        realpath(filename, path);
        // send path length
        int pathlen = strlen(path);

        redisCommand(conn, "rpush cur_input_filepath_1 %s", path);

        LOG("Sent path: %s\n", path);

        redisReply *reply = redisCommand(conn, "brpop fuzz_info_1 10000000");
        char *fuzzInfo = reply->element[1]->str;

        /* Read "shared memory" over TCP */
        uint8_t *shared_mem = malloc(SHM_SIZE);

        shared_mem = (uint8_t *) fuzzInfo + 1;

        /* If successful, copy over to actual shared memory */
        for (int i = 0; i < SHM_SIZE; i++) {
            if (shared_mem[i] != 0) {
                LOG("%d -> %d\n", i, shared_mem[i]);
                trace_bits[i] += shared_mem[i];
            }
        }
        freeReplyObject(reply);

        /* Only try communicating MAX_TRIES times */
        if (try++ > MAX_TRIES) {
            // fail silently...
            DIE("Stopped trying to communicate with server.\n");
        }

    } while (kelinci_status == STATUS_QUEUE_FULL || kelinci_status == STATUS_COMM_ERROR);
    LOG("Received results. Terminating.\n\n");
    redisFree(conn);

    /* Disconnect shared memory */
    if (in_afl) {
        shmdt(trace_bits);
    }

    /* Terminate with CRASH signal if Java program terminated abnormally */
    if (kelinci_status == STATUS_CRASH) {
        LOG("Crashing...\n");
        abort();
    }

    /**
     * If JAVA side timed out, keep looping here till AFL hits its time-out.
     * In a good set-up, the time-out on the JAVA process is slightly longer
     * than AFLs time-out to prevent hitting this.
     **/
    if (kelinci_status == STATUS_TIMEOUT) {
        LOG("Starting infinite loop...\n");
        while (1) {
            sleep(10);
        }
    }

    LOG_AND_CLOSE("Terminating normally.\n");

    return 0;
}