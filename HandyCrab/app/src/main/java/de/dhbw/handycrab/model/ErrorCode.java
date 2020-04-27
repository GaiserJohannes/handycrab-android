package de.dhbw.handycrab.model;

public enum ErrorCode {
    //can rise at every request
    UNKNOWN_ERROR,
    //rise only after incoming response
    INCOMPLETE,
    UNAUTHORIZED,
    EMAIL_ALREADY_ASSIGNED,
    USERNAME_ALREADY_ASSIGNED,
    INVALID_EMAIL,
    INVALID_LOGIN,
    USER_NOT_FOUND,
    INVALID_GEO_LOCATION,
    BARRIER_NOT_FOUND,
    INVALID_USER_ID,
    SOLUTION_NOT_FOUND,
    INVALID_USERNAME,
    INVALID_PASSWORD,
    //can rise at every request
    NO_CONNECTION_TO_SERVER
}
