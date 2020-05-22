/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.launcher;

import android.content.Context;

import com.philips.platform.mya.MyaTabConfig;
import com.philips.platform.mya.interfaces.MyaListener;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to provide input parameters and customizations for My Account.
 *
 * @since 2018.1.0
 */

public class MyaLaunchInput extends UappLaunchInput implements Serializable {

    private static final long serialVersionUID = -1122999835641536577L;
    private transient Context context;
    private MyaListener myaListener;
    private MyaTabConfig myaTabConfig;
    private List<String> settingsMenuList;
    private List<String> profileMenuList;
    private UserDataInterface userDataInterface;

    /**
     * Constructor for My Account launch input
     * @since 2018.1.0
     * @param context need to pass the activity context
     */
    public MyaLaunchInput(Context context) {
        this.context = context;
    }

    /**
     * Get the context
     * @since 2018.1.0
     * @return returns the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get the MyaListener needed to handle profile, settings and logout click callbacks
     * @since 2018.1.0
     * @return returns mya listener
     */
    public MyaListener getMyaListener() {
        return myaListener;
    }

    /**
     * Set the myaListener
     * @since 2018.1.0
     * @param myaListener myaListener
     */
    public void setMyaListener(MyaListener myaListener) {
        this.myaListener = myaListener;
    }

    /**
     * Get MyaTabConfig used to configure the proposition specific tab in mya
     * @since 2018.1.0
     * @return returns the MyaTabConfig
     */
    public MyaTabConfig getMyaTabConfig() {
        return myaTabConfig;
    }

    /**
     * Set the MyaTabConfig used to configure the proposition specific tab in mya
     * @param myaTabConfig send the myaTabConfig
     * @since 2018.1.0
     */
    public void setMyaTabConfig(MyaTabConfig myaTabConfig) {
        this.myaTabConfig = myaTabConfig;
    }

    /**
     * Get the list of items in the Settings menu
     * @since 2018.1.0
     * @return returns the list of Settings items
     */
    public List<String> getSettingsMenuList() {
        return settingsMenuList;
    }

    /**
     * Set the items needed in Settings menu
     * @since 2018.1.0
     * @param settingsMenuList send the array list of Settings items
     */
    public void setSettingsMenuList(List<String> settingsMenuList) {
        this.settingsMenuList = settingsMenuList;
    }

    /**
     * Get the list of items in the Profile menu
     * @since 2018.1.0
     * @return returns the list of Profile items
     */
    public List<String> getProfileMenuList() {
        return profileMenuList;
    }

    /**
     * Set the items needed in Profile menu
     * @since 2018.1.0
     * @param profileMenuList send the array list of Profile items
     */
    public void setProfileMenuList(List<String> profileMenuList) {
        this.profileMenuList = profileMenuList;
    }

    /**
     * Get the User Data Interface needed to set User details
     * @since 2018.1.0
     * @return returns the user data interface
     */
    public UserDataInterface getUserDataInterface(){
        return userDataInterface;
    }

    /**
     * Set the user data interface
     * @since 2018.1.0
     * @param userDataInterface send UserDataInterface
     */
    public void setUserDataInterface(UserDataInterface userDataInterface){
        this.userDataInterface = userDataInterface;
    }

}
