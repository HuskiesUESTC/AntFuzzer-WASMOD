package edu.uestc.antfuzzer.framework.util.middleware.action.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.bean.result.FuzzerFuzzingResult;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class UpdateFuzzerFuzzingResult extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IllegalAccessException, InvocationTargetException {
        FuzzerFuzzingResult fuzzerFuzzingResult = environmentUtil.getFuzzerFuzzingResult();
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        fuzzerFuzzingResult.getActions().add(actionFuzzingResult);

        Map<String, Set<String>> fuzzerVulnerableActions = fuzzerFuzzingResult.getFuzzerVulnerableActions();
        Set<String> vulnerabilities = actionFuzzingResult.getVulnerability();
        if (vulnerabilities.size() > 0) {
            String action = actionFuzzingResult.getName();
            for (String vulnerability : vulnerabilities) {
                Set<String> actions = fuzzerVulnerableActions.computeIfAbsent(vulnerability, k -> new HashSet<>());
                actions.add(action);
            }
        }
        return true;
    }
}
