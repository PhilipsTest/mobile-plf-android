/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.asset;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
public class Data implements Serializable {

    private static final long serialVersionUID = 1324489785506473766L;
    @SerializedName("assets")
    @Expose
    private Assets assets;

    /**
     * No args constructor for use in serialization
     *
     */
    public Data() {
    }

    /**
     *
     * @param assets
     */
    public Data(Assets assets) {
        this.assets = assets;
    }

    /**
     *
     * @return
     * The assets
     */
    public Assets getAssets() {
        return assets;
    }

    /**
     *
     * @param assets
     * The assets
     */
    public void setAssets(Assets assets) {
        this.assets = assets;
    }

}