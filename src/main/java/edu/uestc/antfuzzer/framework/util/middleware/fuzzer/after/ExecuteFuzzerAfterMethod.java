package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class ExecuteFuzzerAfterMethod extends AfterCheck  {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws InvocationTargetException, IllegalAccessException {
        Handler fuzzer = environmentUtil.getFuzzer();
        Method after = fuzzer.getAfter();
        // 执行后置操作
        if (after != null)
            after.invoke(fuzzer.getObject());
        return true;
    }
}
