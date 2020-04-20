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

/**
 * The type Asset contains details of asset
 */
public class Asset  implements Serializable {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("extent")
    @Expose
    private String extent;
    @SerializedName("lastModified")
    @Expose
    private String lastModified;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("asset")
    @Expose
    private String asset;

    /**
     * No args constructor for use in serialization
     *
     */
    public Asset() {
    }

    /**
     *
     * @param extension
     * @param asset
     * @param lastModified
     * @param extent
     * @param description
     * @param locale
     * @param number
     * @param code
     * @param type
     */
    public Asset(String code, String description, String extension, String extent, String lastModified, String locale, String number, String type, String asset) {
        this.code = code;
        this.description = description;
        this.extension = extension;
        this.extent = extent;
        this.lastModified = lastModified;
        this.locale = locale;
        this.number = number;
        this.type = type;
        this.asset = asset;
    }

    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     *
     * @param extension
     * The extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     *
     * @return
     * The extent
     */
    public String getExtent() {
        return extent;
    }

    /**
     *
     * @param extent
     * The extent
     */
    public void setExtent(String extent) {
        this.extent = extent;
    }

    /**
     *
     * @return
     * The lastModified
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     *
     * @param lastModified
     * The lastModified
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     *
     * @return
     * The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     * The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     * The number
     */
    public String getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The asset
     */
    public String getAsset() {
        return asset;
    }

    /**
     *
     * @param asset
     * The asset
     */
    public void setAsset(String asset) {
        this.asset = asset;
    }

}