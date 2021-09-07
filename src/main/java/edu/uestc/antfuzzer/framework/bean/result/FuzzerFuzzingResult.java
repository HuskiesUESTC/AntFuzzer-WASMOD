package edu.uestc.antfuzzer.framework.bean.result;

import lombok.Data;

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
    private Map<String, Set<String>> fuzzerVulnerableActions;
    private LinkedList<ActionFuzzingResult> actions;
}
