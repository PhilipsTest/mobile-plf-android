/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.dscdemo;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.csw.justintime.JustInTimeTextResources;
import com.philips.platform.uappframework.uappinput.UappDependencies;

public class DSDemoAppuAppDependencies extends UappDependencies {
    public JustInTimeTextResources textResources;

    public DSDemoAppuAppDependencies(final AppInfraInterface appInfra,
                                     final JustInTimeTextResources textResources) {
        super(appInfra);
        this.textResources = textResources;
    }
}
