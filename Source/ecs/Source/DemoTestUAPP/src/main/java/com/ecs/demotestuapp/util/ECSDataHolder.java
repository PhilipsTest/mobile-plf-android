package com.ecs.demotestuapp.util;

import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.address.ECSUserProfile;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.config.ECSConfig;
import com.philips.platform.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.orders.ECSOrderHistory;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.ecs.model.products.ECSProducts;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum ECSDataHolder {

    INSTANCE;

    ECSConfig ecsConfig;
    ECSOAuthData ecsoAuthData;
    String propositionID;
    ECSUserProfile ecsUserProfile;
    List<ECSDeliveryMode> ecsDeliveryModes = new ArrayList<>();
    ECSDeliveryMode ecsDeliveryMode;
    ECSProducts ecsProducts;
    List<ECSAddress> ecsAddressList = new ArrayList<>();
    ECSShoppingCart ecsShoppingCart;
    ECSOrderHistory ecsOrderHistory;
    ECSOrderDetail ecsOrderDetailPlaceOrder;

    public UserDataInterface getmUserDataInterface() {
        return mUserDataInterface;
    }

    public void setmUserDataInterface(UserDataInterface mUserDataInterface) {
        this.mUserDataInterface = mUserDataInterface;
    }

    UserDataInterface mUserDataInterface;

    public ECSOrderDetail getEcsOrderDetailOrderHistory() {
        return ecsOrderDetailOrderHistory;
    }

    public void setEcsOrderDetailOrderHistory(ECSOrderDetail ecsOrderDetailOrderHistory) {
        this.ecsOrderDetailOrderHistory = ecsOrderDetailOrderHistory;
    }

    ECSOrderDetail ecsOrderDetailOrderHistory;
    List<ECSPayment> ecsPayments = new ArrayList<>();
    List<ECSRegion> ecsRegions = new ArrayList<>();
    String janrainID;
    List<ECSVoucher> vouchers = new ArrayList<>();

    public UserDataInterface getUserDataInterface() {
        return userDataInterface;
    }

    public void setUserDataInterface(UserDataInterface userDataInterface) {
        this.userDataInterface = userDataInterface;
    }

    UserDataInterface userDataInterface;


    public ECSOrderDetail getEcsOrderDetailFromPlaceOrder() {
        return ecsOrderDetailPlaceOrder;
    }

    public void setEcsOrderDetailOfPlaceOrder(ECSOrderDetail ecsOrderDetailPlaceOrder) {
        this.ecsOrderDetailPlaceOrder = ecsOrderDetailPlaceOrder;
    }


    public List<ECSPayment> getEcsPayments() {
        return ecsPayments;
    }

    public void setEcsPayments(List<ECSPayment> ecsPayments) {
        this.ecsPayments = ecsPayments;
    }


    public ECSOrderHistory getEcsOrderHistory() {
        return ecsOrderHistory;
    }

    public void setEcsOrderHistory(ECSOrderHistory ecsOrderHistory) {
        this.ecsOrderHistory = ecsOrderHistory;
    }


    public List<ECSRegion> getEcsRegions() {
        return ecsRegions;
    }

    public void setEcsRegions(List<ECSRegion> ecsRegions) {
        this.ecsRegions = ecsRegions;
    }


    public ECSServices getEcsServices() {
        return ecsServices;
    }

    private ECSServices ecsServices;

    public ECSConfig getEcsConfig() {
        return ecsConfig;
    }

    public void setEcsConfig(ECSConfig ecsConfig) {
        this.ecsConfig = ecsConfig;
    }

    public ECSOAuthData getEcsoAuthData() {
        return ecsoAuthData;
    }

    public void setEcsoAuthData(ECSOAuthData ecsoAuthData) {
        this.ecsoAuthData = ecsoAuthData;
    }

    public String getPropositionID() {
        return propositionID;
    }

    public void setPropositionID(String propositionID) {
        this.propositionID = propositionID;
    }

    public ECSUserProfile getEcsUserProfile() {
        return ecsUserProfile;
    }

    public void setEcsUserProfile(ECSUserProfile ecsUserProfile) {
        this.ecsUserProfile = ecsUserProfile;
    }


    public ECSDeliveryMode getEcsDeliveryMode() {
        return ecsDeliveryMode;
    }

    public void setEcsDeliveryMode(ECSDeliveryMode ecsDeliveryMode) {
        this.ecsDeliveryMode = ecsDeliveryMode;
    }

    public ECSProducts getEcsProducts() {
        return ecsProducts;
    }

    public void setEcsProducts(ECSProducts ecsProducts) {
        this.ecsProducts = ecsProducts;
    }


    public List<ECSAddress> getEcsAddressList() {
        return ecsAddressList;
    }

    public void setEcsAddressList(List<ECSAddress> ecsAddressList) {
        this.ecsAddressList = ecsAddressList;
    }


    public List<ECSDeliveryMode> getEcsDeliveryModes() {
        return ecsDeliveryModes;
    }

    public void setEcsDeliveryModes(List<ECSDeliveryMode> ecsDeliveryModes) {
        this.ecsDeliveryModes = ecsDeliveryModes;
    }


    public List<ECSVoucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<ECSVoucher> vouchers) {
        this.vouchers = vouchers;
    }


    public ECSShoppingCart getEcsShoppingCart() {
        return ecsShoppingCart;
    }

    public void setEcsShoppingCart(ECSShoppingCart ecsShoppingCart) {
        this.ecsShoppingCart = ecsShoppingCart;
    }


    public String getJanrainID() {
        return janrainID;
    }


    public void setECSService(ECSServices ecsServices) {
        this.ecsServices = ecsServices;
    }

    public void setJanrainID(String janrainID) {
        this.janrainID = janrainID;
    }

    public void resetData() {

        ecsConfig = null;
        ecsoAuthData = null;
        propositionID = null;
        ecsUserProfile = null;
        ecsDeliveryModes = null;
        ecsDeliveryMode = null;
        ecsProducts = null;
        ecsAddressList = null;
        ecsShoppingCart = null;
        ecsShoppingCart = null;
        janrainID = null;

        ecsOrderHistory = null;
        ecsOrderDetailPlaceOrder = null;
        ecsPayments = new ArrayList<>();
        ecsRegions = new ArrayList<>();
        vouchers = new ArrayList<>();
    }

    boolean isUserLoggedIn(){
        return  mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN ;
    }



    public void refreshJanRainID() {

        ArrayList<String> detailsKey = new ArrayList<>();
        detailsKey.add(UserDetailConstants.ACCESS_TOKEN);
        try {

            if(mUserDataInterface!=null && isUserLoggedIn()) {

                HashMap<String, Object> userDetailsMap = mUserDataInterface.getUserDetails(detailsKey);
                String janrainID = userDetailsMap.get(UserDetailConstants.ACCESS_TOKEN).toString();
                ECSDataHolder.INSTANCE.setJanrainID(janrainID);
                ECSDataHolder.INSTANCE.setUserDataInterface(mUserDataInterface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
