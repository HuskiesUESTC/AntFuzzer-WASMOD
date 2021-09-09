package edu.uestc.antfuzzer.framework.bean.result;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@Data
public class EOSFuzzingResult {
    @SerializedName("start_time")
    private long startTime;
    private long time;
    private int count;
    @SerializedName("invalid_count")
    private int invalidCount;
    @SerializedName("vulnerable_smart_contracts")
    private Map<String, Set<String>> vulnerableSmartContracts;
    @SerializedName("smart_contracts")
    private LinkedList<SmartContractFuzzingResult> smartContracts;
    private int number;

    public EOSFuzzingResult() {
        startTime = System.currentTimeMillis();
        vulnerableSmartContracts = new HashMap<>();
        smartContracts = new LinkedList<>();
    }
}
