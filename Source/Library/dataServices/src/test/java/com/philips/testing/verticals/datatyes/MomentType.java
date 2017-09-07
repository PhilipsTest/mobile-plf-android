package com.philips.testing.verticals.datatyes;

import java.util.ArrayList;
import java.util.List;

public class MomentType {
    public static final String UNKNOWN = "UNKNOWN";
    public static final String TREATMENT = "TREATMENT";
    public static final String USER_INFO = "USER_INFO";
    public static final String PHOTO = "PHOTO";
    public static final String NOTE = "NOTE";
    public static final String TEMPERATURE = "TEMPERATURE";

    public static int getIDFromDescription(String description) {
        if (description == null) {
            return -1;
        }
        switch (description.toUpperCase()) {
            case UNKNOWN:
                return -1;
            case TREATMENT:
                return 20;
            case USER_INFO:
                return 21;
            case PHOTO:
                return 22;
            case NOTE:
                return 23;
            case TEMPERATURE:
                return 24;
            default:
                return -1;
        }
    }

    public static String getDescriptionFromID(int ID) {
        switch (ID) {

            case -1:
                return UNKNOWN;
            case 20:
                return TREATMENT;
            case 21:
                return USER_INFO;
            case 22:
                return PHOTO;
            case 23:
                return NOTE;
            case 24:
                return TEMPERATURE;

            default:
                return UNKNOWN;
        }
    }

    public static List<String> getMomentTypes() {
        List<String> momentTypes = new ArrayList<>();
        momentTypes.add(UNKNOWN);
        momentTypes.add(TREATMENT);
        momentTypes.add(USER_INFO);
        momentTypes.add(PHOTO);
        momentTypes.add(NOTE);
        momentTypes.add(TEMPERATURE);
        return momentTypes;
    }
}
