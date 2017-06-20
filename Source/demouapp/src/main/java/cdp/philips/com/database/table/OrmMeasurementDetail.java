/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package cdp.philips.com.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.core.datatypes.MeasurementDetail;

import java.io.Serializable;

import cdp.philips.com.database.annotations.DatabaseConstructor;


/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@DatabaseTable
public class OrmMeasurementDetail implements MeasurementDetail, Serializable {

    private static final long serialVersionUID = 11L;

    @DatabaseField(generatedId = true, unique = true,canBeNull = false)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private OrmMeasurementDetailType type;

    @DatabaseField(canBeNull = false)
    private String value;

    @DatabaseField(foreign = true, foreignAutoRefresh = false, canBeNull = false)
    private OrmMeasurement ormMeasurement;

    @DatabaseConstructor
    OrmMeasurementDetail() {
    }

    public OrmMeasurementDetail(final OrmMeasurementDetailType type, final OrmMeasurement ormMeasurement) {
        this.type = type;
        this.ormMeasurement = ormMeasurement;
        this.id = -1;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getType() {
        return type.getType();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public OrmMeasurement getMeasurement() {
        return ormMeasurement;
    }

    @Override
    public String toString() {
        return "[OrmMeasurementDetail, id=" + id + ", ormMeasurementDetailType=" + type + ", value=" + value + ", ormMeasurement=" + ormMeasurement + "]";
    }
}
