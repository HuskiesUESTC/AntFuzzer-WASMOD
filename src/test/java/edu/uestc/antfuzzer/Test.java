package edu.uestc.antfuzzer;


import java.io.File;

public class Test {
    public static void main(String[] args) {
        String filepath = "/Volumes/data/code/AntFuzzer/hhh/test/test.json";
        System.out.println(File.separator);
        String dir = filepath.substring(0, filepath.lastIndexOf(File.separator));
        System.out.println(dir);
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
