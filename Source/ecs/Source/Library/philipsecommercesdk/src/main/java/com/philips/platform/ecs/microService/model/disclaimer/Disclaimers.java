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
package com.philips.platform.ecs.microService.model.disclaimer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Disclaimers contains list of disclaimers.
 */
public class Disclaimers implements Serializable {
    @SerializedName("disclaimer")
    @Expose
    private List<Disclaimer> disclaimer = new ArrayList<Disclaimer>();

    /**
     * No args constructor for use in serialization
     */
    public Disclaimers() {
    }

    /**
     * @param disclaimer
     */
    public Disclaimers(List<Disclaimer> disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<Disclaimer> getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(List<Disclaimer> disclaimer) {
        this.disclaimer = disclaimer;
    }
}
