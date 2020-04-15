/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.common

import com.philips.cdp.di.ecs.error.ECSError


class MecError( val exception: Exception?, val ecsError: ECSError?, val mECRequestType :MECRequestType?)
