package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class OpUtil {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private PipeUtil pipeUtil;

    @Autowired
    private ThreadUtil threadUtil;

    @Getter
    @Setter
    private String opFilePath;

    public void setDefaultOpFilePath() {
        this.opFilePath = configUtil.getFrameworkConfig().getEosio().getOpFilepath();
    }

    public void rmOpFile() throws IOException {
        File file = new File(opFilePath);
        while (file.exists()) {
            file.delete();
        }
    }
}
