package edu.uestc.antfuzzer.framework.enums;

public enum FuzzingStatus {
    NEXT((byte) 0),
    TIMEOUT((byte) 1),
    CRASH((byte) 2),
    QUEUE_FULL((byte) 3),
    COMM_ERROR((byte) 4),
    DONE((byte) 5),
    MissingAuth((byte) 6),
    SUCCESS((byte) 7);

    private final byte value;

    FuzzingStatus(byte i) {
        this.value = i;
    }

    public byte getValue() {
        return value;
    }
}
