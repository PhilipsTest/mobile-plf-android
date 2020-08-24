package com.philips.cdp.productselection.productselectiontype;

/**
 * Vertical Applications must use this procedure to send the CTN's to the productSelection componenet.
 * <p/>
 * i,e Userdefiled class ({$HardcodedProductList}) must extend the {$ProductModelSelectionType} class to send the information
 * along with the component invoking API. For more details please the integration doc.
 * <p/>
 */
public class HardcodedProductList extends ProductModelSelectionType {

    public String[] mCtnList = null;

    public HardcodedProductList(String[] ctnList) {
        this.mCtnList = ctnList;
    }



    public String[] getHardCodedProductList() {
        return this.mCtnList;
    }
}
