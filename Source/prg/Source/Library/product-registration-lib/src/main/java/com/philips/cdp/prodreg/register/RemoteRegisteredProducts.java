/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.register;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.registeredproducts.RegisteredResponse;
import com.philips.cdp.prodreg.model.registeredproducts.RegisteredResponseData;
import com.philips.cdp.prodreg.prxrequest.RegisteredProductsRequest;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoteRegisteredProducts {
    private static final String TAG = RemoteRegisteredProducts.class.getSimpleName();

    @NonNull
    ResponseListener getPrxResponseListenerForRegisteredProducts(final UserWithProducts userWithProducts, final LocalRegisteredProducts localRegisteredProducts, final RegisteredProductsListener registeredProductsListener) {
        return new ResponseListener() {
            @Override
            public void onResponseSuccess(final ResponseData responseData) {
                RegisteredResponse registeredDataResponse = (RegisteredResponse) responseData;
                RegisteredResponseData[] results = registeredDataResponse.getResults();
                Gson gson = getGson();
                RegisteredProduct[] registeredProducts = userWithProducts.getRegisteredProductsFromResponse(results, gson);
                if (registeredProducts != null && registeredProducts.length > 0) {
                    localRegisteredProducts.migrateLegacyCache(registeredProducts);
                }
                registeredProductsListener.getRegisteredProducts(localRegisteredProducts.getRegisteredProducts(), userWithProducts.getTimeStamp());
            }

            @Override
            public void onResponseError(PrxError prxError) {
                try {
                    if (prxError.getStatusCode() == PrxError.PrxErrorType.AUTHENTICATION_FAILURE.getId()) {
                        userWithProducts.onAccessTokenExpire(null);
                    } else
                        registeredProductsListener.getRegisteredProducts(localRegisteredProducts.getRegisteredProducts(), 0);
                } catch (Exception e) {
                    ProdRegLogger.e(TAG, e.getMessage());
                }
            }
        };
    }

    @NonNull
    protected Gson getGson() {
        return new Gson();
    }

    public void getRegisteredProducts(final Context mContext, final UserWithProducts userWithProducts, UserDataInterface userDataInterface, final RegisteredProductsListener registeredProductsListener) {
        RegisteredProductsRequest registeredProductsRequest = getRegisteredProductsRequest(userDataInterface);
        final RequestManager mRequestManager = getRequestManager(mContext);
        mRequestManager.executeRequest(registeredProductsRequest, getPrxResponseListenerForRegisteredProducts(userWithProducts, new LocalRegisteredProducts(userDataInterface), registeredProductsListener));
    }

    @NonNull
    protected RegisteredProductsRequest getRegisteredProductsRequest(UserDataInterface userDataInterface) {

        RegisteredProductsRequest registeredProductsRequest = new RegisteredProductsRequest(null, ProdRegConstants.REGISTEREDPRODUCTSREQUEST_SERVICE_ID, null, null ,userDataInterface.isOIDCToken());
        try {
            ArrayList<String> detailskey = new ArrayList<>();
            detailskey.add(UserDetailConstants.ACCESS_TOKEN);
            HashMap<String, Object> userDetailsMap = userDataInterface.getUserDetails(detailskey);
            String accessToken = userDetailsMap.get(UserDetailConstants.ACCESS_TOKEN).toString();
            registeredProductsRequest.setAccessToken(accessToken);
        } catch (Exception e) {
            ProdRegLogger.e(TAG, "Error in fetching accessToken :"+e.getMessage());
        }
        return registeredProductsRequest;
    }

    @NonNull
    protected RequestManager getRequestManager(final Context context) {
        AppInfraInterface appInfra = PRUiHelper.getInstance().getAppInfraInstance();
        PRXDependencies prxDependencies = new PRXDependencies(context, appInfra, ProdRegConstants.PRG_SUFFIX); // use existing appinfra instance
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(prxDependencies); // pass prxdependency

        return mRequestManager;
    }
}
