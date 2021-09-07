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
import edu.uestc.antfuzzer.framework.util.FileUtil;

import java.io.IOException;

@Fuzzer(vulnerability = "FakeEOSTransfer",
        fuzzScope = FuzzScope.transfer,
        argDriver = ArgDriver.afl,
        iteration = 500,
        useAccountPool = false
)
public class FakeEOSTransferFuzzer extends BaseFuzzer {
    private final String fakeTransferAgentName = "atknoti";

    private FileUtil.CheckOperation checkOperation;

    @Before
    public boolean init() throws IOException, InterruptedException {
        initFuzzer();
        // 部署代理合约
        startUpEOSToken();
        cleosUtil.createAccount(fakeTransferAgentName, configUtil.getFrameworkConfig().getAccount().getPublicKey());
        cleosUtil.setContract(fakeTransferAgentName, configUtil.getFrameworkConfig().getSmartContracts().getAtknoti());
        cleosUtil.addCodePermission(fakeTransferAgentName);
        canAcceptEOS = canAcceptEOS();
        return canAcceptEOS;
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        // 调用代理合约
        if (canAcceptEOS) {
            opUtil.rmOpFile();
            clearLogFiles();
            cleosUtil.pushAction(
                    fakeTransferAgentName,
                    "transfer",
                    jsonUtil.getJson(
                            "eosio",
                            smartContract.getName(),
                            (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                            (String) argumentGenerator.generateSpecialTypeArgument("string")
                    ),
                    "eosio");
            // 检测opt.txt
            if (fileUtil.checkFile(getCheckOperation(), opUtil.getOpFilePath())) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("FakeEOSTransfer");
                return FuzzingStatus.SUCCESS;
            }
            setResultRecord(action, "FakeEOSTransfer", false);
        }
        return FuzzingStatus.NEXT;
    }

    private FileUtil.CheckOperation getCheckOperation() {
        if (checkOperation == null) {
            checkOperation = (reader, args) -> {
                String target = "CallIndirect";
                int callIndirect = 0;

                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(target) && (++callIndirect) == 2) {
                        return true;
                    }
                }
                return false;
            };
        }
        return checkOperation;
    }
}