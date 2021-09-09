package edu.uestc.antfuzzer.framework.util.middleware.smartContract.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.SmartContractFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

@Component
public class InitSmartContractFuzzingResult extends BeforeCheck {

   @Autowired
   private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() {
        String name = environmentUtil.getSmartContract().getName();
        environmentUtil.setSmartContractFuzzingResult(new SmartContractFuzzingResult(name));
        return true;
    }
}
