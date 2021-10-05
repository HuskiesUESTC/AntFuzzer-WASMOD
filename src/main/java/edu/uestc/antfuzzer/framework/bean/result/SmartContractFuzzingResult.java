package edu.uestc.antfuzzer.framework.bean.result;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@Data
public class SmartContractFuzzingResult {
    private String name;
    private long startTime;
    private long time;
    private int count;
    private int invalidArgumentCount;
    private int maxCoverage;
    private int maxJointCoverage;
    private Map<String, Set<String>> smartContractVulnerableActions;
    private LinkedList<FuzzerFuzzingResult> fuzzers;

    public SmartContractFuzzingResult(String name) {
        this.name = name;
        this.startTime = System.currentTimeMillis();
        this.smartContractVulnerableActions = new HashMap<>();
        this.fuzzers = new LinkedList<>();
    }
}
