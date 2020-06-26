package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Address
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attributes(
        val deliveryMode: DeliveryMode?,
        val deliveryUnits: Int?,
        val email: String?,
        val items: List<Item>?,
        val notifications: List<Notification>?,
        val pricing: Pricing?,
        val promotions: Promotions?,
        val units: Int?,
        val ageConsent: Boolean?,
        val ageConsentApplied: Boolean?,
        val appliedVouchers: List<Voucher>?,
        val applicableDeliveryModes: List<DeliveryMode>?,
        val deliveryAddress: Address
) : Parcelable