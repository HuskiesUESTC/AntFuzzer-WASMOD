package edu.uestc.antfuzzer.framework.util.middleware.eos.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.PipeUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

@Component
public class InitEOSFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;
    @Autowired
    private PipeUtil pipeUtil;

    @Override
    protected boolean currentCheck() throws IOException {
        EOSFuzzingResult eosFuzzingResult = new EOSFuzzingResult();
        eosFuzzingResult.setSmartContracts(new LinkedList<>());
        eosFuzzingResult.setStartTime(System.currentTimeMillis());
        eosFuzzingResult.setVulnerableSmartContracts(new HashMap<>());
        environmentUtil.setEosFuzzingResult(eosFuzzingResult);
        pipeUtil.execute("kill -9 `ps -ef |grep redis|awk '{print $2}'`");
        return true;
    }
}
