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


/**
 * The type Product summary request.
 */
public class CustomerCareContactsRequest extends PrxRequest {

    private static final String PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID = "cc.cdls";
    private String mRequestTag = null;

    /**
     * Instantiates a new Product summary request.
     * @since 2001.0.0
     * @param productCategory product Category
     */
    public CustomerCareContactsRequest(String productCategory) {
        super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE);
        setCategory(productCategory);
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
        setCategory(productCategory);
        this.mRequestTag = requestTag;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new ContactsModel().parseJsonResponseData(jsonObject);
    }
}
