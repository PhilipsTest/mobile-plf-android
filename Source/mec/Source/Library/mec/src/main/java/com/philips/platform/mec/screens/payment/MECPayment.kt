/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.cdp.di.ecs.model.payment.ECSPayment
import java.io.Serializable

class MECPayment (var ecsPayment: ECSPayment): Serializable{
    var isSelected: Boolean =false
}