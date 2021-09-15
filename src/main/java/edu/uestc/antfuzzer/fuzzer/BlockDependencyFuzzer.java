package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.CheckUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Fuzzer(vulnerability = "BlockDependency-MissingAuth",
        fuzzScope = FuzzScope.all,
        argDriver = ArgDriver.afl,
        iteration = 100,
        useAccountPool = true
)
public class BlockDependencyFuzzer extends BaseFuzzer {
    @Autowired
    private CheckUtil checkUtil;

    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        canAcceptEOS = canAcceptEOS();
    }

    private CheckUtil.CheckOperation blockDependencyCheckOperation;

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        Set<String> currentActionVulnerabilities = environmentUtil.getActionFuzzingResult().getVulnerability();

        NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");
        String smartContractName = smartContract.getName();
        String accountName = nameGenerator.generate(arguments);

        boolean isTransferAction = action.equalsIgnoreCase("transfer");
        if (isTransferAction && canAcceptEOS) {
            smartContractName = "eosio.token";
            accountName = "eosio";
            arguments = jsonUtil.getJson(
                    "eosio",
                    smartContract.getName(),
                    (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                    (String) argumentGenerator.generateSpecialTypeArgument("string")
            );
        }

        cleosUtil.pushAction(
                smartContractName,
                action,
                arguments,
                accountName);

        boolean hasBlockDependencyVul = currentActionVulnerabilities.contains("BlockDependency");
        boolean hasMissingAuthVul = currentActionVulnerabilities.contains("MissingAuth");

        if (!hasBlockDependencyVul && checkUtil.checkFile(getBlockDependencyCheckOperation(), "/root/.local/share/eosio/logfile.txt")) {
            currentActionVulnerabilities.add("BlockDependency");
            hasBlockDependencyVul = true;
        }

        // 如果当前不是 transfer 函数
        // 并且没有检测出 missingAuth 漏洞
        if (!isTransferAction && !hasMissingAuthVul && checkUtil.checkFile(checkUtil.getMissingAuthCheckOperation(smartContract), "/root/.local/share/eosio/func.txt")) {
            currentActionVulnerabilities.add("MissingAuth");
            hasMissingAuthVul = true;
        }

        // 如果当前是 Transfer 函数，则检测到 BlockDependency 便立即返回
        if (isTransferAction && hasBlockDependencyVul) {
            return FuzzingStatus.SUCCESS;
        }
        // 如果当前是其他函数，则检测到两种漏洞才能返回
        if (!isTransferAction && hasBlockDependencyVul && hasMissingAuthVul) {
            return FuzzingStatus.SUCCESS;
        }
        return FuzzingStatus.NEXT;
    }

    private CheckUtil.CheckOperation getBlockDependencyCheckOperation() {
        if (blockDependencyCheckOperation == null) {
            blockDependencyCheckOperation = (reader, args) -> {
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
        return blockDependencyCheckOperation;
    }
}