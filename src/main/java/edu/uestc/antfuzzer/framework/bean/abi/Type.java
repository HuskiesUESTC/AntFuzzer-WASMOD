package edu.uestc.antfuzzer.framework.bean.abi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Type {
    @SerializedName("new_type_name")
    String newTypeName;
    String type;
}