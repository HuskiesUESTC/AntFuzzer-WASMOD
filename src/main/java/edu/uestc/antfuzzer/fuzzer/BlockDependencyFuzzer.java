package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.Before;
import edu.uestc.antfuzzer.framework.annotation.Fuzz;
import edu.uestc.antfuzzer.framework.annotation.Fuzzer;
import edu.uestc.antfuzzer.framework.annotation.Param;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Fuzzer(vulnerability = "BlockDependency",
        fuzzScope = FuzzScope.all,
        argDriver = ArgDriver.afl,
        iteration = 200
)
public class BlockDependencyFuzzer extends BaseFuzzer {
    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        initSmartContract();
        canAcceptEOS = canAcceptEOS();
    }

    private FileUtil.CheckOperation checkOperation;

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
//        clearLogFiles();
//        if (canAcceptEOS) {
//            // 若接收EOS, 使用transfer
//            // 成功完成一次转账
//            cleosUtil.pushAction(
//                    "eosio.token",
//                    "transfer",
//                    jsonUtil.getJson(
//                            "eosio",
//                            smartContract.getName(),
//                            (String) argumentGenerator.generateSpecialTypeArgument("asset"),
//                            (String) argumentGenerator.generateSpecialTypeArgument("string")
//                    ),
//                    "eosio"
//            );
//            boolean result = fileUtil.checkFile(getCheckOperation(), "/root/.local/share/eosio/logfile.txt");
//            if (result) {
//                environmentUtil.getActionFuzzingResult().getVulnerability().add("BlockDependency");
//                return FuzzingStatus.SUCCESS;
//            }
//        }
        // 测试发送其他action
        clearLogFiles();
        NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");
        boolean isTransfer = action.equalsIgnoreCase("transfer");
        String smartContractName = isTransfer ? "eosio.token" : smartContract.getName();
        String accountName = isTransfer ? "eosio" : nameGenerator.generate(arguments);
        cleosUtil.pushAction(
                smartContractName,
                action,
                arguments,
                accountName);
        boolean result = fileUtil.checkFile(getCheckOperation(), "/root/.local/share/eosio/logfile.txt");
        if (result) {
            environmentUtil.getActionFuzzingResult().getVulnerability().add("BlockDependency");
            return FuzzingStatus.SUCCESS;
        }
        return FuzzingStatus.NEXT;
    }

    private FileUtil.CheckOperation getCheckOperation() {
        if (checkOperation == null) {
            checkOperation = (reader, args) -> {
                int lineOfTaposFunction = -1;
                int n = 0;
                List<String> log = new ArrayList<>();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("tapos_block_prefix") || line.contains("tapos_block_num")) {
                        lineOfTaposFunction = n;
                    }
                    n++;
                    log.add(line);
                }
                // 如果使用了tapos
                if (lineOfTaposFunction > 0) {
                    for (int i = lineOfTaposFunction; i < log.size(); i++) {
                        if (log.get(i).contains("eosio.token::transfer()")) {
                            return true;
                        }
                    }
                }
                return false;
            };
        }
        return checkOperation;
    }
}