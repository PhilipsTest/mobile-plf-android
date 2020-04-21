
/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.ecs.demouapp.ui.screens;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.Constants.AddressFieldJsonEnum;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.controller.PaymentController;
import com.ecs.demouapp.ui.model.AddressFieldEnabler;
import com.ecs.demouapp.ui.model.SalutationEnum;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.ModelConstants;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddressPresenter implements AddressController.AddressListener, PaymentController.PaymentListener {

    private final AddressContractor addressContractor;

    protected AddressController mAddressController;
    protected PaymentController mPaymentController;

    private final PhoneNumberUtil phoneNumberUtil;


    public AddressPresenter(AddressContractor addressContractor) {

        this.addressContractor = addressContractor;
        mAddressController = new AddressController(addressContractor.getActivityContext(), this);
        mPaymentController = new PaymentController(addressContractor.getActivityContext(), this);
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public AddressContractor getAddressContractor() {
        return addressContractor;
    }

    public void setContinueButtonState(boolean b) {
        addressContractor.setContinueButtonState(b);
    }

    public void setBillingAddressFields(AddressFields addressFields) {
        addressContractor.setBillingAddressFields(addressFields);
    }

    @Override
    public void onGetRegions(Message msg) {
        if(msg.obj instanceof Exception){
            CartModelContainer.getInstance().setRegionList(null);
        }else if (msg.obj instanceof IAPNetworkError) {
            CartModelContainer.getInstance().setRegionList(null);
        } else if (msg.obj instanceof List) {
            List list = (List)msg.obj;
            if(list != null && !list.isEmpty() && list.get(0) instanceof ECSRegion){
                CartModelContainer.getInstance().setRegionList(list);
            }else{
                CartModelContainer.getInstance().setRegionList(null);
            }
        } else {
            CartModelContainer.getInstance().setRegionList(null);
        }
    }

    @Override
    public void onGetUser(Message msg) {

    }

    @Override
    public void onCreateAddress(Message msg) {
        if (msg.obj instanceof ECSAddress) {
            ECSAddress mAddresses = (ECSAddress) msg.obj;
            CartModelContainer.getInstance().setAddressId(mAddresses.getId());
            ArrayList<String> userDataMap = new ArrayList<>();

          /*  userDataMap.add(UserDetailConstants.GIVEN_NAME);
            userDataMap.add(UserDetailConstants.FAMILY_NAME);*/
            userDataMap.add(UserDetailConstants.EMAIL);
            HashMap<String, Object> userDetails = null;
            try{
                userDetails =   ECSUtility.getInstance().getUserDataInterface().getUserDetails(userDataMap);
            } catch (UserDataInterfaceException e) {
                e.printStackTrace();
            }
           // HashMap<String, Object> userDetails=null;
            String email=  (String) userDetails.get(UserDetailConstants.EMAIL);
            CartModelContainer.getInstance().setShippingAddressFields(Utility.prepareAddressFields(mAddresses,email));
            setDeliveryAddress(mAddresses);
            //Track new address creation
            ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                    ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.NEW_SHIPPING_ADDRESS_ADDED);
        } else if (msg.obj instanceof String) {
            addressContractor.hideProgressbar();
            //addressContractor.showErrorMessage(msg);
            ECSUtility.showECSAlertDialog(getAddressContractor().getActivityContext(),"ECS Error",(String)msg.obj);
        }
        addressContractor.hideProgressbar();
    }

    @Override
    public void onGetAddress(Message msg) {

        if (msg.what == RequestCode.UPDATE_ADDRESS) {
            if (msg.obj instanceof IAPNetworkError) {
                addressContractor.showErrorMessage(msg);
            }else if(msg.obj instanceof Exception){
                //addressContractor.showErrorMessage(msg);
                addressContractor.hideProgressbar();
                ECSUtility.showECSAlertDialog(addressContractor.getActivityContext(),"Error",((Exception) msg.obj));

            } else{
                if (addressContractor.getContinueButtonText().equalsIgnoreCase(addressContractor.getActivityContext().getString(R.string.iap_save))) {
                    addressContractor.hideProgressbar();
                    addressContractor.getFragmentActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    ECSAddress addresses = new ECSAddress();
                    addresses.setId(CartModelContainer.getInstance().getAddressId());
                    setDeliveryAddress(addresses);
                }
            }
        }
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
        addressContractor.hideProgressbar();
        if (msg.obj instanceof Boolean ) { // success
            ECSDeliveryMode deliveryMode = addressContractor.getDeliveryModes();
            if (deliveryMode == null)
                getDeliveryModes();
            else
                mPaymentController.getPaymentDetails();
        }else if(msg.obj instanceof String){
            ECSLog.d(ECSLog.LOG, msg.getData().toString());
            ECSUtility.showECSAlertDialog(addressContractor.getActivityContext(),"ECS Error",(String) msg.obj);
        } else{
            ECSLog.d(ECSLog.LOG, msg.getData().toString());
        }
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        handleDeliveryMode(msg, mAddressController);
    }

    @Override
    public void onSetDeliveryMode(Message msg) {

        if (msg.obj instanceof Boolean  && (Boolean)msg.obj){
            if (CartModelContainer.getInstance().getBillingAddress() == null) {
                mPaymentController.getPaymentDetails();
            }else {
                setBillingAddressAndOpenOrderSummary();
            }
        } else {
            addressContractor.hideProgressbar();
            NetworkUtility.getInstance().showErrorMessage(msg, addressContractor.getFragmentActivity().getSupportFragmentManager(), addressContractor.getActivityContext());
        }
    }

    public void updateAddress(AddressFields addressFields, String addressID) {
        mAddressController.updateAddress(addressFields, addressID);
    }

    public void createAddress(AddressFields shippingAddressFields) {
       mAddressController.createAddress(shippingAddressFields);
    }

    public void setDeliveryAddress(ECSAddress address) {
        mAddressController.setDeliveryAddress(address);
    }

    public void getDeliveryModes() {
        mAddressController.getDeliveryModes();
    }

    public void handleDeliveryMode(Message msg, AddressController addressController) {
        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
            addressContractor.hideProgressbar();
        } else if ((msg.obj instanceof IAPNetworkError)) {
            ECSUtility.showECSAlertDialog(addressContractor.getActivityContext(),"Error",((IAPNetworkError) msg.obj).getMessage());
            addressContractor.hideProgressbar();
        } else if ((msg.obj instanceof Exception)) {
            Exception exception = (Exception)msg.obj;
            ECSUtility.showECSAlertDialog(addressContractor.getActivityContext(),"Error",exception);
            addressContractor.hideProgressbar();
        } else if ((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList =( List<ECSDeliveryMode>) msg.obj;
            if (deliveryModeList.size() > 0) {
                CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
                addressController.setDeliveryMode(deliveryModeList.get(0));
            }
        }
    }

    @Override
    public void onGetPaymentDetails(Message msg) {
        addressContractor.hideProgressbar();


        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {

            CartModelContainer.getInstance().setShippingAddressFields(addressContractor.getShippingAddressFields());
            addressContractor.enableView(addressContractor.getBillingAddressView());
            addressContractor.disableView(addressContractor.getShippingAddressView());

        } else if ((msg.obj instanceof Exception)) {
            ECSUtility.showECSAlertDialog(addressContractor.getActivityContext(),"Error",((Exception) msg.obj));
        } else if ((msg.obj instanceof List)) {
            //Track new address creation
            ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                    ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.NEW_SHIPPING_ADDRESS_ADDED);
            List<ECSPayment> mPaymentMethodsList = (List<ECSPayment>) msg.obj;
            CartModelContainer.getInstance().setShippingAddressFields(addressContractor.getShippingAddressFields());
            Bundle bundle = new Bundle();
            bundle.putSerializable(ECSConstant.PAYMENT_METHOD_LIST, (Serializable) mPaymentMethodsList);
            addressContractor.hideProgressbar();
            addressContractor.addPaymentSelectionFragment(bundle);

        }
    }

    @Override
    public void onSetPaymentDetails(Message msg) {

    }

    public void setBillingAddressAndOpenOrderSummary() {
        if (addressContractor.getShippingAddressFields() != null)
            CartModelContainer.getInstance().setShippingAddressFields(addressContractor.getShippingAddressFields());
        if (addressContractor.getBillingAddressFields() != null)
            CartModelContainer.getInstance().setBillingAddress(addressContractor.getBillingAddressFields());
        addressContractor.hideProgressbar();
        addressContractor.addOrderSummaryFragment();
    }

    HashMap<String, String> addressPayload(AddressFields pAddressFields) {
        HashMap<String, String> mShippingAddressHashMap = new HashMap<>();
        if (pAddressFields.getFirstName() != null) {
            mShippingAddressHashMap.put(ModelConstants.FIRST_NAME, pAddressFields.getFirstName());
        }
        if (pAddressFields.getLastName() != null) {
            mShippingAddressHashMap.put(ModelConstants.LAST_NAME, pAddressFields.getLastName());
        }
        if (pAddressFields.getLine1() != null) {
            mShippingAddressHashMap.put(ModelConstants.LINE_1, pAddressFields.getLine1());
        }
        if (pAddressFields.getHouseNumber() != null) {
            mShippingAddressHashMap.put(ModelConstants.HOUSE_NO, pAddressFields.getHouseNumber());
        }
        if (pAddressFields.getTitleCode() != null) {
            mShippingAddressHashMap.put(ModelConstants.TITLE_CODE, pAddressFields.getTitleCode().toLowerCase(Locale.getDefault()));
        }
        if (pAddressFields.getCountryIsocode() != null) {
            mShippingAddressHashMap.put(ModelConstants.COUNTRY_ISOCODE, pAddressFields.getCountryIsocode());
        }
        if (pAddressFields.getPostalCode() != null) {
            mShippingAddressHashMap.put(ModelConstants.POSTAL_CODE, pAddressFields.getPostalCode().trim());
        }
        if (pAddressFields.getTown() != null) {
            mShippingAddressHashMap.put(ModelConstants.TOWN, pAddressFields.getTown());
        }
        final String addressId = CartModelContainer.getInstance().getAddressId();
        if (addressId != null) {
            mShippingAddressHashMap.put(ModelConstants.ADDRESS_ID, addressId);
        }
        if (pAddressFields.getPhone1() != null) {
            mShippingAddressHashMap.put(ModelConstants.PHONE_1, pAddressFields.getPhone1().replaceAll(" ", ""));
        }
        if (pAddressFields.getPhone1() != null) {
            mShippingAddressHashMap.put(ModelConstants.PHONE_2, pAddressFields.getPhone1().replaceAll(" ", ""));
        }
        if (pAddressFields.getEmail() != null) {
            mShippingAddressHashMap.put(ModelConstants.EMAIL_ADDRESS, pAddressFields.getEmail());
        }
        if (!CartModelContainer.getInstance().isAddessStateVisible()) {
            mShippingAddressHashMap.put(ModelConstants.REGION_ISOCODE, null);
        } else {
            mShippingAddressHashMap.put(ModelConstants.REGION_ISOCODE, pAddressFields.getRegionIsoCode());
        }

        return mShippingAddressHashMap;
    }

    //This method can be further refactored as this is not testableF
    boolean validatePhoneNumber(EditText editText, String country, String number) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(number, country);
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            String formattedPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            editText.setText(formattedPhoneNumber);
            editText.setSelection(editText.getText().length());
            return isValid;
        } catch (Exception e) {
            ECSLog.d("ShippingAddressFragment", "NumberParseException");
        }
        return false;
    }

    String addressWithNewLineIfNull(String code) {
        if (!TextUtils.isEmpty(code)) {
            return code.replaceAll("null", " ").trim();
        }
        return null;
    }

    AddressFieldEnabler getAddressFieldEnabler(String country) {

        String addressFieldEnablerJson = null;
        AddressFieldEnabler addressFieldEnabler = null;
        try {
            addressFieldEnablerJson = getAddressFieldEnablerJson();

            JSONObject jsonObject = null;

            jsonObject = new JSONObject(addressFieldEnablerJson);

            JSONObject addressEnablerJsonObject = jsonObject.getJSONObject("excludedFields");
            addressFieldEnabler = new AddressFieldEnabler();

            JSONArray jsonArray = addressEnablerJsonObject.getJSONArray(country);


            for (int i = 0; i < jsonArray.length(); i++) {
                final String excludedField = jsonArray.getString(i);
                AddressFieldJsonEnum addressFieldJsonEnum = AddressFieldJsonEnum.getAddressFieldJsonEnumFromField(excludedField);
                setAddressFieldEnabler(addressFieldEnabler, addressFieldJsonEnum);
            }

        } catch (JSONException e) {

        } catch (IOException e) {

        }

        return addressFieldEnabler;
    }

    private String getAddressFieldEnablerJson() throws IOException {

        AssetManager manager = addressContractor.getActivityContext().getAssets();
        InputStream file = manager.open("addressFieldConfiguration.json");
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    private void setAddressFieldEnabler(AddressFieldEnabler addressFieldEnabler, AddressFieldJsonEnum field) {

        switch (field) {

            case ADDRESS_ONE:
                addressFieldEnabler.setAddress1Enabled(false);
                break;

            case ADDRESS_TWO:
                addressFieldEnabler.setAddress2Enabled(false);
                break;
            case EMAIL:
                addressFieldEnabler.setEmailEnabled(false);
                break;
            case PHONE:
                addressFieldEnabler.setPhoneEnabled(false);
                break;
            case FIRST_NAME:
                addressFieldEnabler.setFirstNameEnabled(false);
                break;
            case LAST_NAME:
                addressFieldEnabler.setLastNmeEnabled(false);
                break;
            case STATE:
                addressFieldEnabler.setStateEnabled(false);
                break;
            case SALUTATION:
                addressFieldEnabler.setSalutationEnabled(false);
                break;
            case COUNTRY:
                addressFieldEnabler.setCountryEnabled(false);
                break;
            case POSTAL_CODE:
                addressFieldEnabler.setPostalCodeEnabled(false);
                break;
            case HOUSE_NUMBER:
                addressFieldEnabler.setHouseNumberEnabled(false);
                break;
            case TOWN:
                addressFieldEnabler.setTownEnabled(false);
                break;

        }
    }

    public SalutationEnum getEnglishSalutation(String salutation) {

        if (salutation.equalsIgnoreCase((addressContractor.getActivityContext().getString(R.string.iap_mr)))) {
            return SalutationEnum.MR;
        } else {
            return SalutationEnum.MS;
        }
    }
}