package com.ecs.demouapp.ui.model.Contact;

import java.io.Serializable;

public class ContactPhone implements Serializable {
    private String phoneNumber;
    private String openingHoursWeekdays;
    private String openingHoursSaturday;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOpeningHoursWeekdays(String openingHoursWeekdays) {
        this.openingHoursWeekdays = openingHoursWeekdays;
    }

    public void setOpeningHoursSaturday(String openingHoursSaturday) {
        this.openingHoursSaturday = openingHoursSaturday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOpeningHoursWeekdays() {
        return openingHoursWeekdays;
    }

    public String getOpeningHoursSaturday() {
        return openingHoursSaturday;
    }
}
