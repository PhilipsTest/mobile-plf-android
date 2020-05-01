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

package com.philips.platform.ecs.microService

import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.config.ECSConfig
import com.philips.platform.ecs.model.products.ECSProduct

interface ECSServiceProvider {

    /**
     * Configure ecs.
     * @since 1.0
     * @param ecsCallback the ecs callback containing boolean response. If configuration is success returns true else false
     */
    fun configureECS(ecsCallback: ECSCallback<Boolean?, Exception?>?)

    /**
     * Configure ecs to get configuration.
     *
     * @param ecsCallback the ecs callback containing ECSConfig object
     */
    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig?, Exception?>?)

    /**
     * Fetch product details containing assets and disclaimer details
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSProduct object
     */
    fun fetchProductDetails(product: ECSProduct?, ecsCallback: ECSCallback<ECSProduct?, java.lang.Exception?>?)
}