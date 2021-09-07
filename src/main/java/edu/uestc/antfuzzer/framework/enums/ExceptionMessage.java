package edu.uestc.antfuzzer.framework.enums;

public enum ExceptionMessage {
    INCORRECT_INPUT_ARGUMENTS("输入参数个数有误"),
    INCORRECT_INPUT_CONFIG_FILEPATH("输入配置文件不存在"),
    FILE_CREATE_FAILED("文件创建失败"),
    CONFIG_FILE_NOT_EXISTS("配置文件不存在"),
    CONFIG_FILE_INVALID("配置文件不正确");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
