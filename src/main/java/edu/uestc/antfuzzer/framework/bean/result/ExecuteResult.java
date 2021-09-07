package edu.uestc.antfuzzer.framework.bean.result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecuteResult {

    private boolean fuzzSuccess = false;
    private boolean vulDetect = false;

    public boolean getFuzzSuccess() { return fuzzSuccess;}

    public void setFuzzSuccess(boolean fuzzSuccess) {
        this.fuzzSuccess = fuzzSuccess;
    }

    public boolean getVulDetect() {
        return vulDetect;
    }

    public void setVulDetect(boolean vulDetect) {
        this.vulDetect = vulDetect;
    }
}
