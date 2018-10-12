package com.philips.cdp.di.iap.response.voucher;

import java.io.Serializable;

public class Vouchers implements Serializable {

    private String code;
    private boolean freeShipping;
    private String value;
    private String valueFormatted;
    private String valueString;
    private String voucherCode;
    private AppliedValue appliedValue;


    public String getCode() {
        return code;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public String getValue() {
        return value;
    }

    public String getValueFormatted() {
        return valueFormatted;
    }

    public String getValueString() {
        return valueString;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public AppliedValue getAppliedValue() {
        return appliedValue;
    }
}
