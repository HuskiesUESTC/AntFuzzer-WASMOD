package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.Before;
import edu.uestc.antfuzzer.framework.annotation.Fuzz;
import edu.uestc.antfuzzer.framework.annotation.Fuzzer;
import edu.uestc.antfuzzer.framework.bean.config.framework.FrameworkConfig;
import edu.uestc.antfuzzer.framework.bean.result.ExecuteResult;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.util.CheckUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Fuzzer(vulnerability = "Rollback",
        fuzzScope = FuzzScope.all,
        iteration = 100,
        argDriver = ArgDriver.afl,
        useAccountPool = true
)
public class RollbackFuzzer extends BaseFuzzer {
    private final String rollbackAgentName = "atkrb";

    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();

        // 部署代理合约
        FrameworkConfig frameworkConfig = configUtil.getFrameworkConfig();
        cleosUtil.createAccount(rollbackAgentName, frameworkConfig.getAccount().getPublicKey());
        cleosUtil.setContract(rollbackAgentName, frameworkConfig.getSmartContracts().getAtkrb());
        cleosUtil.addCodePermission(rollbackAgentName);

        cleosUtil.pushAction(
                "eosio.token",
                "issue",
                jsonUtil.getJson(
                        rollbackAgentName,
                        "3000000.0000 EOS",
                        "FUZZER"),
                "eosio");

    }

    @Fuzz
    public FuzzingStatus fuzz() throws IOException, InterruptedException, AFLException {
        double originalBalance = getCurrentBalance();
        String asset = (String) argumentGenerator.generateSpecialTypeArgument("asset");
        String memo = (String) argumentGenerator.generateSpecialTypeArgument("string");
        cleosUtil.pushAction(
                rollbackAgentName,
                "attack",
                jsonUtil.getJson(smartContract.getName(), asset, memo),
                rollbackAgentName
        );
        Thread.sleep(200);

        double currentBalance = getCurrentBalance();

        boolean result = checkUtil.checkFile(getCheckOperation(currentBalance, originalBalance), "/root/.local/share/eosio/func.txt");
        if (result) {
            environmentUtil.getActionFuzzingResult().getVulnerability().add("Rollback");
            return FuzzingStatus.SUCCESS;
        }
        return FuzzingStatus.NEXT;
    }

    private CheckUtil.CheckOperation getCheckOperation(double currentBalance, double originalBalance) {
        return (reader, args) -> {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("send_inline")) {
                    successExecCount++;
                    successVulCount++;
                    return currentBalance > originalBalance;
                }
            }
            return false;
        };
    }


    public double getCurrentBalance() {
        try {
            String originBalanceString = cleosUtil.getGetCurrencyBalance(rollbackAgentName);
            double currentBalance = 0;
            Pattern balancePattern = Pattern.compile("(\\d+.\\d+)\\sEOS");
            Matcher balanceMatcher = balancePattern.matcher(originBalanceString);
            if (balanceMatcher.matches()) {
                currentBalance = Double.parseDouble(balanceMatcher.group(1));
                return currentBalance;
            } else {
                System.out.println("can't get balance");
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
