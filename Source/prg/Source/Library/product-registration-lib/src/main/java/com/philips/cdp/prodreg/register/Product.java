/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.register;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.MetadataListener;
import com.philips.cdp.prodreg.listener.SummaryListener;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.summary.ProductSummaryResponse;
import com.philips.cdp.prodreg.prxrequest.ProductMetadataRequest;
import com.philips.cdp.prodreg.prxrequest.ProductSummaryRequest;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.platform.appinfra.AppInfraInterface;

import java.io.Serializable;

/**
 * It is used to represent a Product
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 4081810711321162900L;
    protected String productModelNumber;
    protected String productSerialNumber;
    protected String purchaseDate;
    private PrxConstants.Sector sector;
    private PrxConstants.Catalog catalog;
    private String locale;
    private boolean shouldSendEmailAfterRegistration = true;
    private String friendlyName;

    /**
     * Creates instance of Product
     * @param productModelNumber - String productModelNumber
     * @param sector - PrxConstants.Sector sector
     * @param catalog - PrxConstants.Catalog catalog
     * @since 1.0.0
     */
    public Product(String productModelNumber, PrxConstants.Sector sector, PrxConstants.Catalog catalog) {
        this.productModelNumber = productModelNumber;
        this.sector = sector;
        this.catalog = catalog;
    }

    /**
     * API returns the friendlyName
     * @return FriendlyName
     * @since 1.0.0
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * API to set friendlyName
     * @param friendlyName
     * @since 1.0.0
     */
    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * API to execute product metadata request .
     * @param context - Context context
     * @param metadataListener -  MetadataListener metadataListener
     * @since 1.0.0
     */
    public void getProductMetadata(final Context context, final MetadataListener metadataListener) {
        ProductMetadataRequest productMetadataRequest = getProductMetadataRequest(getCtn());
        productMetadataRequest.setSector(getSector());
        productMetadataRequest.setCatalog(getCatalog());
        RequestManager mRequestManager = getRequestManager(context);
        final ResponseListener metadataResponseListener = getPrxResponseListener(metadataListener);
        mRequestManager.executeRequest(productMetadataRequest, metadataResponseListener);
    }

    /**
     *  API to execute product summary request .
     *
     * @param context - Context context
     * @param product - Product product
     * @param summaryListener - SummaryListener summaryListener
     * @since 1.0.0
     */
    public void getProductSummary(final Context context, final Product product, final SummaryListener summaryListener) {
        ProductSummaryRequest productSummaryRequest = getProductSummaryRequest(product);
        productSummaryRequest.setSector(product.getSector());
        productSummaryRequest.setCatalog(product.getCatalog());
        RequestManager mRequestManager = getRequestManager(context);
        final ResponseListener summaryResponseListener = getPrxResponseListenerForSummary(summaryListener);
        mRequestManager.executeRequest(productSummaryRequest, summaryResponseListener);
    }

    /**
     * API to get instance of ProductSummaryRequest from product
     * @param product - Product product
     * @return ProductSummaryRequest
     * @since 1.0.0
     */
    @NonNull
    protected ProductSummaryRequest getProductSummaryRequest(final Product product) {
        return new ProductSummaryRequest(product.getCtn(),ProdRegConstants.PRODUCTSUMMERYREQUEST_SERVICE_ID,getSector(),getCatalog());
    }

    /**
     * API to get ResponseListener instance from metadataListener
     * @param metadataListener - MetadataListener metadataListener
     * @return ResponseListener
     * @since 1.0.0
     */
    @NonNull
    ResponseListener getPrxResponseListener(final MetadataListener metadataListener) {
        return new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                ProductMetadataResponse productMetaData = (ProductMetadataResponse) responseData;
                metadataListener.onMetadataResponse(productMetaData);
            }

            @Override
            public void onResponseError(PrxError prxError) {
                metadataListener.onErrorResponse(prxError.getDescription(), prxError.getStatusCode());
            }
        };
    }

    /**
     * API to get ResponseListener instance from summaryListener
     * @param summaryListener - SummaryListener summaryListener
     * @return ResponseListener
     * @since 1.0.0
     */
    @NonNull
    ResponseListener getPrxResponseListenerForSummary(final SummaryListener summaryListener) {
        return new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                ProductSummaryResponse productSummaryResponse = (ProductSummaryResponse) responseData;
                summaryListener.onSummaryResponse(productSummaryResponse);
            }

            @Override
            public void onResponseError(PrxError prxError) {
                summaryListener.onErrorResponse(prxError.getDescription(), prxError.getStatusCode());
            }
        };
    }

    /**
     * API gets Product
     * @return Product
     * @since 1.0.0
     */
    protected Product getProduct() {
        return this;
    }

    /**
     * API gets RequestManager instance
     * @param context - Context context
     * @return RequestManager
     * @since 1.0.0
     */
    @NonNull
    RequestManager getRequestManager(final Context context) {
        AppInfraInterface appInfra = PRUiHelper.getInstance().getAppInfraInstance();
        PRXDependencies   prxDependencies = new PRXDependencies(context , appInfra,ProdRegConstants.PRG_SUFFIX); // use existing appinfra instance
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(prxDependencies); // pass prxdependency

        return mRequestManager;
    }

    /**
     * API returns Model Number
     *
     * @return return model number as string
     * @since 1.0.0
     */
    public String getCtn() {
        productModelNumber = productModelNumber != null ? productModelNumber : "";
        return productModelNumber;
    }

    /**
     * API returns Serial Number
     * @return return serial number as string
     * @since 1.0.0
     */
    public String getSerialNumber() {
        productSerialNumber = productSerialNumber != null ? productSerialNumber : "";
        return productSerialNumber;
    }

    /**
     * API sets the serial number
     * @param serialNumber serial Number
     *                     @since 1.0.0
     */

    public void setSerialNumber(final String serialNumber) {
        this.productSerialNumber = serialNumber;
    }

    /**
     * API return Sector
     * @return return sector as Enum's
     * @since 1.0.0
     */
    public PrxConstants.Sector getSector() {
        return sector;
    }

    /**
     * API return Catalog
     * @return return catalog as Enum's
     * @since 1.0.0
     */
    public PrxConstants.Catalog getCatalog() {
        return catalog;
    }

    /**
     * API return Locale
     * @return return local as string
     * @since 1.0.0
     */
    public String getLocale() {
        return locale;
    }

    /**
     * API set Local
     * @param locale local
     *               @since 1.0.0
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * API return date
     * @return Purchase date
     * @since 1.0.0
     */
    public String getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * API set Date
     * @param purchaseDate purchase date
     *                     @since 1.0.0
     */

    public void setPurchaseDate(final String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * API return email
     * @return return Email
     * @since 1.0.0
     */
    public boolean getEmail() {
        return shouldSendEmailAfterRegistration;
    }

    /**
     * API sets email confirmations
     * @param shouldSendEmailAfterRegistration shouldSendEmailAfterRegistration
     *                                         @since 1.0.0
     */
    public void sendEmail(final boolean shouldSendEmailAfterRegistration) {
        this.shouldSendEmailAfterRegistration = shouldSendEmailAfterRegistration;
    }

    /**
     * API passes CTN to get Metadata
     * @param ctn ctn
     * @return ctn as string
     * @since 1.0.0
     */
    public ProductMetadataRequest getProductMetadataRequest(String ctn) {
        return new ProductMetadataRequest(ctn, ProdRegConstants.PRODUCTMETADATAREQUEST_SERVICE_ID,getSector(),getCatalog());
    }

}
