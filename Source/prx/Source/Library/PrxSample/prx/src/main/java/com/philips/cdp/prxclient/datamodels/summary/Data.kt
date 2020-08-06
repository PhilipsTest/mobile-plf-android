package com.philips.cdp.prxclient.datamodels.summary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
open class Data {
    /**
     *
     * @return
     * The locale
     */
    /**
     *
     * @param locale
     * The locale
     */
    @SerializedName("locale")
    @Expose
    var locale: String? = null

    /**
     *
     * @return
     * The ctn
     */
    /**
     *
     * @param ctn
     * The ctn
     */
    @SerializedName("ctn")
    @Expose
    var ctn: String? = null

    /**
     *
     * @return
     * The dtn
     */
    /**
     *
     * @param dtn
     * The dtn
     */
    @SerializedName("dtn")
    @Expose
    var dtn: String? = null

    /**
     *
     * @return
     * The leafletUrl
     */
    /**
     *
     * @param leafletUrl
     * The leafletUrl
     */
    @SerializedName("leafletUrl")
    @Expose
    var leafletUrl: String? = null

    /**
     *
     * @return
     * The productTitle
     */
    /**
     *
     * @param productTitle
     * The productTitle
     */
    @SerializedName("productTitle")
    @Expose
    var productTitle: String? = null

    /**
     *
     * @return
     * The alphanumeric
     */
    /**
     *
     * @param alphanumeric
     * The alphanumeric
     */
    @SerializedName("alphanumeric")
    @Expose
    var alphanumeric: String? = null

    /**
     *
     * @return
     * The brandName
     */
    /**
     *
     * @param brandName
     * The brandName
     */
    @SerializedName("brandName")
    @Expose
    var brandName: String? = null

    /**
     *
     * @return
     * The brand
     */
    /**
     *
     * @param brand
     * The brand
     */
    @SerializedName("brand")
    @Expose
    var brand: Brand? = null

    /**
     *
     * @return
     * The familyName
     */
    /**
     *
     * @param familyName
     * The familyName
     */
    @SerializedName("familyName")
    @Expose
    var familyName: String? = null

    /**
     *
     * @return
     * The productURL
     */
    /**
     *
     * @param productURL
     * The productURL
     */
    @SerializedName("productURL")
    @Expose
    var productURL: String? = null

    /**
     *
     * @return
     * The productPagePath
     */
    /**
     *
     * @param productPagePath
     * The productPagePath
     */
    @SerializedName("productPagePath")
    @Expose
    var productPagePath: String? = null

    /**
     *
     * @return
     * The descriptor
     */
    /**
     *
     * @param descriptor
     * The descriptor
     */
    @SerializedName("descriptor")
    @Expose
    var descriptor: String? = null

    /**
     *
     * @return
     * The domain
     */
    /**
     *
     * @param domain
     * The domain
     */
    @SerializedName("domain")
    @Expose
    var domain: String? = null

    /**
     *
     * @return
     * The versions
     */
    /**
     *
     * @param versions
     * The versions
     */
    @SerializedName("versions")
    @Expose
    var versions: List<String> = ArrayList()

    /**
     *
     * @return
     * The productStatus
     */
    /**
     *
     * @param productStatus
     * The productStatus
     */
    @SerializedName("productStatus")
    @Expose
    var productStatus: String? = null

    /**
     *
     * @return
     * The imageURL
     */
    /**
     *
     * @param imageURL
     * The imageURL
     */
    @SerializedName("imageURL")
    @Expose
    var imageURL: String? = null

    /**
     *
     * @return
     * The sop
     */
    /**
     *
     * @param sop
     * The sop
     */
    @SerializedName("sop")
    @Expose
    var sop: String? = null

    /**
     *
     * @return
     * The somp
     */
    /**
     *
     * @param somp
     * The somp
     */
    @SerializedName("somp")
    @Expose
    var somp: String? = null

    /**
     *
     * @return
     * The eop
     */
    /**
     *
     * @param eop
     * The eop
     */
    @SerializedName("eop")
    @Expose
    var eop: String? = null

    /**
     *
     * @return
     * The isDeleted
     */
    @SerializedName("isDeleted")
    @Expose
    var isIsDeleted = false
        private set

    /**
     *
     * @return
     * The priority
     */
    /**
     *
     * @param priority
     * The priority
     */
    @SerializedName("priority")
    @Expose
    var priority: Long = 0

