/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.baseapp.screens.dataservices.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.baseapp.screens.dataservices.database.annotations.DatabaseConstructor;

import java.io.Serializable;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@DatabaseTable
public class OrmMomentDetailType implements Serializable {
    private static final long serialVersionUID = 11L;

    @DatabaseField(id = true, canBeNull = false)
    private int id;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseConstructor
    OrmMomentDetailType() {
    }

    public OrmMomentDetailType(final int id,final String momentType) {
        this.id = id;
        this.description = momentType;
    }

    public String getType() {
        return description;
    }

    @Override
    public String toString() {
        return "[OrmMomentDetailType, id=" + id + ", description=" + description + "]";
    }
}
