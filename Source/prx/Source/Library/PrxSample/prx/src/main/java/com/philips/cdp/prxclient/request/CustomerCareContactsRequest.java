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

package com.philips.cdp.prxclient.request;


import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.datamodels.contacts.ContactsModel;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * The type Product summary request.
 */
public class CustomerCareContactsRequest extends PrxRequest {

    private static final String PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID = "cc.cdls";
    private String mRequestTag = null;
    private String mProductCategory;

    /**
     * Instantiates a new Product summary request.
     * @since 2002.0.0
     * @param productCategory product Category
     */
    public CustomerCareContactsRequest(String productCategory) {
        super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE);
        mProductCategory = productCategory;
    }

    /**
     * Instantiates a new Product summary request.
     * @since 2001.0.0
     * @param productCategory         product Category
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    public CustomerCareContactsRequest(String productCategory, PrxConstants.Sector sector, PrxConstants.Catalog catalog, String requestTag) {
        super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, sector, catalog);
        this.mRequestTag = requestTag;
        mProductCategory = productCategory;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new ContactsModel().parseJsonResponseData(jsonObject);
    }

    @Override
    public Map<String, String> getReplaceURLMap() {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("productCategory", mProductCategory);
        replaceUrl.put("productSector",getSector().toString());
        replaceUrl.put("productCatalog",getCatalog().toString());
        return replaceUrl;
    }
}
