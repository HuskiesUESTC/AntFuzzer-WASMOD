package edu.uestc.antfuzzer.framework.type.number.integer;

import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.type.number.NumberGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class IntGenerator extends NumberGenerator {

    public IntGenerator(Random random, ArgDriver argDriver, AFLUtil aflUtil, int byteSize, float rate) {
        super(random, argDriver, aflUtil, byteSize, rate);
    }

    @Override
    public String covertBytesToNumber(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return "0.f";
        int len = bytes.length;
        if (len >= byteSize)
            bytes = Arrays.copyOf(bytes, byteSize);
        BigInteger result = new BigInteger(bytes);
        return result.toString();
    }

    @Override
    public String generateNumberFromLocal() {
        BigInteger bound = BigInteger.valueOf(2).pow(byteSize * 8 - 1).subtract(BigInteger.ONE);
        BigInteger[] result = {bound, BigInteger.ZERO, bound.negate()};
        BigInteger other = new BigInteger(byteSize * 8 - 1, random);
        if (random.nextBoolean())
            other = other.negate();
        BigInteger num = random.nextFloat() < rate ? result[random.nextInt(3)] : other;
        return num.toString();
    }
}
