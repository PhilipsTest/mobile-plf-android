/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.summary;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Brand  implements Serializable {

    private static final long serialVersionUID = 8553900234112274309L;
    @SerializedName("brandLogo")
    @Expose
    private String brandLogo;

    /**
     * No args constructor for use in serialization
     *
     */
    public Brand() {
    }

    /**
     *
     * @param brandLogo
     */
    public Brand(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    /**
     *
     * @return
     * The brandLogo
     */
    public String getBrandLogo() {
        return brandLogo;
    }

    /**
     *
     * @param brandLogo
     * The brandLogo
     */
    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

}