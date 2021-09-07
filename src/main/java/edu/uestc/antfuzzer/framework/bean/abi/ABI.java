package edu.uestc.antfuzzer.framework.bean.abi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ABI {

    @SerializedName("____comment")
    private String comment;

    private String version;

    private List<Type> types;

    private List<Struct> structs;

    private List<Action> actions;

    private List<Table> tables;

    @SerializedName("ricardian_clauses")
    private List<String> ricardianClauses;
    private List<String> variants;
}