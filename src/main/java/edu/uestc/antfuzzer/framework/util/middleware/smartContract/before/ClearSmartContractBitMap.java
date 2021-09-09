package edu.uestc.antfuzzer.framework.util.middleware.smartContract.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;

@Component
public class ClearSmartContractBitMap extends BeforeCheck {

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() throws IOException {
        bitMapUtil.clearBitMap();
        return true;
    }
}
