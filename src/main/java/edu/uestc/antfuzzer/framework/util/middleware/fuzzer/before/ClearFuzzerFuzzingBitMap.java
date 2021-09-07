package edu.uestc.antfuzzer.framework.util.middleware.fuzzer.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class ClearFuzzerFuzzingBitMap extends BeforeCheck {

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        bitMapUtil.clearBitMap();
        return true;
    }
}
