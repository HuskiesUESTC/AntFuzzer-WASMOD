package edu.uestc.antfuzzer.framework.bean;

import lombok.Data;

import java.util.List;

@Data
public class Type {
    private List<String> memoStringPool;

    public Type(List<String> memoStringPool) {
        this.memoStringPool = memoStringPool;
    }
}