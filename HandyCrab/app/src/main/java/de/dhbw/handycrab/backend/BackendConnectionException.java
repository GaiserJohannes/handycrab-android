package de.dhbw.handycrab.backend;

import de.dhbw.handycrab.model.ErrorCode;

public class BackendConnectionException extends RuntimeException {

    private ErrorCode errorCode;

    private int httpStatusCode;

    BackendConnectionException(ErrorCode errorCode, int httpStatusCode) {
        super();
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return getErrorCode().toString();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
