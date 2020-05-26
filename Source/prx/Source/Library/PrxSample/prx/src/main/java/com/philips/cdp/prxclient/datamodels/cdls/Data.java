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

package com.philips.cdp.prxclient.datamodels.cdls;

import java.util.List;

public class Data {
    private List<ContactPhone> phone;
    private List<ChatData> chat;
    private List<EmailData> email;

    public List<ContactPhone> getPhone() {
        return phone;
    }
    public void setPhone(List<ContactPhone> phone) {
        this.phone = phone;
    }

    public List<ChatData> getChat() {
        return chat;
    }

    public List<EmailData> getEmail() {
        return email;
    }
}
