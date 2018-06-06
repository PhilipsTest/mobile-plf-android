/*
 * Copyright (c) 2018 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.csw.justintime;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

public class JustInTimeConsentDependencies {
    public static ConsentDefinition consentDefinition;
    public static JustInTimeTextResources textResources;
    public static AppInfraInterface appInfra;
    public static JustInTimeWidgetHandler completionListener;
}
