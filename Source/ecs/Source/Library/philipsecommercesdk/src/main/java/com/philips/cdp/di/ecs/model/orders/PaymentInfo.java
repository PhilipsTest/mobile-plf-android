package com.philips.cdp.di.ecs.model.orders;

import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.payment.CardType;

import java.io.Serializable;

public class PaymentInfo  implements Serializable {


    private ECSAddress billingAddress;
    private String cardNumber;
    private CardType cardType;
    private boolean defaultPayment;
    private String expiryMonth;
    private String expiryYear;
    private boolean saved;

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


}
