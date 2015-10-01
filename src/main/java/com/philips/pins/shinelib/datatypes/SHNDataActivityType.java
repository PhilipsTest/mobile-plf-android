package com.philips.pins.shinelib.datatypes;

/**
 * Created by 310188215 on 13/05/15.
 */
public class SHNDataActivityType extends SHNData {
    public enum SHNCM3ActivityType {
        SHNCM3ActivityTypeUnspecified,
        SHNCM3ActivityTypeOther,
        SHNCM3ActivityTypeWalk,
        SHNCM3ActivityTypeCycleIndoor,
        SHNCM3ActivityTypeRun,
        SHNCM3ActivityTypeCycleOutdoor
    }

    public enum SHNActivityType {
        SHNActivityTypeUnspecified,
        SHNActivityTypeCycle,
        SHNActivityTypeRun,
        SHNActivityTypeWalk,
        SHNActivityTypeSleep
    }

    private final SHNActivityType wearableActivityType;
    private final SHNActivityType manualActivityType;
    private final SHNCM3ActivityType cm3ActivityType;

    public SHNDataActivityType(SHNActivityType wearableActivityType, SHNActivityType manualActivityType, SHNCM3ActivityType cm3ActivityType) {
        this.wearableActivityType = wearableActivityType;
        this.manualActivityType = manualActivityType;
        this.cm3ActivityType = cm3ActivityType;
    }

    public SHNActivityType getWearableActivityType() {
        return wearableActivityType;
    }

    public SHNActivityType getManualActivityType() {
        return manualActivityType;
    }

    public SHNCM3ActivityType getCm3ActivityType() {
        return cm3ActivityType;
    }

    @Override
    public SHNDataType getSHNDataType() {
        return SHNDataType.ActivityType;
    }

    @Override
    public String toString() {
        return "ActivityType Wearable: " + getWearableActivityType().name() + " Manual: " + getManualActivityType().name() + " CM3: " + getCm3ActivityType().name();
    }
}
