package com.philips.platform.ccb.model

data class BotResponseData(
    val activities: List<Activity>,
    val watermark: String?
)