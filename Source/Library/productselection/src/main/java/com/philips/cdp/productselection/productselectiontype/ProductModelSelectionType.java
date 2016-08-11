package com.philips.cdp.productselection.productselectiontype;

import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

/**
 * Created by naveen@philips.com on 12-Feb-16.
 */
public abstract class ProductModelSelectionType extends UappLaunchInput {

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
