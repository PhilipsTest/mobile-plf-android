package com.philips.platform.pif.chi;

import com.philips.platform.pif.chi.datamodel.ConsentState;

public interface PostConsentTypeCallback {
    void onPostConsentFailed(ConsentError error);

    void onPostConsentSuccess(ConsentState consentState);
}
