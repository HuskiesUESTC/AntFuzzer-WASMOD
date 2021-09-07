package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.enums.ExceptionMessage;

@Component
public class InputArgumentUtil {

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private ConfigUtil configUtil;

    private int index;
    private int length;
    private String[] arguments;

    public void parseCommand(String[] args) {
        index = 0;
        length = args.length;
        arguments = args;
        while (index < length) {
            String command = getCommand();
            switch (command) {
                case "-fuzzingConfigFile":
                    changeFuzzingConfigFilepath();
                    break;
                default:
                    break;
            }
        }
    }

    private void changeFuzzingConfigFilepath() {
        String filepath = getCommand();
        if (!fileUtil.exists(filepath)) {
            throw new IllegalArgumentException(ExceptionMessage.INCORRECT_INPUT_CONFIG_FILEPATH.getMessage());
        }
        configUtil.getFrameworkConfig().setFuzzingConfigFile(filepath);
    }

    private String getCommand() {
        if (index < length) {
            return arguments[index++];
        }
        throw new IllegalArgumentException(ExceptionMessage.INCORRECT_INPUT_ARGUMENTS.getMessage());
    }
}
