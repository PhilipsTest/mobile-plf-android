/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration


import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.uappframework.uappinput.UappDependencies

/**
 * MECDependencies handles the dependency required for MEC. So right now, MEC has one dependency i.e AppInfra. So vertical needs to initialize MECDependencies and set the app infra object. This app infra object will be responsible for logging, tagging and some configuration.
 * Create MECDependencies instance from AppInfraInterface and UserDataInterface object
 * @param appInfra  to pass the instance of AppInfraInterface
 * @param userDataInterface to pass the instance of UserDataInterface
 * @since 1.0.0
 */
class MECDependencies(appInfra: AppInfraInterface, val userDataInterface: UserDataInterface) : UappDependencies(appInfra)


