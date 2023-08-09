package com.vale.valechat.common;

/**
 * Error code
 */
public enum ErrorCode {
    SUCCESS(200, "OK", ""),
    PARAMS_ERROR(400, "Request parameter error", ""),
    NULL_ERROR(404, "Request data is empty", ""),
    NOT_LOGIN(405, "Not logged in", ""),
    NO_AUTH(401,"No permission", ""),
    TOO_MANY_REQUEST(429, "请求过于频繁",""),
    SYSTEM_ERROR(500, "Internal exception of the system", ""),
    OPERATION_ERROR(501, "Operation failed", "");
//    SUCCESS(200, "OK", ""),
//    PARAMS_ERROR(40000, "Request parameter error", ""),
//    NULL_ERROR(40001, "Request data is empty", ""),
//    NOT_LOGIN(40100, "Not logged in", ""),
//    NO_AUTH(40101,"No permission", ""),
//    SYSTEM_ERROR(50000, "Internal exception of the system", ""),
//    OPERATION_ERROR(50001, "Operation failed", "");


    private final int code;

    /**
     * Status code information
     */
    private final String message;
    /**
     * Status code description (details)
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
