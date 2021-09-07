package edu.uestc.antfuzzer.framework.util.middleware;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class BeforeCheck implements Middleware {

    protected BeforeCheck next;

    public BeforeCheck next(BeforeCheck next) {
        this.next = next;
        return next;
    }

    public boolean check() throws InterruptedException, InvocationTargetException, IllegalAccessException, IOException {
        if (!currentCheck())
            return false;
        return next == null || next.check();
    }

    protected abstract boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException;

}
