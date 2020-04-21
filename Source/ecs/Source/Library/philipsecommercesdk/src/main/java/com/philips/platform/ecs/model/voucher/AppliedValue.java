/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.voucher;

import java.io.Serializable;

public class AppliedValue implements Serializable {

    private static final long serialVersionUID = -2438759277104163832L;
    private String currencyIso;
    private String formattedValue;
    private String priceType;
    private String value;

    public String getCurrencyIso() {
        return currencyIso;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public String getPriceType() {
        return priceType;
    }

    public String getValue() {
        return value;
    }
}
