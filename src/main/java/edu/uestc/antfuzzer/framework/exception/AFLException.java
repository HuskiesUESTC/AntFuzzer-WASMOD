package edu.uestc.antfuzzer.framework.exception;

import edu.uestc.antfuzzer.framework.enums.AFLExceptionStatus;
import lombok.Getter;

public class AFLException extends Exception {

    @Getter
    private final AFLExceptionStatus exceptionStatus;
    @Getter
    private String message;

    public AFLException(AFLExceptionStatus exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public AFLException(AFLExceptionStatus exceptionStatus, String message) {
        this.exceptionStatus = exceptionStatus;
        this.message = message;
    }
}