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

package com.mec.demouapp;

public enum  DependencyHolder {

    INSTANCE;

    MecDemoUAppDependencies mecDemoUAppDependencies;

    public MecDemoUAppDependencies getMecDemoUAppDependencies() {
        return mecDemoUAppDependencies;
    }

    public void setMecDemoUAppDependencies(MecDemoUAppDependencies mecDemoUAppDependencies) {
        this.mecDemoUAppDependencies = mecDemoUAppDependencies;
    }

}
