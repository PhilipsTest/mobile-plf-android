package com.philips.platform.appinfra.tagging;

public enum ErrorCategory {

    TECHNICAL_ERROR("TechnicalError"),

    USER_ERROR("UserError"),

    INFORMATIONAL_ERROR("InformationalError");

    private String mValue;

    ErrorCategory(final String value) {
        mValue = value;
    }


    public String getValue() {
        return mValue;
    }
}
