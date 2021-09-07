package edu.uestc.antfuzzer.framework.util;

import edu.uestc.antfuzzer.framework.annotation.Autowired;
import edu.uestc.antfuzzer.framework.annotation.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PipeUtil {

    @Autowired
    private ThreadUtil threadUtil;

    public List<String> executeWithResult(String command) throws InterruptedException, IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        process.waitFor();
        reader.close();
        process.destroy();
        return result;
    }

    public void execute(String command, boolean verbose) throws IOException {
        if (verbose)
            System.out.println(command);
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        new DealProcessStream(process.getInputStream()).run();
        new DealProcessStream(process.getErrorStream()).run();
        process.destroy();
    }

    public void execute(String command) throws IOException {
        execute(command, false);
    }

    public void waitForExecute(String command) throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();
        while (true) {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 10000)
                return;
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = errorReader.readLine();
            process.waitFor();
            process.destroy();
            threadUtil.waitFor(400);
            if (line.contains("Error")) {
                return;
            }
        }
    }

    private static class DealProcessStream extends Thread {

        private final InputStream inputStream;

        public DealProcessStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                if (builder.length() > 0)
                    System.out.println(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
