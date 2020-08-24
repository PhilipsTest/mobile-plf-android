package com.philips.cdp.productselection.productselectiontype;

import com.philips.cdp.prxclient.PrxConstants.Catalog;
import com.philips.cdp.prxclient.PrxConstants.Sector;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

/**
 */
public abstract class ProductModelSelectionType {

    public Catalog mCatalog;
    public Sector mSector;
    public SummaryModel[] mSummaryModelList;
    public String[] mCtnList = null;

    public Sector getSector() {
        return mSector;
    }

    public void setSector(Sector sector) {
        this.mSector = sector;
    }

    public Catalog getCatalog() {
        return mCatalog;
    }

    public void setCatalog(Catalog catalog) {
        this.mCatalog = catalog;
    }

    public void setProductModelList(SummaryModel[] summaryModelList) {
        this.mSummaryModelList = summaryModelList;
    }

    public SummaryModel[] getSummaryModelList() {
        return this.mSummaryModelList;
    }

    public String[] getHardCodedProductList() {
        return this.mCtnList;
    }
}
