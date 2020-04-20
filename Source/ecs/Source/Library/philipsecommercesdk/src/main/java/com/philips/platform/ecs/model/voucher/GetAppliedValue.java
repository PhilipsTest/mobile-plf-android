/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.voucher;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetAppliedValue implements Serializable {

    private List<ECSVoucher> vouchers = new ArrayList<>();

    public void setVouchers(List<ECSVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    public List<ECSVoucher> getVouchers() {
        return vouchers;
    }
}
