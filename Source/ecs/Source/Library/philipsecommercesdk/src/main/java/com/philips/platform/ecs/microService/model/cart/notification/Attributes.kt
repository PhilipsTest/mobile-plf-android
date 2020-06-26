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

package com.philips.platform.ecs.microService.model.cart.notification

data class Attributes(
    val ageConsent: Boolean,
    val ageConsentApplied: Boolean,
    val appliedVouchers: List<AppliedVoucher>,
    val deliveryAddress: DeliveryAddress,
    val deliveryModes: DeliveryModes,
    val deliveryUnits: Int,
    val email: String,
    val items: List<Item>,
    val marketingOptinApplied: Boolean,
    val notifications: List<Notification>,
    val pricing: Pricing,
    val promotions: Promotions,
    val units: Int
)