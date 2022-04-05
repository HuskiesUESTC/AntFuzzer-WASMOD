package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

@Component
public class LogUtil {

    // 正常的日期格式
    private final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    private ConfigUtil configUtil;
    // 全局的logger
    private Logger logger;

    /**
     * 为log设置等级
     */
    private void setLogLevel(Logger log, Level level) {
        log.setLevel(level);
    }

    private void addConsoleHandler(Logger log, Level level) {
        // 控制台输出的handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        // 设置控制台输出的等级（如果ConsoleHandler的等级高于或者等于log的level，则按照FileHandler的level输出到控制台，如果低于，则按照Log等级输出）
        consoleHandler.setLevel(level);
        // 添加控制台的handler
        log.addHandler(consoleHandler);
    }

    private void addFileHandler(Logger log, Level level, String filePath) {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(filePath);
            // 设置输出文件的等级（如果FileHandler的等级高于或者等于log的level，则按照FileHandler的level输出到文件，如果低于，则按照Log等级输出）
            fileHandler.setLevel(level);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    // 设置文件输出格式
                    return record.getMessage() + "\n";
                }
            });

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        // 添加输出文件handler
        assert fileHandler != null;
        log.addHandler(fileHandler);
    }

    public Logger loadLogger(String smartContractName, String vulnerability) {
        Logger logger = Logger.getLogger(smartContractName);
        // 为log设置全局等级
        logger.setLevel(Level.ALL);
        // 添加控制台handler
        addConsoleHandler(logger, Level.INFO);
        // 添加文件输出handler
        String logFilePath = configUtil.getFrameworkConfig().getLogDir() + "/history/" + generateCurrentDateString() + "/contracts/" + smartContractName + "/" + vulnerability + ".log";
        File file = new File(logFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        addFileHandler(logger, Level.INFO, logFilePath);
        // 设置不适用父类的handlers，这样不会在控制台重复输出信息
        logger.setUseParentHandlers(false);
        return logger;
    }

    public void logException(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        if (e != null) {
            for (StackTraceElement element : e.getStackTrace()) {
                stringBuilder.append("\r\n\t").append(element);
            }
        }
        if (stringBuilder.length() > 0) {
            Logger logger = loadLogger();
            logger.log(Level.INFO, stringBuilder.toString());
        }
    }

    public Logger loadLogger() {
        if (logger == null) {
            // 考虑到未来可能使用多线程，这里进行了同步
            synchronized (LogUtil.class) {
                if (logger == null) {
                    // 获取Log
                    logger = Logger.getLogger("Global");
                    // 为log设置全局等级
                    logger.setLevel(Level.ALL);
                    // 添加控制台handler
                    addConsoleHandler(logger, Level.INFO);
                    // 添加文件输出handler
                    String logFilePath = configUtil.getFrameworkConfig().getLogDir() + "/system.log";
                    File file = new File(logFilePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    addFileHandler(logger, Level.INFO, logFilePath);
                    // 设置不适用父类的handlers，这样不会在控制台重复输出信息
                    logger.setUseParentHandlers(false);
                }
            }
        }
        return logger;
    }

    private String generateCurrentDateString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }
}
