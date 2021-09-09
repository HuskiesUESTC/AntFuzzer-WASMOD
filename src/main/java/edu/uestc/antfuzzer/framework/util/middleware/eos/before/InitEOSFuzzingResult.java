package edu.uestc.antfuzzer.framework.util.middleware.eos.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;

@Component
public class InitEOSFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IOException {
        environmentUtil.setEosFuzzingResult(new EOSFuzzingResult());
        return true;
    }
}
