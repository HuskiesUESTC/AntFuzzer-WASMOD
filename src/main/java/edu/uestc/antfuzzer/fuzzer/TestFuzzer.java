package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.SmartContract;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.type.ArgumentGenerator;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.EOSUtil;
import edu.uestc.antfuzzer.framework.util.TypeUtil;

import java.io.IOException;

@Fuzzer(
        vulnerability = "Test",
        iteration = 10000,
        fuzzScope = FuzzScope.all,
        useAccountPool = true,
        argDriver = ArgDriver.afl
)
public class TestFuzzer {
    @Contract
    private SmartContract smartContract;
    @Autowired
    private TypeUtil typeUtil;
    @Autowired
    private EOSUtil eosUtil;

    private EOSUtil.CleosUtil cleosUtil;
    // 参数生成器
    private ArgumentGenerator argumentGenerator;

    @Before
    public void init() throws IOException, InterruptedException {
        argumentGenerator = typeUtil.getGenerator(smartContract);
        cleosUtil = eosUtil.getCleosUtil();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException {
        NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");
        cleosUtil.pushAction(
                smartContract.getName(),
                action,
                arguments,
                nameGenerator.generate(arguments));
        return FuzzingStatus.NEXT;
    }
}
