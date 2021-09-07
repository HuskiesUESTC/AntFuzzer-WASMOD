package edu.uestc.antfuzzer.framework.util.middleware.action.before;


import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

@Component
public class InitActionFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        ActionFuzzingResult currentActionFuzzingResult = environmentUtil.getActionFuzzingResult();
        Action action = environmentUtil.getAction();
        String smartContract = environmentUtil.getSmartContract().getName();
        if (currentActionFuzzingResult == null || !currentActionFuzzingResult.getName().equals(action.getName()) || !smartContract.equals(currentActionFuzzingResult.getSmartContract())) {
            ActionFuzzingResult actionFuzzingResult = new ActionFuzzingResult();
            actionFuzzingResult.setName(environmentUtil.getAction().getName());
            actionFuzzingResult.setStartTime(System.currentTimeMillis());
            actionFuzzingResult.setVulnerability(new HashSet<>());
            actionFuzzingResult.setSmartContract(smartContract);
            currentActionFuzzingResult = actionFuzzingResult;
            environmentUtil.setActionFuzzingResult(actionFuzzingResult);
        }
        if (environmentUtil.getFuzzer().getFuzzerInfo().getArgDriver() != ArgDriver.afl) {
            currentActionFuzzingResult.setCount(0);
        }
        return true;
    }
}
