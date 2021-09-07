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
        FuzzerFuzzingResult fuzzerFuzzingResult = new FuzzerFuzzingResult();
        fuzzerFuzzingResult.setName(environmentUtil.getFuzzer().getFuzzerInfo().getVulnerability());
        fuzzerFuzzingResult.setActions(new LinkedList<>());
        fuzzerFuzzingResult.setStartTime(System.currentTimeMillis());
        fuzzerFuzzingResult.setFuzzerVulnerableActions(new HashMap<>());
        environmentUtil.setFuzzerFuzzingResult(fuzzerFuzzingResult);
        return true;
    }
}
