package com.philips.platform.ccb.model

class CCBConversation(var token: String, var conversationId: String, val conversationToken: String, val streamUrl: String, val expires_in: Int, val referenceGrammarId: String) {
}