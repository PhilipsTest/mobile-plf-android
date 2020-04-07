package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ClientType;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.integration.GrantType;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;
import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;

public class HybrisOAthAuthenticationFragment extends BaseAPIFragment {

    String refreshToken = "refreshToken";

    EditText etSecret, etClient, etOAuthID;

    String secret, client;

    @Override
    public void onResume() {
        super.onResume();

        if (ECSDataHolder.INSTANCE.getJanrainID() != null) {
            refreshToken = ECSDataHolder.INSTANCE.getJanrainID();
        }
        etSecret = getLinearLayout().findViewWithTag("et_one");
        if (ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.PRODUCTION)) {
            etSecret.setText("prod_inapp_54321");
        } else if ((ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.ACCEPTANCE))||ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.STAGING)){
            etSecret.setText("acc_inapp_12345");
        } else {
            etSecret.setText("secret");
        }

        etClient = getLinearLayout().findViewWithTag("et_two");
        if (ECSDataHolder.INSTANCE.getUserDataInterface().isOIDCToken())
            etClient.setText(ClientType.OIDC.getType());
        else
            etClient.setText(ClientType.JANRAIN.getType());
        etOAuthID = getLinearLayout().findViewWithTag("et_three");
        etOAuthID.setText(refreshToken);
    }


    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().hybrisOAthAuthentication(getECSOAuthProvider(), new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData ecsoAuthData) {

                ECSDataHolder.INSTANCE.setEcsoAuthData(ecsoAuthData);
                gotoResultActivity(getJsonStringFromObject(ecsoAuthData));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e, ECSError ecsError) {

                String errorString = getFailureString(e, ecsError);
                gotoResultActivity(errorString);
                getProgressBar().setVisibility(View.GONE);
            }
        });

    }

    public String getAuthID() {
        return getTextFromEditText(etOAuthID);
    }

    public ECSOAuthProvider getECSOAuthProvider() {

        secret = getTextFromEditText(etSecret);
        client = getTextFromEditText(etClient);

        ECSOAuthProvider ecsoAuthProvider = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return getAuthID();
            }

            @Override
            public ClientType getClientType() {
                if (ECSDataHolder.INSTANCE.getUserDataInterface().isOIDCToken()){
                    return ClientType.OIDC;
                }
                return ClientType.JANRAIN;
            }

            @Override
            public String getClientSecret() {
                return secret;
            }

            @Override
            public GrantType getGrantType() {
                if (ECSDataHolder.INSTANCE.getUserDataInterface().isOIDCToken()) {
                    return GrantType.OIDC;
                }
                return GrantType.JANRAIN;
            }
        };

        return ecsoAuthProvider;
    }

    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsoAuthData(null);
    }
}
