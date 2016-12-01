/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package cdp.philips.com.mydemoapp.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import cdp.philips.com.mydemoapp.database.annotations.DatabaseConstructor;
import cdp.philips.com.mydemoapp.database.datatypes.MeasurementDetailType;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@DatabaseTable
public class OrmMeasurementDetailType implements Serializable {

    private static final long serialVersionUID = 11L;

    @DatabaseField(id = true, canBeNull = false)
    private int id;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseConstructor
    OrmMeasurementDetailType() {
    }

    public OrmMeasurementDetailType(final int id, final String momentType) {
        this.id = id;
        this.description = momentType;
    }

    public String getType() {
        return description;
    }

    @Override
    public String toString() {
        return "[OrmMeasurementDetailType, id=" + id + ", description=" + description + "]";
    }
}
