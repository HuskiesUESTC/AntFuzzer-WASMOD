package edu.uestc.antfuzzer.framework.util.middleware;

import java.lang.reflect.InvocationTargetException;

public abstract class AfterCheck implements Middleware {

    protected AfterCheck prev;

    public AfterCheck prev(AfterCheck prev) {
        this.prev = prev;
        return prev;
    }

    public boolean check() throws InvocationTargetException, IllegalAccessException {
        if (prev != null && !prev.check())
            return false;
        return currentCheck();
    }

    protected abstract boolean currentCheck() throws InvocationTargetException, IllegalAccessException;
}
