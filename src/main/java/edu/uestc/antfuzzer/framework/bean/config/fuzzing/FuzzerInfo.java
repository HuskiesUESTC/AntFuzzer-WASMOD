package edu.uestc.antfuzzer.framework.bean.config.fuzzing;

import com.google.gson.annotations.SerializedName;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FuzzerInfo {
    private String vulnerability;

    private Integer iteration;

    @SerializedName("fuzz_scope")
    private FuzzScope fuzzScope;

    @SerializedName("use_account_pool")
    private Boolean useAccountPool;

    @SerializedName("arg_driver")
    private ArgDriver argDriver;

    @SerializedName("limit_iteration")
    private Integer limitIteration;

    public FuzzerInfo(String vulnerability, int iteration, FuzzScope fuzzScope, boolean useAccountPool, ArgDriver argDriver) {
        this.vulnerability = vulnerability;
        this.iteration = iteration;
        this.fuzzScope = fuzzScope;
        this.useAccountPool = useAccountPool;
        this.argDriver = argDriver;
    }
}
