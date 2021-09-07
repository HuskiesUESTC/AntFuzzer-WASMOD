package edu.uestc.antfuzzer.framework.bean;

import edu.uestc.antfuzzer.framework.enums.FuzzingStatus;
import lombok.Getter;
import lombok.Setter;

public class FuzzInfo {

    @Getter
    @Setter
    private byte[] mem;

    @Getter
    @Setter
    private FuzzingStatus status;

    @Getter
    @Setter
    private int id;

    public FuzzInfo(byte[] mem, FuzzingStatus status, int id) {
        this.mem = mem;
        this.status = status;
        this.id = id;
    }
}
