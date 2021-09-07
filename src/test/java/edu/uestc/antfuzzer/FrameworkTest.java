package edu.uestc.antfuzzer;

import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzerInfo;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzingConfig;
import edu.uestc.antfuzzer.framework.core.BeanFactory;
import edu.uestc.antfuzzer.framework.core.ClassLoader;
import edu.uestc.antfuzzer.framework.exception.ConfigException;
import edu.uestc.antfuzzer.framework.util.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class FrameworkTest {

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
     * 测试ioc容器是否异常InvocationTargetException
     */
    @Test
    public void testAnnotation() {
        Set<Handler> fuzzers = environmentUtil.getFuzzers();
        System.out.println(fuzzers.size());
        assertTrue(fuzzers.size() > 0);

    }

    /**
     * 测试配置文件的json序列化
     */
    @Test
    public void convertJsonToFuzzingConfig() throws IOException {
        FileUtil fileUtil = beanFactory.getComponent(FileUtil.class);
        JsonUtil jsonUtil = beanFactory.getComponent(JsonUtil.class);
        String filepath = "./Test.json";
        if (fileUtil.exists(filepath)) {
            String data = fileUtil.read(filepath);
            FuzzingConfig fuzzingConfig = jsonUtil.parseJsonToObject(FuzzingConfig.class, data);
            System.out.println(fuzzingConfig);
        }
    }

    /**
     * 测试配置文件的json序列化
     */
    @Test
    public void convertFuzzingConfigToJson() {
        Set<Handler> fuzzers = environmentUtil.getFuzzers();
        assertTrue(fuzzers.size() > 0);
        JsonUtil jsonUtil = beanFactory.getComponent(JsonUtil.class);
        for (Handler handler : fuzzers) {
            assertNotNull(handler);
            FuzzerInfo fuzzerInfo = handler.getFuzzerInfo();
            String json = jsonUtil.covertObjectToJson(fuzzerInfo);
            System.out.println(json);
        }
    }

    /**
     * 测试配置文件重置对其他类中依赖的影响
     */
    @Test
    public void scanSmartContracts() {
        FileUtil fileUtil = beanFactory.getComponent(FileUtil.class);
        String[] dirs = fileUtil.getValidSmartContractDirs();
        assertTrue(dirs != null && dirs.length > 0);
    }

    /**
     * 测试smartContract加载
     */
    @Test
    public void loadSmartContracts() {
        SmartContractUtil smartContractUtil = beanFactory.getComponent(SmartContractUtil.class);
        SmartContract smartContract = smartContractUtil.loadSmartContract("blog");
        assertNotNull(smartContract);
    }
}
