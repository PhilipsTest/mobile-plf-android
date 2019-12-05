package com.philips.cdp.prodreg.model.registerproduct;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Attributes implements Serializable {

    @SerializedName("promotions")
    private Promotions promotions;

    @SerializedName("serialNumber")
    private String serialNumber;

    @SerializedName("purchased")
    private String purchased;

    @SerializedName("productId")
    private String productId;

    @SerializedName("documents")
    private List<Object> documents;

    @SerializedName("created")
    private String created;

    @SerializedName("extendedWarrantyMonths")
    private int extendedWarrantyMonths;

    @SerializedName("lastModified")
    private String lastModified;

    @SerializedName("micrositeId")
    private String micrositeId;

    public String getExtendedWarrantyExpires() {
        return extendedWarrantyExpires;
    }

    public void setExtendedWarrantyExpires(String extendedWarrantyExpires) {
        this.extendedWarrantyExpires = extendedWarrantyExpires;
    }

    @SerializedName("extendedWarrantyExpires")
    private String extendedWarrantyExpires;

    public void setPromotions(Promotions promotions) {
        this.promotions = promotions;
    }

    public Promotions getPromotions() {
        return promotions;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setPurchased(String purchased) {
        this.purchased = purchased;
    }

    public String getPurchased() {
        return purchased;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setDocuments(List<Object> documents) {
        this.documents = documents;
    }

    public List<Object> getDocuments() {
        return documents;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreated() {
        return created;
    }

    public void setExtendedWarrantyMonths(int extendedWarrantyMonths) {
        this.extendedWarrantyMonths = extendedWarrantyMonths;
    }

    public int getExtendedWarrantyMonths() {
        return extendedWarrantyMonths;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setMicrositeId(String micrositeId) {
        this.micrositeId = micrositeId;
    }

    public String getMicrositeId() {
        return micrositeId;
    }

    @Override
    public String toString() {
        return
                "Attributes{" +
                        "promotions = '" + promotions + '\'' +
                        ",serialNumber = '" + serialNumber + '\'' +
                        ",purchased = '" + purchased + '\'' +
                        ",productId = '" + productId + '\'' +
                        ",documents = '" + documents + '\'' +
                        ",created = '" + created + '\'' +
                        ",extendedWarrantyExpires = '" + extendedWarrantyExpires + '\'' +
                        ",extendedWarrantyMonths = '" + extendedWarrantyMonths + '\'' +
                        ",lastModified = '" + lastModified + '\'' +
                        ",micrositeId = '" + micrositeId + '\'' +
                        "}";
    }
}
