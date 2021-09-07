package edu.uestc.antfuzzer.framework.util.middleware.action.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.afl.AFLGeneratorInfoCollection;
import edu.uestc.antfuzzer.framework.util.AFLUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

@Component
public class SwitchAFL extends BeforeCheck {

    @Autowired
    private AFLUtil aflUtil;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        if (environmentUtil.isCurrentActionUsingAFLDriver()) {
            // 如果当前是在正式测试阶段
            if (environmentUtil.getActionFuzzingResult().getCount() > -1) {
                LinkedList<AFLGeneratorInfoCollection> currentAFLGeneratorInfoCollection = environmentUtil.getAflGeneratorInfoCollectionList();
                environmentUtil.setCurrentAFLGeneratorInfoCollection(currentAFLGeneratorInfoCollection.poll());
                environmentUtil.setNextAFLGeneratorInfoCollection(currentAFLGeneratorInfoCollection.peek());
                aflUtil.switchAFL();
            }
        }
        aflUtil.setDefaultUsingAFLDriver();
        return true;
    }
}
