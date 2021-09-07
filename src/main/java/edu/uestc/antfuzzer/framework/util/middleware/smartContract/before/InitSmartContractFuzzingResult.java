package edu.uestc.antfuzzer.framework.util.middleware.smartContract.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.SmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.util.HashMap;
import java.util.LinkedList;

@Component
public class InitSmartContractFuzzingResult extends BeforeCheck {

   @Autowired
   private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() {
        SmartContractFuzzingResult smartContractFuzzingResult = new SmartContractFuzzingResult();
        smartContractFuzzingResult.setName(environmentUtil.getSmartContract().getName());
        smartContractFuzzingResult.setFuzzers(new LinkedList<>());
        smartContractFuzzingResult.setStartTime(System.currentTimeMillis());
        smartContractFuzzingResult.setSmartContractVulnerableActions(new HashMap<>());
        environmentUtil.setSmartContractFuzzingResult(smartContractFuzzingResult);
        return true;
    }
}
