package edu.uestc.antfuzzer.framework.bean.abi;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Action {
    String name;
    String type;
    @SerializedName("ricardian_contract")
    String ricardianContract;
}