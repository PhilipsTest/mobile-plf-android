/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.catk.datamodel;

import com.philips.platform.pif.chi.datamodel.ConsentStates;

import org.joda.time.DateTime;

public class ConsentDTO {
    private String locale;
    private ConsentStates status;
    private String type;
    private int version;
    private DateTime timestamp;

    public ConsentDTO(String locale, ConsentStates status, String type, int version) {
        this.locale = locale;
        this.status = status;
        this.type = type;
        this.version = version;
    }

    public ConsentDTO(String locale, ConsentStates status, String type, int version, DateTime timestamp) {
        this(locale, status, type, version);
        this.timestamp = timestamp;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String  locale) {
        this.locale = locale;
    }

    public ConsentStates getStatus() {
        return status;
    }

    public void setStatus(ConsentStates status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsentDTO)) return false;

        ConsentDTO consent = (ConsentDTO) o;

        if (version != consent.version) return false;
        if (locale != null ? !locale.equals(consent.locale) : consent.locale != null) return false;
        if (status != consent.status) return false;
        if (type != null ? !type.equals(consent.type) : consent.type != null) return false;
        return timestamp != null ? timestamp.equals(consent.timestamp) : consent.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = locale != null ? locale.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
