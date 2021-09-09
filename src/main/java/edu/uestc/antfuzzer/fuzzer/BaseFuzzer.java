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

import java.io.IOException;
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
    LogUtil logUtil;
    @Autowired
    JsonUtil jsonUtil;
    @Autowired
    EnvironmentUtil environmentUtil;
    @Autowired
    FileUtil fileUtil;
    @Autowired
    CheckUtil checkUtil;

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
    public void initFuzzer() throws IOException, InterruptedException {
        argumentGenerator = typeUtil.getGenerator(smartContract);
        cleosUtil = eosUtil.getCleosUtil();
        // 解锁钱包
        cleosUtil.unlockWallet(configUtil.getFrameworkConfig().getAccount().getPrivateKey());
        // 分配代币
        eosUtil.setUpEOSToken(smartContract.getName());
    }

//    /**
//     * 初始化智能合约并分发测试使用的代币
//     */
//    public void initSmartContract() throws IOException, InterruptedException {
//        // 部署智能合约
//        cleosUtil.unlockWallet(configUtil.getFrameworkConfig().getAccount().getPrivateKey());
//        String contractDir = configUtil.getFuzzingConfig().getSmartContractDir() + "/" + smartContract.getName();
//        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
////        cleosUtil.createAccount(smartContract.getName(), configUtil.getAccountPublicKey());
//        cleosUtil.setContract(smartContract.getName(), contractDir);
////        cleosUtil.addCodePermission(smartContract.getName());
//        // 初始化EOS
//        startUpEOSToken();
//    }
//
//    /**
//     * 随机执行一个action
//     */
//    public void pushRandomAction() throws IOException, InterruptedException, AFLException {
//        Action actionToPush = getRandomAction();
//        if (actionToPush == null) {
//            return;
//        }
//        EOSUtil.CleosUtil cleosUtil = eosUtil.getCleosUtil();
//        ArgumentGenerator argumentGenerator = typeUtil.getGenerator(smartContract);
//        String args = argumentGenerator.generateFuzzingArguments(actionToPush);
//        cleosUtil.pushAction(
//                smartContract.getName(),
//                actionToPush.getName(),
//                args,
//                "eosio"
//        );
//    }

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
                jsonUtil.getJson(from, to, asset, memo),
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
            memo = memoStringPool.get(Math.abs(random.nextInt()) % memoStringPool.size());
            transfer(from, to, asset, memo);
            return action;
        }
        transfer(from, to, asset, memo);
        return action;
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
     * @return true if it can
     */
    public boolean canAcceptEOS() {
        // 测试是否能接收EOS
        try {
            fileUtil.rmLogFiles();
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
            String filepath = configUtil.getFrameworkConfig().getEosio().getOpFilepath();
            return checkUtil.checkFile(checkUtil.getAcceptEOSTokenCheckOperation(smartContract), filepath);
        } catch (IOException | InterruptedException | AFLException exception) {
            return false;
        }
    }
}
