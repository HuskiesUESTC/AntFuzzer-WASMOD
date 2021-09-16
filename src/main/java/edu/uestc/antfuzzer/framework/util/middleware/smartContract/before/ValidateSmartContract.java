package edu.uestc.antfuzzer.framework.util.middleware.smartContract.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.ConfigUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

@Component
public class ValidateSmartContract extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private ConfigUtil configUtil;

    private boolean afterBreakPoint;

    @Override
    protected boolean currentCheck() {
//        // 判断当前fuzzer是否处于断点之后
        return environmentUtil.getSmartContract() != null;
    }
}