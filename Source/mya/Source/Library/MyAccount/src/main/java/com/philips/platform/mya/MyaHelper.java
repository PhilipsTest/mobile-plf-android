/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya;


import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.catk.CatkInputs;
import com.philips.platform.consenthandlerinterface.ConsentHandlerMapping;
import com.philips.platform.consenthandlerinterface.datamodel.ConsentDefinition;
import com.philips.platform.mya.interfaces.MyaListener;
import com.philips.platform.mya.launcher.MyaLaunchInput;
import com.philips.platform.uid.thememanager.ThemeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MyaHelper {

    private static MyaHelper instance;
    private AppInfraInterface appInfra;
    private MyaListener myaListener;
    private ThemeConfiguration themeConfiguration;
    private MyaLaunchInput myaLaunchInput;
    private List<ConsentHandlerMapping> consentHandlerMappingList;

    private MyaHelper() {
    }

    public static MyaHelper getInstance() {
        if (instance == null) {
            synchronized (MyaHelper.class) {
                if (instance == null) {
                    instance = new MyaHelper();
                }
            }
        }
        return instance;
    }

    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public void setAppInfra(AppInfraInterface appInfra) {
        this.appInfra = appInfra;
    }

    public List<ConsentHandlerMapping> getConsentHandlerMappingList() {
        return consentHandlerMappingList;
    }

    public void setConfigurations(List<ConsentHandlerMapping> consentHandlerMappingList) {
        throwExceptionWhenDuplicateTypesExist(consentHandlerMappingList);
        this.consentHandlerMappingList = consentHandlerMappingList == null ? new ArrayList<ConsentHandlerMapping>() : consentHandlerMappingList;
    }

    private void throwExceptionWhenDuplicateTypesExist(List<ConsentHandlerMapping> consentHandlerMappingList) {
        List<String> uniqueTypes = new ArrayList<>();
        if (consentHandlerMappingList != null && !consentHandlerMappingList.isEmpty()) {
            for (ConsentHandlerMapping configuration : consentHandlerMappingList) {
                if (configuration != null) {
                    for (ConsentDefinition definition : configuration.getConsentDefinitionList()) {
                        if (definition != null) {
                            for (String type : definition.getTypes()) {
                                if (uniqueTypes.contains(type)) {
                                    throw new CatkInputs.InvalidInputException("Not allowed to have duplicate types in your Definitions, type:" + type + " occurs in multiple times");
                                }
                                uniqueTypes.add(type);
                            }
                        }
                    }
                }
            }
        }
    }

    public MyaListener getMyaListener() {
        return myaListener;
    }

    public void setMyaListener(MyaListener myaListener) {
        this.myaListener = myaListener;
    }

    public ThemeConfiguration getThemeConfiguration() {
        return themeConfiguration;
    }

    public void setThemeConfiguration(ThemeConfiguration themeConfiguration) {
        this.themeConfiguration = themeConfiguration;
    }

    public MyaLaunchInput getMyaLaunchInput() {
        return myaLaunchInput;
    }

    public void setMyaLaunchInput(MyaLaunchInput myaLaunchInput) {
        this.myaLaunchInput = myaLaunchInput;
    }
}


