package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.FuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

@Component
public class SetFuzzerFuzzingResult extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() {
        FuzzerFuzzingResult fuzzerFuzzingResult = environmentUtil.getFuzzerFuzzingResult();
        int count = 0;
        int invalidArgumentCount = 0;
        for (ActionFuzzingResult actionFuzzingResult : fuzzerFuzzingResult.getActions()) {
            count += actionFuzzingResult.getCount();
            invalidArgumentCount += actionFuzzingResult.getInvalidArgumentCount();
        }
        // 返回当前合约的代码覆盖率信息
        String smartContract = environmentUtil.getSmartContract().getName();
        fuzzerFuzzingResult.setCoverage(bitMapUtil.getTotalBitMap().getAllBitMapCounts().getOrDefault(smartContract, 0));
        fuzzerFuzzingResult.setJointCoverage(bitMapUtil.getTotalBitMap().getJointBitMapCount());
        fuzzerFuzzingResult.setCount(count);
        fuzzerFuzzingResult.setInvalidArgumentCount(invalidArgumentCount);
        fuzzerFuzzingResult.setTime(System.currentTimeMillis() - fuzzerFuzzingResult.getStartTime());
        return true;
    }
}
