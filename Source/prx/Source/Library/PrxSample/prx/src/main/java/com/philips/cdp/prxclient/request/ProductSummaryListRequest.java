package com.philips.cdp.prxclient.request;


import android.text.TextUtils;

import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.datamodels.summary.PRXSummaryListResponse;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Product summary request.
 */
public class ProductSummaryListRequest extends PrxRequest {

    private static final String PRXSummaryDataServiceID = "prxclient.summarylist";
    private String mRequestTag = null;
    private List<String> ctns;

    /**
     * Instantiates a new Product summary request.
     *
     * @param ctns       product ctns
     * @param sector     sector
     * @param catalog    catalog
     * @param requestTag request tag
     * @since 1.0.0
     */
    public ProductSummaryListRequest(List<String> ctns, PrxConstants.Sector sector,
                                     PrxConstants.Catalog catalog, String requestTag) {
        super(PRXSummaryDataServiceID, sector, catalog);
        this.ctns = ctns;
        this.mRequestTag = requestTag;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new PRXSummaryListResponse().parseJsonResponseData(jsonObject);
    }

    private String getString(List<String> ctns) {
        return TextUtils.join(",", ctns);
    }

    @Override
    public Map<String, String> getReplaceURLMap() {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("ctns", getString(ctns));
        replaceUrl.put("sector", getSector().toString());
        replaceUrl.put("catalog", getCatalog().toString());
        return replaceUrl;
    }
}
