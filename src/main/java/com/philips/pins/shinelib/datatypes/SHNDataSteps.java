package com.philips.pins.shinelib.datatypes;

/**
 * Created by 310188215 on 13/05/15.
 */
public class SHNDataSteps extends SHNData {
    private final int steps;

    public SHNDataSteps(int steps) {

        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public SHNDataType getSHNDataType() {
        return SHNDataType.Steps;
    }

    @Override
    public String toString() {
        return "Steps: " + getSteps();
    }
}
