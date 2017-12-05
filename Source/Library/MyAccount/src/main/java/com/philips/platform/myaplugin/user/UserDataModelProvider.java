package com.philips.platform.myaplugin.user;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.platform.myaplugin.uappadaptor.DataModel;
import com.philips.platform.myaplugin.uappadaptor.DataModelType;
import com.philips.platform.myaplugin.uappadaptor.UserDataModel;
import com.philips.platform.myaplugin.uappadaptor.UserInterface;

import java.io.Serializable;


public class UserDataModelProvider extends UserInterface implements Serializable {

    private transient UserDataModel userDataModel;
    private transient Context context;

    public UserDataModelProvider(Context context) {
        this.context = context;
        if (userDataModel == null) {
            userDataModel = new UserDataModel();
        }
    }

    @Override
    public DataModel getData(DataModelType dataModelType) {
        if (userDataModel == null) {
            userDataModel = new UserDataModel();
        }
        fillUserData();
        return userDataModel;
    }

    @Override
    public boolean isUserLoggedIn() {
        User user = new User(context);
        return user.isUserSignIn();
    }

    @Override
    public void logOut(LogoutHandler logoutHandler) {
        User user = new User(context);
        user.logout(logoutHandler);
    }

    private void fillUserData() {
        User user = new User(context);
        userDataModel.setName(user.getDisplayName());
        userDataModel.setBirthday(user.getDateOfBirth());
        userDataModel.setEmail(user.getEmail());
        userDataModel.setAccessToken(user.getDisplayName());
        userDataModel.setGivenName(user.getGivenName());
        userDataModel.setBirthday(user.getDateOfBirth());
        userDataModel.setEmailVerified(user.isEmailVerified());
        userDataModel.setMobileNumber(user.getMobile());
        userDataModel.setMobileVerified(user.isMobileVerified());
        userDataModel.setGender(user.getGender().toString());
//        userDataModel.setVerified(user.isTermsAndConditionAccepted());
        userDataModel.setFamilyName(user.getFamilyName());
    }




}
