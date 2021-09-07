package edu.uestc.antfuzzer.framework.util;


import edu.uestc.antfuzzer.framework.annotation.Component;

import java.util.Random;

@Component
public class StringUtil {

    public String createRandomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            string.append(str.charAt(number));
        }
        return string.toString();
    }
}
