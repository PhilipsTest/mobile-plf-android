/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.voucher;

import java.io.Serializable;

/**
 * The type Ecs voucher contains the voucher details. This object is returned with updated value when user do applyVoucher,fetchAppliedVouchers,removeVoucher
 */
public class ECSVoucher implements Serializable {

    private static final long serialVersionUID = -7277844355281972422L;
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
    public void setCode(String code) {
        this.code = code;
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
    public void setAppliedValue(AppliedValue appliedValue) {
        this.appliedValue = appliedValue;
    }
}
