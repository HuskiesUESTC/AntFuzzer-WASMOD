package edu.uestc.antfuzzer.framework.util.middleware.action.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.FuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class InitActionFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        ActionFuzzingResult currentActionFuzzingResult = environmentUtil.getActionFuzzingResult();
        Action action = environmentUtil.getAction();
        String smartContract = environmentUtil.getSmartContract().getName();
        FuzzerFuzzingResult fuzzerFuzzingResult = environmentUtil.getFuzzerFuzzingResult();
        // 如果当前 actionFuzzingResult 与 合约名不符
        // 如果 actionFuzzingResult 为空，或者 与当前函数名不同
        if (currentActionFuzzingResult == null || !currentActionFuzzingResult.getName().equals(action.getName()) || !currentActionFuzzingResult.getSmartContract().equals(smartContract)) {
            // 若 fuzzerFuzzingResult 中有相应的函数
            if (fuzzerFuzzingResult.getActions() != null) {
                for (ActionFuzzingResult actionFuzzingResult : fuzzerFuzzingResult.getActions()) {
                    if (action.getName().equals(actionFuzzingResult.getName())) {
                        actionFuzzingResult.setStartTime(System.currentTimeMillis());
                        environmentUtil.setActionFuzzingResult(actionFuzzingResult);
                        return true;
                    }
                }
            }
            // 如果仍然为空
            currentActionFuzzingResult = new ActionFuzzingResult(smartContract, action.getName());
            boolean isUsingAFL = environmentUtil.isCurrentActionUsingAFLDriver();
            // 默认为-1
            currentActionFuzzingResult.setCount(isUsingAFL ? -1 : 0);
            environmentUtil.setActionFuzzingResult(currentActionFuzzingResult);
        }
        // 检查是否需要对比
        FuzzerFuzzingResult compareFuzzerFuzzingResult = environmentUtil.getCompareFuzzerFuzzingResult();
        String actionName = action.getName();
        if (environmentUtil.isCompare() && compareFuzzerFuzzingResult != null) {
            for (ActionFuzzingResult actionFuzzingResult : compareFuzzerFuzzingResult.getActions()) {
                if (actionName.equals(actionFuzzingResult.getName())) {
                    environmentUtil.setCompareActionFuzzingResult(actionFuzzingResult);
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
