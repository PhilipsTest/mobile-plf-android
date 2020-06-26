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

data class DeliveryModes(
    val collectionPoint: Boolean,
    val deliveryCost: DeliveryCost,
    val description: String,
    val id: String,
    val label: String
)