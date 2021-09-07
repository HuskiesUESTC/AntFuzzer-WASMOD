package edu.uestc.antfuzzer;

import edu.uestc.antfuzzer.framework.bean.config.framework.FrameworkConfig;
import edu.uestc.antfuzzer.framework.core.BeanFactory;
import edu.uestc.antfuzzer.framework.core.ClassLoader;
import edu.uestc.antfuzzer.framework.exception.ConfigException;
import edu.uestc.antfuzzer.framework.util.*;
import org.junit.Before;
import org.junit.Test;

public class EOSTest {

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
            String[] args = new String[]{"-fuzzingConfigFile", "./Test.json"};
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
    public void testAPI() {
        try {
            FrameworkConfig frameworkConfig = configUtil.getFrameworkConfig();
            String startSh = frameworkConfig.getNodeosStartShell();
            pipeUtil.execute("rm -rf /Users/jiangtianxing/Library/Application\\ Support/eosio");
            pipeUtil.execute("rm ./log/nodeos.log");
            cleosUtil.unlockWallet(frameworkConfig.getAccount().getPrivateKey());
            pipeUtil.execute("/bin/bash " + startSh);

            pipeUtil.waitForExecute("cleos get currency balance account testeosfrom");
            // 部署系统合约
            cleosUtil.createAccount("eosio.token", frameworkConfig.getEosTokenPublicKey());
            cleosUtil.setContract("eosio.token", frameworkConfig.getEosTokenPublicKey());
            cleosUtil.pushAction(
                    "eosio.token",
                    "create",
                    jsonUtil.getJson(
                            "eosio",
                            "1000000000.0000 EOS"),
                    "eosio.token");
            pipeUtil.execute("killall nodeos");
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
