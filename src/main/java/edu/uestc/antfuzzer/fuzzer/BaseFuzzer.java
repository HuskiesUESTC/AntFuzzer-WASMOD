package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Contract;
import edu.uestc.antfuzzer.framework.annotation.Log;
import edu.uestc.antfuzzer.framework.bean.Balance;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.ArgumentGenerator;
import edu.uestc.antfuzzer.framework.util.*;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFuzzer {
    @Contract
    SmartContract smartContract;
    @Log
    Logger logger;
    @Autowired
    EOSUtil eosUtil;
    @Autowired
    ConfigUtil configUtil;
    @Autowired
    TypeUtil typeUtil;
    @Autowired
    PipeUtil pipeUtil;
    @Autowired
    OpUtil opUtil;
    @Autowired
    LogUtil logUtil;
    @Autowired
    JsonUtil jsonUtil;
    @Autowired
    SmartContractUtil smartContractUtil;
    @Autowired
    EnvironmentUtil environmentUtil;
    @Autowired
    FileUtil fileUtil;

    // 参数生成器
    ArgumentGenerator argumentGenerator;
    EOSUtil.CleosUtil cleosUtil;

    boolean canAcceptEOS = false;

    // 总共测试次数
    int testCount = 0;
    // 测试成功次数
    int successExecCount = 0;
    // 发现漏洞次数
    int successVulCount = 0;

    /**
     * 初始化参数生成器和cleos工具
     */
    public void initFuzzer() {
        argumentGenerator = typeUtil.getGenerator(smartContract);
        cleosUtil = eosUtil.getCleosUtil();
    }

    /**
     * 初始化智能合约并分发测试使用的代币
     */
    public void initSmartContract() throws IOException, InterruptedException {
        // 部署智能合约
        String contractDir = configUtil.getFuzzingConfig().getSmartContractDir() + "/" + smartContract.getName();
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
//        cleosUtil.createAccount(smartContract.getName(), configUtil.getAccountPublicKey());
        cleosUtil.setContract(smartContract.getName(), contractDir);
//        cleosUtil.addCodePermission(smartContract.getName());
        // 初始化EOS
        startUpEOSToken();
    }

    /**
     * 为测试账户分发代币
     */
    public void startUpEOSToken() throws IOException, InterruptedException {
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
        cleosUtil.pushAction(
                "eosio.token",
                "issue",
                jsonUtil.getJson(
                        (String) "eosio",
                        (String) "100000000.0000 EOS",
                        (String) "start up"
                ),
                "eosio"
        );

        cleosUtil.pushAction(
                "eosio.token",
                "transfer",
                jsonUtil.getJson(
                        (String) "eosio",
                        (String) smartContract.getName(),
                        (String) "10000000.0000 EOS",
                        (String) "fuzzer"
                ),
                "eosio"
        );
    }

    /**
     * 随机执行一个action
     */
    public void pushRandomAction() throws IOException, InterruptedException, AFLException {
        Action actionToPush = getRandomAction();
        if (actionToPush == null) {
            return;
        }
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
        ArgumentGenerator argumentGenerator = typeUtil.getGenerator(smartContract);
        String args = argumentGenerator.generateFuzzingArguments(actionToPush);
        cleosUtil.pushAction(
                smartContract.getName(),
                actionToPush.getName(),
                args,
                "eosio"
        );
    }

    public Action getRandomAction() {
        Random random = new Random();
        List<Action> actions = smartContract.getAbi().getActions();
        if (actions.size() == 0) {
            return null;
        }
        return actions.get(Math.abs(random.nextInt()) % actions.size());
    }

    public void pushAction(Action action, String args, String testAccount) throws IOException, InterruptedException {
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
        cleosUtil.pushAction(
                smartContract.getName(),
                action.getName(),
                args,
                testAccount
        );
    }

    public void transfer(String from, String to, String asset, String memo) throws IOException, InterruptedException {
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
        cleosUtil.pushAction(
                "eosio.token",
                "transfer",
                jsonUtil.getJson(
                        (String) from,
                        (String) to,
                        (String) asset,
                        (String) memo
                ),
                from
        );
    }

    public String transferWithMemoStringPool(String from, String to) throws IOException, InterruptedException, AFLException {
        ArgumentGenerator argumentGenerator = typeUtil.getGenerator(smartContract);
        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
        List<String> memoStringPool = smartContract.getType().getMemoStringPool();
        String asset = (String) argumentGenerator.generateSpecialTypeArgument("asset");
        String memo = "no memo";
        String action = String.format("cleos push action eosio.token transfer \\'[%s, %s, %s, %s]\\'", from, to, asset, memo);
        if (memoStringPool.size() != 0) {
            Random random = new Random();
            memo = (String) memoStringPool.get(Math.abs(random.nextInt()) % memoStringPool.size());
            transfer(from, to, asset, memo);
            return action;
        }
        transfer(from, to, asset, memo);
        return action;
    }

    public void setResultRecord(String actionName, String vulnerability, boolean checkResult) {
        String format = "contract: %s,\n" +
                "function: %s, \n" +
                "vulnerability: %s, \n" +
                "fuzz: %s\n";
        if (checkResult) {
            System.out.println(String.format(format, smartContract.getName(), actionName, vulnerability, "success"));
            Map<String, List<String>> result = smartContract.getResult();
            if (result == null)
                result = new HashMap<>();
            List<String> record = result.get(actionName);
            if (record == null)
                record = new ArrayList<>();
            record.add(vulnerability);
            result.put(actionName, record);
            smartContract.setResult(result);
        } else {
            System.out.println(String.format(format, smartContract.getName(), actionName, vulnerability, "failed"));
        }
    }

    public void setResultRecord(String actionName, String vulnerability, boolean checkResult, String successCase, String info) {
        String format = "contract: %s,\n" +
                "function: %s, \n" +
                "vulnerability: %s, \n" +
                "fuzz: %s\n";
        if (checkResult) {
            System.out.println(String.format(format, smartContract.getName(), actionName, vulnerability, "success"));
            Map<String, List<String>> result = smartContract.getResult();
            if (result == null)
                result = new HashMap<>();
            List<String> record = result.get(actionName);
            if (record == null)
                record = new ArrayList<>();
            record.add(vulnerability);
            record.add(successCase);
            record.add(info);
            result.put(actionName, record);
            smartContract.setResult(result);
        } else {
            System.out.println(String.format(format, smartContract.getName(), actionName, vulnerability, "failed"));
        }
    }

    public void clearLogFiles() {
        File rootDir = new File("/root/.local/share/eosio");
        if (rootDir.exists() && rootDir.isDirectory()) {
            File[] files = rootDir.listFiles((dir, name) -> name.endsWith("txt") && !name.equals("coverage.txt"));
            if (files != null && files.length > 0) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 返回当前合约账户的代币余额
     *
     * @param contract 代币合约
     * @param account  要查询的账户
     * @param symbol   代币名称
     * @return 当前合约账户的代币余额
     */
    public Balance getCurrentBalance(String contract, String account, String symbol) {
        try {
            String command = String.format("cleos get currency balance %s %s %s", contract, account, symbol);
            List<String> executeResult = pipeUtil.executeWithResult(command);
            Pattern balancePattern = Pattern.compile("(\\d+).(\\d+) " + symbol);
            Matcher balanceMatcher = balancePattern.matcher(executeResult.toString());
            if (balanceMatcher.matches()) {
                double quantity = Double.parseDouble(balanceMatcher.group(1)) + Double.parseDouble(balanceMatcher.group(2)) * 0.1;
                return new Balance(quantity, symbol);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 测试被测合约能否接受EOS转账
     *
     * @return true if it can
     */
    public boolean canAcceptEOS() {
        // 测试是否能接收EOS
        try {
            clearLogFiles();
            cleosUtil.pushAction(
                    "eosio.token",
                    "transfer",
                    jsonUtil.getJson(
                            "eosio",
                            smartContract.getName(),
                            (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                            (String) argumentGenerator.generateSpecialTypeArgument("string")
                    ),
                    "eosio"
            );
            BufferedReader opTxt = new BufferedReader(new FileReader(configUtil.getFrameworkConfig().getEosio().getOpFilepath()));
            String line = null;
            int callIndirectCount = 0;
            while ((line = opTxt.readLine()) != null) {
                if (line.contains("CallIndirect") && (++callIndirectCount == 2)) {
                    return true;
                }
            }
            opTxt.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    void writeResultToFile() throws IOException {
        File reportDir = null;
        // 为每个合约的报告创建文件夹
        reportDir = new File("reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter("reports/" + smartContract.getName() + ".txt", true));
        Map<String, List<String>> result = smartContract.getResult();
        for (Map.Entry<String, List<String>> elm : result.entrySet()) {
            String action = elm.getKey();
            if (action == null) {
                continue;
            }
            String vulnerability = elm.getValue().get(0);
        }
        out.write("---------------------------------------\n");
        out.write("---------------------------------------\n");
        out.close();
    }
}
