package edu.uestc.antfuzzer.framework.bean.abi;

import lombok.Data;

import java.util.List;

@Data
public class Struct {
    String name;
    String base;
    List<Field> fields;

    @Data
    public class Field {
        String name;
        String type;
    }
}