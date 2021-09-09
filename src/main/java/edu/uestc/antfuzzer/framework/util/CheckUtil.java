package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.SmartContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CheckUtil {

    @Autowired
    private ThreadUtil threadUtil;
    /**
     * 检查文件
     *
     * @param checkOperation 检测逻辑
     * @param filepath       检测文件
     * @param objects        相关参数
     * @return 检测结果
     */
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
            if (currentTime - startTime > 50) {
                return false;
            }
            threadUtil.waitFor(50);
        }
    }

    /**
     * 检测逻辑
     */
    public interface CheckOperation {
        boolean checkAllLines(BufferedReader reader, Object... args) throws IOException;
    }

    /**
     * MissingAuth 检测逻辑
     */
    public CheckOperation getMissingAuthCheckOperation(SmartContract smartContract) {
        return new CheckOperation() {
            final String[] sensitiveOps = {"db_store_i64", "db_update_i64", "db_remove_i64"};
            @Override
            public boolean checkAllLines(BufferedReader reader, Object... args) throws IOException {
                int lastAuthLine = -1;

                List<String> logs = new ArrayList<>();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    logs.add(line);
                    boolean fp = false;
                    for (String sensitiveOp : sensitiveOps) {
                        // 使用了敏感操作
                        // 若创建对象是合约创建并付费的表，则本次检查为误报
                        String scCreateTable = String.format("db_store_i64(%s)", smartContract.getName());
                        if (line.contains(scCreateTable)) {
                            fp = true;
                        }
                        if (line.contains(sensitiveOp)) {
                            // 向前搜索
                            int i = logs.size() - 1;
                            for (int j = i; j >= lastAuthLine && j >= 0; j--) {
                                if (logs.get(j).contains("require_authorization")) {
                                    lastAuthLine = j;
                                }
                            }
                            // 直到搜索到第一行仍然没有找到require_authorization
                            if (lastAuthLine < 0 && !fp) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    /**
     * AcceptEOS 检测逻辑
     */
    public CheckOperation getAcceptEOSTokenCheckOperation(SmartContract smartContract) {
        return (reader, args) -> {
            String line = null;
            int callIndirectCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("CallIndirect") && (++callIndirectCount == 2)) {
                    return true;
                }
            }
            return false;
        };
    }
}
