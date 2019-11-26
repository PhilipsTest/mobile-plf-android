package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;

public class JanrainRefreshOAuthFragment extends BaseAPIFragment {

    String refreshToken = "refreshToken";

    EditText etSecret,etClient,etOAuthID;

    String secret,client;

    @Override
    public void onResume() {
        super.onResume();

        ECSDataHolder.INSTANCE.refreshJanRainID();


        if(ECSDataHolder.INSTANCE.getJanrainID()!=null){
            refreshToken = ECSDataHolder.INSTANCE.getJanrainID();
        }

        etSecret = getLinearLayout().findViewWithTag("et_one");
        etSecret.setText("secret");

        etClient = getLinearLayout().findViewWithTag("et_two");
        etClient.setText("mobile_android");

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

    public String getAuthID(){
        return   getTextFromEditText(etOAuthID);
    }

    public ECSOAuthProvider getECSOAuthProvider(){

        secret =getTextFromEditText(etSecret);
        client  = getTextFromEditText(etClient);

        ECSOAuthProvider ecsoAuthProvider = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return getAuthID();
            }

            @Override
            public String getClientID() {
                return client;
            }

            @Override
            public String getClientSecret() {
                return secret;
            }
        };

        return ecsoAuthProvider;
    }

    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsoAuthData(null);
    }
}
