package edu.uestc.antfuzzer.framework.util.middleware.eos.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.SmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

@Component
public class SetEOSFuzzingResult extends AfterCheck {


    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() {
        EOSFuzzingResult eosFuzzingResult = environmentUtil.getEosFuzzingResult();
        int count = 0;
        int invalidArgumentCount = 0;
        for (SmartContractFuzzingResult smartContractFuzzingResult : eosFuzzingResult.getSmartContracts()) {
            count += smartContractFuzzingResult.getCount();
            invalidArgumentCount += smartContractFuzzingResult.getInvalidArgumentCount();
        }
        eosFuzzingResult.setCount(count);
        eosFuzzingResult.setInvalidCount(invalidArgumentCount);
        eosFuzzingResult.setTime(System.currentTimeMillis());
        return true;
    }
}
