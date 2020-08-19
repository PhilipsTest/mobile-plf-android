/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.ecs.microService.model.cart.ECSItem

class MECCartProductReview(val entries: ECSItem, val overallRating: String, val overallReview: String)
