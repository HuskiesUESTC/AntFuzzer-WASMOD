package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class CheckTime extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException {
        // 如果代码覆盖率发生了变化
        int coverage = bitMapUtil.getCoverage();
        if (bitMapUtil.getCoverage() > environmentUtil.getLastCoverage()) {
            environmentUtil.setLastCoverage(coverage);
            environmentUtil.setLastCoverageChangeTime(environmentUtil.getActionFuzzingResult().getCount());
            environmentUtil.setLastCoverageChangeTimestamp(System.currentTimeMillis());
        }
        // 如果需要进行对比
        ActionFuzzingResult compareActionFuzzingResult = environmentUtil.getCompareActionFuzzingResult();
        if (environmentUtil.isCompare() && compareActionFuzzingResult != null) {
            long totalTime = System.currentTimeMillis() - environmentUtil.getActionFuzzingResult().getStartTime();
            return totalTime < compareActionFuzzingResult.getTime();
        }
        // 正常情况下，对比覆盖率保持不变的时间以及迭代轮次
        long timeOffset = System.currentTimeMillis() - environmentUtil.getLastCoverageChangeTimestamp();
        return environmentUtil.getActionFuzzingResult().getCount() - environmentUtil.getLastCoverageChangeTime() < 1000 && timeOffset < 50000;
    }
}
