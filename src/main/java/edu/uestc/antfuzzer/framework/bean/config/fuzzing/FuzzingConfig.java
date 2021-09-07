package edu.uestc.antfuzzer.framework.bean.config.fuzzing;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
public class FuzzingConfig {
    private Set<FuzzerInfo> fuzzers;

    @SerializedName("output_file")
    private String outputFile;

    @SerializedName("smart_contract_dir")
    private String smartContractDir;


    @SerializedName("start_from")
    private String startFrom;

    @SerializedName("has_breakpoint")
    @Getter
    private Boolean hasBreakpoint;
}