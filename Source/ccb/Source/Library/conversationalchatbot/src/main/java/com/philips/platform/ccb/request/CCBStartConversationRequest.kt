package com.philips.platform.ccb.request

import com.android.volley.Request
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.rest.CCBRequestInterface
import java.util.HashMap

class CCBStartConversationRequest : CCBRequestInterface {
    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.START_CONVERSATION
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ "ew0KICAiYWxnIjogIlJTMjU2IiwNCiAgImtpZCI6ICJMaXMyNEY4cUFxa2VQeW1ZUk9xVzd3anJKdFEiLA0KICAieDV0IjogIkxpczI0RjhxQXFrZVB5bVlST3FXN3dqckp0USIsDQogICJ0eXAiOiAiSldUIg0KfQ.ew0KICAiYm90IjogIldDQi1FVVcxLVhMTkNBUkUtVUstMDEtTlAiLA0KICAic2l0ZSI6ICJnOFllX29OcnFzNCIsDQogICJjb252IjogIkIyYTZjWUZaUEZpR1gwZVBQRW5TQjgtayIsDQogICJuYmYiOiAxNTkzNjAyMjMwLA0KICAiZXhwIjogMTU5MzYwNTgzMCwNCiAgImlzcyI6ICJodHRwczovL2RpcmVjdGxpbmUuYm90ZnJhbWV3b3JrLmNvbS8iLA0KICAiYXVkIjogImh0dHBzOi8vZGlyZWN0bGluZS5ib3RmcmFtZXdvcmsuY29tLyINCn0.DoSNoVdz6CnQUvPm7uXPTg_u6nr3UssjlSti6SRsj6LhDMRq_ArWN2aq2a_QCTiDTiD7HyUbBLDkcF8mW6WofdU3DDPkYEWtEy1v1vcUzBeucoqGUkXE8vDSQaZGAchRo_ExXwXJkbjUqM7SmJgrIit-zCO-SGuVsxAvzhmX-cYSWZKGxoSNRhfT9u9ar_PvgFYvWR__jndg6FJD2HQyZJ-Yjgh2yX1GomMrUrFH1ClC51HPLDfNSYyiCYe1rGTdGLh5GcGC6HAJ6xWkLN8jOZU8yb6JQrNAQy1K5lGguHpvQCjC1Fnj5qwG8UCD_UG5hY0DNLMRTUbSbWGDzeXyZw"
        return headers
    }

    override fun getBody(): String? {
        return null
    }

    override fun getMethodType(): Int {
        return Request.Method.POST
    }

}