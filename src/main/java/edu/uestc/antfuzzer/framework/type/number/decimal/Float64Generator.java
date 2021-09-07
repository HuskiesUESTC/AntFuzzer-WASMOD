package edu.uestc.antfuzzer.framework.type.number.decimal;


import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.type.number.NumberGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;

import java.util.Arrays;
import java.util.Random;

public class Float64Generator extends NumberGenerator {

    public Float64Generator(Random random, ArgDriver argDriver, AFLUtil aflUtil, float rate) {
        super(random, argDriver, aflUtil, 8, rate);
    }

    @Override
    public String covertBytesToNumber(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return "0.f";
        int len = bytes.length;
        if (len >= byteSize * 8) {
            bytes = Arrays.copyOf(bytes, byteSize * 8);
            len = byteSize;
        }
        long value = 0;
        for (int i = 0; i < len; i++)
            value |= (long) (bytes[i] & 0xff) << (8 * i);
        double num = Double.longBitsToDouble(value);
        return String.valueOf(num);
    }

    @Override
    public String generateNumberFromLocal() {
        double[] result = {Double.MAX_EXPONENT, Double.MAX_VALUE, Double.MIN_EXPONENT, Double.MIN_NORMAL, Double.MIN_VALUE};
        return String.valueOf(result[random.nextInt(5)]);
    }
}
