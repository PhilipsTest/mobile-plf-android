/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.disclaimer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {


    public Disclaimers getDisclaimers() {
        return disclaimers;
    }

    public void setDisclaimers(Disclaimers disclaimers) {
        this.disclaimers = disclaimers;
    }

    @SerializedName("disclaimers")
    @Expose
    private Disclaimers disclaimers;


    public Data() {
    }

    public Data(Disclaimers disclaimers) {
        this.disclaimers = disclaimers;
    }


}
