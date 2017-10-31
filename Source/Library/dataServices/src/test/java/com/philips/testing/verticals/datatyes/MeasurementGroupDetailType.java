package com.philips.testing.verticals.datatyes;

import java.util.ArrayList;
import java.util.List;

public class MeasurementGroupDetailType {
    public static final String UNKNOWN = "UNKNOWN";
    public static final String TEMP_OF_DAY = "TEMP_OF_DAY";
    public static final String REFERENCE_GROUP_ID ="referenceGroupId";
    public static final String GROUP_ID="groupId";

    public static int getIDFromDescription(String description) {
        if (description == null) {
            return -1;
        }
        switch (description.toUpperCase()) {
            case UNKNOWN:
                return -1;
            case TEMP_OF_DAY:
                return 77;
            case REFERENCE_GROUP_ID:
                return 78;
            case GROUP_ID:
                return 79;
            default:
                return -1;
        }
    }

    public static String getDescriptionFromID(int ID) {
        switch (ID) {
            case -1:
                return UNKNOWN;
            case 46:
                return TEMP_OF_DAY;
            case 78:
                return REFERENCE_GROUP_ID;
            case 79:
                return GROUP_ID;
            default:
                return UNKNOWN;
        }
    }

    public static List<String> getMeasurementGroupDetailType() {
        List<String> measurementGroupDetailType = new ArrayList<>();
        measurementGroupDetailType.add(UNKNOWN);
        measurementGroupDetailType.add(TEMP_OF_DAY);
        measurementGroupDetailType.add(REFERENCE_GROUP_ID);
        measurementGroupDetailType.add(GROUP_ID);
        return measurementGroupDetailType;
    }
}
