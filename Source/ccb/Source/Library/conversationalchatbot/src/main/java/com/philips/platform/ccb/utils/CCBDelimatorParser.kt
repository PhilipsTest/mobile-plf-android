package com.philips.platform.ccb.utils

import android.util.Log
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory

class CCBDelimatorParser : SpanFactory {
    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any? {
        Log.i("CCBDelimatorParser","CCBDelimatorParser")
        return configuration.spansFactory()
    }
}