    /**
     *
     * @return
     * The price
     */
    /**
     *
     * @param price
     * The price
     */
    @SerializedName("price")
    @Expose
    var price: Price? = null

    /**
     *
     * @return
     * The reviewStatistics
     */
    /**
     *
     * @param reviewStatistics
     * The reviewStatistics
     */
    @SerializedName("reviewStatistics")
    @Expose
    var reviewStatistics: ReviewStatistics? = null

    /**
     *
     * @return
     * The keyAwards
     */
    /**
     *
     * @param keyAwards
     * The keyAwards
     */
    @SerializedName("keyAwards")
    @Expose
    var keyAwards: List<String> = ArrayList()

    /**
     *
     * @return
     * The wow
     */
    /**
     *
     * @param wow
     * The wow
     */
    @SerializedName("wow")
    @Expose
    var wow: String? = null

    /**
     *
     * @return
     * The subWOW
     */
    /**
     *
     * @param subWOW
     * The subWOW
     */
    @SerializedName("subWOW")
    @Expose
    var subWOW: String? = null

    /**
     *
     * @return
     * The marketingTextHeader
     */
    /**
     *
     * @param marketingTextHeader
     * The marketingTextHeader
     */
    @SerializedName("marketingTextHeader")
    @Expose
    var marketingTextHeader: String? = null

    /**
     *
     * @return
     * The careSop
     */
    /**
     *
     * @param careSop
     * The careSop
     */
    @SerializedName("careSop")
    @Expose
    var careSop: String? = null

    /**
     *
     * @return
     * The filterKeys
     */
    /**
     *
     * @param filterKeys
     * The filterKeys
     */
    @SerializedName("filterKeys")
    @Expose
    var filterKeys: List<String> = ArrayList()

    /**
     *
     * @return
     * The subcategory
     */
    /**
     *
     * @param subcategory
     * The subcategory
     */
    @SerializedName("subcategory")
    @Expose
    var subcategory: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param descriptor
     * @param locale
     * @param subWOW
     * @param familyName
     * @param alphanumeric
     * @param brandName
     * @param dtn
     * @param wow
     * @param productStatus
     * @param priority
     * @param leafletUrl
     * @param domain
     * @param careSop
     * @param eop
     * @param ctn
     * @param productPagePath
     * @param reviewStatistics
     * @param marketingTextHeader
     * @param sop
     * @param isDeleted
     * @param filterKeys
     * @param versions
     * @param price
     * @param subcategory
     * @param somp
     * @param productTitle
     * @param brand
     * @param keyAwards
     * @param imageURL
     * @param productURL
     */
    constructor(locale: String?, ctn: String?, dtn: String?, leafletUrl: String?, productTitle: String?, alphanumeric: String?, brandName: String?, brand: Brand?, familyName: String?, productURL: String?, productPagePath: String?, descriptor: String?, domain: String?, versions: List<String>, productStatus: String?, imageURL: String?, sop: String?, somp: String?, eop: String?, isDeleted: Boolean, priority: Long, price: Price?, reviewStatistics: ReviewStatistics?, keyAwards: List<String>, wow: String?, subWOW: String?, marketingTextHeader: String?, careSop: String?, filterKeys: List<String>, subcategory: String?) {
        this.locale = locale
        this.ctn = ctn
        this.dtn = dtn
        this.leafletUrl = leafletUrl
        this.productTitle = productTitle
        this.alphanumeric = alphanumeric
        this.brandName = brandName
        this.brand = brand
        this.familyName = familyName
        this.productURL = productURL
        this.productPagePath = productPagePath
        this.descriptor = descriptor
        this.domain = domain
        this.versions = versions
        this.productStatus = productStatus
        this.imageURL = imageURL
        this.sop = sop
        this.somp = somp
        this.eop = eop
        isIsDeleted = isDeleted
        this.priority = priority
        this.price = price
        this.reviewStatistics = reviewStatistics
        this.keyAwards = keyAwards
        this.wow = wow
        this.subWOW = subWOW
        this.marketingTextHeader = marketingTextHeader
        this.careSop = careSop
        this.filterKeys = filterKeys
        this.subcategory = subcategory
    }

    /**
     *
     * @param isDeleted
     * The isDeleted
     */
    fun setIsDeleted(isDeleted: Boolean) {
        isIsDeleted = isDeleted
    }

}