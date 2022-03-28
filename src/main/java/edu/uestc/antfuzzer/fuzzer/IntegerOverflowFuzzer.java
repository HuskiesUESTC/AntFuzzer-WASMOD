package edu.uestc.antfuzzer.fuzzer;

import edu.uestc.antfuzzer.framework.annotation.*;
import edu.uestc.antfuzzer.framework.bean.abi.Action;
import edu.uestc.antfuzzer.framework.bean.result.ExecuteResult;
import edu.uestc.antfuzzer.framework.enums.ArgDriver;
import edu.uestc.antfuzzer.framework.enums.FuzzScope;
import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import edu.uestc.antfuzzer.framework.enums.ParamType;
import edu.uestc.antfuzzer.framework.util.type.ArgumentGenerator;
import edu.uestc.antfuzzer.framework.util.type.TypeUtil;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Fuzzer(vulnerability = "IntegerOverflow",
        fuzzScope = FuzzScope.all,
        iteration = 10,
        argDriver = ArgDriver.local,
        useAccountPool = true
)
public class IntegerOverflowFuzzer extends BaseFuzzer {
    @Autowired
    ArgumentGenerator argumentGenerator;
    @Autowired
    TypeUtil typeUtil;

    private class Unop<T> {
        public T value;
        public T res;
    }

    private class Binop<T> {
        public T lhs;
        public T rhs;
        public T res;

