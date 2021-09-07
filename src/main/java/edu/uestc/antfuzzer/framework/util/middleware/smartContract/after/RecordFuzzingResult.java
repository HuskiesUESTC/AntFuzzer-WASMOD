package edu.uestc.antfuzzer.framework.util.middleware.smartContract.after;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.result.EOSFuzzingResult;
import edu.uestc.antfuzzer.framework.util.ConfigUtil;
import edu.uestc.antfuzzer.framework.util.EnvironmentUtil;
import edu.uestc.antfuzzer.framework.util.JsonUtil;
import edu.uestc.antfuzzer.framework.util.middleware.AfterCheck;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class RecordFuzzingResult extends AfterCheck {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Override
    protected boolean currentCheck() throws InvocationTargetException, IllegalAccessException {
        try {
            String resultFilePath = configUtil.getFuzzingConfig().getOutputFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath));
            EOSFuzzingResult eosFuzzingResult = environmentUtil.getEosFuzzingResult();
            eosFuzzingResult.setNumber(environmentUtil.getNumber());
            String json = jsonUtil.covertObjectToJson(eosFuzzingResult);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
