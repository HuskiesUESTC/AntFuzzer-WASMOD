package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.abi.Struct;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzerInfo;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.util.*;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class InitFuzzerFuzzingEnvironment extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private EOSUtil eosUtil;

    @Autowired
    private FuzzerUtil fuzzerUtil;

    @Autowired
    private TypeUtil typeUtil;

    @Autowired
    private LogUtil logUtil;

    private SmartContract smartContract;
    private Handler fuzzer;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        smartContract = environmentUtil.getSmartContract();
        fuzzer = environmentUtil.getFuzzer();

        return setSmartContractActions() &&
                validateFuzzerFuzzingScope() && // 比对fuzzer的测试域是否符合当前fuzzing合约的函数
                initEOSEnvironment() && // 初始化EOS环境
                injectObjectIntoFuzzer() && // 为fuzzer注入参数
                setCurrentArgumentGenerator() && // 设置参数生成器
                setEnvironmentArguments() && // 设置环境变量
                executeFuzzerBeforeMethod(); // 执行before
    }

    /**
     * 设置当前需要测试的智能合约函数
     */
    private boolean setSmartContractActions() {
        environmentUtil.setActions(smartContract.getAbi().getActions());
        return true;
    }

    /**
     * 比对fuzzer的测试域是否符合当前函数
     */
    private boolean validateFuzzerFuzzingScope() {
        List<Action> actions = environmentUtil.getActions();
        // 检查合约函数的检测范围，如果是 transfer 的话，首先检查 ABI 中是否包含 transfer，没有直接跳过
        FuzzScope fuzzScope = fuzzer.getFuzzerInfo().getFuzzScope();
        if (fuzzScope == FuzzScope.transfer) {
            List<Action> newActions = new ArrayList<>();
            boolean hasTransferAction = false;
            for (Action action : actions) {
                if (action.getName().equalsIgnoreCase("transfer")) {
                    newActions.add(action);
                    environmentUtil.setActions(newActions);
                    hasTransferAction = true;
                    break;
                }
            }
            // 如果没有transfer函数则自动添加
            // 同时添加transfer的类型信息
            if (!hasTransferAction) {
                newActions.add(new Action("transfer", "transfer", ""));
                environmentUtil.setActions(newActions);
                SmartContract smartContract = environmentUtil.getSmartContract();
                List<Struct.Field> fields = new ArrayList<>();
                Struct.Field quantity = new Struct.Field("quantity", "asset");
                fields.add(quantity);
                Struct struct = Struct.builder()
                        .name("transfer")
                        .base("")
                        .fields(fields)
                        .build();
                struct.setName("transfer");
                smartContract.getAbi().getStructs().add(struct);
            }
        }
        return true;
    }

    /**
     * 初始化EOS环境
     */
    private boolean initEOSEnvironment() throws IOException, InterruptedException {
        eosUtil.initEOS(smartContract.getName(), fuzzer.getFuzzerInfo().getUseAccountPool());
        return true;
    }

    /**
     * 为fuzzer注入参数
     */
    private boolean injectObjectIntoFuzzer() throws IllegalAccessException {
        fuzzerUtil.wireSmartContract(fuzzer.getObject().getClass(), fuzzer.getObject(), smartContract); // 为fuzzer注入合约
        Logger logger = logUtil.loadLogger(smartContract.getName(), fuzzer.getFuzzerInfo().getVulnerability());
        fuzzerUtil.wireLogger(fuzzer.getObject().getClass(), fuzzer.getObject(), logger);// 为fuzzer注入日志
        return true;
    }

    /**
     * 设置参数生成器
     */
    private boolean setCurrentArgumentGenerator() {
        // 设置参数生成器：由于可能在 fuzzer 的前置操作中调用，因此需要最先设置
        ArgDriver argDriver = fuzzer.getFuzzerInfo().getArgDriver();
        typeUtil.setArgDriver(argDriver); // 设置驱动类型
        environmentUtil.setCurrentArgumentGenerator(typeUtil.getGenerator(smartContract));
        return true;
    }

    /**
     * 设置环境变量
     */
    private boolean setEnvironmentArguments() {
        FuzzerInfo fuzzerInfo = fuzzer.getFuzzerInfo();
        // 记录当前fuzzer是否使用了aflDriver
        environmentUtil.setCurrentActionUsingAFLDriver(fuzzerInfo.getArgDriver() == ArgDriver.afl);
        // 记录当前fuzzer需要fuzzing的次数
        environmentUtil.setCurrentActionTotalFuzzingCount(fuzzerInfo.getIteration());
        return true;
    }

    /**
     * 执行before
     */
    private boolean executeFuzzerBeforeMethod() throws InvocationTargetException, IllegalAccessException {
        Method before = fuzzer.getBefore();
        // 执行前置操作: 如果返回值为 false 则中断当前执行; 如果返回值为 true 或 null 则继续执行
        if (before != null) {
            environmentUtil.setExecutingBeforeMethod(true);
            Object[] params = typeUtil.generateBeforeParams(
                    environmentUtil.getCurrentArgumentGenerator(),
                    before.getParameters());
            Boolean status = (Boolean) before.invoke(fuzzer.getObject(), params);
            environmentUtil.setExecutingBeforeMethod(false);
            return status == null || status;
        }
        return true;
    }
}
