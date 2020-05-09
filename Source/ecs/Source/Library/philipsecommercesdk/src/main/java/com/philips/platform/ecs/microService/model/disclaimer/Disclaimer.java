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
package com.philips.platform.ecs.microService.model.disclaimer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * The type Disclaimer contains disclaimer contents.
 */
public class Disclaimer implements Serializable {


    @SerializedName("disclaimerText")
    @Expose
    private String disclaimerText;


    @SerializedName("code")
    @Expose
    private String code;


    @SerializedName("rank")
    @Expose
    private String rank;


    @SerializedName("referenceName")
    @Expose
    private String referenceName;


   /* @SerializedName("disclaimElements")
    @Expose
    private List<String> disclaimElements = new ArrayList<String>();*/


    /**
     * No args constructor for use in serialization
     */
    public Disclaimer() {
    }

    public Disclaimer(String disclaimerText, String code, String rank, String referenceName, List<String> disclaimElements) {
        this.disclaimerText = disclaimerText;
        this.code = code;
        this.rank = rank;
        this.referenceName = referenceName;
        //this.disclaimElements = disclaimElements;
    }

    public String getDisclaimerText() {
        return disclaimerText;
    }

    public void setDisclaimerText(String disclaimerText) {
        this.disclaimerText = disclaimerText;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

   /* public List<String> getDisclaimElements() {
        return disclaimElements;
    }

    public void setDisclaimElements(List<String> disclaimElements) {
        this.disclaimElements = disclaimElements;
    }*/

}
