package edu.uestc.antfuzzer.framework.util.middleware.smartContract.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzingConfig;
import edu.uestc.antfuzzer.framework.util.ConfigUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

@Component
public class ValidateSmartContract extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private ConfigUtil configUtil;

    private Boolean afterBreakPoint;

    @Override
    protected boolean currentCheck() {
        // 判断当前fuzzer是否处于断点之后
        FuzzingConfig fuzzingConfig = configUtil.getFuzzingConfig();
        SmartContract smartContract = environmentUtil.getSmartContract();
        if (smartContract != null) {
            // 如果有断点
            if (fuzzingConfig.getHasBreakpoint() != null && fuzzingConfig.getHasBreakpoint()) {
                if (smartContract.getName().equals(fuzzingConfig.getStartFrom())) {
                    afterBreakPoint = true;
                }
                return afterBreakPoint;
            }
            return true;
        }
        return false;
    }
}