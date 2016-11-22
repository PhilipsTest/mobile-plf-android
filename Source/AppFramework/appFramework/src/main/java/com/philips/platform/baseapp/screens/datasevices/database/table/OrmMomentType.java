/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.baseapp.screens.datasevices.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.core.datatypes.MomentType;
import com.philips.platform.datasevices.database.annotations.DatabaseConstructor;

import java.io.Serializable;


/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@DatabaseTable
public class OrmMomentType implements Serializable {
    static final long serialVersionId = 11L;
    static final long serialVersionUID = 11L;

    @DatabaseField(id = true, canBeNull = false)
    private int id;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseConstructor
    OrmMomentType() {
    }

    public OrmMomentType(final MomentType momentType) {
        this.id = momentType.getId();
        this.description = momentType.getDescription();
    }

    public MomentType getType() {
        return MomentType.fromId(id);
    }

    @Override
    public String toString() {
        return "[OrmMomentType, id=" + id + ", description=" + description + "]";
    }
}
