package edu.uestc.antfuzzer.framework.util.middleware.eos.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.util.HashMap;
import java.util.LinkedList;

@Component
public class InitEOSFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() {
        EOSFuzzingResult eosFuzzingResult = new EOSFuzzingResult();
        eosFuzzingResult.setSmartContracts(new LinkedList<>());
        eosFuzzingResult.setStartTime(System.currentTimeMillis());
        eosFuzzingResult.setVulnerableSmartContracts(new HashMap<>());
        environmentUtil.setEosFuzzingResult(eosFuzzingResult);
        return true;
    }
}
