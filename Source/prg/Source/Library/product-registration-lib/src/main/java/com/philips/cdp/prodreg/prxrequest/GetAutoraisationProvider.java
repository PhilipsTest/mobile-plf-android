package com.philips.cdp.prodreg.prxrequest;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.logging.ProdRegLogger;

import java.util.Map;

public class GetAutoraisationProvider {
    static void getAuthoraisationProvider(String url, Map<String, String> headers, boolean isOidcToken) {
        ProdRegLogger.i("Product Registration Request"," isOidcToken "+ isOidcToken);

        if(isOidcToken){
            if (url.contains(ProdRegConstants.CHINA_DOMAIN)){
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.OIDC_AUTHORIZATION_PROVIDER_VAL_CN);
            } else {
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.OIDC_AUTHORIZATION_PROVIDER_VAL_EU);
                ProdRegLogger.i("Product Registration Request",url+ " does not contain china domain.");
            }
        }else{
            if (url.contains(ProdRegConstants.CHINA_DOMAIN)){
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.JANRAIN_AUTHORIZATION_PROVIDER_VAL_CN);
            } else {
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.JANRAIN_AUTHORIZATION_PROVIDER_VAL_EU);
                ProdRegLogger.i("Product Registration Request",url+ " does not contain china domain.");
            }
        }
    }
}
