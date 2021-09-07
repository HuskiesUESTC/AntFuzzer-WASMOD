package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;

@Component
public class EndCurrentFuzzingIteration extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IllegalAccessException, InvocationTargetException {
        return environmentUtil.getFuzzingStatus() == FuzzingStatus.NEXT;
    }
}
