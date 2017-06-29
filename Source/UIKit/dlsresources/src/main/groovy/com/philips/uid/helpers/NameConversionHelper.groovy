package com.philips.uid.helpers

import com.philips.uid.DLSResourceConstants

import static org.junit.Assert.assertNotNull

class NameConversionHelper {
    static def getControlAttributeName(control, parent = null) {
        assertNotNull(control)
        def prefix = DLSResourceConstants.LIB_PREFIX
        prefix = prefix + (removeHyphensAndCapitalize(parent ?: ""))
        return prefix
                .concat(removeHyphensAndCapitalize(control.component))
                .concat(removeHyphensAndCapitalize(control.getContext()))
                .concat(control.getState().sum { it.capitalize() } ?: "")
                .concat(removeHyphensAndCapitalize(control.getControlProperty().item))
                .concat(removeHyphensAndCapitalize(control.getControlProperty().type))
    }

    static def removeHyphensAndCapitalize(string) {
        string.split("-").collect { it.capitalize() }.join("")
    }

    static def replaceHyphenWithUnderScores(string) {
        string.replaceAll("-", "_")
    }
}