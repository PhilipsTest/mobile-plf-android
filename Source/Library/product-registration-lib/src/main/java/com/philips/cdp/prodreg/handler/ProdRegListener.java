package com.philips.cdp.prodreg.handler;

import com.philips.cdp.prxclient.response.ResponseData;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public interface ProdRegListener {

    void onProdRegSuccess(ResponseData responseData);

    void onProdRegFailed(ProdRegError prodRegError);
}
