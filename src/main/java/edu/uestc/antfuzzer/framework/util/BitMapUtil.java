package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.enums.AFLExceptionStatus;
import edu.uestc.antfuzzer.framework.exception.AFLException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

@Component
public class BitMapUtil {

    public final int SIZE = 65536;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private LogUtil logUtil;

    private byte[] prevBitMap;
    private byte[] totalBitMap;

    public byte[] getBitMap() throws AFLException {
        String coverageFilePath = configUtil.getFrameworkConfig().getEosio().getCoverageFilepath();
        File coverageFile = new File(coverageFilePath);
        if (!coverageFile.exists()) {
            System.out.println(coverageFilePath + " 文件不存在!");
            return getPreviousBitMap();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(coverageFile));
            String line = null;
            if (totalBitMap == null)
                totalBitMap = new byte[SIZE];
            byte[] currentBitMap = new byte[SIZE];
            while ((line = reader.readLine()) != null && !line.trim().equalsIgnoreCase("")) {
                if (line.startsWith("Run") || line.startsWith("End"))
                    continue;
                int index = Integer.parseInt(line);
                currentBitMap[index] += 1;
                totalBitMap[index] = 1;
            }
            prevBitMap = currentBitMap;
            return currentBitMap;
        } catch (IOException exception) {
            logUtil.logException(exception);
            throw new AFLException(AFLExceptionStatus.COVERAGE_FILE_NOT_EXISTS);
        }
    }

    public byte[] getPreviousBitMap() {
        if (prevBitMap == null)
            prevBitMap = new byte[SIZE];
        return prevBitMap;
    }

    public int getCoverage() {
        if (totalBitMap == null)
            return 0;
        int i = 0;
        for (byte b : totalBitMap)
            if (b > 0) i++;
        return i;
    }

    public boolean clearBitMap() {
        totalBitMap = null;
        String coverageFilePath = configUtil.getFrameworkConfig().getEosio().getCoverageFilepath();
        File coverageFile = new File(coverageFilePath);
        return coverageFile.exists() && coverageFile.delete();
    }
}
