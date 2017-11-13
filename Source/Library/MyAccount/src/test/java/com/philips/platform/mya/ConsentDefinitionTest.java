package com.philips.platform.mya;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;


public class ConsentDefinitionTest {

    @Test
    public void constructor_tranformsLocaledInAccourdanceWithHSDPFormat() {
        ConsentDefinition canadaConsentDefinition = new ConsentDefinition("somelocalizedText", "someToolTip", "someConsentType", 2, Locale.CANADA);
        assertEquals("en-CA", canadaConsentDefinition.getLocale());
    }

}