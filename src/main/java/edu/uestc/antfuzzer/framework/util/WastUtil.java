package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WastUtil {

    @Autowired
    private PipeUtil pipeUtil;

    public List<String> getStringsInFunctionWithCertainParam(String wastFilePath, String param) throws IOException, InterruptedException {
        String wasmToWast = "eosio-wasm2wast %s";
        String command = String.format(wasmToWast, wastFilePath);
        List<String> wast = pipeUtil.executeWithResult(command);
        return getStringsInFunctionWithCertainParam(wast, param);
    }

    private List<String> getStringsInFunctionWithCertainParam(List<String> wast, String param) {
        Set<String> memoStringPoolNoDup = new HashSet<>();
        List<String> memoStringPool = new ArrayList<>();
        if (wast.size() == 0)
            return memoStringPool;
        Set<String> stringNumbers = new HashSet<>();
        int funStart = findFirstIndent(wast, 0, 2);
        while (funStart != -1) {
            String currentLine = wast.get(funStart);
            int funEnd = findFirstIndent(wast, funStart + 1, 2);
            if (currentLine.contains("func (;") && currentLine.contains(param)) {
                for (int i = funStart + 1; i < funEnd; ++i) {
                    String searchLine = wast.get(i);
                    if (searchLine.contains("i32.const")) {
                        int j = searchLine.length() - 1;
                        for (; j >= 0; j--) {
                            char ch = searchLine.charAt(j);
                            if (ch < '0' || ch > '9')
                                break;
                        }
                        stringNumbers.add(searchLine.substring(j + 1));
                    }

                }
            }
            funStart = findFirstIndent(wast, funStart + 1, 2);
        }
        for (String stringNumber : stringNumbers) {
            int lineNumber = findLineNumberBackward(wast, stringNumber);
            if (wast.get(lineNumber).contains("data (i32.const")) {
                String target = getOnlyString(wast, lineNumber);
                String candidateMemo = target.substring(0, target.length() - 2);
                if (candidateMemo.length() <= 40 && !memoStringPoolNoDup.contains(candidateMemo)) {
                    memoStringPoolNoDup.add(candidateMemo);
                    memoStringPool.add(candidateMemo);
                }
            }
        }
        return memoStringPool;
    }

    private int findFirstIndent(List<String> wast, int start, int space) {
        int size = wast.size();
        for (int i = start; i < size; i++) {
            String currentLine = wast.get(i);
            StringBuilder blanks = new StringBuilder();
            for (int j = 0; j < space; j++)
                blanks.append(" ");
            if (currentLine.contains(blanks) && currentLine.charAt(space) != ' ')
                return i;
        }
        return -1;
    }

    private int findLineNumberBackward(List<String> wast, String stringNumber) {
        int n = wast.size();
        for (int i = n - 1; i >= 0; i--) {
            String currentLine = wast.get(i);
            if (currentLine.contains(stringNumber))
                return i;
        }
        return -1;
    }

    private String getOnlyString(List<String> wast, int index) {
        String currentLine = wast.get(index);
        int start = currentLine.indexOf('"');
        int end = currentLine.lastIndexOf('"');
        if (start == end)
            return "";
        return wast.get(index).substring(start + 1, end - 1);
    }
}
