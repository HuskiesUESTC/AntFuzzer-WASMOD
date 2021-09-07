package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;

@Component
public class PrintFuzzingResult extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() throws IllegalAccessException, InvocationTargetException {
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        System.out.println("SmartContract: " + environmentUtil.getSmartContract().getName() +
                "; Action: " + actionFuzzingResult.getName() +
                "; Count: " + actionFuzzingResult.getCount() +
                "; Coverage: " + bitMapUtil.getCoverage());
        System.out.println();
        return true;
    }
}
