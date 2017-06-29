package com.philips.amwelluapp.utility;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.practice.Practice;

import com.americanwell.sdk.entity.provider.ProviderInfo;

import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.SDKCallback;
import com.philips.amwelluapp.intake.PTHMedication;
import com.philips.amwelluapp.intake.PTHMedicationCallback;
import com.philips.amwelluapp.login.PTHAuthentication;
import com.philips.amwelluapp.login.PTHLoginCallBack;

import com.philips.amwelluapp.practice.PTHPractice;
import com.philips.amwelluapp.practice.PTHPracticesListCallback;

import com.philips.amwelluapp.login.PTHGetConsumerObjectCallBack;
import com.philips.amwelluapp.providerslist.PTHProvidersListCallback;

import com.philips.amwelluapp.registration.PTHConsumer;
import com.philips.amwelluapp.sdkerrors.PTHSDKError;
import com.philips.amwelluapp.welcome.PTHInitializeCallBack;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PTHManager {
    private static PTHManager sPthManager = null;
    private AWSDK mAwsdk = null;
    private PTHConsumer mPTHConsumer= null;

    public PTHConsumer getPTHConsumer() {
        return mPTHConsumer;
    }

    public void setPTHConsumer(PTHConsumer mPTHConsumer) {
        this.mPTHConsumer = mPTHConsumer;
    }


    public static PTHManager getInstance() {
        if (sPthManager == null) {
            sPthManager = new PTHManager();
        }
        return sPthManager;
    }


    public AWSDK getAwsdk(Context context) throws AWSDKInstantiationException {
        if (mAwsdk == null) {
            mAwsdk = AWSDKFactory.getAWSDK(context);
        }
        return mAwsdk;
    }

    public void authenticate(Context context, String username, String password, String variable, final PTHLoginCallBack pthLoginCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).authenticate(username, password, variable, new SDKCallback<Authentication, SDKError>() {
            @Override
            public void onResponse(Authentication authentication, SDKError sdkError) {
                PTHAuthentication pthAuthentication = new PTHAuthentication();
                pthAuthentication.setAuthentication(authentication);

                PTHSDKError pthsdkError = new PTHSDKError();
                pthsdkError.setSdkError(sdkError);
                pthLoginCallBack.onLoginResponse(pthAuthentication, pthsdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                pthLoginCallBack.onLoginFailure(throwable);
            }
        });
    }

    public void initializeTeleHealth(Context context, final PTHInitializeCallBack pthInitializeCallBack) throws MalformedURLException, URISyntaxException, AWSDKInstantiationException, AWSDKInitializationException {
        final Map<AWSDK.InitParam, Object> initParams = new HashMap<>();
        /*initParams.put(AWSDK.InitParam.BaseServiceUrl, "https://sdk.myonlinecare.com");
        initParams.put(AWSDK.InitParam.ApiKey, "62f5548a"); //client key*/
        initParams.put(AWSDK.InitParam.BaseServiceUrl, "https://ec2-54-172-152-160.compute-1.amazonaws.com");
        initParams.put(AWSDK.InitParam.ApiKey, "3c0f99bf"); //client key


        getAwsdk(context).initialize(
                initParams, new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        PTHSDKError pthsdkError = new PTHSDKError();
                        pthsdkError.setSdkError(sdkError);
                        pthInitializeCallBack.onInitializationResponse(aVoid, pthsdkError);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        pthInitializeCallBack.onInitializationFailure(throwable);
                    }
                });
    }


    public void getConsumerObject(Context context,Authentication authentication,final PTHGetConsumerObjectCallBack pthGetConsumerObjectCallBack) throws AWSDKInstantiationException {

        getAwsdk(context).getConsumerManager().getConsumer(authentication, new SDKCallback<Consumer, SDKError>() {
            @Override
            public void onResponse(Consumer consumer, SDKError sdkError) {
                pthGetConsumerObjectCallBack.onReceiveConsumerObject(consumer,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                pthGetConsumerObjectCallBack.onError(throwable);
            }
        });
    }


    public void getPractices(Context context,Consumer consumer, final PTHPracticesListCallback pthPracticesListCallback) throws AWSDKInstantiationException {


        getAwsdk(context).getPracticeProvidersManager().getPractices(consumer, new SDKCallback<List<Practice>, SDKError>() {
            @Override
            public void onResponse(List<Practice> practices, SDKError sdkError) {
                PTHPractice pTHPractice = new PTHPractice();
                pTHPractice.setPractices(practices);
                pthPracticesListCallback.onPracticesListReceived(pTHPractice,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                pthPracticesListCallback.onPracticesListFetchError(throwable);
            }
        });
    }


    public void getProviderList(Context context, Consumer consumer, Practice practice,final PTHProvidersListCallback pthProvidersListCallback) throws AWSDKInstantiationException {

        getAwsdk(context).getPracticeProvidersManager().findProviders(consumer, practice, null, null, null, null, null, null, null, new SDKCallback<List<ProviderInfo>, SDKError>() {
            @Override
            public void onResponse(List<ProviderInfo> providerInfos, SDKError sdkError) {
                pthProvidersListCallback.onProvidersListReceived(providerInfos, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("onGetMedicationReceived","failure");
            }
        });

    }

    @VisibleForTesting
    public void setAwsdk(AWSDK awsdk) {
        this.mAwsdk = awsdk;
    }

    public void getMedication(Context context , Consumer consumer, final PTHMedicationCallback.PTHGetMedicationCallback pTHGetMedicationCallback ) throws AWSDKInstantiationException{
        getAwsdk(context).getConsumerManager().getMedications(consumer, new SDKCallback<List<Medication>, SDKError>() {
            @Override
            public void onResponse(List<Medication> medications, SDKError sdkError) {
                PTHMedication pTHMedication = new PTHMedication();
                pTHMedication.setMedicationList(medications);
                pTHGetMedicationCallback.onGetMedicationReceived(pTHMedication,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("onGetMedicationReceived","failure");
            }
        });

    }
}
