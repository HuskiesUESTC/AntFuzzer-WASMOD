package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.enums.ExceptionMessage;

import java.io.*;
import java.net.URL;

@Component
public class FileUtil {

    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ThreadUtil threadUtil;

    public String[] getValidSmartContractDirs() {
        String rootDir = configUtil.getFuzzingConfig().getSmartContractDir();
        File root = new File(rootDir);
        if (root.isDirectory()) {
            return root.list((dir, name) -> {
                String subFilePath = dir.getAbsolutePath() + "/" + name;
                File subFile = new File(subFilePath);
                if (subFile.isFile())
                    return false;
                String abiFilePath = subFilePath + "/" + name + ".abi";
                String wasmFilePath = subFilePath + "/" + name + ".wasm";
                File abiFile = new File(abiFilePath);
                File wasmFile = new File(wasmFilePath);
                return abiFile.isFile() && wasmFile.isFile();
            });
        }
        return new String[]{};
    }

    /**
     * 判断文件是否存在
     */
    public boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 文件换名
     */
    public boolean move(String from, String to) {
        File originFile = new File(from);
        if (originFile.exists()) {
            File destFile = new File(to);
            if (destFile.exists() && !destFile.delete()) {
                return false;
            }
            return originFile.renameTo(destFile);
        }
        return false;
    }

    /**
     * 写文件
     *
     * @param filePath 文件地址
     * @param data     文件内容
     * @param append   是否追加
     */
    public void write(String filePath, String data, boolean append) throws IOException {
        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile())
            throw new IOException(String.format(ExceptionMessage.FILE_CREATE_FAILED.getMessage(), filePath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * 读文件
     */
    public String read(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line);
        reader.close();
        return builder.toString();
    }

    /**
     * 读资源文件
     */
    public String readRecourse(String filename) throws IOException {
        URL url = FileUtil.class.getClassLoader().getResource(filename);
        if (url == null) {
            throw new FileNotFoundException(filename + ExceptionMessage.CONFIG_FILE_NOT_EXISTS.getMessage());
        }
        String filepath = url.getPath();
        return read(filepath);
    }

    public boolean checkFile(CheckOperation checkOperation, String filepath, Object... objects) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        while (true) {
            File file = new File(filepath);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                boolean result = checkOperation.checkAllLines(reader, objects);
                reader.close();
                return result;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > 100) {
                return false;
            }
            threadUtil.waitFor(50);
        }
    }

    public interface CheckOperation {
        boolean checkAllLines(BufferedReader reader, Object... args) throws IOException;
    }
}
