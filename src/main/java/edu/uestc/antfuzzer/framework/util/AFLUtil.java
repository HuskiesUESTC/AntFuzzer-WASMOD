package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.FuzzInfo;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfo;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfoCollection;
import edu.uestc.antfuzzer.framework.enums.AFLExceptionStatus;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.StringGenerator;
import edu.uestc.antfuzzer.framework.type.interfaces.AFLGenerator;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class AFLUtil {

    private final static int endAFLWaitTime = 1000;
    private final static int startAFLWaitTime = 6000;
    private final static int waitGapTime = 1000;
    private final static String exampleFilePath = "./fuzz/interface%s/in_dir/example";
    private final static String outputDir = "./fuzz/interface%s/out_dir";
    private final static String logFilepath = "./log/afl%s.log";
    private String prevStr = null;

    @Getter
    private final LinkedList<AFL> aflPool;

    @Getter
    private AFL to;

    @Getter
    @Setter
    private boolean usingAFLDriver;

    @Autowired
    private PipeUtil pipeUtil;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private ThreadUtil threadUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private LogUtil logUtil;

    public AFLUtil() {
        aflPool = new LinkedList<>();
        to = new AFL(1);
        aflPool.offer(to);
        aflPool.offer(new AFL(2));
    }

    /**
     * 清空当前生成的参数
     */
    public void clearArguments() {
        clearArguments(to);
    }


    public void clearArguments(AFL afl) {
        afl.bytes = null;
        afl.currentArgumentIndex = -1;
    }

    /**
     * 设置默认没有使用AFL
     */
    public void setDefaultUsingAFLDriver() {
        this.usingAFLDriver = false;
    }

    /**
     * 获取生成的参数字节
     *
     * @param aflGenerator 参数类型
     * @param defaultStr   默认字符串
     * @param exampleStr   示例字符串
     * @return 生成的参数字节
     * @throws AFLException 异常
     */
    public byte[] getByteArrayOfArgument(AFLGenerator aflGenerator, String defaultStr, String exampleStr) throws AFLException {
        if (environmentUtil.isExecutingBeforeMethod()) {
            return defaultStr.getBytes(StandardCharsets.UTF_8);
        }
        // 获取当前action的参数列表
        AFLGeneratorInfoCollection currentAFLGeneratorInfoCollection = environmentUtil.getCurrentAFLGeneratorInfoCollection();
        // 用于按照优先级对参数类型进行排序
        PriorityQueue<AFLGeneratorInfo> aflGeneratorInfoQueue = currentAFLGeneratorInfoCollection.getQueue();
        // 存储参数类型列表
        ArrayList<AFLGeneratorInfo> aflGeneratorInfoList = currentAFLGeneratorInfoCollection.getList();

        byte[] result = defaultStr.getBytes(StandardCharsets.UTF_8);
        int currentArgumentIndex = (++to.currentArgumentIndex);
        // 如果是首次从AFL中获取参数
        if (environmentUtil.getActionFuzzingResult().getCount() == 0) {
            exampleStr = (exampleStr == null) ? defaultStr : exampleStr;
            AFLGeneratorInfo generatorInfo = new AFLGeneratorInfo(aflGenerator.getClass(), aflGenerator.getPriority(), aflGenerator.getByteSize(), exampleStr, to.currentArgumentIndex);
            aflGeneratorInfoQueue.offer(generatorInfo);
            aflGeneratorInfoList.add(generatorInfo.getIndex(), generatorInfo);
            return result;
        }
        // 如果是从afl中获取参数
        // 若果当前byte数组为空
        waitAFL(to);
        if (to.bytes == null) {
            try {
                String data = getAflRandomStr();
                // 对于不足的位进行随机补全
                int currentAFLArgumentSize = data.getBytes(StandardCharsets.UTF_8).length;
                if (currentAFLArgumentSize < to.totalArgumentSize) {
                    int len = to.totalArgumentSize - currentAFLArgumentSize;
                    data += stringUtil.createRandomStr(len);
                }
                // 此时实际产生的字节长度可能大于等于所需要的字节长度
                to.bytes = data.getBytes(StandardCharsets.UTF_8);
                // 根据生成的字节长度调整不同参数类型的位置
                adjustStringAFLGeneratorInfo(aflGeneratorInfoQueue);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                logUtil.logException(exception);
                throw new AFLException(AFLExceptionStatus.CUR_INPUT_FILE_NOT_EXISTS);
            }
        }
        String errorMsg = "Current afl generators exist error";
        AFLExceptionStatus status = AFLExceptionStatus.INVALID_ARGUMENTS;
        if (currentArgumentIndex < aflGeneratorInfoList.size() && currentArgumentIndex > -1) {
            AFLGeneratorInfo aflGeneratorInfo = aflGeneratorInfoList.get(currentArgumentIndex);
            errorMsg = "Current afl arguments have error";
            if (aflGeneratorInfo != null && to.bytes != null && to.bytes.length > 0) {
                errorMsg = "Current afl arguments' split has error";
                int start = aflGeneratorInfo.getStart();
                int end = aflGeneratorInfo.getEnd();
                if (start >= 0 && end >= 0 && start <= end && end <= to.bytes.length) {
                    try {
                        return Arrays.copyOfRange(to.bytes, aflGeneratorInfo.getStart(), aflGeneratorInfo.getEnd());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        errorMsg = "参数生成错误";
                        logUtil.logException(exception);
                    }
                }
            }
        }
        if (!isAFLRunning()) {
            status = AFLExceptionStatus.PROCESS_EXIT;
            errorMsg = "AFL运行终止";
        }
        throw new AFLException(status, errorMsg);
    }

    /**
     * 获取字符串
     */
    private String getAflRandomStr() throws AFLException, InterruptedException {
        int count = 0;
        while (count++ < 15) {
            try {
                String filepath = redisUtil.getInputFilepath(to.id);
                if (fileUtil.exists(filepath)) {
                    String data = fileUtil.read(filepath);
                    if (!data.equals(prevStr)) {
                        prevStr = data;
                        System.out.println("AFL generate:" + prevStr);
                        return data;
                    }
                }
            } catch (IOException ignored) {
            }
            FuzzInfo fuzzInfo = new FuzzInfo(environmentUtil.getBitmap().getJointBitMap(), FuzzingStatus.NEXT, to.id);
            redisUtil.pushFuzzInfo(fuzzInfo);
            threadUtil.waitFor(50);
        }
        // 检查当前afl进程是否存在
        boolean isRunning = to.checkIfRunning();
        AFLExceptionStatus status = isRunning ? AFLExceptionStatus.PROCESS_EXIT : AFLExceptionStatus.CUR_INPUT_FILE_NOT_EXISTS;
        throw new AFLException(status);
    }

    /**
     * 获取生成的String类型的参数
     */
    public String getStringOfArgument(AFLGenerator aflGenerator, String defaultStr) throws IOException, AFLException {
        String result = "";
        byte[] data = getByteArrayOfArgument(aflGenerator, defaultStr, null);
        if (data != null && data.length > 0)
            result = new String(data);
        return result;
    }

    /**
     * 重新调整string类型参数的起止位置
     */
    private void adjustStringAFLGeneratorInfo(PriorityQueue<AFLGeneratorInfo> aflGeneratorInfoQueue) {
        // 计算string类型参数的个数
        int totalStringArgumentCount = 0;
        for (AFLGeneratorInfo aflGeneratorInfo : aflGeneratorInfoQueue) {
            if (aflGeneratorInfo.getClazz() == StringGenerator.class)
                totalStringArgumentCount++;
        }
        // 重新划分每种参数的起始位置
        AFLGeneratorInfo lastAFLGeneratorInfo = null;
        int stringByteSize = -1;
        while (aflGeneratorInfoQueue.size() > 0) {
            AFLGeneratorInfo aflGeneratorInfo = aflGeneratorInfoQueue.poll();
            // 起始位置默认为上一参数的截止位置
            int start = (lastAFLGeneratorInfo == null) ? 0 : lastAFLGeneratorInfo.getEnd();
            aflGeneratorInfo.setStart(start);
            // 如果是普通类型
            if (aflGeneratorInfo.getClazz() != StringGenerator.class) {
                aflGeneratorInfo.setEnd(start + aflGeneratorInfo.getByteSize());
                // 如果是字符串类型
            } else {
                // 计算string类型参数的长度
                if (stringByteSize == -1) {
                    int extraByteSize = to.bytes.length - aflGeneratorInfo.getStart();
                    stringByteSize = extraByteSize / totalStringArgumentCount;
                }
                aflGeneratorInfo.setEnd(start + stringByteSize);
            }
            lastAFLGeneratorInfo = aflGeneratorInfo;
        }
    }

    /**
     * 切换AFL
     */
    public void switchAFL() throws InterruptedException {
        AFL from = aflPool.poll();
        to = aflPool.peek();
        aflPool.offer(from);

        AFLGeneratorInfoCollection currentAFLGeneratorInfoCollection = environmentUtil.getCurrentAFLGeneratorInfoCollection();
        AFLGeneratorInfoCollection nextAFLGeneratorInfoCollection = environmentUtil.getNextAFLGeneratorInfoCollection();

        assert from != null;
        if (from.isRunning())
            endAFL(from);

        if (nextAFLGeneratorInfoCollection != null && nextAFLGeneratorInfoCollection.getList().size() > 0)
            startAFL(from, nextAFLGeneratorInfoCollection);

        if (currentAFLGeneratorInfoCollection == null || currentAFLGeneratorInfoCollection.getList().size() == 0)
            return;

        if (!to.checkIfRunning())
            startAFL(to, currentAFLGeneratorInfoCollection);

        waitAFL(to);
    }

    /**
     * 判断当前afl是否运行
     */
    public boolean isAFLRunning() {
        return to.checkIfRunning();
    }

    /**
     * 启动AFL
     */
    private void startAFL(AFL afl, AFLGeneratorInfoCollection aflGeneratorInfoCollection) {
        threadUtil.execute(() -> {
            try {
                // 如果是中途崩溃，需要清除历史参数
                if (afl.bytes != null)
                    clearArguments(afl);
                afl.setTime(System.currentTimeMillis());
                afl.totalArgumentSize = 0;

                int id = afl.getId();
                redisUtil.clearFilepath(id);

                // 设置参数生成器的相关参数，并写入种子文件
                StringBuilder exampleData = new StringBuilder();
                List<AFLGeneratorInfo> aflGeneratorInfoQueue = aflGeneratorInfoCollection.getList();
                for (AFLGeneratorInfo aflGeneratorInfo : aflGeneratorInfoQueue) {
                    exampleData.append(aflGeneratorInfo.getExampleStr());
                    afl.totalArgumentSize += aflGeneratorInfo.getByteSize();
                }
                // byte 补足
                if (afl.totalArgumentSize < 4) {
                    afl.totalArgumentSize = 4;
                }
                int len = afl.totalArgumentSize - exampleData.toString().getBytes(StandardCharsets.UTF_8).length;
                if (len > 0) {
                    exampleData.append(stringUtil.createRandomStr(len));
                }
                String seedString = exampleData.toString();
                afl.totalArgumentSize = seedString.getBytes(StandardCharsets.UTF_8).length;

                waitAFL(afl);
                afl.setRunning(true);

                fileUtil.write(String.format(exampleFilePath, id), seedString, false);

                redisUtil.clearFuzzInfo(afl.getId());
                pipeUtil.execute("rm -rf " + String.format(logFilepath, id));
                pipeUtil.execute("rm -rf " + String.format(outputDir, id));
                String command = String.format("/bin/bash ./script/afl%s.sh %s", id, exampleData.length());
                pipeUtil.execute(command, true);
            } catch (IOException exception) {
                exception.printStackTrace();
                logUtil.logException(exception);
            }
        }, true);
    }

    /**
     * 终止AFL
     */
    private void endAFL(AFL afl) {
        threadUtil.execute(() -> {
            try {
                waitAFL(afl);
                afl.setRunning(false);
                afl.setTime(System.currentTimeMillis());
                pipeUtil.execute(String.format("killall afl-fuzz%s", afl.getId()));
                threadUtil.waitFor(endAFLWaitTime);
            } catch (InterruptedException | IOException exception) {
                exception.printStackTrace();
                logUtil.logException(exception);
            }
        }, true);
    }

    /**
     * 终止所有的AFL
     */
    public void endAFL() {
        for (AFL afl : aflPool)
            endAFL(afl);
    }

    /**
     * 等待AFL启动或终止完成
     */
    private void waitAFL(AFL afl) {
        try {
            // 如果当前afl正在关闭
            while (!afl.isRunning() && System.currentTimeMillis() - afl.getTime() < endAFLWaitTime)
                threadUtil.waitFor(waitGapTime);
            // 如果afl正在开启
            while (afl.isRunning() && System.currentTimeMillis() - afl.getTime() < startAFLWaitTime)
                threadUtil.waitFor(waitGapTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            logUtil.logException(exception);
        }
    }

    public class AFL {
        int currentArgumentIndex = -1;
        // 需要生成参数的大小
        int totalArgumentSize = 0;

        @Getter
        private final int id;
        @Setter
        @Getter
        private boolean running;
        @Getter
        @Setter
        private long time;

        private byte[] bytes;

        public AFL(int id) {
            this.id = id;
        }

        public boolean checkIfRunning() {
            try {
                String command = String.format("ps -ef|grep afl-fuzz%s|grep -v grep|wc -l", id);
                List<String> result = pipeUtil.executeWithResult(command);
                int num = Integer.parseInt(result.get(0).trim());
                running = num > 0;
            } catch (InterruptedException | IOException exception) {
                exception.printStackTrace();
                logUtil.logException(exception);
            }
            return running;
        }
    }
}