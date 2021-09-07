package edu.uestc.antfuzzer.framework.type.number.decimal;

import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.type.number.NumberGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;

import java.util.Arrays;
import java.util.Random;

public class Float32Generator extends NumberGenerator {

    public Float32Generator(Random random, ArgDriver argDriver, AFLUtil aflUtil, float rate) {
        super(random, argDriver, aflUtil, 4, rate);
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
        int value = 0;
        for (int i = 0; i < len; i++)
            value |= (bytes[i] & 0xff) << (8 * i);
        float num = Float.intBitsToFloat(value);
        return String.valueOf(num);
    }

    @Override
    public String generateNumberFromLocal() {
        float[] result = {Float.MAX_EXPONENT, Float.MAX_VALUE, Float.MIN_EXPONENT, Float.MIN_NORMAL, Float.MIN_VALUE};
        return String.valueOf(result[random.nextInt(5)]);
    }
}
