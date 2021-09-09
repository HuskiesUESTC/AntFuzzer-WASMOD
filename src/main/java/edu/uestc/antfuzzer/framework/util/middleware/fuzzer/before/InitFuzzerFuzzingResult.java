package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.FuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.util.HashMap;
import java.util.LinkedList;

@Component
public class InitFuzzerFuzzingResult extends BeforeCheck  {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() {
        String name = environmentUtil.getFuzzer().getFuzzerInfo().getVulnerability();
        environmentUtil.setFuzzerFuzzingResult(new FuzzerFuzzingResult(name));
        return true;
    }
}
