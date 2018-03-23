package com.philips.cdp.registration.consents;

import android.content.Context;

import com.philips.cdp.registration.R;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

public class URConsentProviderTest {

    @Mock
    private Context mContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ShouldStringsMatch_FetchMarketingConsentDefinition() throws Exception {
        URConsentProvider.fetchMarketingConsentDefinition();
        Mockito.verify(mContext).getString(R.string.reg_DLS_OptIn_Promotional_Message_Line1);
        Mockito.verify(mContext).getString(R.string.reg_DLS_PhilipsNews_Description_Text);
    }

    @Test
    public void Should_FetchMarketingConsentDefinition() throws Exception {
        ConsentDefinition consentDefinition = URConsentProvider.fetchMarketingConsentDefinition();
        Assert.assertNotNull(consentDefinition);
    }

    @Test
    public void ShouldOverride_USR_MARKETING_CONSENT() throws Exception {
        String USR_MARKETING_CONSENT = "USR_MARKETING_CONSENT_UPDATED";
        final ArrayList<String> types = new ArrayList<>();
        types.add(USR_MARKETING_CONSENT);
        ConsentDefinition newConsentDefinition = new ConsentDefinition(0, 0, types, 1);
        Assert.assertNotNull(newConsentDefinition);
    }
}