/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import androidx.annotation.VisibleForTesting;

import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.payment.CardType;

import java.io.Serializable;

public class PaymentInfo  implements Serializable {


    private static final long serialVersionUID = 4215127411026286981L;
    private ECSAddress billingAddress;
    private String cardNumber;
    private CardType cardType;
    private boolean defaultPayment;
    private String expiryMonth;
    private String expiryYear;
    private boolean saved;
    private String accountHolderName;

    public ECSAddress getBillingAddress() {
        return billingAddress;
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }


    public boolean isDefaultPayment() {
        return defaultPayment;
    }


    public String getExpiryMonth() {
        return expiryMonth;
    }


    public String getExpiryYear() {
        return expiryYear;
    }

    public boolean isSaved() {
        return saved;
    }


    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    @VisibleForTesting
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    @VisibleForTesting
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    @VisibleForTesting
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    @VisibleForTesting
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

}
