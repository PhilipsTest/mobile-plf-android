package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Address
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attributes(
        val deliveryMode: DeliveryMode?,
        val applicableDeliveryModes: List<DeliveryMode>?,
        val deliveryUnits: Int?,
        val units: Int?,
        val email: String?,
        val items: List<ECSItem>?,
        val pricing: Pricing?,
        val promotions: Promotions?,
        val ageConsent: Boolean?,
        val ageConsentApplied: Boolean?,
        val marketingOptinApplied :Boolean?,
        val appliedVouchers: List<Voucher>?,
        val deliveryAddress: Address?,
        val notifications: List<Notification>?
) : Parcelable