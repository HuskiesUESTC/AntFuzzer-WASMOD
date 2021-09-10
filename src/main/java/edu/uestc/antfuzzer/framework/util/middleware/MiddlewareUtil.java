package edu.uestc.antfuzzer.framework.util.middleware;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.middleware.action.after.SetActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.action.after.UpdateFuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.action.before.InitActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.action.before.SetLastCoverageChangeTimestamp;
import edu.uestc.antfuzzer.framework.util.middleware.action.before.SwitchAFL;
import edu.uestc.antfuzzer.framework.util.middleware.eos.after.EndAFL;
import edu.uestc.antfuzzer.framework.util.middleware.eos.after.SetEOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.eos.before.InitEOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.after.ExecuteFuzzerAfterMethod;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.after.SetFuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.after.UpdateSmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before.ClearFuzzerFuzzingBitMap;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before.GenerateAFLSeed;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before.InitFuzzerFuzzingEnvironment;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before.InitFuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after.EndCurrentFuzzingIteration;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after.PrintFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after.SendFuzzingResultToAFL;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before.CheckTime;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before.ClearAFLHistoryInfo;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before.ClearLogFile;
import edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before.UpdateActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.smartContract.after.RecordFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.smartContract.after.SetSmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.smartContract.before.ClearSmartContractBitMap;
import edu.uestc.antfuzzer.framework.util.middleware.smartContract.before.InitSmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.middleware.smartContract.before.ValidateSmartContract;

@Component
public class MiddlewareUtil {

    @Autowired
    private InitEOSFuzzingResult initEOSFuzzingResult;

    public Middleware getBeforeEOSFuzzingMiddleware() {
        return initEOSFuzzingResult;
    }

    @Autowired
    private ValidateSmartContract validateSmartContract;
    @Autowired
    private InitSmartContractFuzzingResult initSmartContractFuzzingResult;
    @Autowired
    private ClearSmartContractBitMap clearSmartContractBitMap;

    public Middleware getBeforeSmartContractFuzzingMiddleware() {
        validateSmartContract
                .next(initSmartContractFuzzingResult)
                .next(clearSmartContractBitMap);
        return validateSmartContract;
    }

    @Autowired
    private InitFuzzerFuzzingResult initFuzzerFuzzingResult;
    @Autowired
    private InitFuzzerFuzzingEnvironment initFuzzerFuzzingEnvironment;
    @Autowired
    private ClearFuzzerFuzzingBitMap clearFuzzerFuzzingBitMap;

    public Middleware getBeforeFuzzerFuzzingMiddleware() {
        initFuzzerFuzzingResult
                .next(initFuzzerFuzzingEnvironment)
                .next(clearFuzzerFuzzingBitMap);
        return initFuzzerFuzzingResult;
    }

    @Autowired
    private InitActionFuzzingResult initActionFuzzingResult;
    @Autowired
    private SwitchAFL switchAFL;
    @Autowired
    private SetLastCoverageChangeTimestamp setLastCoverageChangeTimestamp;

    public Middleware getBeforeActionFuzzingMiddleware() {
        initActionFuzzingResult
                .next(switchAFL)
                .next(setLastCoverageChangeTimestamp);
        return initActionFuzzingResult;
    }

    @Autowired
    private UpdateActionFuzzingResult updateActionFuzzingResult;
    @Autowired
    private CheckTime checkTime;
    @Autowired
    private ClearAFLHistoryInfo clearAFLHistoryInfo;
    @Autowired
    private ClearLogFile clearLogFile;

    public Middleware getBeforeFuzzingMiddleware() {
        checkTime
                .next(updateActionFuzzingResult)
                .next(clearAFLHistoryInfo)
                .next(clearLogFile);
        return checkTime;
    }

    public Middleware getAfterFuzzingMiddleware() {
        sendFuzzingResultToAFL
                .prev(endCurrentFuzzingIteration)
                .prev(printFuzzingResult);
        return sendFuzzingResultToAFL;
    }

    @Autowired
    private PrintFuzzingResult printFuzzingResult;
    @Autowired
    private SendFuzzingResultToAFL sendFuzzingResultToAFL;
    @Autowired
    private EndCurrentFuzzingIteration endCurrentFuzzingIteration;

    public Middleware getAfterActionFuzzingMiddleware() {
        updateFuzzerFuzzingResult.prev(setActionFuzzingResult);
        return updateFuzzerFuzzingResult;
    }

    @Autowired
    private SetActionFuzzingResult setActionFuzzingResult;
    @Autowired
    private UpdateFuzzerFuzzingResult updateFuzzerFuzzingResult;

    public Middleware getAfterFuzzerFuzzingMiddleware() {
        updateSmartContractFuzzingResult
                .prev(setFuzzerFuzzingResult)
                .prev(executeFuzzerAfterMethod);
        return updateSmartContractFuzzingResult;
    }

    @Autowired
    private UpdateSmartContractFuzzingResult updateSmartContractFuzzingResult;
    @Autowired
    private SetFuzzerFuzzingResult setFuzzerFuzzingResult;
    @Autowired
    private ExecuteFuzzerAfterMethod executeFuzzerAfterMethod;

    public Middleware getAfterSmartContractFuzzingMiddleware() {
        recordFuzzingResult
                .prev(setSmartContractFuzzingResult);
        return recordFuzzingResult;
    }

    @Autowired
    private SetSmartContractFuzzingResult setSmartContractFuzzingResult;
    @Autowired
    private RecordFuzzingResult recordFuzzingResult;


    public Middleware getAfterEOSFuzzingMiddleware() {
        recordFuzzingResult
                .prev(setEOSFuzzingResult)
                .prev(endAFL);
        return recordFuzzingResult;
    }

    @Autowired
    private SetEOSFuzzingResult setEOSFuzzingResult;
    @Autowired
    private EndAFL endAFL;

    @Autowired
    private GenerateAFLSeed generateAFLSeed;

    public Middleware getGenerateAFLSeedMiddleware() {
        return generateAFLSeed;
    }
}
