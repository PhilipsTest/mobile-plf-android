package com.ecs.demotestuapp.fragments;


import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ClientID;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.integration.ECSOAuthProvider;
import com.philips.platform.ecs.integration.GrantType;
import com.philips.platform.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;

public class HybrisRefreshOAuthFragment extends BaseAPIFragment {

    String refreshToken = "refreshToken";

    EditText etSecret, etClient, etOAuthID;

    String secret, client;

    @Override
    public void onResume() {
        super.onResume();

        if (ECSDataHolder.INSTANCE.getEcsoAuthData() != null) {
            refreshToken = ECSDataHolder.INSTANCE.getEcsoAuthData().getRefreshToken();
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
            etClient.setText(ClientID.OIDC.getType());
        else
            etClient.setText(ClientID.JANRAIN.getType());
        etOAuthID = getLinearLayout().findViewWithTag("et_three");
        etOAuthID.setText(refreshToken);
    }

    public String getAuthID() {

        if (ECSDataHolder.INSTANCE.getEcsoAuthData() != null) {
            refreshToken = ECSDataHolder.INSTANCE.getEcsoAuthData().getRefreshToken();
        }
        return refreshToken;
    }


    public void executeRequest() {
        secret = getTextFromEditText(etSecret);
        client = getTextFromEditText(etClient);
        ECSOAuthProvider ecsoAuthProvider = new ECSOAuthProvider() {

            @Override
            public String getOAuthID() {
                return getAuthID();
            }

            @Override
            public ClientID getClientID() {
                if (ECSDataHolder.INSTANCE.getUserDataInterface().isOIDCToken())
                    return ClientID.OIDC;
                return ClientID.JANRAIN;
            }

            @Override
            public String getClientSecret() {
                return secret;
            }

            @Override
            public GrantType getGrantType() {
                return GrantType.REFRESH_TOKEN;
            }
        };

        ECSDataHolder.INSTANCE.getEcsServices().hybrisRefreshOAuth(ecsoAuthProvider, new ECSCallback<ECSOAuthData, Exception>() {
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


    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsoAuthData(null);
    }
}

