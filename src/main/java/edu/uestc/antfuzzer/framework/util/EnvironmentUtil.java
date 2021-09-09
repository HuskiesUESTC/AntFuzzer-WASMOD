package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfoCollection;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.FuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.SmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.type.ArgumentGenerator;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
@Data
public class EnvironmentUtil {

    // 判断当前是否正在执行 before 函数
    private boolean executingBeforeMethod;

    // 所有action的参数生成器列表
    LinkedList<AFLGeneratorInfoCollection> aflGeneratorInfoCollectionList;

    // 当前action的参数生成器列表
    private AFLGeneratorInfoCollection currentAFLGeneratorInfoCollection;

    // 下一个action的参数生成器列表
    private AFLGeneratorInfoCollection nextAFLGeneratorInfoCollection;

    private Set<Handler> fuzzers;

    // 函数
    private ActionFuzzingResult actionFuzzingResult;
    // bitmap
    private BitMapUtil.BitMap bitmap;

    // 当前合约函数测试的结果
    private FuzzingStatus fuzzingStatus;

    // 当前smartContract
    private SmartContract smartContract;

    // 当前actions
    private List<Action> actions;

    // 当前fuzzer
    private Handler fuzzer;

    // 当前action
    private Action action;

    // EOSFuzzingResult
    private EOSFuzzingResult eosFuzzingResult;

    // smartContractFuzzingResult
    private SmartContractFuzzingResult smartContractFuzzingResult;

    // fuzzerFuzzingResult
    private FuzzerFuzzingResult fuzzerFuzzingResult;

    // 当前合约的参数生成器
    private ArgumentGenerator currentArgumentGenerator;

    // 记录当前fuzzer是否使用了aflDriver
    private boolean isCurrentActionUsingAFLDriver;

    // 记录当前fuzzer需要fuzzing的次数
    private int currentActionTotalFuzzingCount;

    // 记录上一次代码覆盖率
    private int lastCoverage;

    // 记录上一次代码覆盖率变化时候的值
    private int lastCoverageChangeTime;

    // 记录上一次代码覆盖率变化时候的时间戳
    private long lastCoverageChangeTimestamp;

    // 记录已经测试过的合约数量
    private int number;


}
