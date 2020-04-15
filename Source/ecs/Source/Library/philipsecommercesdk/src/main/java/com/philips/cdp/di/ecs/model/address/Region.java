/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.model.address;

import java.io.Serializable;

/**
 * The type Region contains code and name of all the regions.
 */
public class Region implements Serializable{
    private static final long serialVersionUID = -8194436630244580653L;

    private String countryIso;
    private String isocode;

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    private String isocodeShort;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public String getIsocode() {
        return isocode;
    }

    public String getIsocodeShort() {
        return isocodeShort;
    }
    public void setIsocodeShort(String isocodeShort) {
        this.isocodeShort = isocodeShort;
    }

    public String getName() {
        return name;
    }
}
