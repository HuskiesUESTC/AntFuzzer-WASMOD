package edu.uestc.antfuzzer.framework.util.middleware.action.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;

@Component

public class SetActionFuzzingResult extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IllegalAccessException, InvocationTargetException {
        // 将当前的结果放入fuzzerResult中
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        actionFuzzingResult.setTime(System.currentTimeMillis() - actionFuzzingResult.getStartTime());
        return true;
    }
}
