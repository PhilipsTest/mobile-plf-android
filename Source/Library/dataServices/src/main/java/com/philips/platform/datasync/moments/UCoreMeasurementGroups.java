package com.philips.platform.datasync.moments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by 310218660 on 11/18/2016.
 */

public class UCoreMeasurementGroups {

    @Expose
    @Nullable
    private List<UCoreMeasurementGroups> measurementGroups;

    @Expose
    @Nullable
    private List<UCoreMeasurement> measurements;

    @Expose
    @Nullable
    private List<UCoreMeasurementGroupDetail> details;

    @Nullable
    public List<UCoreMeasurementGroupDetail> getMeasurementGroupDetails() {
        return details;
    }

    public void setDetails(@Nullable final List<UCoreMeasurementGroupDetail> details) {
        this.details = details;
    }

    @Nullable
    public List<UCoreMeasurementGroups> getMeasurementGroups() {
        return measurementGroups;
    }

    public void setMeasurementGroups(@Nullable List<UCoreMeasurementGroups> measurementGroups) {
        this.measurementGroups = measurementGroups;
    }

    @Nullable
    public List<UCoreMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(@Nullable List<UCoreMeasurement> measurements) {
        this.measurements = measurements;
    }
}
