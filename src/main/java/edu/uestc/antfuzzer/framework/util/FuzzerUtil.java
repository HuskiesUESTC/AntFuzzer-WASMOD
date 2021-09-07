package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.annotation.Contract;
import edu.uestc.antfuzzer.framework.annotation.Log;
import edu.uestc.antfuzzer.framework.bean.SmartContract;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class FuzzerUtil {

    public void wireSmartContract(Class<?> clazz, Object object, SmartContract smartContract) throws IllegalAccessException {
        Set<Field> fieldSet = new HashSet<>();
        fieldSet.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fieldSet.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        // 注入工具类
        for (Field field : fieldSet) {
            if (field.getAnnotation(Contract.class) != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(object, smartContract);
                break;
            }
        }
    }

    public void wireLogger(Class<?> clazz, Object object, Logger logger) throws IllegalAccessException {
        Set<Field> fieldSet = new HashSet<>();
        fieldSet.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fieldSet.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        // 注入工具类
        for (Field field : fieldSet) {
            if (field.getAnnotation(Log.class) != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(object, logger);
                break;
            }
        }
    }
}
