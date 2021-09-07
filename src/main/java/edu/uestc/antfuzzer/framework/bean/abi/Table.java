package edu.uestc.antfuzzer.framework.bean.abi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Table {
    String name;
    String type;
    @SerializedName("index_type")
    String indexType;
    @SerializedName("key_names")
    List<String> keyNames;
    @SerializedName("key_types")
    List<String> keyTypes;
}