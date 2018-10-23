/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ths.appointment;

import com.philips.platform.ths.providerslist.THSProviderInfo;

import java.util.Date;

public interface THSAppointmentInterface {
    THSProviderInfo getTHSProviderInfo();
    Date getAppointmentDate();
}
