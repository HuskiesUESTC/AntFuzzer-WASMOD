package edu.uestc.antfuzzer.framework.annotation;

import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Fuzzer {
    String vulnerability();

    int iteration() default 100;

    FuzzScope fuzzScope() default FuzzScope.all;

    String unKnownActionName() default "unKnown";

    boolean useAccountPool() default true;

    ArgDriver argDriver() default ArgDriver.local;

    String compareTo() default "";
}
