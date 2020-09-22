/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.cdp.prxclient.datamodels.cdls

import java.io.Serializable

class ContactPhone(
        var phoneNumber: String? = null,
        var openingHoursWeekdays: String? = null,
        var openingHoursSaturday: String? = null
) : Serializable