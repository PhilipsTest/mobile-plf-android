package com.philips.cdp.prxclient.datamodels.summary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class Price {
    /**
     *
     * @return
     * The productPrice
     */
    /**
     *
     * @param productPrice
     * The productPrice
     */
    @SerializedName("productPrice")
    @Expose
    var productPrice: String? = null

    /**
     *
     * @return
     * The displayPriceType
     */
    /**
     *
     * @param displayPriceType
     * The displayPriceType
     */
    @SerializedName("displayPriceType")
    @Expose
    var displayPriceType: String? = null

    /**
     *
     * @return
     * The displayPrice
     */
    /**
     *
     * @param displayPrice
     * The displayPrice
     */
    @SerializedName("displayPrice")
    @Expose
    var displayPrice: String? = null

    /**
     *
     * @return
     * The currencyCode
     */
    /**
     *
     * @param currencyCode
     * The currencyCode
     */
    @SerializedName("currencyCode")
    @Expose
    var currencyCode: String? = null

    /**
     *
     * @return
     * The formattedPrice
     */
    /**
     *
     * @param formattedPrice
     * The formattedPrice
     */
    @SerializedName("formattedPrice")
    @Expose
    var formattedPrice: String? = null

    /**
     *
     * @return
     * The formattedDisplayPrice
     */
    /**
     *
     * @param formattedDisplayPrice
     * The formattedDisplayPrice
     */
    @SerializedName("formattedDisplayPrice")
    @Expose
    var formattedDisplayPrice: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param currencyCode
     * @param formattedDisplayPrice
     * @param formattedPrice
     * @param displayPriceType
     * @param displayPrice
     * @param productPrice
     */
    constructor(productPrice: String?, displayPriceType: String?, displayPrice: String?, currencyCode: String?, formattedPrice: String?, formattedDisplayPrice: String?) {
        this.productPrice = productPrice
        this.displayPriceType = displayPriceType
        this.displayPrice = displayPrice
        this.currencyCode = currencyCode
        this.formattedPrice = formattedPrice
        this.formattedDisplayPrice = formattedDisplayPrice
    }

}