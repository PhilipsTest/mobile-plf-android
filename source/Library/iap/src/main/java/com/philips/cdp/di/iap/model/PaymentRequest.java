package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.core.StoreSpec;
import com.philips.cdp.di.iap.response.payment.MakePaymentData;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.utils.ModelConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class PaymentRequest extends AbstractModel {

    public PaymentRequest(final StoreSpec store, final Map<String, String> query, final DataLoadListener listener) {
        super(store, query, listener);
    }

    @Override
    public Object parseResponse(final Object response) {
        return new Gson().fromJson(response.toString(), MakePaymentData.class);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public Map<String, String> requestBody() {
        AddressFields billingAddress = CartModelContainer.getInstance().getBillingAddress();

        Map<String, String> params = new HashMap<>();
        if (!CartModelContainer.getInstance().isSwitchToBillingAddress()) {
            params.put(ModelConstants.ADDRESS_ID, CartModelContainer.getInstance().getAddressId());
            setBillingAddressParams(billingAddress, params);
        } else
            setBillingAddressParams(billingAddress, params);

        return params;
    }

    private void setBillingAddressParams(AddressFields billingAddress, Map<String, String> params) {
        params.put(ModelConstants.FIRST_NAME, billingAddress.getFirstName());
        params.put(ModelConstants.LAST_NAME, billingAddress.getLastName());
        params.put(ModelConstants.TITLE_CODE, billingAddress.getTitleCode().toLowerCase(Locale.getDefault()));
        params.put(ModelConstants.COUNTRY_ISOCODE, billingAddress.getCountryIsocode());
        if (HybrisDelegate.getInstance().getStore().getCountry().equalsIgnoreCase("US")) {
            params.put(ModelConstants.REGION_ISOCODE, CartModelContainer.getInstance().getRegionIsoCode());
        }
        params.put(ModelConstants.LINE_1, billingAddress.getLine1());
        params.put(ModelConstants.LINE_2, billingAddress.getLine2());
        params.put(ModelConstants.POSTAL_CODE, billingAddress.getPostalCode());
        params.put(ModelConstants.TOWN, billingAddress.getTown());
        params.put(ModelConstants.PHONE_1, billingAddress.getPhoneNumber());
        params.put(ModelConstants.PHONE_2, "");
    }

    @Override
    public String getUrl() {
        return store.getSetPaymentUrl(params.get(ModelConstants.ORDER_NUMBER));
    }
}
