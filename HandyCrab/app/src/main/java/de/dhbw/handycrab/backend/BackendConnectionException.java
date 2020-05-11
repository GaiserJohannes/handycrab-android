package de.dhbw.handycrab.backend;

import android.content.Context;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.ErrorCode;

public class BackendConnectionException extends RuntimeException {

    private ErrorCode errorCode;

    private int httpStatusCode;

    public BackendConnectionException(ErrorCode errorCode, int httpStatusCode) {
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

    public String getDetailedMessage(Context context) {
        switch (errorCode) {
            case NO_CONNECTION_TO_SERVER:
                return context.getString(R.string.noConnectionToServerError);
            case INCOMPLETE:
                return context.getString(R.string.incompleteError);
            case UNAUTHORIZED:
                return context.getString(R.string.unauthorizedError);
            case EMAIL_ALREADY_ASSIGNED:
                return context.getString(R.string.emailAlreadyAssignedError);
            case USERNAME_ALREADY_ASSIGNED:
                return context.getString(R.string.usernameAlreadyAssignedError);
            case INVALID_EMAIL:
                return context.getString(R.string.invalidEmailError);
            case INVALID_LOGIN:
                return context.getString(R.string.invalidLoginError);
            case USER_NOT_FOUND:
                return context.getString(R.string.userNotFoundError);
            case INVALID_GEO_LOCATION:
                return context.getString(R.string.invalidGeoLocationError);
            case BARRIER_NOT_FOUND:
                return context.getString(R.string.barrierNotFoundError);
            case INVALID_USER_ID:
                return context.getString(R.string.invalidUserIdError);
            case SOLUTION_NOT_FOUND:
                return context.getString(R.string.solutionNotFoundError);
            case INVALID_USERNAME:
                return context.getString(R.string.invalidUsernameError);
            case INVALID_PASSWORD:
                return context.getString(R.string.invalidPasswordError);
            case PICTURE_TO_BIG:
                return context.getString(R.string.pictureToBig);
            case INVALID_PICTURE_FORMAT:
                return context.getString(R.string.invalidPictureFormat);
            case PICTURE_NOT_FOUND:
                return context.getString(R.string.pictureNotFound);
            case INVALID_JSON:
                return context.getString(R.string.invalidJson);
            case INVALID_OBJECT_ID:
                return context.getString(R.string.invalidObjectID);
            default:
                return context.getString(R.string.unknownError);
        }
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
