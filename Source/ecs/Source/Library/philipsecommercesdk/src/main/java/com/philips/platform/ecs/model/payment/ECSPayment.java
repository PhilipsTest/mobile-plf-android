/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.payment;

import androidx.annotation.VisibleForTesting;

import com.philips.platform.ecs.model.address.ECSAddress;

import java.io.Serializable;

/**
 * The type Ecs payment contains all the payment details including address which is a billing address during payment.
 * This object is returned when fetchPaymentsDetails and makePayment is called
 */
public class ECSPayment implements Serializable {

    private static final long serialVersionUID = 1083630169028052247L;
    private String accountHolderName;
    private ECSAddress billingAddress;

    @VisibleForTesting
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    private String cardNumber;
    private CardType cardType;
    private boolean defaultPayment;

    @VisibleForTesting
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    @VisibleForTesting
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    private String expiryMonth;
    private String expiryYear;
    private String id;
    private boolean saved;
    private String subscriptionId;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setBillingAddress(ECSAddress billingAddress) {
        this.billingAddress = billingAddress;
    }
    public ECSAddress getBillingAddress() {
        return billingAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
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

    public String getId() {
        return id;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
