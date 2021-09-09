package edu.uestc.antfuzzer.framework.util.middleware.fuzzing.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.ActionFuzzingResult;
import edu.uestc.antfuzzer.framework.util.BitMapUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Component
public class PrintFuzzingResult extends AfterCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private BitMapUtil bitMapUtil;

    @Override
    protected boolean currentCheck() throws IllegalAccessException, InvocationTargetException {
        ActionFuzzingResult actionFuzzingResult = environmentUtil.getActionFuzzingResult();
        BitMapUtil.BitMap bitMap = bitMapUtil.getTotalBitMap();
        // 打印基础信息
        String info = "SmartContract:" + environmentUtil.getSmartContract().getName() + "; " +
                "Action:" + actionFuzzingResult.getName() + "; " +
                "Count:" + actionFuzzingResult.getCount() + "; ";
        System.out.println(info);
        // 打印代码覆盖率信息
        StringBuilder coverage = new StringBuilder();
        coverage.append("JointCoverage:").append(bitMap.getJointBitMapCount()).append("; ");
        for (Map.Entry<String, Integer> item : bitMap.getAllBitMapCounts().entrySet()) {
            coverage.append(item.getKey()).append(":").append(item.getValue()).append("; ");
        }
        System.out.println(coverage.toString());
        System.out.println();
        return true;
    }
}
