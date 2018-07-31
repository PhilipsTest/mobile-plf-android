package com.philips.platform.appinfra.utility;

import com.philips.platform.appinfra.timesync.TimeSyncSntpClient;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AIUtility {

    /**
     * Converts string to date
     *
     * @param strDate string that has to be converted to date
     * @return Date Converted date
     */
    public static Date convertStringToDate(String strDate, String... patterns) {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
        if (patterns.length == 0) {
            patterns = new String[3];
            patterns[0] = "yyyy-MM-dd HH:mm:ss.SSSZ";
            patterns[1] = "yyyy-MM-dd HH:mm:ss Z";
            patterns[2] = "yyyy-MM-dd HH:mm:ss.SSSZ";
        }
        for (String pattern : patterns) {
            dateTimeFormatterBuilder.appendOptional(DateTimeFormat.forPattern(pattern).getParser());
        }
        return dateTimeFormatterBuilder.toFormatter().parseDateTime(strDate).toDate();
    }

    /**
     * Converts date to string
     *
     * @param date date that has to be converted to string
     * @return String Converted string
     */
    public static String convertDateToString(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(TimeSyncSntpClient.UTC));
        return String.valueOf(dateFormat.format(date));
    }
}
