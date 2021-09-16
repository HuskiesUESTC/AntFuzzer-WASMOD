package edu.uestc.antfuzzer.framework.util.middleware.eos.before;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzingConfig;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.ConfigUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.FileUtil;
import edu.uestc.antfuzzer.framework.util.JsonUtil;
import edu.uestc.antfuzzer.framework.util.middleware.BeforeCheck;

import java.io.IOException;

@Component
public class InitEOSFuzzingResult extends BeforeCheck {

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private ConfigUtil configUtil;

    @Override
    protected boolean currentCheck() throws IOException {
        environmentUtil.setEosFuzzingResult(new EOSFuzzingResult());
        // 如果使用了 compareTo 则加载相应的对比文件
        FuzzingConfig fuzzingConfig = configUtil.getFuzzingConfig();
        String compareTo = fuzzingConfig.getCompareTo();
        if (compareTo != null && !compareTo.trim().equals("")) {
            if (compareTo.equals(configUtil.getFuzzingConfig().getOutputFile())) {
                throw new IOException("对比文件与当前输出结果文件不可相同");
            }
            environmentUtil.setCompare(true);
            String json = fileUtil.read(compareTo);
            EOSFuzzingResult compareEOSFuzzingResult = jsonUtil.parseJsonToObject(EOSFuzzingResult.class, json);
            environmentUtil.setCompareEOSFuzzingResult(compareEOSFuzzingResult);
        }
        String startFrom = fuzzingConfig.getStartFrom();
        // 如果设置了断点，直接加载当前文件作为 EOSFuzzing的结果
        if (fuzzingConfig.getHasBreakpoint() != null && fuzzingConfig.getHasBreakpoint() && startFrom != null && !startFrom.equals("")) {
            environmentUtil.setHasBreakpoint(true);
            String json = fileUtil.read(startFrom);
            environmentUtil.setEosFuzzingResult(jsonUtil.parseJsonToObject(EOSFuzzingResult.class, json));
        }
        return true;
    }
}
