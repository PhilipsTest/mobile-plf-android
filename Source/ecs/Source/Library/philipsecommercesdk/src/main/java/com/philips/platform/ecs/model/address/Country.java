/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.address;

import java.io.Serializable;

/**
 * The type Country which contains name and code of all the countries .
 */
public class Country implements Serializable{
    private static final long serialVersionUID = 3239898965381617362L;
    
    private String isocode;
    private String name;

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;

    }

    public String getIsocode() {
        return isocode;
    }
}
