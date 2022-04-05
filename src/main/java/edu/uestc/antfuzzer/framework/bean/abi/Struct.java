package edu.uestc.antfuzzer.framework.bean.abi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Struct {
    String name;
    String base;
    List<Field> fields;

    @Data
    public static class Field {
        String name;
        String type;

        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}