package edu.uestc.antfuzzer.framework.type.interfaces;


import edu.uestc.antfuzzer.framework.exception.AFLException;

import java.io.IOException;

public interface TypeGenerator {
    /**
     * 生成测试字符串
     */
    String generate() throws IOException, InterruptedException, AFLException;
}
