/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.core.datatypes;

import java.io.Serializable;

/**
 * DataBase Interface for creating MomentDetail Object
 */
public interface MomentDetail extends BaseAppData, Serializable {

    String getType();

    String getValue();

    void setValue(String value);

    Moment getMoment();
}
