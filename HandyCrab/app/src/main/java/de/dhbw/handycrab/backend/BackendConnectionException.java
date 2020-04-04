package de.dhbw.handycrab.backend;

import de.dhbw.handycrab.model.ErrorCode;

public class BackendConnectionException extends RuntimeException {
    private int errorCode;

    BackendConnectionException(ErrorCode errorCode){
        super();
        this.errorCode = errorCode.ordinal();
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.values()[errorCode];
    }
}
