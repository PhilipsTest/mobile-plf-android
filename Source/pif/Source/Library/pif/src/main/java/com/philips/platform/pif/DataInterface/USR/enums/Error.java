package com.philips.platform.pif.DataInterface.USR.enums;

//TODO: Shashi, Check with Deepthi about this error name
public class Error {
    private int errCode;
    private String errDesc;

    public enum UserDetailError {
        InvalidFields(1000, "INVALID FIELDS"),
        NotLoggedIn(1001, "USER NOT LOGGED IN");

        private int errorCode;
        private String errorMsg;

        UserDetailError(int errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }


    public Error(int errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    public Error(UserDetailError userDetailError){
        errCode = userDetailError.errorCode;
        errDesc = userDetailError.errorMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }
}
