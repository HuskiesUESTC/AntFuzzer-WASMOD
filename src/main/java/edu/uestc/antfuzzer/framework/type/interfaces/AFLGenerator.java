package edu.uestc.antfuzzer.framework.type.interfaces;

public interface AFLGenerator {
    /**
     * 获取用于生成参数的字节数
     */
    int getByteSize();

    /**
     * 获取用于生成参数字节的优先级
     */
    int getPriority();
}
