package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.Before;
import edu.uestc.antfuzzer.framework.annotation.Fuzz;
import edu.uestc.antfuzzer.framework.annotation.Fuzzer;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.util.CheckUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Fuzzer(vulnerability = "FakeEOSTransfer",
        fuzzScope = FuzzScope.transfer,
        argDriver = ArgDriver.afl,
        iteration = 50,
        useAccountPool = false
)
public class FakeEOSTransferFuzzer extends BaseFuzzer {
    private final String fakeTransferAgentName = "atknoti";

    private CheckUtil.CheckOperation checkOperation;

    @Before
    public boolean init() throws IOException, InterruptedException {
        initFuzzer();
        // 部署代理合约
        cleosUtil.createAccount(fakeTransferAgentName, configUtil.getFrameworkConfig().getAccount().getPublicKey());
        cleosUtil.setContract(fakeTransferAgentName, configUtil.getFrameworkConfig().getSmartContracts().getAtknoti());
        cleosUtil.addCodePermission(fakeTransferAgentName);
        canAcceptEOS = canAcceptEOS();
        return canAcceptEOS;
    }

    @Fuzz
    public FuzzingStatus fuzz() throws IOException, InterruptedException, AFLException {
        fileUtil.rmLogFiles();
        // 调用代理合约
        String[] names = {smartContract.getName(), fakeTransferAgentName};
        cleosUtil.pushAction(
                fakeTransferAgentName,
                "transfer",
                jsonUtil.getJson(
                        smartContract.getName(),
                        smartContract.getName(),
                        (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                        (String) argumentGenerator.generateSpecialTypeArgument("string")
                ),
                names[new Random().nextInt(2)]);
        // 检测opt.txt
        if (checkAllLines()) {
            environmentUtil.getActionFuzzingResult().getVulnerability().add("FakeEOSTransfer");
            return FuzzingStatus.SUCCESS;
        }
        return FuzzingStatus.NEXT;
    }

    private boolean checkAllLines() {
        try {
            Set<String> callIndirect = new HashSet<>();
            List<String> address = new ArrayList<>();
            BufferedReader bf = new BufferedReader(new FileReader("/root/.local/share/eosio/op.txt"));
            String line = null;
            String target = "CallIndirect";
            while ((line = bf.readLine()) != null) {
                if (line.startsWith(target)) {
                    if (!callIndirect.contains(line)) {
                        callIndirect.add(line);
                        address.add(line);
                    }
                    if (callIndirect.size() >= 2) {
                        return address.get(address.size() - 1).equals(transferAddress);
                    }
                }
            }
            bf.close();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CheckUtil.CheckOperation getCheckOperation() {
//        Pattern logFrameSizeTypePattern = Pattern.compile("CallIndirect\\(isNotHost,Goto\\(\\(([0-9]+)\\)\\)");
//        Pattern logFrameSizeTypePattern = Pattern.compile(" {2}\\(type (\\$[a-zA-Z0-9]+) \\(func \\(param i32\\)\\)\\)");
        Set<String> callIndirect = new HashSet<>();
        if (checkOperation == null) {
            checkOperation = (reader, args) -> {
                String target = "CallIndirect";
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(target)) {
                        callIndirect.add(line);
                        if (callIndirect.size() >= 2) {
                            for (String s : callIndirect) {
                                System.out.println(s);
                            }
                            return true;
                        }
                    }
                }
                return false;
            };
        }
        return checkOperation;
    }

    private boolean checkStart() {
        try {
            BufferedReader bf = new BufferedReader(new FileReader("/root/.local/share/eosio/func.txt"));
            String line = bf.readLine();
            bf.close();
            return line == null || line.contains("send_inline() atknoti");
        } catch (IOException e) {
            return false;
        }
    }
}