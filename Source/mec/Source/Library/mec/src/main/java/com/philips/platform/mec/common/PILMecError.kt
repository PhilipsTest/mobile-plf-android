package com.philips.platform.mec.common

import com.philips.platform.ecs.microService.error.ECSError

class PILMecError(val ecsError: ECSError, val mECRequestType:MECRequestType?) {
}