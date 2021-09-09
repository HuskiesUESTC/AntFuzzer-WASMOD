package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.result.ExecuteResult;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.CheckUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Fuzzer(vulnerability = "HackRecipient",
        fuzzScope = FuzzScope.all,
        iteration = 50,
        useAccountPool = true
)
public class HackRecipientFuzzer extends BaseFuzzer {
    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        // 部署测试账号
        cleosUtil.createAccount("eidosatk", configUtil.getFrameworkConfig().getAccount().getPublicKey());
        cleosUtil.addCodePermission("eidosatk");
        transfer("eosio", "eidosatk", "10000000.0000 EOS", "fuzzer");
        canAcceptEOS = canAcceptEOS();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");

        cleosUtil.pushAction(
                smartContract.getName(),
                action,
                arguments,
                nameGenerator.generate(arguments));
        Thread.sleep(600);
        ExecuteResult checkResult = checkHackRecipient();
        if (checkResult.getVulDetect()) {
            environmentUtil.getActionFuzzingResult().getVulnerability().add("HackRecipient");
            return FuzzingStatus.SUCCESS;
        }

        if (canAcceptEOS) {
            fileUtil.rmLogFiles();
            cleosUtil.pushAction(
                    "eosio.token",
                    "transfer",
                    jsonUtil.getJson(
                            "eidosatk",
                            smartContract.getName(),
                            (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                            (String) argumentGenerator.generateSpecialTypeArgument("string")
                    ),
                    "eosio"
            );
            checkResult = checkHackRecipient();
            if (checkResult.getVulDetect()) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("HackRecipient");
                return FuzzingStatus.SUCCESS;
            }
        }
        return FuzzingStatus.NEXT;
    }


    private ExecuteResult checkHackRecipient() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/root/.local/share/eosio/func.txt"));
            String line = null;
            boolean signedTransaction = false;
            boolean sentTransaction = false;
            boolean sentRecipient = false;
            while ((line = reader.readLine()) != null) {
                if ((line.contains("send_deferred") || line.contains("send_inline")) && line.contains(smartContract.getName())) {
                    signedTransaction = true;
                    sentTransaction = true;
                }

                if (line.startsWith(smartContract.getName()) && line.contains("require_recipient")) {
                    sentRecipient = true;
                }
            }
            successExecCount++;
            return new ExecuteResult(true, signedTransaction && sentTransaction && sentRecipient);
        } catch (IOException e) {
            System.out.println("op file not exist");
            return new ExecuteResult(false, false);
        }
    }

    /**
     * analyse
     */
    private CheckUtil.CheckOperation getCheckOperation() {
        // 使用了get_self()为action签名
        // 使用send_deferred()发送action
        // 使用require_recipient()将回执发给使用者
        return (reader, args) -> {
            try {
                String line = null;
                boolean signedTransaction = false;
                boolean sentTransaction = false;
                boolean sentRecipient = false;
                while ((line = reader.readLine()) != null) {
                    if ((line.contains("send_deferred") || line.contains("send_inline")) && line.contains(smartContract.getName())) {
                        signedTransaction = true;
                        sentTransaction = true;
                    }

                    if (line.startsWith(smartContract.getName()) && line.contains("require_recipient")) {
                        sentRecipient = true;
                    }
                }
                return signedTransaction && sentTransaction && sentRecipient;
            } catch (IOException e) {
                return false;
            }
        };
    }
}
