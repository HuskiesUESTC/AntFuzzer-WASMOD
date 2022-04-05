package edu.uestc.antfuzzer.framework.type.number;


import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.interfaces.AFLGenerator;
import edu.uestc.antfuzzer.framework.type.interfaces.TypeGenerator;
import edu.uestc.antfuzzer.framework.util.AFLUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public abstract class NumberGenerator implements TypeGenerator, AFLGenerator {
    protected Random random;
    protected ArgDriver argDriver;
    protected String data;
    protected AFLUtil aflUtil;
    // 本地生成时随机生成边界值的概率
    protected float rate;
    protected int byteSize;

    public NumberGenerator(Random random, ArgDriver argDriver, AFLUtil aflUtil, int byteSize, float rate) {
        this.random = random;
        this.argDriver = argDriver;
        this.aflUtil = aflUtil;
        this.byteSize = byteSize;
        this.rate = rate;
    }
//
//    protected String convertRandomStrToBinaryStr(String str) {
//        return convertByteArrToBinaryStr(str.getBytes(StandardCharsets.UTF_8));
//    }
//    protected String convertByteArrToBinaryStr(byte[] arr) {
//        return new BigInteger(1, arr).toString(2);
//    }
//
//    protected String convertByteArrToBinaryStr(byte[] arr, int size) {
//        String data = convertByteArrToBinaryStr(arr);
//        if (data.length() > size * 8)
//            data = data.substring(0, size * 8);
//        return data;
//    }

    protected String createRandomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random(System.nanoTime());
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            string.append(str.charAt(number));
        }
        return string.toString();
    }

    protected String generateFromLocal() throws IOException, AFLException {
        String randomStr = generateRandomStrFromLocal();
        return covertBytesToNumber(randomStr);
    }

    public String generateFromAFL() throws AFLException {
        String exampleStr = generateRandomStrFromLocal();
        String numberStr = covertBytesToNumber(exampleStr);
        byte[] bytes = aflUtil.getByteArrayOfArgument(this, numberStr, exampleStr);
        String result = covertBytesToNumber(bytes);
        System.out.println("generate: " + result);
        return result;
    }

    @Override
    public int getByteSize() {
        return byteSize;
    }

    @Override
    public int getPriority() {
        return byteSize;
    }

    @Override
    public String generate() throws IOException, AFLException, InterruptedException {
        return (argDriver == ArgDriver.afl) ? this.generateFromAFL() : generateNumberFromLocal();
    }

    /**
     * 将二进制转化为特定类型的数字
     */
    public String covertBytesToNumber(String binaryStr) {
        if (binaryStr == null || binaryStr.equalsIgnoreCase(""))
            return null;
        byte[] bytes = binaryStr.getBytes(StandardCharsets.UTF_8);
        return covertBytesToNumber(bytes);
    }

    public abstract String covertBytesToNumber(byte[] bytes);

    /**
     * 从本地生成字符串
     */
    public abstract String generateNumberFromLocal();

    /**
     * 从本地生成随机字符串
     */
    public String generateRandomStrFromLocal() {
        return createRandomStr(byteSize);
    }
}
