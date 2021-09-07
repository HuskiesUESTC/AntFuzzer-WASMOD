package edu.uestc.antfuzzer.framework.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.bean.Type;
import edu.uestc.antfuzzer.framework.bean.abi.ABI;

import java.io.IOException;
import java.util.List;

@Component
public class SmartContractUtil {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private WastUtil wastUtil;

    public SmartContract loadSmartContract(String contractName) {
        String contractRoot = configUtil.getFuzzingConfig().getSmartContractDir();
        String abiFilePath = contractRoot + "/" + contractName + "/" + contractName + ".abi";
        String wasmFilePath = contractRoot + "/" + contractName + "/" + contractName + ".wasm";
        System.out.println(abiFilePath);
        SmartContract smartContract = null;
        try {
            // 创建ABI对象
            String abiData = fileUtil.read(abiFilePath);
            ABI abi = (new Gson()).fromJson(abiData, ABI.class);
            // 创建Type对象
            List<String> memoStringPool = wastUtil.getStringsInFunctionWithCertainParam(wasmFilePath, "(param i32 i64 i64 i32 i32)");
            Type type = new Type(memoStringPool);
            // 创建smartContract对象
            smartContract = new SmartContract(contractName, abi, type);
        } catch (IOException | JsonSyntaxException | InterruptedException e) {
            System.out.println(contractName + ":abi file invalid");
            e.printStackTrace();
        }
        return smartContract;
    }

}
