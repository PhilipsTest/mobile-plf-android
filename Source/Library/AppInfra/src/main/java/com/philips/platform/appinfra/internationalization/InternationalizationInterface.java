/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.internationalization;

//import android.os.LocaleList;

import java.util.Locale;

/**
 * The interface Local interface.
 */
public interface InternationalizationInterface {

    /**
     * Gets country.
     *
     * @return the country
     */
//    public String getCountry();

    /**
     * Gets local .
     *
     * @return the local
     */
    Locale getUILocale();

    /**
     * Gets String locale value .
     *
     * @return the String with Concatenated format
     */
    String getUILocaleString();

}
