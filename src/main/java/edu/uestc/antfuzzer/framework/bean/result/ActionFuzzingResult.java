package edu.uestc.antfuzzer.framework.bean.result;

import lombok.Data;

import java.util.Set;

@Data
public class ActionFuzzingResult {
    private String smartContract;
    private String name;
    private long startTime;
    private long time;
    private int count;
    private int invalidArgumentCount;
    private Set<String> vulnerability;
}