        Binop(T lhs, T rhs, T result) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.res = result;
        }
    }

    // 创建参数存储池
    List<BigInteger> uint32Pool = new ArrayList<>();
    List<BigInteger> int32Pool = new ArrayList<>();
    List<BigInteger> uint64Pool = new ArrayList<>();
    List<BigInteger> int64Pool = new ArrayList<>();
    List<BigInteger> assetPool = new ArrayList<>();

    @Before
    public void init() throws IOException, InterruptedException {
        initFuzzer();
        argumentGenerator = typeUtil.getGenerator(smartContract);
        canAcceptEOS = canAcceptEOS();
    }

    @Fuzz
    public FuzzingStatus fuzz(@Param(ParamType.Arguments) String arguments,
                              @Param(ParamType.Action) String action) throws IOException, InterruptedException {
        Action nextAction = getRandomAction();
        if (nextAction != null) {
            System.out.println(nextAction.getName());

            ArgumentGenerator.Arguments arg = argumentGenerator.generateAndStoreFuzzingArguments(nextAction);
            System.out.println(arg.getFuzzingArguments());

            extractIntegers(arg);
            fileUtil.rmLogFiles();
            cleosUtil.pushAction(
                    smartContract.getName(),
                    nextAction.getName(),
                    arg.getFuzzingArguments(),
                    (String) argumentGenerator.generateSpecialTypeArgument("name")
            );

            ExecuteResult checkResult = checkOverflow();
            if (checkResult.getVulDetect()) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("IntegerOverflow");
                environmentUtil.getActionFuzzingResult().getAction().add(arg.getFuzzingArguments());
                System.out.println(arg.getFuzzingArguments());
                return FuzzingStatus.SUCCESS;
            }
        }

        fileUtil.rmLogFiles();
        if (canAcceptEOS) {
            int64Pool.clear();
            int32Pool.clear();
            uint32Pool.clear();
            uint64Pool.clear();
            assetPool.clear();
            fileUtil.rmLogFiles();
            String asset = (String) argumentGenerator.generateSpecialTypeArgument("asset");
            String memo = (String) argumentGenerator.generateSpecialTypeArgument("string");
            Pattern assetPattern = Pattern.compile("(\\d+).(\\d+) (\\S+)");
            Matcher assetMatcher = assetPattern.matcher(asset);
            if (assetMatcher.find()) {
                BigInteger assetAmount;
                BigInteger intPart = new BigInteger(assetMatcher.group(1));
                BigInteger fracPart = new BigInteger(assetMatcher.group(2));
                assetAmount = intPart.multiply(BigInteger.valueOf(10000));
                assetAmount = assetAmount.add(fracPart);
                assetPool.add(assetAmount);
            }
            cleosUtil.pushAction(
                    "eosio.token",
                    "transfer",
                    jsonUtil.getJson(
                            "eosio",
                            smartContract.getName(),
                            asset,
                            memo
                    ),
                    "eosio"
            );
            ExecuteResult checkResult = checkOverflow();
            if (checkResult.getVulDetect()) {
                environmentUtil.getActionFuzzingResult().getVulnerability().add("IntegerOverflow");
                return FuzzingStatus.SUCCESS;
            }
        }
        return FuzzingStatus.NEXT;
    }

    private ExecuteResult checkOverflow() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileUtil.getOpFilepath()));
            List<String> op = new ArrayList<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                op.add(line);
            }
            for (int i = 0; i < op.size(); i++) {
                // 检查64位整数溢出情况
                if (op.get(i).contains("Add<uint64_t>")) {
                    Pattern opPattern = PatternGenerator.generatePattern(op.get(i));
                    Matcher opMatcher = opPattern.matcher(op.get(i));

                    if (opMatcher.matches()) {
                        BigInteger lhs = new BigInteger(opMatcher.group(1));
                        BigInteger rhs = new BigInteger(opMatcher.group(2));
                        BigInteger res = new BigInteger(opMatcher.group(3));

                        for (BigInteger integer : uint64Pool) {
                            if (lhs.equals(integer) || rhs.equals(integer)) {
                                OverflowChecker oc = new UnsignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(op.get(i));
                                    bf.write("\n");
                                    bf.close();
                                    boolean FP = false;
                                    if (!op.get(i).equals("Add<uint64_t>(18446744073709551615,18446744073709551611)(18446744073709551610)")) {
                                        return new ExecuteResult(true, true);
                                    }
                                }
                            }
                        }

                        for (BigInteger integer : int64Pool) {
                            if (lhs.equals(integer)) {
                                rhs = rhs.subtract(new BigInteger("18446744073709551616"));
                                res = res.subtract(new BigInteger("18446744073709551616"));

                                OverflowChecker oc = new SignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(lhs.toString());
                                    bf.write(" ");
                                    bf.write(rhs.toString());
                                    bf.write("\n");
                                    bf.close();
                                    return new ExecuteResult(true, true);
                                }
                            } else if (rhs.equals(integer)) {
                                lhs = rhs.subtract(new BigInteger("18446744073709551616"));
                                res = res.subtract(new BigInteger("18446744073709551616"));

                                OverflowChecker oc = new SignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(lhs.toString());
                                    bf.write(" ");
                                    bf.write(rhs.toString());
                                    bf.write("\n");
                                    bf.close();
                                    if (!op.get(i).equals("Add<uint64_t>(18446744073709551615,18446744073709551611)(18446744073709551610)")) {
                                        return new ExecuteResult(true, true);
                                    }
                                }
                            }
                        }
                    }
                }
                // 检查32位整数溢出情况
                else if (op.get(i).contains("Add<uint32_t>")) {
                    Pattern opPattern = PatternGenerator.generatePattern(op.get(i));
                    Matcher opMatcher = opPattern.matcher(op.get(i));

                    if (opMatcher.matches()) {
                        BigInteger lhs = new BigInteger(opMatcher.group(1));
                        BigInteger rhs = new BigInteger(opMatcher.group(2));
                        BigInteger res = new BigInteger(opMatcher.group(3));

                        for (BigInteger integer : uint32Pool) {
                            if (lhs.equals(integer) || rhs.equals(integer)) {
                                OverflowChecker oc = new UnsignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(op.get(i));
                                    bf.write("\n");
                                    bf.close();
                                    successVulCount++;
                                    successExecCount++;
                                    return new ExecuteResult(true, true);
                                }
                            }
                        }
                        for (BigInteger integer : int32Pool) {
                            if (lhs.equals(integer)) {
                                rhs = rhs.subtract(new BigInteger("4294967296"));
                                res = res.subtract(new BigInteger("4294967296"));

//                                result.add(res);

                                OverflowChecker oc = new SignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(op.get(i));
                                    bf.write("\n");
                                    bf.close();
                                    successVulCount++;
                                    successExecCount++;
                                    return new ExecuteResult(true, true);
                                }
                            } else if (rhs.equals(integer)) {
                                lhs = lhs.subtract(new BigInteger("4294967296"));
                                res = res.subtract(new BigInteger("4294967296"));

                                OverflowChecker oc = new SignedAddOverflowChecker();
                                if (oc.checkOverflow(op.get(i))) {
                                    BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                                    bf.write(op.get(i));
                                    bf.write("\n");
                                    bf.close();
                                    successVulCount++;
                                    successExecCount++;
                                    return new ExecuteResult(true, true);
                                }
                            }
                        }
                    }
                }

                else {
                    OverflowChecker oc = OverflowChecker.getUniqueChecker(op.get(i));
                    if (oc.checkOverflow(op.get(i))) {
                        BufferedWriter bf = new BufferedWriter(new FileWriter("overflow.txt", true));
                        bf.write(op.get(i));
                        bf.write("\n");
                        bf.close();
                        successVulCount++;
                        successExecCount++;
                        return new ExecuteResult(true, true);
                    }
                }
            }
        } catch (IOException e) {
            return new ExecuteResult(false, false);
        }
        successExecCount++;
        return new ExecuteResult(true, false);
    }

    static class PatternGenerator {
        public static String opType(String opString) {
            String[] ops = {"Add<uint64_t>", "Sub<uint64_t>", "Mul<uint64_t>", "Shl<uint64_t>",
                    "Add<uint32_t>", "Sub<uint32_t>", "Mul<uint32_t>", "Shl<uint32_t>",
                    /*"Add<int64_t>", "Sub<int64_t>", "Mul<int64_t>", "Shl<int64_t>",*/
                    /*"Add<int32_t>", "Sub<int32_t>", "Mul<int32_t>", "Shl<int32_t>"*/};
            for (String op : ops) {
                if (opString.startsWith(op)) {
                    return op;
                }
            }
            return "other op";
        }

        static Pattern generatePattern(String opString) {
            String ot = opType(opString);
            return Pattern.compile(ot + "\\((\\d+),(\\d+)\\)\\((\\d+)\\)");
        }
    }

    abstract static class OverflowChecker {
        abstract public boolean checkOverflow(String opString);
        public boolean checkOverflow(BigInteger lhs, BigInteger rhs, BigInteger res) {return false;}

        static OverflowChecker getUniqueChecker(String opType) {
            OverflowChecker oc = null;
            switch (opType) {
                case "Add<uint64_t>":
                case "Add<uint32_t>":
                    oc = new UnsignedAddOverflowChecker();
                    break;
//                case "Add<int64_t>":
//                case "Add<int32_t>":
//                    oc = new SignedAddOverflowChecker();
//                    break;
                case "Sub<uint64_t>":
                case "Sub<uint32_t>":
                    oc = new UnsignedSubOverflowChecker();
                    break;
//                case "Sub<int64_t>":
//                case "SUb<int32_t>":
//                    oc = new SignedSubOverflowChecker();
//                    break;
                case "Shl<uint64_t>":
                case "Shl<uint32_t>":
                    oc = new UnsignedShlOverflowChecker();
                    break;
//                case "Shl<int64_t>":
//                case "Shl<int32_t>":
//                    oc = new SignedShlOverflowChecker();
//                    break;
                default:
                    oc = new NoneOverflowChecker();
            }
            return oc;
        }

        static String opType(String opString) {
            return PatternGenerator.opType(opString);
        }
    }

    static class UnsignedAddOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                // System.out.println("uint add overflow");
                // System.out.printf("%s + %s = %s\n", lhs.toString(), rhs.toString(), res.toString());

                return res.compareTo(lhs) < 0 || res.compareTo(rhs) < 0;
            }
            return false;
        }
    }

    static class SignedAddOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                // System.out.println("int add overflow");
                // System.out.printf("%s + %s = %s\n", lhs.toString(), rhs.toString(), res.toString());

                BigInteger zero = BigInteger.valueOf(0);
                // 上溢出
                if (lhs.compareTo(zero) > 0 && rhs.compareTo(zero) > 0) {
                    return res.compareTo(lhs) < 0 || res.compareTo(rhs) < 0;
                }
                // 下溢出
                if (lhs.compareTo(zero) < 0 && rhs.compareTo(zero) < 0) {
                    return res.compareTo(lhs) > 0 && res.compareTo(rhs) > 0;
                }
            }
            return false;
        }

        @Override
        public boolean checkOverflow(BigInteger lhs, BigInteger rhs, BigInteger res) {
            BigInteger zero = BigInteger.valueOf(0);
            // 上溢出
            if (lhs.compareTo(zero) > 0 && rhs.compareTo(zero) > 0) {
                return res.compareTo(lhs) < 0 || res.compareTo(rhs) < 0;
            }
            // 下溢出
            if (lhs.compareTo(zero) < 0 && rhs.compareTo(zero) < 0) {
                return res.compareTo(lhs) > 0 && res.compareTo(rhs) > 0;
            }
            return false;
        }
    }

    static class UnsignedSubOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                // System.out.println("uint sub overflow");
                // System.out.printf("%s - %s = %s\n", lhs.toString(), rhs.toString(), res.toString());

                // 无符号减法下溢出
                return res.compareTo(lhs) > 0;
            }
            return false;
        }
    }

    static class SignedSubOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                // System.out.println("int sub overflow");
                // System.out.printf("%s - %s = %s\n", lhs.toString(), rhs.toString(), res.toString());

                BigInteger zero = BigInteger.valueOf(0);

                // a - b: a为正数 b为负数 产生上溢出
                if (lhs.compareTo(zero) >= 0 && rhs.compareTo(zero) < 0) {
                    return lhs.compareTo(res) < 0;
                }

                // a - b: a为负数 b为正数 产生下溢出
                if (lhs.compareTo(zero) < 0 && rhs.compareTo(zero) > 0) {
                    return res.compareTo(lhs) > 0;
                }
            }
            return false;
        }
    }

    static class UnsignedShlOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                // System.out.println("uint shl overflow");
                // System.out.printf("%s << %s = %s\n", lhs.toString(), rhs.toString(), res.toString());

                final BigInteger zero = BigInteger.valueOf(0);

                return res.compareTo(lhs) <= 0 && lhs.compareTo(zero) != 0 && rhs.compareTo(zero) != 0;
            }
            return false;
        }
    }

    static class SignedShlOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            Pattern opPattern = PatternGenerator.generatePattern(opString);
            Matcher opMatcher = opPattern.matcher(opString);
            if (opMatcher.matches()) {
                BigInteger lhs = new BigInteger(opMatcher.group(1));
                BigInteger rhs = new BigInteger(opMatcher.group(2));
                BigInteger res = new BigInteger(opMatcher.group(3));

                final BigInteger zero = BigInteger.valueOf(0);

                // 正数左移上溢出
                if (lhs.compareTo(zero) > 0) {
                    return res.compareTo(lhs) <= 0 && rhs.compareTo(zero) != 0;
                }
                // 负数左移下溢出
                if (lhs.compareTo(zero) < 0) {
                    return res.compareTo(lhs) >= 0 && rhs.compareTo(zero) != 0;
                }
            }
            return false;
        }
    }

    static class UnsignedMulOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            return false;
        }
    }

    static class SignedMulOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            return false;
        }
    }

    static class NoneOverflowChecker extends OverflowChecker {
        @Override
        public boolean checkOverflow(String opString) {
            return false;
        }
    }

    boolean usedSensitiveOp() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/root/.local/share/eosio/func.txt"));
            String[] sensitiveOperations = {"send_inline", "send_deferred", "db_store_i64", "db_update_i64",
                    "db_remove_i64"};

            String line = null;
            while ((line = reader.readLine()) != null) {
                for (String sensitiveOp : sensitiveOperations) {
                    if (line.contains(sensitiveOp)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void extractIntegers(ArgumentGenerator.Arguments arguments) {
        uint32Pool.clear();
        int32Pool.clear();
        uint64Pool.clear();
        int64Pool.clear();
        assetPool.clear();


        for (String s : arguments.getUint32()) {
            System.out.println(s);
            uint32Pool.add(new BigInteger(s));
        }

        for (String s : arguments.getUint64()) {
            System.out.println(s);
            uint64Pool.add(new BigInteger(s));
        }

        for (String s : arguments.getInt32()) {
            System.out.println(s);
            int32Pool.add(new BigInteger(s));
        }

        for (String s : arguments.getInt64()) {
            System.out.println(s);
            int64Pool.add(new BigInteger(s));
        }

        Pattern assetPattern = Pattern.compile("(\\d+).(\\d+) (\\S+)");
        for (String s : arguments.getAsset()) {
            System.out.println(s);
            Matcher assetMatcher = assetPattern.matcher(s);
            if (assetMatcher.find()) {
                BigInteger assetAmount;
                BigInteger intPart = new BigInteger(assetMatcher.group(1));
                BigInteger fracPart = new BigInteger(assetMatcher.group(2));
                assetAmount = intPart.multiply(BigInteger.valueOf(10000));
                assetAmount = assetAmount.add(fracPart);
                assetPool.add(assetAmount);
            }
        }
    }

    private Binop<BigInteger> resolve(String opString) {
        Pattern opPattern = PatternGenerator.generatePattern(opString);
        Matcher opMatcher = opPattern.matcher(opString);
        if (opMatcher.matches()) {
            BigInteger lhs = new BigInteger(opMatcher.group(1));
            BigInteger rhs = new BigInteger(opMatcher.group(2));
            BigInteger res = new BigInteger(opMatcher.group(3));
            return new Binop<>(lhs, rhs, res);
        }
        return null;
    }
}
