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

package com.philips.cdp.prxclient.datamodels.contacts;

public class EmailData {
    private String label;
    private String contentPath;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getLabel() {
        return label;
    }

    public String getContentPath() {
        return contentPath;
    }
}
