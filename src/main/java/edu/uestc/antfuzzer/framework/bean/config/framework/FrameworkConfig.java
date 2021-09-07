package edu.uestc.antfuzzer.framework.bean.config.framework;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FrameworkConfig {
    // fuzzing 配置文件
    @SerializedName("fuzzing_config_file")
    private String fuzzingConfigFile;
    // 默认的输出文件名
    @SerializedName("default_output_file")
    private String defaultOutputFile;
    // 默认的智能合约文件夹名
    @SerializedName("default_smart_contract_dir")
    private String defaultSmartContractDir;
    // eosio.token 公钥
    @SerializedName("eos_token_public_key")
    private String eosTokenPublicKey;
    // 账户密钥
    private Account account;
    // nodeos启动脚本
    @SerializedName("nodeos_start_shell")
    private String nodeosStartShell;
    // 系统与代理合约
    @SerializedName("smart_contracts")
    private SmartContracts smartContracts;
    // eos相关文件
    private EOSIO eosio;
    // 日志目录
    @SerializedName("log_dir")
    private String logDir;


    @Data
    public class SmartContracts {
        private String atkforg;
        private String atknoti;
        private String atkrero;
        private String atkrb;
        @SerializedName("eosio_token")
        private String eosioToken;
    }

    @Data
    public class EOSIO {
        @SerializedName("coverage_filepath")
        private String coverageFilepath;
        @SerializedName("op_filepath")
        private String opFilepath;
    }

    @Data
    public class Account {
        @SerializedName("public_key")
        private String publicKey;
        @SerializedName("private_key")
        private String privateKey;
    }
}
