package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.bean.config.fuzzing.FuzzingConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InstrumentUtil {
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private PipeUtil pipeUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private SmartContractUtil smartContractUtil;

    @Getter
    @Setter
    private Wast wast;

    public String deCompileWasm(String contractName) throws IOException, InterruptedException {
        // 反编译wasm文件, 并返回反编译后的wast文件路径
        FuzzingConfig fuzzingConfig = configUtil.getFuzzingConfig();
        String wasmFilePath = fuzzingConfig.getSmartContractDir() + "/" + contractName + "/" + contractName + ".wasm";
        String wastFilePath = fuzzingConfig.getSmartContractDir() + "/" + contractName + "/" + contractName + ".wast";
        String command = String.format("/root/wabt/bin/wasm2wat --generate-names %s -o %s", wasmFilePath, wastFilePath);

        pipeUtil.execute(command);
        return wastFilePath;
    }

    public String reCompileWast(String contractName) throws IOException {
        FuzzingConfig fuzzingConfig = configUtil.getFuzzingConfig();

        // 重新编译wast文件, 并返回编译后的wasm文件路径
        String wasmFilePath = fuzzingConfig.getSmartContractDir() + "/" + contractName + "/" + contractName + ".wasm";
        String wastFilePath = fuzzingConfig.getSmartContractDir()  + "/" + contractName + "/" + contractName + ".wast";

        String command = String.format("/root/wabt/bin/wat2wasm %s -o %s", wastFilePath, wasmFilePath);
        pipeUtil.execute(command);
        return wasmFilePath;
    }

    public boolean instrument(String contractName) {
        try {
            // 反编译并读取wast文件
            String wastFilePath = deCompileWasm(contractName);

            // 读取wast文件到Wast对象
            BufferedReader in = new BufferedReader(new FileReader(wastFilePath));
            // 先将文件流读取到List
            List<String> wastStream = new ArrayList<>();
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                wastStream.add(s);
            }
            // 从List构造Wast对象
            wast = new Wast(wastStream);
            // 插装
            wastStream = wast.instrument();

            // 将修改后的文件重新写回wast文件
            BufferedWriter out = new BufferedWriter(new FileWriter(wastFilePath));
            for (String s : wastStream) {
                out.write(s);
                out.write("\n");
            }
            out.close();

            // 重新编译wast为wasm
            reCompileWast(contractName);
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private class Wast {
        List<String> imports;
        List<FuncBody> funcBodies;
        List<String> data;

        Wast() {
            imports = new ArrayList<>();
            funcBodies = new ArrayList<>();
            data = new ArrayList<>();
        }

        Wast(List<String> wast) {
            // 初始化
            imports = new ArrayList<>();
            funcBodies = new ArrayList<>();
            data = new ArrayList<>();

            // 找到第一个和最后一个函数的入口, 在此之前是声明区和导入区
            int firstFuncStart = findFirstFuncStart(wast);
            // 找到最后一个函数的入口, 在此之后是数据区
            int lastFuncEnd = findLastFuncEnd(wast);

            // 将导入区装入imports变量
            for (int i = 0; i < firstFuncStart; i++) {
                imports.add(wast.get(i));
            }

            // 分割函数体
            for (int fst = firstFuncStart, fed = fst; fed > 0 && fed < lastFuncEnd; fst = findFuncEntry(wast, fed + 1)) {
                fed = findFuncEntry(wast, fst + 1) - 1;
                if (fst > 0) {
                    FuncBody funcBody = new FuncBody();
                    funcBody.start = fst;
                    if (fed > 0) {
                        funcBody.end = fed;
                    } else {
                        funcBody.end = lastFuncEnd;
                        fed = lastFuncEnd;
                    }
                    for (int i = funcBody.start; i <= funcBody.end; i++) {
                        funcBody.body.add(wast.get(i));
                    }
                    funcBodies.add(funcBody);
                }
            }

            // 分割数据区
            int lastFunc = funcBodies.get(funcBodies.size() - 1).end;
            for (int i = lastFunc + 1; i < wast.size(); i++) {
                data.add(wast.get(i));
            }
        }

        public int findFirstFuncStart(List<String> wast) {
            Pattern funcPattern = Pattern.compile(" {2}\\(func \\$[a-zA-Z0-9]+[\\S\\s]*");
            for (int i = 0; i < wast.size() - 1; i++) {
                Matcher funcMatcher = funcPattern.matcher(wast.get(i));
                if (funcMatcher.matches()) {
                    return i;
                }
            }
            return -1;
        }

        public int findLastFuncEnd(List<String> wast) {
            // 从后向前找比较快
            for (int i = wast.size() - 1; i >= 0; i--) {
                if (wast.get(i).startsWith("  (table")) {
                    return i - 1;
                }
            }
            return -1;
        }

        public int findFuncEntry(List<String> wast, int start) {
            if (start < 0) {
                return -1;
            }
            // 找到函数入口
            Pattern funcPattern = Pattern.compile(" {2}\\(func \\$[a-zA-Z0-9]+[\\S\\s]*");
//            Pattern funcPattern = Pattern.compile(" {2}\\(func \\(;\\d+;\\)[\\S\\s]*");

            for (int i = start; i < wast.size(); i++) {
                Matcher funcMatcher = funcPattern.matcher(wast.get(i));
                if (funcMatcher.matches()) {
                    return i;
                }
            }
            return -1;
        }

        public void print() {
            String splitLine = "---------------------------";
            System.out.println("imports section");
            System.out.println(splitLine);
            for (String s : imports) {
                System.out.println(s);
            }

            System.out.println("function bodies");
            System.out.println(splitLine);
            for (FuncBody funcBody : funcBodies) {
                for (String s : funcBody.body) {
                    System.out.println(s);
                }
            }

            System.out.println("data section");
            System.out.println(splitLine);
            for (String s : data) {
                System.out.println(s);
            }
        }

        List<String> instrument() {
            instrumentImports();
            for (int i = 0; i < funcBodies.size(); i++) {
                instrumentFunction(i);
            }
            return merge();
        }

        private void instrumentFunction(int index) {
            FuncBody funcBody = funcBodies.get(index);

            // 如果一个函数要压栈, 那么只可能在最初的十行之内压栈
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < funcBody.body.size() && i < 10; i++) {
                // 合成为一个一个10行的字符串, 方便使用正则表达式
                sb.append(funcBody.body.get(i));
                sb.append(System.getProperty("line.separator"));
            }
            String code = sb.toString();
            Pattern framePattern = Pattern.compile(" {4}global.get (\\$[a-zA-Z0-9]+)\\n" +
                    " {4}i32.const (\\d+)\\n" +
                    " {4}i32.sub\\n");
            Matcher frameMatcher = framePattern.matcher(code);
            // 插入插装代码
            if (frameMatcher.find()) {
                String replacement = frameMatcher.group(0) +
                        "    i32.const " + frameMatcher.group(2) + "\n" +
                        "    call $env.log_frame_size" + "\n" +
                        "    global.get " + frameMatcher.group(1) + "\n" +
                        "    i32.const 0" + "\n" +
                        "    call $env.transform_addr" + "\n";
                replacement = Matcher.quoteReplacement(replacement);
                String result = frameMatcher.replaceFirst(replacement);

                // 将修改后的代码写回FuncBody函数
                // body成员使用的是链表, 在头部操作性能可接受
                // 移除前10个元素
                for (int i = 0; i < 10; i++) {
                    funcBody.body.remove(0);
                }

                // 插入12行(修改过后的代码为12行)
                String[] r = result.split("\\n");
                for (int i = r.length - 1; i >= 0; i--) {
                    funcBody.body.add(0, r[i]);
                }
            }

            // 抓取退栈代码
//            Pattern returnPattern = Pattern.compile(" {4}local.get (\\$[a-zA-Z0-9]+)\\n" +
//                    " {4}i32.const (\\d+)\\n" +
//                    " {4}i32.add\\n" +
//                    " {4}global.set (\\$[a-zA-Z0-9]+)");
//            sb = new StringBuilder();
//            for (int i = funcBody.body.size() - 4; i < funcBody.body.size() && i <= 4 && i >= 0; i++) {
//                sb.append(funcBody.body.get(i));
//            }

//            code = sb.toString();
//            Matcher returnMatcher = returnPattern.matcher(code);
//            if (returnMatcher.find()) {
//                String replacement =
//                        "    i32.const " + returnMatcher.group(2) + "\n" +
//                        "    call $env.func_return" + "\n" + returnMatcher.group(0);
//                replacement = Matcher.quoteReplacement(replacement);
//                String result = returnMatcher.replaceFirst(replacement);
//
//                // 将修改后的代码写回FuncBody函数
//                // body成员使用的是链表, 在头部操作性能可接受
//                // 移除前10个元素
//                for (int i = 0; i < 4; i++) {
//                    funcBody.body.remove(funcBody.body.size() - 1);
//                }
//
//                // 插入12行(修改过后的代码为12行)
//                String[] r = result.split("\\n");
//                for (String s : r) {
//                    funcBody.body.add(funcBody.body.size(), s);
//                }
//            }
        }

        private void instrumentImports() {
            // 处理imports部分
            // 将log_frame_size函数导入
            Pattern logFrameSizeTypePattern = Pattern.compile(" {2}\\(type (\\$[a-zA-Z0-9]+) \\(func \\(param i32\\)\\)\\)");
            Pattern transformAddrTypePattern = Pattern.compile(" {2}\\(type (\\$[a-zA-Z0-9]+) \\(func \\(param i32 i32\\)\\)\\)");
            int type = 0;
            // 导入log_frame_size
            boolean logFrameSizeImported = false;
            boolean transformAddrImported = false;
            for (String s : imports) {
                if (s.startsWith("  (type")) {
                    type++;
                }
                Matcher logFrameSizeTypeMatcher = logFrameSizeTypePattern.matcher(s);
                if (logFrameSizeTypeMatcher.find()) {
                    // 如果本身已经有该参数的函数
                    // 直接复用其类型即可
                    String log_frame_size = String.format("  (import \"env\" \"log_frame_size\" (func $env.log_frame_size (type %s)))", logFrameSizeTypeMatcher.group(1));
                    imports.add(log_frame_size);
                    logFrameSizeImported = true;
                    break;
                }
            }

            if (!logFrameSizeImported) {
                String funcType = String.format("  (type $t%d (func (param i32)))", type);
                imports.add(type + 1, funcType);
                String log_frame_size = String.format("  (import \"env\" \"log_frame_size\" (func $env.log_frame_size (type $%d)))", type);
                imports.add(log_frame_size);
            }

            type = 0;
            for (String s : imports) {
                if (s.startsWith("  (type")) {
                    type++;
                }
                Matcher transformAddrTypeMatcher = transformAddrTypePattern.matcher(s);
                if (transformAddrTypeMatcher.find()) {
                    String transform_addr = String.format("  (import \"env\" \"transform_addr\" (func $env.transform_addr (type %s)))", transformAddrTypeMatcher.group(1));
                    imports.add(transform_addr);
                    transformAddrImported = true;
                    break;
                }
            }

            if (!transformAddrImported) {
                String funcType = String.format("  (type $t%d (func (param i32 i32)))", type);
                imports.add(type + 1, funcType);
                String transform_addr = String.format("  (import \"env\" \"transform_addr\" (func $transform_addr (type $%d)))", type);
                imports.add(transform_addr);
            }

        }

        List<String> merge() {
            List<String> wast = new ArrayList<>();
            wast.addAll(imports);
            for (FuncBody fb : funcBodies) {
                wast.addAll(fb.body);
            }
            wast.addAll(data);
            return wast;
        }
    }

    private class FuncBody {
        int start;
        int end;
        List<String> body;

        FuncBody() {
            start = -1;
            end = -1;
            // 可能要在中间插入
            // 使用链表效率高
            body = new LinkedList<>();
        }
    }
}
