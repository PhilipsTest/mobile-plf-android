package com.philips.platform.ccb.model

data class Activity(
    val attachments: List<Attachment>?,
    val channelId: String,
    val conversation: Conversation,
    val entities: List<Any>?,
    val from: From,
    val id: String,
    val replyToId: String?,
    var text: String,
    val timestamp: String,
    val type: String,
    val recipient: Recipient?
)