/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.asset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.platform.ecs.util.PRXImageAssetFilter;
import com.philips.platform.ecs.util.PRXImageAssetFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * Project : PRX Common Component.
 */


/**
 * The type Assets contains list of assets
 */
public class Assets implements Serializable {

    private static final long serialVersionUID = 3594476307105100868L;
    @SerializedName("asset")
    @Expose
    private List<Asset> asset = new ArrayList<Asset>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Assets() {
    }

    /**
     *
     * @param asset
     */
    public Assets(List<Asset> asset) {
        this.asset = asset;
    }

    /**
     *
     * @return
     * The asset
     */
    public List<Asset> getAsset() {
        return asset;
    }

    public List<Asset> getValidPRXImageAssets(){
        return new PRXImageAssetFilter().getValidPRXAssets(asset);
    }

    /**
     *
     * @param asset
     * The asset
     */
    public void setAsset(List<Asset> asset) {
        this.asset = asset;
    }

}