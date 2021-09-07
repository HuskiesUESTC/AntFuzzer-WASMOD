package edu.uestc.antfuzzer.framework.core;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.Handler;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzerInfo;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BeanFactory {
    private ClassLoader classLoader;
    @Getter
    private Set<Handler> fuzzers;
    @Getter
    private Map<Class<?>, Object> components;

    public BeanFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
        fuzzers = new HashSet<>();
        components = new HashMap<>();
        initComponents();
        initFuzzersAndHandlers();
    }

    public <T> T getComponent(Class<T> clazz) {
        return (T) components.get(clazz);
    }

    private void initComponents() {
        Set<Class<?>> componentSet = classLoader.getComponentSet();
        try {
            for (Class<?> clazz : componentSet) {
                Object component = clazz.newInstance();
                components.put(clazz, component);
            }
            // 注入component
            for (Class<?> clazz : componentSet) {
                Object component = components.get(clazz);
                autoWireFields(clazz, component);
                components.put(clazz, component);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initFuzzersAndHandlers() {
        Set<Class<?>> fuzzerSet = classLoader.getFuzzerSet();
        for (Class<?> clazz : fuzzerSet) {
            try {
                Object fuzzer = clazz.newInstance();
                autoWireFields(clazz, fuzzer);
                Method[] methods = clazz.getDeclaredMethods();
                Handler handler = new Handler(fuzzer);
                for (Method method : methods) {
                    if (method.getAnnotation(Before.class) != null)
                        handler.setBefore(method);
                    else if (method.getAnnotation(Fuzz.class) != null)
                        handler.setFuzz(method);
                    else if (method.getAnnotation(After.class) != null)
                        handler.setAfter(method);
                }
                if (handler.getFuzz() != null) {
                    Fuzzer annotation = clazz.getAnnotation(Fuzzer.class);
//                    FuzzerInfo fuzzerInfo = new FuzzerInfo(annotation.vulnerability(), annotation.iteration(), annotation.fuzzScope(), annotation.useAccountPool(), annotation.argDriver());
                    FuzzerInfo fuzzerInfo = new FuzzerInfo(annotation.vulnerability(), annotation.iteration(), annotation.fuzzScope(), annotation.useAccountPool(), annotation.argDriver());
                    handler.setFuzzerInfo(fuzzerInfo);
                    fuzzers.add(handler);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void autoWireFields(Class<?> clazz, Object object) throws IllegalAccessException {
        Set<Field> fieldSet = new HashSet<>();
        fieldSet.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fieldSet.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

        // 注入工具类
        for (Field field : fieldSet) {
            if (field.getAnnotation(Autowired.class) != null) {
                Class<?> fieldClazz = field.getType();
                Object fieldValue = components.get(fieldClazz);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(object, fieldValue);
            }
        }
    }

}