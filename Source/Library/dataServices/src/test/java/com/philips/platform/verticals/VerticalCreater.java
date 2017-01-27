package com.philips.platform.verticals;

import android.support.annotation.NonNull;

import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.datatypes.UserCharacteristics;
import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.MeasurementGroupDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.testing.verticals.datatyes.MomentType;
import com.philips.testing.verticals.table.OrmCharacteristics;
import com.philips.testing.verticals.table.OrmConsent;
import com.philips.testing.verticals.table.OrmMoment;
import com.philips.testing.verticals.table.OrmMomentType;
import com.philips.testing.verticals.table.OrmSynchronisationData;

import org.joda.time.DateTime;

/**
 * Created by 310218660 on 12/12/2016.
 */

public class VerticalCreater implements BaseAppDataCreator {
    @NonNull
    @Override
    public OrmMoment createMoment(@NonNull String creatorId, @NonNull String subjectId, @NonNull String type) {
        final OrmMomentType ormMomentType = new OrmMomentType(MomentType.getIDFromDescription(type), type);
        return new OrmMoment(creatorId, subjectId, ormMomentType);
    }

    @NonNull
    @Override
    public Moment createMomentWithoutUUID(@NonNull String creatorId, @NonNull String subjectId, @NonNull String type) {
        final OrmMomentType ormMomentType = new OrmMomentType(MomentType.getIDFromDescription(type), type);
        return new OrmMoment(creatorId, subjectId, ormMomentType);
    }

    @NonNull
    @Override
    public MomentDetail createMomentDetail(@NonNull String type, @NonNull Moment moment) {
        /*OrmMomentDetailType ormMomentDetailType = new OrmMomentDetailType(MomentDetailType.getIDFromDescription(type), type);
        return new OrmMomentDetail(ormMomentDetailType,(OrmMoment) moment);*/
        return null;
    }

    @Override
    public Measurement createMeasurement(@NonNull String type, @NonNull MeasurementGroup measurementGroup) {
        /*OrmMeasurementType ormMeasurementType = new OrmMeasurementType(MeasurementType.getIDFromDescription(type),
                type,
                MeasurementType.getUnitFromDescription(type));
        return new OrmMeasurement(ormMeasurementType,(OrmMeasurementGroup) measurementGroup);*/
        return null;
    }

    @NonNull
    @Override
    public MeasurementDetail createMeasurementDetail(@NonNull String type, @NonNull Measurement measurement) {
        /*OrmMeasurementDetailType ormMeasurementDetailType = new OrmMeasurementDetailType(MeasurementDetailType.getIDFromDescription(type), type);
        return new OrmMeasurementDetail(ormMeasurementDetailType,(OrmMeasurement)measurement);*/
        return null;
    }

    @NonNull
    @Override
    public MeasurementGroup createMeasurementGroup(@NonNull MeasurementGroup measurementGroup) {
        return null;
    }

    @NonNull
    @Override
    public MeasurementGroup createMeasurementGroup(@NonNull Moment moment) {
        //return new OrmMeasurementGroup((OrmMoment)moment);
        return null;
    }

    @NonNull
    @Override
    public MeasurementGroupDetail createMeasurementGroupDetail(@NonNull String type, @NonNull MeasurementGroup measurementGroup) {
       /* OrmMeasurementGroupDetailType ormMeasurementGroupDetailType = new OrmMeasurementGroupDetailType(MeasurementGroupDetailType.getIDFromDescription(type), type);
        return new OrmMeasurementGroupDetail(ormMeasurementGroupDetailType,(OrmMeasurementGroup)measurementGroup);*/
        return null;
    }

    @NonNull
    @Override
    public SynchronisationData createSynchronisationData(@NonNull String guid, boolean inactive, @NonNull DateTime lastModifiedTime, int version) {
        return new OrmSynchronisationData(guid, inactive, lastModifiedTime, version);
    }

    @NonNull
    @Override
    public Consent createConsent(@NonNull String creatorId) {
        return new OrmConsent(creatorId);
    }

    @NonNull
    @Override
    public ConsentDetail createConsentDetail(@NonNull String type, @NonNull String status, @NonNull String version, String deviceIdentificationNumber, boolean isSynchronized, @NonNull Consent consent) {
        return new ConsentDetailImpl(type, status, version, deviceIdentificationNumber, isSynchronized, consent);
    }

    @NonNull
    @Override
    public Settings createSettings(String type, String value) {
        return null;
    }

    @NonNull
    @Override
    public UserCharacteristics createCharacteristics(@NonNull String creatorId) {
        return new OrmCharacteristics(creatorId);
    }

    @NonNull
    @Override
    public Characteristics createCharacteristicsDetails(@NonNull String type, @NonNull String value, @NonNull UserCharacteristics userCharacteristics, @NonNull Characteristics characteristics) {
        return null;
    }

    @NonNull
    @Override
    public Characteristics createCharacteristicsDetails(@NonNull String type, @NonNull String value, @NonNull UserCharacteristics userCharacteristics) {
        return null;
    }
}
