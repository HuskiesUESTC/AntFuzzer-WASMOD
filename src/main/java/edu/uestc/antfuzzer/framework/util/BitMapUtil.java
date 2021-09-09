package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class BitMapUtil {

    public static final int SIZE = 65536;

    @Autowired
    private ConfigUtil configUtil;

    // 之前合约的代码覆盖率
    private BitMap prevBitMap;
    // 当前合约的整体代码覆盖率
    private BitMap totalBitMap;

    public BitMap getBitMap() {
        String coverageFilePath = configUtil.getFrameworkConfig().getEosio().getCoverageFilepath();
        File coverageFile = new File(coverageFilePath);
        if (!coverageFile.exists()) {
            System.out.println(coverageFilePath + " 文件不存在!");
            return getPreviousBitMap();
        }
        try {
            String line = null;
            if (totalBitMap == null) {
                totalBitMap = new BitMap(new byte[SIZE], new HashMap<>());
            }
            BufferedReader reader = new BufferedReader(new FileReader(coverageFile));
            BitMap curBitMap = new BitMap(new byte[SIZE], new HashMap<>());
            String contract = null;
            boolean isFirstIndex = false;
            int prevContractLocation = -1;
            int prevJointLocation = -1;
            while ((line = reader.readLine()) != null) {
                // 如果是合约名
                if (line.contains("Contract")) {
                    contract = line.split(":")[1];
                    curBitMap.allBitMaps.putIfAbsent(contract, new byte[SIZE]);
                    totalBitMap.allBitMaps.putIfAbsent(contract, new byte[SIZE]);
                    continue;
                }
                // 如果是时间
                if (line.contains("startTime")) {
                    isFirstIndex = true;
                    continue;
                }
                int curLocation = getIndex(Long.parseLong(line));

                if (contract != null) {

                    if (!isFirstIndex) {
                        int index = (curLocation ^ prevContractLocation) % SIZE;
                        curBitMap.allBitMaps.get(contract)[index]++;
                        totalBitMap.allBitMaps.get(contract)[index] = 1;
                    }
                    prevContractLocation = curLocation >> 1;
                    isFirstIndex = false;

                    int curJointLocation = getIndex(contract, Long.parseLong(line));
                    if (prevJointLocation != -1) {
                        int jointIndex = (curJointLocation ^ prevJointLocation) % SIZE;
                        curBitMap.jointBitMap[jointIndex]++;
                        totalBitMap.jointBitMap[jointIndex] = 1;
                    }
                    prevJointLocation = curJointLocation >> 1;
                }
            }
            reader.close();
            prevBitMap = curBitMap;
            return curBitMap;
        } catch (IOException e) {
            return getPreviousBitMap();
        }
    }

    public int getCoverage() {
        if (totalBitMap == null) {
            return 0;
        }
        return totalBitMap.getJointBitMapCount();
    }

    private int getIndex(long num) {
        Random random = new Random(num);
        return random.nextInt(SIZE);
    }

    private int getIndex(String contractName, long num) {
        char[] chs = contractName.toCharArray();
        for (char ch : chs) {
            num += ch;
        }
        return getIndex(num);
    }

    public BitMap getPreviousBitMap() {
        if (prevBitMap == null) {
            prevBitMap = new BitMap(new byte[SIZE], new HashMap<>());
        }
        return prevBitMap;
    }

    public BitMap getTotalBitMap() {
        if (totalBitMap == null) {
            return getPreviousBitMap();
        }
        return totalBitMap;
    }

    public void clearBitMapFile() {
        String coverageFilePath = configUtil.getFrameworkConfig().getEosio().getCoverageFilepath();
        File coverageFile = new File(coverageFilePath);
        if (coverageFile.exists()) {
            coverageFile.delete();
        }
    }

    public void clearBitMap() {
        totalBitMap = null;
        prevBitMap = null;
        clearBitMapFile();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BitMap {
        // 联合覆盖率
        private byte[] jointBitMap;
        // 不同合约各自的代码覆盖率
        private Map<String, byte[]> allBitMaps;

        /**
         * 获得合约的联合代码覆盖率
         */
        public int getJointBitMapCount() {
            return getBitMapCount(jointBitMap);
        }

        /**
         * 获得所有合约各自的代码覆盖率
         */
        public Map<String, Integer> getAllBitMapCounts() {
            Map<String, Integer> result = new HashMap<>();
            for (Map.Entry<String, byte[]> item : allBitMaps.entrySet()) {
                String contract = item.getKey();
                int count = getBitMapCount(item.getValue());
                result.put(contract, count);
            }
            return result;
        }

        private int getBitMapCount(byte[] bitmap) {
            int count = 0;
            for (int i = 0; i < BitMapUtil.SIZE; i++) {
                if (bitmap[i] > 0) {
                    count++;
                }
            }
            return count;
        }
    }
}
