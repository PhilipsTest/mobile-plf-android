/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.ecs.demotestuapp.integration;

public enum DependencyHolder {

    INSTANCE;

    public EcsDemoTestUAppDependencies getuAppDependencies() {
        return uAppDependencies;
    }

    public void setuAppDependencies(EcsDemoTestUAppDependencies uAppDependencies) {
        this.uAppDependencies = uAppDependencies;
    }

    EcsDemoTestUAppDependencies uAppDependencies;



}
