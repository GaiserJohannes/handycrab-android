package de.dhbw.handycrab.backend;

import de.dhbw.handycrab.model.ErrorCode;

public class BackendConnectionException extends RuntimeException {

    private int errorCode;

    private int httpStatusCode;

    BackendConnectionException(int errorCode, int httpStatusCode){
        super();
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.values()[errorCode];
    }

    public String getMessage(){
        return  getErrorCode().toString();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
