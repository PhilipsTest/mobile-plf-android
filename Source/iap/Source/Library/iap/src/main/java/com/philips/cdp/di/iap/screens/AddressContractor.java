package com.philips.cdp.di.iap.screens;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;

/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

public interface AddressContractor extends AddressFieldDecider{


    void setContinueButtonState(boolean state);
    void setContinueButtonText(String buttonText);

    String getContinueButtonText();

    boolean getCheckBoxState();

    void setBillingAddressFields(AddressFields addressFields);

    void setShippingAddressFields(AddressFields shippingAddressFields);

    View getShippingAddressView();

    View getBillingAddressView();

    Activity getActivityContext();

    FragmentActivity getFragmentActivity();

    AddressBillingView getDLSBillingAddress();

    void enableView(View view);

    void disableView(View view);

    void hideProgressbar();

    void showProgressbar();

    void showErrorMessage(Message msg);

    DeliveryModes getDeliveryModes();

    void addOrderSummaryFragment();

    void addPaymentSelectionFragment(Bundle bundle);


    AddressFields getBillingAddressFields();

    AddressFields getShippingAddressFields();

    boolean isShippingAddressFilled();
    boolean isBillingAddressFilled();
    boolean isAddressFilledFromDeliveryAddress();

    void setShippingAddressFilledStatus(boolean status);
    void setBillingAddressFilledStatus(boolean status);
    void setAddressFilledFromDeliveryAddressStatus(boolean status);

}
