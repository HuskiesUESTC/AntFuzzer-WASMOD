package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.config.framework.FrameworkConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EOSUtil {
    @Autowired
    private PipeUtil pipeUtil;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private JsonUtil jsonUtil;
    @Getter
    private final CppUtil cppUtil;
    @Getter
    private final CleosUtil cleosUtil;
    @Getter
    private List<Account> accountPool;

    public EOSUtil() {
        cppUtil = new CppUtil();
        cleosUtil = new CleosUtil();
        accountPool = new ArrayList<>();
    }

    public void initEOS() throws IOException, InterruptedException {
        initEOS(null);
    }

    public void initEOS(String contractName) throws IOException, InterruptedException {
        initEOS(contractName, false);
    }

    public void initEOS(String contractName, boolean useAccountPool) throws InterruptedException, IOException {
        FrameworkConfig frameworkConfig = configUtil.getFrameworkConfig();
        String startSh = frameworkConfig.getNodeosStartShell();
        pipeUtil.execute("killall nodeos");
        pipeUtil.execute("rm -rf /root/.local/share/eosio");
        pipeUtil.execute("rm ./log/nodeos.log");
        cleosUtil.unlockWallet(frameworkConfig.getAccount().getPrivateKey());
        pipeUtil.execute("/bin/bash " + startSh);

        pipeUtil.waitForExecute("cleos get currency balance account testeosfrom");
        // 部署系统合约
        cleosUtil.createAccount("eosio.token", frameworkConfig.getEosTokenPublicKey());
        cleosUtil.setContract("eosio.token", frameworkConfig.getSmartContracts().getEosioToken());
        cleosUtil.pushAction(
                "eosio.token",
                "create",
                jsonUtil.getJson(
                        "eosio",
                        "1000000000000.0000 EOS"),
                "eosio.token");

        // 初始化用户池
        String accountPublicKey = frameworkConfig.getAccount().getPublicKey();
        if (useAccountPool) {
            cleosUtil.createAccount("fuzzacc1", accountPublicKey);
            cleosUtil.createAccount("fuzzacc2", accountPublicKey);
            cleosUtil.createAccount("fuzzacc3", accountPublicKey);
            accountPool.add(new Account("fuzzacc1", accountPublicKey));
            accountPool.add(new Account("fuzzacc2", accountPublicKey));
            accountPool.add(new Account("fuzzacc3", accountPublicKey));
        }
        // 部署测试合约
        if (contractName != null && !contractName.trim().equalsIgnoreCase("")) {
            cleosUtil.createAccount(contractName, accountPublicKey);
            cleosUtil.pushAction(
                    "eosio.token",
                    "issue",
                    jsonUtil.getJson(contractName, "1000000000.0000 EOS", "fuzzer"),
                    "eosio");
            cleosUtil.setContract(contractName, configUtil.getFuzzingConfig().getSmartContractDir() + "/" + contractName);
            cleosUtil.addCodePermission(contractName);
        }
    }

    public void setUpEOSToken(String smartContract) throws IOException, InterruptedException {
        cleosUtil.pushAction(
                "eosio.token",
                "issue",
                jsonUtil.getJson("eosio", "10000000000.0000 EOS", "fuzzer"),
                "eosio");

        cleosUtil.pushAction(
                "eosio.token",
                "transfer",
                jsonUtil.getJson("eosio", smartContract, "1000000000.0000 EOS", "fuzzer"),
                "eosio"
        );
    }

    public class CppUtil {
        /**
         * eosio-cpp -o {contractBinary} {contractSource} -DCONTRACT_NAME='{smartContract}'
         */
        public void compileSmartContract(String contractBinary, String contractSource, String contract) throws IOException {
            String compileSmartContract = "eosio-cpp -o %s %s -DCONTRACT_NAME=\'\"%s\"\'";
            String command = String.format(compileSmartContract, contractBinary, contractSource, contract);
            pipeUtil.execute(command);
        }

        public void compileSmartContract(String contractDir, String contractName) throws IOException {
            File file = new File(contractDir);
            System.out.println(contractDir);
            if (file.exists() && file.isDirectory()) {
                String dirName = file.getName();
                System.out.println(dirName);
                String contractSource = contractDir + dirName + ".cpp";
                String contractBinary = contractDir + dirName + ".wasm";
                compileSmartContract(contractBinary, contractSource, contractName);
            }
        }
    }

    public class CleosUtil {
        /**
         * cleos get currency balance eosio.token {account} EOS
         */
        public String getGetCurrencyBalance(String account) throws IOException, InterruptedException {
            String getCurrencyBalance = "cleos get currency balance eosio.token %s EOS";
            String command = String.format(getCurrencyBalance, account);
            List<String> result = pipeUtil.executeWithResult(command);
            StringBuilder builder = new StringBuilder();
            for (String line : result) {
                builder.append(line);
            }
            return builder.toString();
        }

        /**
         * cleos push action {contract} {action} '{actionParameters}' -p {account} @active
         */
        public void pushAction(String contract, String action, String actionParameters, String account) throws IOException, InterruptedException {
            String pushAction = "cleos push action %s %s '%s' -p %s@active -f 2>&1";
            actionParameters = actionParameters.replaceAll("'", "");
            String command = String.format(pushAction, contract, action, actionParameters, account);
            pipeUtil.execute(command, true);
        }

        /**
         * cleos create account eosio {account} {publicKey}
         */
        public void createAccount(String account, String publicKey) throws IOException {
            String createAccount = "cleos create account eosio %s %s";
            String command = String.format(createAccount, account, publicKey);
            System.out.println(command);
            pipeUtil.execute(command);
        }

        /**
         * cleos set contract {contract} {contractDir} -p {account}@active
         */
        public void setContract(String contract, String contractDir) throws IOException, InterruptedException {
            String setContract = "cleos set contract %s %s -p %s@active";
            String command = String.format(setContract, contract, contractDir, contract);
            pipeUtil.executeWithResult(command);
        }

        /**
         * cleos set account permission {account} active --add-code
         */
        public void addCodePermission(String account) throws IOException {
            String addCodePermission = "cleos set account permission %s active --add-code";
            String command = String.format(addCodePermission, account);
            pipeUtil.execute(command);
        }

        /**
         * echo {walletPrivateKey} | cleos wallet unlock
         */
        public void unlockWallet(String walletPrivateKey) throws IOException {
            String addCodePermission = "echo %s | cleos wallet unlock";
            String command = String.format(addCodePermission, walletPrivateKey);
            pipeUtil.execute(command);
        }
    }

    @Data
    @AllArgsConstructor
    public class Account {
        private String name;
        private String publicKey;
    }
}