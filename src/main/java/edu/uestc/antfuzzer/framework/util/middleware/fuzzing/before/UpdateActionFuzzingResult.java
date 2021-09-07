package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class UpdateActionFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        int count = actionFuzzingResult.getCount();
        actionFuzzingResult.setCount(count + 1);
        return true;
    }
}
