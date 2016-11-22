package com.philips.platform.baseapp.screens.datasevices.database.table;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.baseapp.screens.datasevices.database.EmptyForeignCollection;
import com.philips.platform.baseapp.screens.datasevices.database.annotations.DatabaseConstructor;
import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.ConsentDetail;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collection;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@DatabaseTable
public class OrmConsent implements Consent, Serializable {

    public static final long serialVersionUID = 11L;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String creatorId;

    @DatabaseField(canBeNull = false)
    private DateTime dateTime = new DateTime();


    @ForeignCollectionField(eager = true)
    private ForeignCollection<OrmConsentDetail> ormConsentDetails = new EmptyForeignCollection<>();

    @DatabaseConstructor
    OrmConsent() {
    }

    public OrmConsent(@NonNull final String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    @Override
    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public DateTime getDateTime() {
        return dateTime;
    }

    @Override
    public Collection<? extends OrmConsentDetail> getConsentDetails() {
        return ormConsentDetails;
    }


    @Override
    public void addConsentDetails(final ConsentDetail consentDetail) {
        ormConsentDetails.add((OrmConsentDetail) consentDetail);
    }

    @Override
    public String toString() {
        return "[OrmConsent, id=" + id + ", creatorId=" + creatorId + ", dateTime=" + dateTime + "]";
    }
}
