package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.AFLUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class ClearAFLHistoryInfo extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private AFLUtil aflUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        if (environmentUtil.isCurrentActionUsingAFLDriver())
            aflUtil.clearArguments();
        return true;
    }
}
