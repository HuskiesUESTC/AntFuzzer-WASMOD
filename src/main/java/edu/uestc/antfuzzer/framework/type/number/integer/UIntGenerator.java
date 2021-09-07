package edu.uestc.antfuzzer.framework.type.number.integer;

import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.type.number.NumberGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class UIntGenerator extends NumberGenerator {
    public UIntGenerator(Random random, ArgDriver argDriver, AFLUtil aflUtil, int byteSize, float rate) {
        super(random, argDriver, aflUtil, byteSize, rate);
    }

    @Override
    public String covertBytesToNumber(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return "0";
        int len = bytes.length;
        if (len >= byteSize)
            bytes = Arrays.copyOf(bytes, byteSize);
        if (bytes[0] < 0) {
            byte[] extended = new byte[bytes.length + 1];
            extended[0] = 0;
            System.arraycopy(bytes, 0, extended, 1, bytes.length);
            bytes = extended;
        }
        BigInteger number = new BigInteger(bytes);
        return number.toString();
    }

    @Override
    public String generateNumberFromLocal() {
        BigInteger[] result = {BigInteger.ZERO, BigInteger.valueOf(2).pow(byteSize * 8).subtract(BigInteger.ONE)};
        BigInteger num = random.nextFloat() < rate ? result[random.nextInt(2)] : new BigInteger(byteSize * 8, random);
        return num.toString();
    }
}
