/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/

package com.philips.platform.dscdemo.database.datatypes;

import java.util.ArrayList;
import java.util.List;

public class MeasurementType {
    public static final String UNKNOWN = "UNKNOWN";
    public static final String TEMPERATURE = "Temperature";
    public static final String AMOUNT = "AMOUNT";
    public static final String WEIGHT = "WEIGHT";
    public static final String DURATION = "DURATION";
    public static final String RELATIVE_HUMIDITY = "RELATIVE_HUMIDITY";
    public static final String LENGTH = "LENGTH";
    public static final String DEEP_SLEEP_TIME = "DeepSleepTime";
    public static final String TOTAL_SLEEP_TIME = "TotalSleepTime";


    public static int getIDFromDescription(String description) {
        if (description == null) {
            return -1;
        }

        if (description.equalsIgnoreCase(TEMPERATURE)) {
            return 41;
        } else if (description.equalsIgnoreCase(DEEP_SLEEP_TIME)) {
            return 47;
        } else if (description.equalsIgnoreCase(TOTAL_SLEEP_TIME)) {
            return 48;
        } else {
            return -1;
        }
    }

    public static String getUnitFromDescription(String description) {
        if (description == null) {
            return "default";
        }
        switch (description.toUpperCase()) {
            case UNKNOWN:
                return "UnknownUnit";
            case TEMPERATURE:
                return "\u2103"; /** Unicode of Degree Celsius */
            case AMOUNT:
                return "ml";
            case WEIGHT:
                return "kg";
            case DURATION:
                return "seconds";
            case RELATIVE_HUMIDITY:
                return "RelativeHumidity";
            case LENGTH:
                return "cm";
            case DEEP_SLEEP_TIME:
                return "milliseconds";
            case TOTAL_SLEEP_TIME:
                return "milliseconds";
            default:
                return "UnknownUnit";
        }
    }

    public static String getDescriptionFromID(int ID) {
        switch (ID) {

            case -1:
                return UNKNOWN;
            case 41:
                return TEMPERATURE;
            case 42:
                return AMOUNT;
            case 43:
                return WEIGHT;
            case 44:
                return DURATION;
            case 45:
                return RELATIVE_HUMIDITY;
            case 46:
                return LENGTH;
            case 47:
                return DEEP_SLEEP_TIME;
            case 48:
                return TOTAL_SLEEP_TIME;
            default:
                return UNKNOWN;
        }
    }

    public static List<String> getMeasurementTypes() {
        List<String> measurementType = new ArrayList<>();
        measurementType.add(UNKNOWN);
        measurementType.add(TEMPERATURE);
        measurementType.add(AMOUNT);
        measurementType.add(WEIGHT);
        measurementType.add(DURATION);
        measurementType.add(RELATIVE_HUMIDITY);
        measurementType.add(LENGTH);
        measurementType.add(DEEP_SLEEP_TIME);
        measurementType.add(TOTAL_SLEEP_TIME);
        return measurementType;
    }
}
