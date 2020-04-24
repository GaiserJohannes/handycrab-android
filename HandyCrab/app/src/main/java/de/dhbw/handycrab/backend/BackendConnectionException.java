package de.dhbw.handycrab.backend;

import android.content.Context;
import android.content.res.Resources;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.ErrorCode;

public class BackendConnectionException extends RuntimeException {

    private ErrorCode errorCode;

    private int httpStatusCode;

    BackendConnectionException(ErrorCode errorCode, int httpStatusCode){
        super();
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage(){
        return  getErrorCode().toString();
    }

    public String getDetailedMessage(Context context){
        switch (errorCode){
            case NO_CONNECTION_TO_SERVER: return context.getString(R.string.noConnectionToServerError);
            case INCOMPLIETE: return context.getString(R.string.incompleteError);
            case UNAUTHORIZED: return context.getString(R.string.unauthorized);
            case EMAIL_ALREADY_ASSIGNED: return context.getString(R.string.emailAlreadyAssignedError);
            case USERNAME_ALREADY_ASSIGNED: return context.getString(R.string.usernameAlreadyAssignedError);
            case INVALID_EMAIL: return context.getString(R.string.invalidEmailError);
            case INVALID_LOGIN: return context.getString(R.string.invalidLoginError);
            case USER_NOT_FOUND: return context.getString(R.string.userNotFound);
            case INVALID_GEO_LOCATION: return context.getString(R.string.invalidGeoLocation);
            case BARRIER_NOT_FOUND: return context.getString(R.string.barrierNotFound);
            case INVALID_USER_ID: return context.getString(R.string.invalidUserId);
            case SOLUTION_NOT_FOUND: return context.getString(R.string.solutionNotFound);
            case INVALID_USERNAME: return context.getString(R.string.invalidUsernameError);
            case INVALID_PASSWORD: return context.getString(R.string.invalidPasswordError);
            default: return context.getString(R.string.unknownError);
        }
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
