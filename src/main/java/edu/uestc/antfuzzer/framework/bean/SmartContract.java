package edu.uestc.antfuzzer.framework.bean;

import edu.uestc.antfuzzer.framework.bean.abi.ABI;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SmartContract {

    private String name;
    private ABI abi;
    private Type type;
    private Map<String, List<String>> result;

    public SmartContract(String name, ABI abi, Type type) {
        this.name = name;
        this.abi = abi;
        this.type = type;
        result = new HashMap<>();
    }
}