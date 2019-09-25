/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.model.payment;

import java.io.Serializable;
import java.util.List;

public class PaymentMethods implements Serializable {
    private List<ECSPayment> payments;

    public List<ECSPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<ECSPayment> payments) {
        this.payments = payments;
    }
}
