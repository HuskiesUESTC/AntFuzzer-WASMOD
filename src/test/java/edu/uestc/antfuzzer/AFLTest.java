package edu.uestc.antfuzzer;

import edu.uestc.antfuzzer.framework.bean.FuzzInfo;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfoCollection;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.core.BeanFactory;
import edu.uestc.antfuzzer.framework.core.ClassLoader;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.exception.ConfigException;
import edu.uestc.antfuzzer.framework.type.StringGenerator;
import edu.uestc.antfuzzer.framework.type.interfaces.TypeGenerator;
import edu.uestc.antfuzzer.framework.type.number.decimal.Float32Generator;
import edu.uestc.antfuzzer.framework.type.number.integer.IntGenerator;
import edu.uestc.antfuzzer.framework.util.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AFLTest {

    private BeanFactory beanFactory;
    private EnvironmentUtil environmentUtil;

    /**
     * 初始化框架
     */
    @Before
    public void initFramework() {
        try {
            beanFactory = new BeanFactory(new ClassLoader());
            // 加载默认配置文件
            ConfigUtil configUtil = beanFactory.getComponent(ConfigUtil.class);
            configUtil.loadConfig();
            // 解析命令行输入的参数
            InputArgumentUtil inputArgumentUtil = beanFactory.getComponent(InputArgumentUtil.class);
            String[] args = new String[]{"-fuzzingConfigFile", "./Test.json"};
            inputArgumentUtil.parseCommand(args);
            // 获取fuzzer
            configUtil.loadFuzzer(beanFactory.getFuzzers());
            // 获取环境变量
            environmentUtil = beanFactory.getComponent(EnvironmentUtil.class);
        } catch (ConfigException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * AFL-redis-AntFuzzer联合调试
     * /Users/jiangtianxing/code/afl/afl-fuzz2 -i /Users/jiangtianxing/code/AntFuzzer/fuzz/interface2/in_dir -o /Users/jiangtianxing/code/AntFuzzer/fuzz/interface2/out_dir -t 1000000 -l 12 /Users/jiangtianxing/code/AntFuzzer/fuzz/interface2/interface @@
     */
    public void connectTest() throws AFLException, IOException {
        Random random = new Random();
        RedisUtil redisUtil = beanFactory.getComponent(RedisUtil.class);
        FileUtil fileUtil = beanFactory.getComponent(FileUtil.class);
        byte[] bitmap = new byte[65536];
        String prevStr = null;
        for (int i = 0; i < 100000; i++) {
            String filepath = redisUtil.getInputFilepath(2);
            if (!fileUtil.exists(filepath)) {
                continue;
            }
            String data = fileUtil.read(filepath);
            bitmap[random.nextInt(65536)] = 1;
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, 2));
            if (data.equals(prevStr)) {
                continue;
            }
            prevStr = data;
            System.out.println(data);
        }
    }

    /**
     * 单个AFL参数生成测试
     */
    @Test
    public void singleAFLTest() throws InterruptedException, AFLException, IOException {
        // 初始化所有action的参数生成器列表
        environmentUtil.setAflGeneratorInfoCollectionList(new LinkedList<>());

        Random random = new Random();
        AFLUtil aflUtil = beanFactory.getComponent(AFLUtil.class);

        aflUtil.endAFL();

        ArgDriver argDriver = ArgDriver.afl;
        IntGenerator int32Generator = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator = new Float32Generator(random, argDriver, aflUtil, 0.2f);

        // 初始化随机数种子
        generateSingleAFLSeed(int32Generator, stringGenerator, float32Generator);
        // 启动afl
        aflUtil.switchAFL();

        RedisUtil redisUtil = beanFactory.getComponent(RedisUtil.class);
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        byte[] bitmap = new byte[65536];
        for (int i = 0; i < 2000; i++) {
            actionFuzzingResult.setCount(i + 1);

            int32Generator.generate();
            stringGenerator.generate();
            float32Generator.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        aflUtil.endAFL();
    }

    private void generateSingleAFLSeed(TypeGenerator... typeGenerators) throws InterruptedException, AFLException, IOException {
        LinkedList<AFLGeneratorInfoCollection> aflGeneratorInfoCollectionList = environmentUtil.getAflGeneratorInfoCollectionList();
        // 初始化当前action的参数生成器列表
        AFLGeneratorInfoCollection currentAflGeneratorInfoCollection = new AFLGeneratorInfoCollection();
        aflGeneratorInfoCollectionList.offer(currentAflGeneratorInfoCollection);
        environmentUtil.setCurrentAFLGeneratorInfoCollection(currentAflGeneratorInfoCollection);
        // 初始化fuzzing结果
        ActionFuzzingResult actionFuzzingResult = new ActionFuzzingResult();
        actionFuzzingResult.setCount(0);
        environmentUtil.setActionFuzzingResult(actionFuzzingResult);
        // 预执行
        for (TypeGenerator typeGenerator : typeGenerators) {
            typeGenerator.generate();
        }
    }

    /**
     * 两个AFL参数生成测试
     */
    @Test
    public void doubleAFLTest() throws InterruptedException, AFLException, IOException {
        // 初始化所有action的参数列表
        environmentUtil.setAflGeneratorInfoCollectionList(new LinkedList<>());

        Random random = new Random();
        AFLUtil aflUtil = beanFactory.getComponent(AFLUtil.class);
        ArgDriver argDriver = ArgDriver.afl;

        aflUtil.endAFL();

        // action1的参数生成器列表
        IntGenerator int32Generator1 = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator1 = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator1 = new Float32Generator(random, argDriver, aflUtil, 0.2f);

        // action2的参数生成列表
        IntGenerator int32Generator2 = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator2a = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator2 = new Float32Generator(random, argDriver, aflUtil, 0.2f);
        IntGenerator int64Generator2 = new IntGenerator(random, argDriver, aflUtil, 8, 0.2f);
        StringGenerator stringGenerator2b = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);

        AFLGeneratorInfoCollection currentAFLGeneratorInfoCollection = generateDoubleAFLSeed(int32Generator1, stringGenerator1, float32Generator1);
        ActionFuzzingResult actionFuzzingResult1 = environmentUtil.getActionFuzzingResult();

        aflUtil.clearArguments();

        AFLGeneratorInfoCollection nextAFLGeneratorInfoCollection = generateDoubleAFLSeed(stringGenerator2a, int32Generator2, float32Generator2, int64Generator2, stringGenerator2b);
        ActionFuzzingResult actionFuzzingResult2 = environmentUtil.getActionFuzzingResult();

        environmentUtil.setCurrentAFLGeneratorInfoCollection(currentAFLGeneratorInfoCollection);
        environmentUtil.setNextAFLGeneratorInfoCollection(nextAFLGeneratorInfoCollection);

        environmentUtil.setActionFuzzingResult(actionFuzzingResult1);
        RedisUtil redisUtil = beanFactory.getComponent(RedisUtil.class);
        byte[] bitmap = new byte[65536];
        // 启动afl
        aflUtil.switchAFL();
        for (int i = 0; i < 1000; i++) {
            int count = actionFuzzingResult1.getCount();
            actionFuzzingResult1.setCount(count + 1);

            int32Generator1.generate();
            stringGenerator1.generate();
            float32Generator1.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        // 切换afl
        environmentUtil.setCurrentAFLGeneratorInfoCollection(nextAFLGeneratorInfoCollection);
        environmentUtil.setNextAFLGeneratorInfoCollection(null);

        environmentUtil.setActionFuzzingResult(actionFuzzingResult2);
        aflUtil.switchAFL();
        bitmap = new byte[65536];
        actionFuzzingResult2 = environmentUtil.getActionFuzzingResult();
        aflUtil.clearArguments();

        for (int i = 0; i < 2000; i++) {
            int count = actionFuzzingResult2.getCount();
            actionFuzzingResult2.setCount(count + 1);

            int32Generator2.generate();
            stringGenerator2a.generate();
            float32Generator2.generate();
            int64Generator2.generate();
            stringGenerator2b.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        aflUtil.endAFL();
    }

    private AFLGeneratorInfoCollection generateDoubleAFLSeed(TypeGenerator... typeGenerators) throws InterruptedException, AFLException, IOException {
        // 获取所有action的参数生成器列表
        LinkedList<AFLGeneratorInfoCollection> aflGeneratorInfoCollectionList = environmentUtil.getAflGeneratorInfoCollectionList();

        // 初始化当前action的参数生成器列表
        AFLGeneratorInfoCollection currentAflGeneratorInfoCollection = new AFLGeneratorInfoCollection();
        aflGeneratorInfoCollectionList.offer(currentAflGeneratorInfoCollection);
        environmentUtil.setCurrentAFLGeneratorInfoCollection(currentAflGeneratorInfoCollection);

        // 初始化fuzzing结果
        ActionFuzzingResult actionFuzzingResult = new ActionFuzzingResult();
        actionFuzzingResult.setCount(0);
        environmentUtil.setActionFuzzingResult(actionFuzzingResult);

        // 预执行
        for (TypeGenerator typeGenerator : typeGenerators) {
            typeGenerator.generate();
        }

        return currentAflGeneratorInfoCollection;
    }

    @Test
    public void tripleAFLTest() throws InterruptedException, AFLException, IOException {
        // 初始化所有action的参数列表
        environmentUtil.setAflGeneratorInfoCollectionList(new LinkedList<>());

        Random random = new Random();
        AFLUtil aflUtil = beanFactory.getComponent(AFLUtil.class);
        aflUtil.endAFL();


        ArgDriver argDriver = ArgDriver.afl;

        // action1的参数生成器列表
        IntGenerator int32Generator1 = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator1 = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator1 = new Float32Generator(random, argDriver, aflUtil, 0.2f);

        // action2的参数生成列表
        IntGenerator int32Generator2 = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator2a = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator2 = new Float32Generator(random, argDriver, aflUtil, 0.2f);
        IntGenerator int64Generator2 = new IntGenerator(random, argDriver, aflUtil, 8, 0.2f);
        StringGenerator stringGenerator2b = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);

        // action3的参数生成列表
        IntGenerator int32Generator3 = new IntGenerator(random, argDriver, aflUtil, 4, 0.2f);
        StringGenerator stringGenerator3a = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        StringGenerator stringGenerator3b = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        StringGenerator stringGenerator3c = new StringGenerator(new ArrayList<>(), random, argDriver, aflUtil);
        Float32Generator float32Generator3 = new Float32Generator(random, argDriver, aflUtil, 0.2f);

        // 参数生成器生成
        AFLGeneratorInfoCollection firstAFLGeneratorInfoCollection = generateDoubleAFLSeed(int32Generator1, stringGenerator1, float32Generator1);
        ActionFuzzingResult actionFuzzingResult1 = environmentUtil.getActionFuzzingResult();

        aflUtil.clearArguments();

        AFLGeneratorInfoCollection secondAFLGeneratorInfoCollection = generateDoubleAFLSeed(stringGenerator2a, int32Generator2, float32Generator2, int64Generator2, stringGenerator2b);
        ActionFuzzingResult actionFuzzingResult2 = environmentUtil.getActionFuzzingResult();

        aflUtil.clearArguments();

        AFLGeneratorInfoCollection thirdAFLGeneratorInfoCollection = generateDoubleAFLSeed(int32Generator3, stringGenerator3a, stringGenerator3b, stringGenerator3c, float32Generator3);
        ActionFuzzingResult actionFuzzingResult3 = environmentUtil.getActionFuzzingResult();


        RedisUtil redisUtil = beanFactory.getComponent(RedisUtil.class);
        byte[] bitmap = new byte[65536];

        // 设置当前与下一个参数生成器列表
        environmentUtil.setCurrentAFLGeneratorInfoCollection(firstAFLGeneratorInfoCollection);
        environmentUtil.setNextAFLGeneratorInfoCollection(secondAFLGeneratorInfoCollection);

        environmentUtil.setActionFuzzingResult(actionFuzzingResult1);

        // 启动afl
        aflUtil.switchAFL();
        for (int i = 0; i < 1000; i++) {
            int count = actionFuzzingResult1.getCount();
            actionFuzzingResult1.setCount(count + 1);

            int32Generator1.generate();
            stringGenerator1.generate();
            float32Generator1.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        // 切换afl
        bitmap = new byte[65536];

        environmentUtil.setCurrentAFLGeneratorInfoCollection(secondAFLGeneratorInfoCollection);
        environmentUtil.setNextAFLGeneratorInfoCollection(thirdAFLGeneratorInfoCollection);

        environmentUtil.setActionFuzzingResult(actionFuzzingResult2);

        aflUtil.switchAFL();
        actionFuzzingResult2 = environmentUtil.getActionFuzzingResult();
        aflUtil.clearArguments();

        for (int i = 0; i < 2000; i++) {
            int count = actionFuzzingResult2.getCount();
            actionFuzzingResult2.setCount(count + 1);

            int32Generator2.generate();
            stringGenerator2a.generate();
            float32Generator2.generate();
            int64Generator2.generate();
            stringGenerator2b.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        // 切换afl
        bitmap = new byte[65536];

        environmentUtil.setCurrentAFLGeneratorInfoCollection(thirdAFLGeneratorInfoCollection);
        environmentUtil.setNextAFLGeneratorInfoCollection(null);

        environmentUtil.setActionFuzzingResult(actionFuzzingResult3);

        aflUtil.switchAFL();
        actionFuzzingResult3 = environmentUtil.getActionFuzzingResult();
        aflUtil.clearArguments();

        for (int i = 0; i < 500; i++) {
            int count = actionFuzzingResult3.getCount();
            actionFuzzingResult3.setCount(count + 1);

            int32Generator3.generate();
            stringGenerator3a.generate();
            stringGenerator3b.generate();
            stringGenerator3c.generate();
            float32Generator3.generate();

            aflUtil.clearArguments();
            bitmap[random.nextInt(65536)] = 1;
            environmentUtil.setBitmap(bitmap);
            redisUtil.pushFuzzInfo(new FuzzInfo(bitmap, FuzzingStatus.NEXT, aflUtil.getTo().getId()));
        }

        aflUtil.endAFL();
    }
}
