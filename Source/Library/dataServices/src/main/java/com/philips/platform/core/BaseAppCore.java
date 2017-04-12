/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.core;

import android.support.annotation.NonNull;

import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.Insight;
import com.philips.platform.core.datatypes.InsightMetadata;
import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.MeasurementGroupDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.platform.core.monitors.DBMonitors;
import com.philips.platform.core.monitors.ErrorMonitor;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.BlobDataCreater;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.Backend;
import com.philips.platform.datasync.blob.BlobMetaData;
import com.philips.platform.datasync.insights.UCoreInsight;

import org.joda.time.DateTime;

import java.util.Map;

import javax.inject.Inject;

public class BaseAppCore implements BaseAppDataCreator,BlobDataCreater {
    @Inject
    Eventing eventing;

    @Inject
    BaseAppDataCreator database;

    @Inject
    DBMonitors dbMonitors;

    @Inject
    Backend appBackend;

    @Inject
    ErrorMonitor errorMonitor;

    @Inject
    public BaseAppCore() {
        DataServicesManager.getInstance().getAppComponant().injectBaseAppCore(this);
    }

    public void start() {
        dbMonitors.start(eventing);
        appBackend.start(eventing);
        errorMonitor.start(eventing);
    }

    public void stop() {
        appBackend.stop();
        dbMonitors.stop();
        errorMonitor.stop();
    }

    @NonNull
    public Moment createMoment(final String creatorId, final String subjectId, String type) {
        return database.createMoment(creatorId, subjectId, type);
    }


    @NonNull
    public MomentDetail createMomentDetail(final String type, final Moment moment) {
        return database.createMomentDetail(type, moment);
    }

    @NonNull
    public Measurement createMeasurement(final String type, final MeasurementGroup measurementGroup) {
        return database.createMeasurement(type, measurementGroup);
    }

    @NonNull
    public MeasurementDetail createMeasurementDetail(final String type, final Measurement measurement) {
        return database.createMeasurementDetail(type, measurement);
    }

    @NonNull
    @Override
    public MeasurementGroup createMeasurementGroup(MeasurementGroup measurementGroup) {
        return database.createMeasurementGroup(measurementGroup);
    }

    @NonNull
    @Override
    public MeasurementGroup createMeasurementGroup(Moment moment) {
        return database.createMeasurementGroup(moment);
    }

    @NonNull
    @Override
    public MeasurementGroupDetail createMeasurementGroupDetail(String type, MeasurementGroup measurementGroup) {
        return database.createMeasurementGroupDetail(type, measurementGroup);
    }

    @NonNull
    @Override
    public SynchronisationData createSynchronisationData(String guid, boolean inactive, DateTime lastModifiedTime, int version) {
        return database.createSynchronisationData(guid, inactive, lastModifiedTime, version);
    }

    @NonNull
    @Override
    public ConsentDetail createConsentDetail(String type, String status, String version, String deviceIdentificationNumber) {
        return database.createConsentDetail(type, status, version,
                deviceIdentificationNumber);
    }

    @NonNull
    @Override
    public Settings createSettings(String type, String value) {
        return database.createSettings(type, value);
    }


    @NonNull
    @Override
    public Characteristics createCharacteristics(String type, String value, Characteristics characteristics) {
        return database.createCharacteristics(type, value, characteristics);
    }

    @NonNull
    @Override
    public Characteristics createCharacteristics(String type, String value) {
        return database.createCharacteristics(type, value);
    }

    @NonNull
    @Override
    public Insight createInsight() {
        return database.createInsight();
    }

    @NonNull
    @Override
    public InsightMetadata createInsightMetaData(String key, String value ,Insight insight) {
        return database.createInsightMetaData(key,value,insight);
    }

    @NonNull
    @Override
    public BlobMetaData createBlobMetaData() {
        return ((BlobDataCreater) database).createBlobMetaData();
    }


}