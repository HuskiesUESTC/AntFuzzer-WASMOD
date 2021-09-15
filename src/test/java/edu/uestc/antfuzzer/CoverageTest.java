package edu.uestc.antfuzzer;

import edu.uestc.antfuzzer.framework.core.BeanFactory;
import edu.uestc.antfuzzer.framework.core.ClassLoader;
import edu.uestc.antfuzzer.framework.exception.ConfigException;
import edu.uestc.antfuzzer.framework.util.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class CoverageTest {
    private BeanFactory beanFactory;
    private EnvironmentUtil environmentUtil;
    private EOSUtil eosUtil;
    private EOSUtil.CleosUtil cleosUtil;
    private ConfigUtil configUtil;
    private PipeUtil pipeUtil;
    private JsonUtil jsonUtil;

    /**
     * 初始化框架
     */
    @Before
    public void initFramework() {
        try {
            beanFactory = new BeanFactory(new ClassLoader());
            // 加载默认配置文件
            configUtil = beanFactory.getComponent(ConfigUtil.class);
            configUtil.loadConfig();
            // 解析命令行输入的参数
            InputArgumentUtil inputArgumentUtil = beanFactory.getComponent(InputArgumentUtil.class);
            String[] args = new String[]{"-fuzzingConfigFile", "./config/test.json"};
            inputArgumentUtil.parseCommand(args);
            // 获取fuzzer
            configUtil.loadFuzzer(beanFactory.getFuzzers());
            // 获取环境变量
            environmentUtil = beanFactory.getComponent(EnvironmentUtil.class);
            eosUtil = beanFactory.getComponent(EOSUtil.class);
            cleosUtil = eosUtil.getCleosUtil();
            pipeUtil = beanFactory.getComponent(PipeUtil.class);
            jsonUtil = beanFactory.getComponent(JsonUtil.class);
        } catch (ConfigException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void coverageTest() {
        BitMapUtil bitMapUtil = beanFactory.getComponent(BitMapUtil.class);
        BitMapUtil.BitMap bitMap = bitMapUtil.getBitMap();
        for (Map.Entry<String, Integer> entry : bitMap.getAllBitMapCounts().entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        System.out.println("all");
        System.out.println(bitMap.getJointBitMapCount());
    }
}
