package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.FileUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.lang.reflect.InvocationTargetException;

@Component
public class ClearLogFile extends BeforeCheck {

    @Autowired
    private FileUtil fileUtil;

    @Override
    protected boolean currentCheck() throws InvocationTargetException, IllegalAccessException {
        fileUtil.rmLogFiles();
        return true;
    }
}
