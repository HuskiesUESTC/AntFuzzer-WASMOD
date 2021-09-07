package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.result.ExecuteResult;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Fuzzer(vulnerability = "MissingAuth",
        fuzzScope = FuzzScope.all,
        iteration = 500,
        argDriver = ArgDriver.afl,
        useAccountPool = true
)
public class MissingAuthFuzzer extends BaseFuzzer {
    @Before
    public void init() throws IOException, InterruptedException {
        opUtil.setOpFilePath("/root/.local/share/eosio/func.txt");
        initFuzzer();
        startUpEOSToken();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        // 删除记录文件
        opUtil.rmOpFile();
        NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");
        cleosUtil.pushAction(
                smartContract.getName(),
                action,
                arguments,
                nameGenerator.generate(arguments));
        ExecuteResult checkResult = checkMissingAuth();
        if (checkResult.getVulDetect()) {
            environmentUtil.getActionFuzzingResult().getVulnerability().add("MissingAuth");
            return FuzzingStatus.SUCCESS;
        }
        setResultRecord(action, "Missing Auth", checkResult.getVulDetect());
        return FuzzingStatus.NEXT;
    }

    private ExecuteResult checkMissingAuth() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(opUtil.getOpFilePath()));
            String[] sensitiveOperations = {"db_store_i64", "db_update_i64", "db_remove_i64"};

            String line = null;
            List<String> funcTxtFile = new ArrayList<>();
            int lastAuthLine = -1;
            while ((line = reader.readLine()) != null) {
                funcTxtFile.add(line);
            }

            boolean FP = false;

            for (int i = 0; i < funcTxtFile.size(); i++) {
                FP = false;
                for (String sensitiveOp : sensitiveOperations) {
                    // 使用了敏感操作
                    // 若创建对象是合约创建并付费的表 则本次检测为误报
                    String scCreateTable = String.format("db_store_i64(%s)", smartContract.getName());
                    if (funcTxtFile.get(i).contains(scCreateTable)) {
                        FP = true;
                    }
                    if (funcTxtFile.get(i).contains(sensitiveOp)) {
                        // 向前搜索
                        for (int j = i; j >= lastAuthLine && j >= 0; j--) {
                            if (funcTxtFile.get(j).contains("require_authorization")) {
                                lastAuthLine = j;
                            }
                        }

                        // 直到搜索到第一行仍没有找到require_authorization
                        if (lastAuthLine < 0 && !FP) {
                            successExecCount++;
                            successVulCount++;
                            return new ExecuteResult(true, true);
                        }
                    }
                }
            }
            successExecCount++;
            return new ExecuteResult(true, false);
        } catch (IOException e) {
            System.out.println("log file not exist");
            return new ExecuteResult(false, false);
        }
    }
}
