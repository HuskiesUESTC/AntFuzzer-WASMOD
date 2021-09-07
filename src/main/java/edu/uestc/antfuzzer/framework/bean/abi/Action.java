package edu.uestc.antfuzzer.framework.bean.abi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Action {
    String name;
    String type;
    @SerializedName("ricardian_contract")
    String ricardianContract;
}