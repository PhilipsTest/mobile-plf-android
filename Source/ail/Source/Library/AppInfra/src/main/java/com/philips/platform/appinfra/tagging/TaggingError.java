package com.philips.platform.appinfra.tagging;

import androidx.annotation.NonNull;

public class TaggingError {
    private String errorType = null;
    private String serverName = null;
    private String errorCode = null;
    private String errorMsg = null;

    public TaggingError(@NonNull String errorType, @NonNull String serverName, @NonNull String errorCode, @NonNull String errorMsg) {
        this.errorType = errorType;
        this.serverName = serverName;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public TaggingError(@NonNull String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public TaggingError() {
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getServerName() {
        return serverName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
