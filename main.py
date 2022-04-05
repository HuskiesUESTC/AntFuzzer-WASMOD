import os
import psutil
import redis
import threading

script_path = '/root/AntFuzzer/mvn.sh'
root_path = '/root/AntFuzzer/config/'
client = redis.StrictRedis()
config_key = 'AntFuzzer-Config'
init_key = 'AntFuzzer-Init'


# 检测是否存在Java进程
def check_process():
    pl = psutil.pids()
    for pid in pl:
        if psutil.Process(pid).name() == 'java':
            print("Java exists")
            return True
    else:
        return False


# 定时执行
def execute_task():
    # 初始化
    if (not client.__contains__(init_key)) or client.get(init_key) == b'0':
        # 执行脚本的配置文件
        config = [
            # 'MissingAuth.json',
            # 'BlockDependency-MissingAuth.json',
            # 'Rollback.json',
            'FakeEOSTransfer.json',
            'ForgedNotification.json',
            # 'HackRecipient.json',
            'IntegerOverflow.json',
            # 'StackOverflow.json',
            # coverage
            # 'coverage/AFL-Test.json',
            # 'coverage/Local-Test.json',
            # eosfuzzer
            #   afl
            'eosfuzzer/afl/FakeEOSTransfer.json',
            # 'eosfuzzer/afl/ForgedNotification.json',
            # 'eosfuzzer/afl/MissingAuth.json',
            # 'eosfuzzer/afl/BlockDependency-MissingAuth.json',
            # 'eosfuzzer/afl/Rollback.json',
            # 'eosfuzzer/afl/HackRecipient.json',
            # 'eosfuzzer/afl/Test.json',
            #   local
            # 'eosfuzzer/local/FakeEOSTransfer.json',
            # 'eosfuzzer/local/ForgedNotification.json',
            # 'eosfuzzer/local/MissingAuth.json',
            # 'eosfuzzer/local/BlockDependency-MissingAuth.json',
            # 'eosfuzzer/local/Rollback.json',
            # 'eosfuzzer/local/HackRecipient.json',
            # 'eosfuzzer/local/IntegerOverflow.json',
            # 'eosfuzzer/local/StackOverflow.json',
            # xblocks
            #   afl
            # 'xblocks/afl/FakeEOSTransfer.json',
            # 'xblocks/afl/ForgedNotification.json',
            # 'xblocks/afl/MissingAuth.json',
            # 'xblocks/afl/BlockDependency-MissingAuth.json',
            # 'xblocks/afl/Rollback.json',
            # 'xblocks/afl/HackRecipient.json',
            #   local
            # 'xblocks/local/FakeEOSTransfer.json',
            # 'xblocks/local/ForgedNotification.json',
            # 'xblocks/local/MissingAuth.json',
            # 'xblocks/local/BlockDependency-MissingAuth.json',
            # 'xblocks/local/Rollback.json',
            # 'xblocks/local/HackRecipient.json',
            # 'xblocks/local/IntegerOverflow.json',
            # 'xblocks/local/StackOverflow.json',
        ]
        # 将配置文件加入到redis队列中
        for item in config:
            client.rpush(config_key, item)
        # 设置初始化
        client.set(init_key, 1)
    # 如果 Java 进程存在，则间隔一段时间后再进行检查
    if check_process() is True:
        timer = threading.Timer(10, execute_task)
        timer.start()
    # 如果 还有 任务，则继续执行
    if client.llen(config_key) > 0:
        file = str(client.lpop(config_key), 'utf-8')
        config_filepath = root_path + file
        os.system(f"bash {script_path} {config_filepath}")
        timer = threading.Timer(10, execute_task)
        timer.start()
    # 如果 Java 进程运行完，且没有任务则终止
    else:
        client.set(init_key, 0)


# client.delete(init_key)
# client.delete(config_key)
execute_task()
