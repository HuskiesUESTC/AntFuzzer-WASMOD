package edu.uestc.antfuzzer.framework.bean.result;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@Data
public class FuzzerFuzzingResult {
    private String name;
    private long startTime;
    private long time;
    private int count;
    private int invalidArgumentCount;
    private int coverage;
    private int jointCoverage;
    private Map<String, Set<String>> fuzzerVulnerableActions;
    private LinkedList<ActionFuzzingResult> actions;

    public FuzzerFuzzingResult(String name) {
        this.name = name;
        this.startTime = System.currentTimeMillis();
        this.fuzzerVulnerableActions = new HashMap<>();
        this.actions = new LinkedList<>();
    }
}
