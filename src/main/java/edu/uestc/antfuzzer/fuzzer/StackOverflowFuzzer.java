package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.CheckUtil;
import edu.uestc.antfuzzer.framework.util.InstrumentUtil;
import edu.uestc.antfuzzer.framework.util.type.ArgumentGenerator;
import edu.uestc.antfuzzer.framework.util.type.TypeUtil;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Fuzzer(vulnerability = "StackOverflow",
        fuzzScope = FuzzScope.all,
        iteration = 100,
        argDriver = ArgDriver.local,
        useAccountPool = true
)
public class StackOverflowFuzzer extends BaseFuzzer {
    @Autowired
    TypeUtil typeUtil;

    ArgumentGenerator localArgumentGenerator;

    @Autowired
    private InstrumentUtil instrumentUtil;

    private boolean instrumentStatus = false;

    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        localArgumentGenerator = typeUtil.getGenerator(smartContract);
        instrumentStatus = instrumentUtil.instrument(smartContract.getName());
        canAcceptEOS = canAcceptEOS();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        if (instrumentStatus) {
            boolean isTransferAction = action.equalsIgnoreCase("transfer");

            if (isTransferAction && !canAcceptEOS) {
                return FuzzingStatus.SUCCESS;
            }

            NameGenerator nameGenerator = (NameGenerator) argumentGenerator.getTypeGeneratorCollection().get("name");
            String smartContractName = smartContract.getName();
            String accountName = nameGenerator.generate(arguments);
            boolean result = false;
            if (isTransferAction) {
                smartContractName = "eosio.token";
                accountName = "eosio";
                arguments = jsonUtil.getJson(
                        "eosio",
                        smartContract.getName(),
                        (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                        (String) argumentGenerator.generateSpecialTypeArgument("string")
                );
                cleosUtil.pushAction(smartContractName, action, arguments, accountName);
                result = checkUtil.checkFile(getCheckOperation(), "/root/.local/share/eosio/logfile.txt");
            } else {
                Action nextAction = null;
                List<Action> actions = smartContract.getAbi().getActions();
                for (Action act : actions) {
                    if (act.getName().equals(action)) {
                        nextAction = act;
                        break;
                    }
                }
                ArgumentGenerator.Arguments arg = localArgumentGenerator.generateAndStoreFuzzingArguments(nextAction);
                cleosUtil.pushAction(smartContractName, action, arg.getFuzzingArguments(), accountName);

                result = result || checkUtil.checkFile(getCheckOperation(), "/root/.local/share/eosio/logfile.txt");
            }
            if (result) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("StackOverflow");
                return FuzzingStatus.SUCCESS;
            }
            return FuzzingStatus.NEXT;
        }
        return FuzzingStatus.SUCCESS;
    }

    private CheckUtil.CheckOperation checkOperation;

    private CheckUtil.CheckOperation getCheckOperation() {
        if (checkOperation == null) {
            checkOperation = new CheckUtil.CheckOperation() {

                final Pattern sizePattern = Pattern.compile("log_frame_size\\((\\d+)\\)");
                final Pattern addrPattern = Pattern.compile("transform_addr\\((\\d+)\\)");
                final Pattern lengthPattern = Pattern.compile("memcpy\\((\\d+), (\\d+), (\\d+)\\)");

                @Override
                public boolean checkAllLines(BufferedReader reader, Object... args) throws IOException {
                    String line = null;
                    List<String> logs = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        logs.add(line);
                    }
                    // 收集每个栈的长度和地址区间
                    List<CallFrame> frames = new ArrayList<>();
                    for (int i = 0; i < logs.size(); i++) {
                        Matcher sizeMathcer = sizePattern.matcher(logs.get(i));
                        if (sizeMathcer.find()) {
                            int size = Integer.parseInt(sizeMathcer.group(1));
                            if (i < logs.size() - 1) {
                                Matcher addrMatcher = addrPattern.matcher(logs.get(i + 1));
                                if (addrMatcher.find()) {
                                    long top = Long.parseLong(addrMatcher.group(1));
                                    long bottom = top - size;
                                    frames.add(new CallFrame(top, bottom, size));
                                }
                            }
                        }
                        Matcher lengthMatcher = lengthPattern.matcher(logs.get(i));
                        if (lengthMatcher.find()) {
                            long dst = Long.parseLong(lengthMatcher.group(1));
                            long len = Integer.parseInt(lengthMatcher.group(3));

                            for (CallFrame cf : frames) {
                                // 复制目标地址处于栈帧范围内
                                if (Math.abs(cf.top - dst) <= 256 + cf.size || Math.abs(cf.bottom - dst) <= cf.size) {
                                    if (len > cf.size) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }

                @AllArgsConstructor
                class CallFrame {
                    private final long top;
                    private final long bottom;
                    private final int size;
                }
            };
        }
        return checkOperation;
    }

}
