package edu.uestc.antfuzzer.framework.util.middleware;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface Middleware {
    boolean check() throws InterruptedException, InvocationTargetException, IllegalAccessException, IOException;
}
