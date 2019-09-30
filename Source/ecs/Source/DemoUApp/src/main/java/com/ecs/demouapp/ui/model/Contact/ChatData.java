package com.ecs.demouapp.ui.model.Contact;

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
