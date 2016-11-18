/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.core;

import android.support.annotation.NonNull;

import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementDetailType;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.MeasurementGroupDetail;
import com.philips.platform.core.datatypes.MeasurementGroupDetailType;
import com.philips.platform.core.datatypes.MeasurementType;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.MomentDetailType;
import com.philips.platform.core.datatypes.MomentType;
import com.philips.platform.core.datatypes.SynchronisationData;

import org.joda.time.DateTime;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public interface BaseAppDataCreator {

    @NonNull
    Moment createMoment(@NonNull final String creatorId, @NonNull final String subjectId, @NonNull final MomentType type);

    @NonNull
    Moment createMomentWithoutUUID(@NonNull final String creatorId, @NonNull final String subjectId, @NonNull final MomentType type);

    @NonNull
    MomentDetail createMomentDetail(@NonNull final MomentDetailType type, @NonNull final Moment moment);

    /*@NonNull
    Measurement createMeasurement(@NonNull final MeasurementType type, @NonNull final Moment moment);*/

    Measurement createMeasurement(@NonNull final MeasurementType type, @NonNull final MeasurementGroup measurementGroup);

    @NonNull
    MeasurementDetail createMeasurementDetail(@NonNull final MeasurementDetailType type, @NonNull final Measurement measurement);

    @NonNull
    MeasurementGroup createMeasurementGroup(@NonNull final MeasurementGroup measurementGroup);

    @NonNull
    MeasurementGroup createMeasurementGroup(@NonNull final Moment moment);

    @NonNull
    MeasurementGroupDetail createMeasurementGroupDetail(@NonNull final MeasurementGroupDetailType type, @NonNull final MeasurementGroup measurementGroup);

    @NonNull
    SynchronisationData createSynchronisationData(@NonNull final String guid, final boolean inactive, @NonNull final DateTime lastModifiedTime, final int version);

}
