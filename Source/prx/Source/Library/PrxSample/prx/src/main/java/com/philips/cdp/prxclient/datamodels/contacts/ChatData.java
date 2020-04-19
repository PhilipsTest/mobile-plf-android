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

public class ChatData {
    private String openingHoursWeekdays;
    private String openingHoursSaturday;
    private String content;

    public void setOpeningHoursWeekdays(String openingHoursWeekdays) {
        this.openingHoursWeekdays = openingHoursWeekdays;
    }

    public void setOpeningHoursSaturday(String openingHoursSaturday) {
        this.openingHoursSaturday = openingHoursSaturday;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOpeningHoursWeekdays() {
        return openingHoursWeekdays;
    }

    public String getOpeningHoursSaturday() {
        return openingHoursSaturday;
    }

    public String getContent() {
        return content;
    }
}
