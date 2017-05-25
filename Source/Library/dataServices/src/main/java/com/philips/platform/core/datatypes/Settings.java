/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.core.datatypes;

import java.io.Serializable;

/**
 * Data-Base Interface for creating Settings Object
 */
public interface Settings extends BaseAppData, Serializable {

     String UNIT ="Unit";
     String LOCALE="Locale";

    String getUnit();
    String getLocale();

    void setUnit(String unit);
    void setLocale(String locale);


    int getId();
    void setID(int id);
}
