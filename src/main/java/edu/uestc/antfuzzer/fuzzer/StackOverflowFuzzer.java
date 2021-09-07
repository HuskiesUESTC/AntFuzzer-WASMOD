package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.result.ExecuteResult;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.exception.AFLException;
import edu.uestc.antfuzzer.framework.type.NameGenerator;
import edu.uestc.antfuzzer.framework.util.InstrumentUtil;
import edu.uestc.antfuzzer.framework.util.type.ArgumentGenerator;
import edu.uestc.antfuzzer.framework.util.type.TypeUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Fuzzer(vulnerability = "StackOverflow",
        fuzzScope = FuzzScope.all,
        iteration = 20,
        argDriver = ArgDriver.local,
        useAccountPool = true
)

public class StackOverflowFuzzer extends BaseFuzzer {
    @Autowired
    TypeUtil typeUtil;

    ArgumentGenerator argumentGenerator;
    private class CallFrame {
        long top;
        long bottom;
        int size;
        CallFrame(long t, long b, int s) {
            this.top = t;
            this.bottom = b;
            this.size = s;
        }
    }

    @Autowired
    private InstrumentUtil instrumentUtil;

    private boolean instrumentStatus = false;

    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        instrumentStatus = instrumentUtil.instrument(smartContract.getName());
        argumentGenerator = typeUtil.getGenerator(smartContract);
        initSmartContract();
        canAcceptEOS = canAcceptEOS();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException, AFLException {
        if (instrumentStatus) {
            clearLogFiles();
            if (canAcceptEOS) {
                cleosUtil.pushAction(
                        "eosio.token",
                        "transfer",
                        jsonUtil.getJson(
                                (String) "eosio",
                                (String) smartContract.getName(),
                                (String) argumentGenerator.generateSpecialTypeArgument("asset"),
                                (String) argumentGenerator.generateSpecialTypeArgument("string")
                        ),
                        "eosio"
                );
                ExecuteResult overflow = checkOverflow();
                if (overflow.getVulDetect()) {
                    environmentUtil.getActionFuzzingResult().getVulnerability().add("StackOverflow");
                    return FuzzingStatus.SUCCESS;
                }
                setResultRecord("transfer", "StackOverflow", overflow.getVulDetect());
            }

            clearLogFiles();
            NameGenerator nameGenerator = (NameGenerator) super.argumentGenerator.getTypeGeneratorCollection().get("name");
            cleosUtil.pushAction(
                    smartContract.getName(),
                    action,
                    arguments,
                    nameGenerator.generate(arguments));
            ExecuteResult overflow = checkOverflow();
            if (overflow.getVulDetect()) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("StackOverflow");
                return FuzzingStatus.SUCCESS;
            }
            return FuzzingStatus.NEXT;
        }
        return FuzzingStatus.SUCCESS;
    }

    private ExecuteResult checkOverflow() throws IOException {
        // 读取文件
        try {
            BufferedReader in = new BufferedReader(new FileReader("/root/.local/share/eosio/logfile.txt"));
            // 循环读取栈帧长度和拷贝长度
            String line = null;
            Pattern sizePattern = Pattern.compile("log_frame_size\\((\\d+)\\)");
            Pattern addrPattern = Pattern.compile("transform_addr\\((\\d+)\\)");
//            Pattern returnPattern = Pattern.compile("func_return\\((\\d+)\\)");
            Pattern lengthPattern = Pattern.compile("memcpy\\((\\d+), (\\d+), (\\d+)\\)");
            List<String> logFile = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                logFile.add(line);
            }

            // 收集每个栈的长度和地址区间
            List<CallFrame> frames = new ArrayList<>();
            for (int i = 0; i < logFile.size(); i++) {
                Matcher sizeMatcher = sizePattern.matcher(logFile.get(i));
                if (sizeMatcher.find()) {
                    int size = Integer.parseInt(sizeMatcher.group(1));
                    if (i < logFile.size() - 1) {
                        Matcher addrMatcher = addrPattern.matcher(logFile.get(i + 1));
                        if (addrMatcher.find()) {
                            long top = Long.parseLong(addrMatcher.group(1));
                            long bottom = top - size;
                            frames.add(new CallFrame(top, bottom, size));
                        }
                    }
                }
                Matcher lengthMatcher = lengthPattern.matcher(logFile.get(i));
                if (lengthMatcher.find()) {
                    long dst = Long.parseLong(lengthMatcher.group(1));
                    long src = Long.parseLong(lengthMatcher.group(2));
                    int len = Integer.parseInt(lengthMatcher.group(3));

                    for (CallFrame cf : frames) {
                        // 复制目标地址处于栈帧范围内
                        if (Math.abs(cf.top - dst) <= cf.size || Math.abs(cf.bottom - dst) <= cf.size) {
                            if (len > cf.size) {
                                successExecCount++;
                                successVulCount++;
                                return new ExecuteResult(true, true);
                            }
                        }
                    }
                }
            }
            successExecCount++;
            return new ExecuteResult(true, false);
        } catch (IOException e) {
            System.out.println("log file not exist");
            return new ExecuteResult(false, false);
        }
    }
}
