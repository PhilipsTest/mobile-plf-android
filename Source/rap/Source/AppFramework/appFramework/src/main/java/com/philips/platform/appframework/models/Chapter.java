/*
 *  Copyright (c) Koninklijke Philips N.V., 2017
 *  All rights are reserved. Reproduction or dissemination
 *  in whole or in part is prohibited without the prior written
 *  consent of the copyright holder.
 */

package com.philips.platform.appframework.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by philips on 10/02/17.
 */

public class Chapter implements Serializable{
    private static final long serialVersionUID = -1038722280847305323L;
    @SerializedName("chapterName")
    private String chapterName;

    @SerializedName("commonComponents")
    private ArrayList<CommonComponent> commonComponentsList;

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public ArrayList<CommonComponent> getCommonComponentsList() {
        return commonComponentsList;
    }

    public void setCommonComponentsList(ArrayList<CommonComponent> commonComponentsList) {
        this.commonComponentsList = commonComponentsList;
    }
}
