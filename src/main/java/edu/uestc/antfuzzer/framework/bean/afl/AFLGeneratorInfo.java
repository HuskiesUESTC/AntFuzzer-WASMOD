package edu.uestc.antfuzzer.framework.bean.afl;

import lombok.Data;

@Data
public class AFLGeneratorInfo {
    private Class<?> clazz;
    private int priority;
    private int byteSize;
    private int start;
    private int end;
    private String exampleStr;
    private int index;

    /**
     * 生成参数类型信息
     *
     * @param clazz      参数生成器的类型信息
     * @param priority   优先级
     * @param byteSize   限定字符长度
     * @param exampleStr 默认字符串
     * @param index      参数位置
     */
    public AFLGeneratorInfo(Class<?> clazz, int priority, int byteSize, String exampleStr, int index) {
        this.clazz = clazz;
        this.priority = priority;
        this.byteSize = byteSize;
        this.exampleStr = exampleStr;
        this.index = index;
    }
}
