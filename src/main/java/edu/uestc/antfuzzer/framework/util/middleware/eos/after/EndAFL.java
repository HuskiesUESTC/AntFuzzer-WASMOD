package edu.uestc.antfuzzer.framework.util.middleware.eos.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.AFLUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;

@Component
public class EndAFL extends AfterCheck {

    @Autowired
    private AFLUtil aflUtil;

    @Override
    protected boolean currentCheck() throws InvocationTargetException, IllegalAccessException {
        aflUtil.endAFL();
        return true;
    }
}
