/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.utility;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.UploadAttachment;
import com.americanwell.sdk.entity.billing.CreatePaymentRequest;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.entity.consumer.RemindOptions;
import com.americanwell.sdk.entity.enrollment.ConsumerEnrollment;
import com.americanwell.sdk.entity.enrollment.DependentEnrollment;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.insurance.HealthPlan;
import com.americanwell.sdk.entity.insurance.Relationship;
import com.americanwell.sdk.entity.insurance.Subscription;
import com.americanwell.sdk.entity.insurance.SubscriptionUpdateRequest;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.AvailableProviders;
import com.americanwell.sdk.entity.provider.EstimatedVisitCost;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.entity.visit.Vitals;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.MatchmakerCallback;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.StartVisitCallback;
import com.americanwell.sdk.manager.ValidationReason;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.URConfigurationConstants;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.appointment.THSAvailableProviderCallback;
import com.philips.platform.ths.appointment.THSAvailableProviderList;
import com.philips.platform.ths.appointment.THSAvailableProvidersBasedOnDateCallback;
import com.philips.platform.ths.cost.ApplyCouponCallback;
import com.philips.platform.ths.cost.CreateVisitCallback;
import com.philips.platform.ths.cost.THSVisit;
import com.philips.platform.ths.insurance.THSInsuranceCallback;
import com.philips.platform.ths.insurance.THSSubscription;
import com.philips.platform.ths.insurance.THSSubscriptionUpdateRequest;
import com.philips.platform.ths.intake.THSCondition;
import com.philips.platform.ths.intake.THSConditionsCallBack;
import com.philips.platform.ths.intake.THSConditionsList;
import com.philips.platform.ths.intake.THSMedication;
import com.philips.platform.ths.intake.THSMedicationCallback;
import com.philips.platform.ths.intake.THSSDKCallback;
import com.philips.platform.ths.intake.THSSDKValidatedCallback;
import com.philips.platform.ths.intake.THSUpdateConditionsCallback;
import com.philips.platform.ths.intake.THSUpdateConsumerCallback;
import com.philips.platform.ths.intake.THSUpdateVitalsCallBack;
import com.philips.platform.ths.intake.THSVisitContext;
import com.philips.platform.ths.intake.THSVisitContextCallBack;
import com.philips.platform.ths.intake.THSVitalSDKCallback;
import com.philips.platform.ths.intake.THSVitals;
import com.philips.platform.ths.intake.selectimage.THSDeleteDocumentCallback;
import com.philips.platform.ths.intake.selectimage.THSUploadDocumentCallback;
import com.philips.platform.ths.login.THSAuthentication;
import com.philips.platform.ths.login.THSGetConsumerObjectCallBack;
import com.philips.platform.ths.login.THSLoginCallBack;
import com.philips.platform.ths.payment.THSAddress;
import com.philips.platform.ths.payment.THSCreatePaymentRequest;
import com.philips.platform.ths.payment.THSPaymentCallback;
import com.philips.platform.ths.payment.THSPaymentMethod;
import com.philips.platform.ths.pharmacy.THSConsumerShippingAddressCallback;
import com.philips.platform.ths.pharmacy.THSGetPharmaciesCallback;
import com.philips.platform.ths.pharmacy.THSPreferredPharmacyCallback;
import com.philips.platform.ths.pharmacy.THSUpdatePharmacyCallback;
import com.philips.platform.ths.pharmacy.THSUpdateShippingAddressCallback;
import com.philips.platform.ths.practice.THSPracticeCallback;
import com.philips.platform.ths.practice.THSPracticeList;
import com.philips.platform.ths.practice.THSPracticesListCallback;
import com.philips.platform.ths.providerdetails.THSFetchEstimatedCostCallback;
import com.philips.platform.ths.providerdetails.THSMatchMakingCallback;
import com.philips.platform.ths.providerdetails.THSProviderDetailsCallback;
import com.philips.platform.ths.providerslist.THSOnDemandSpeciality;
import com.philips.platform.ths.providerslist.THSOnDemandSpecialtyCallback;
import com.philips.platform.ths.providerslist.THSProviderInfo;
import com.philips.platform.ths.providerslist.THSProvidersListCallback;
import com.philips.platform.ths.registration.THSCheckConsumerExistsCallback;
import com.philips.platform.ths.registration.THSConsumerWrapper;
import com.philips.platform.ths.registration.THSRegistrationFragment;
import com.philips.platform.ths.registration.dependantregistration.THSConsumer;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.sdkerrors.THSSDKPasswordError;
import com.philips.platform.ths.settings.THSGetAppointmentsCallback;
import com.philips.platform.ths.settings.THSVisitReportAttachmentCallback;
import com.philips.platform.ths.settings.THSVisitReportDetailCallback;
import com.philips.platform.ths.settings.THSVisitReportListCallback;
import com.philips.platform.ths.visit.THSCancelVisitCallBack;
import com.philips.platform.ths.visit.THSStartVisitCallback;
import com.philips.platform.ths.visit.THSVisitSummary;
import com.philips.platform.ths.visit.THSVisitSummaryCallbacks;
import com.philips.platform.ths.welcome.THSInitializeCallBack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;


public class THSManager {
    private static THSManager sTHSManager = null;
    private AWSDK mAwsdk = null;
    private THSConsumerWrapper mTHSConsumerWrapper = null;
    private THSVisitContext mVisitContext = null;
    private boolean isMatchMakingVisit;
    private THSConsumer mThsConsumer;
    private THSConsumer mThsParentConsumer;
    private boolean mIsReturningUser = true;


    @VisibleForTesting
    private User mUser;

    private AppInfraInterface mAppInfra;

    public AppTaggingInterface getThsTagging() {
        return mAppTaggingInterface;
    }



    AppTaggingInterface mAppTaggingInterface;

    @VisibleForTesting
    public boolean TEST_FLAG = false;

    public boolean isMatchMakingVisit() {
        return isMatchMakingVisit;
    }

    public void setMatchMakingVisit(boolean matchMakingVisit) {
        isMatchMakingVisit = matchMakingVisit;
    }


    public THSVisitContext getPthVisitContext() {
        return mVisitContext;
    }

    public void setVisitContext(THSVisitContext mVisitContext) {
        this.mVisitContext = mVisitContext;
    }


    public THSConsumerWrapper getPTHConsumer() {
       return mTHSConsumerWrapper;
    }

    public void setPTHConsumer(THSConsumerWrapper mTHSConsumerWrapper) {
        this.mTHSConsumerWrapper = mTHSConsumerWrapper;
    }


    public static THSManager getInstance() {
        if (sTHSManager == null) {
            sTHSManager = new THSManager();
        }
        return sTHSManager;
    }


    public AWSDK getAwsdk(Context context) throws AWSDKInstantiationException {
        if (mAwsdk == null) {
            mAwsdk = AWSDKFactory.getAWSDK(context);
        }
        return mAwsdk;
    }

    void authenticate(Context context, String username, String password, String variable, final THSLoginCallBack<THSAuthentication, THSSDKError> THSLoginCallBack) throws AWSDKInstantiationException {
        AmwellLog.i(AmwellLog.LOG,"Login - SDK API Called");
        getAwsdk(context).authenticate(username, password, variable, new SDKCallback<Authentication, SDKError>() {
            @Override
            public void onResponse(Authentication authentication, SDKError sdkError) {
                AmwellLog.i(AmwellLog.LOG,"Login - On Response");
                THSAuthentication THSAuthentication = new THSAuthentication();
                THSAuthentication.setAuthentication(authentication);

                THSSDKError THSSDKError = new THSSDKError();
                THSSDKError.setSdkError(sdkError);
                THSLoginCallBack.onLoginResponse(THSAuthentication, THSSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                THSLoginCallBack.onLoginFailure(throwable);
            }
        });
    }

    public void authenticateMutualAuthToken(Context context,final THSLoginCallBack<THSAuthentication, THSSDKError> THSLoginCallBack) throws AWSDKInstantiationException {
        String token = getThsConsumer().getHsdpUUID()+":" + getAppName() +":"+ getThsConsumer().getHsdoToken();
        getAwsdk(context).authenticateMutual(token, new SDKCallback<Authentication, SDKError>() {
            @Override
            public void onResponse(Authentication authentication, SDKError sdkError) {
                AmwellLog.i(AmwellLog.LOG,"Login - On Response");
                THSAuthentication THSAuthentication = new THSAuthentication();
                THSAuthentication.setAuthentication(authentication);

                THSSDKError THSSDKError = new THSSDKError();
                THSSDKError.setSdkError(sdkError);
                THSLoginCallBack.onLoginResponse(THSAuthentication, THSSDKError);

            }

            @Override
            public void onFailure(Throwable throwable) {
                THSLoginCallBack.onLoginFailure(throwable);
            }
        });
    }

    public void completeEnrollment(Context context,final THSAuthentication thsAuthentication, final THSGetConsumerObjectCallBack thsGetConsumerObjectCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().completeEnrollment(thsAuthentication.getAuthentication(),null,null,null, new SDKCallback<Consumer, SDKPasswordError>() {
            @Override
            public void onResponse(Consumer consumer, SDKPasswordError sdkPasswordError) {
                setConsumer(consumer);
                thsGetConsumerObjectCallBack.onReceiveConsumerObject(consumer,sdkPasswordError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsGetConsumerObjectCallBack.onError(throwable);
            }
        });
    }

    public void checkConsumerExists(final Context context, final THSCheckConsumerExistsCallback<Boolean, THSSDKError> thsCheckConsumerExistsCallback) throws AWSDKInstantiationException {

        getAwsdk(context).getConsumerManager().checkConsumerExists(getThsConsumer().getHsdpUUID(), new SDKCallback<Boolean, SDKError>() {
            @Override
            public void onResponse(Boolean aBoolean, SDKError sdkError) {
                if(!getThsConsumer().isDependent()) {
                    setIsReturningUser(aBoolean);
                }
                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                thsCheckConsumerExistsCallback.onResponse(aBoolean,thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsCheckConsumerExistsCallback.onFailure(throwable);
            }
        });
    }

    @NonNull
    public User getUser(Context context) {
        if(TEST_FLAG){
            return mUser;
        }
        return new User(context);
    }

    public void enrollConsumer(final Context context, Date dateOfBirth,String firstName,String lastName,Gender gender,State state,final THSSDKValidatedCallback<THSConsumerWrapper, SDKError> thssdkValidatedCallback) throws AWSDKInstantiationException {
        final ConsumerEnrollment newConsumerEnrollment = getConsumerEnrollment(context, dateOfBirth, firstName, lastName, gender, state);

        getAwsdk(context).getConsumerManager().enrollConsumer(newConsumerEnrollment,
                new SDKValidatedCallback<Consumer, SDKPasswordError>() {
                    @Override
                    public void onValidationFailure(Map<String, ValidationReason> map) {
                        AmwellLog.i(AmwellLog.LOG,"validationFail");
                        thssdkValidatedCallback.onValidationFailure(map);
                    }

                    @Override
                    public void onResponse(Consumer consumer, SDKPasswordError sdkPasswordError) {
                        setIsReturningUser(true);
                        THSConsumerWrapper thsConsumer = new THSConsumerWrapper();
                        thsConsumer.setConsumer(consumer);
                        setPTHConsumer(thsConsumer);
                        AmwellLog.i(AmwellLog.LOG,"onGetPaymentMethodResponse");
                        thssdkValidatedCallback.onResponse(thsConsumer,sdkPasswordError);

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        AmwellLog.i(AmwellLog.LOG,"onFail");
                        thssdkValidatedCallback.onFailure(throwable);
                    }
                });
    }

    @NonNull
    private ConsumerEnrollment getConsumerEnrollment(Context context, Date dateOfBirth, String firstName, String lastName, Gender gender, State state) throws AWSDKInstantiationException {
        final ConsumerEnrollment newConsumerEnrollment = getAwsdk(context).getConsumerManager().getNewConsumerEnrollment();
        newConsumerEnrollment.setAcceptedDisclaimer(true);

        newConsumerEnrollment.setSourceId(getThsConsumer().getHsdpUUID());
        newConsumerEnrollment.setConsumerAuthKey(getThsConsumer().getHsdpUUID());

        newConsumerEnrollment.setEmail(getThsConsumer().getEmail());
        newConsumerEnrollment.setPassword("Password123*");

        newConsumerEnrollment.setDob(SDKLocalDate.valueOf(dateOfBirth));

        newConsumerEnrollment.setFirstName(firstName);
        newConsumerEnrollment.setGender(gender);
        newConsumerEnrollment.setLastName(lastName);

        newConsumerEnrollment.setLegalResidence(state);
        return newConsumerEnrollment;
    }

    public void enrollDependent(Context context, Date dateOfBirth, String firstName, String lastName, Gender gender, State state, final THSSDKValidatedCallback<THSConsumerWrapper, SDKError> thssdkValidatedCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().enrollDependent(getDependantEnrollment(context, dateOfBirth, firstName, lastName, gender), new SDKValidatedCallback<Consumer, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thssdkValidatedCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(Consumer consumer, SDKError sdkError) {
                THSConsumerWrapper thsConsumerWrapper = new THSConsumerWrapper();
                thsConsumerWrapper.setConsumer(consumer);

                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                setConsumer(consumer);

                thssdkValidatedCallback.onResponse(thsConsumerWrapper,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
               thssdkValidatedCallback.onFailure(throwable);
            }
        });
    }

    @NonNull
    private DependentEnrollment getDependantEnrollment(Context context, Date dateOfBirth, String firstName, String lastName, Gender gender) throws AWSDKInstantiationException {
        final DependentEnrollment newConsumerEnrollment = getAwsdk(context).getConsumerManager().getNewDependentEnrollment(getThsParentConsumer().getConsumer());

        newConsumerEnrollment.setSourceId(getThsConsumer().getHsdpUUID());




        newConsumerEnrollment.setDob(SDKLocalDate.valueOf(dateOfBirth));

        newConsumerEnrollment.setFirstName(firstName);
        newConsumerEnrollment.setGender(gender);
        newConsumerEnrollment.setLastName(lastName);


        return newConsumerEnrollment;
    }


    public void initializeTeleHealth(Context context, final THSInitializeCallBack<Void, THSSDKError> THSInitializeCallBack) throws MalformedURLException, URISyntaxException, AWSDKInstantiationException, AWSDKInitializationException {
        final Map<AWSDK.InitParam, Object> initParams = new HashMap<>();
       /*initParams.put(AWSDK.InitParam.BaseServiceUrl, "https://sdk.myonlinecare.com");
        initParams.put(AWSDK.InitParam.ApiKey, "62f5548a"); //client key*/

       /*initParams.put(AWSDK.InitParam.BaseServiceUrl, "https://stagingOC169.mytelehealth.com/");
        initParams.put(AWSDK.InitParam.ApiKey, "dc573250"); //client key*/

        initParams.put(AWSDK.InitParam.BaseServiceUrl, "https://iot11.amwellintegration.com");
        initParams.put(AWSDK.InitParam.ApiKey, "3c0f99bf"); //client key

        AmwellLog.i(AmwellLog.LOG,"Initialize - SDK API Called");
        getAwsdk(context).initialize(
                initParams, new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        AmwellLog.i(AmwellLog.LOG,"Initialize - onGetPaymentMethodResponse from Amwell SDK");
                        THSSDKError THSSDKError = new THSSDKError();
                        THSSDKError.setSdkError(sdkError);
                        THSInitializeCallBack.onInitializationResponse(aVoid, THSSDKError);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        THSInitializeCallBack.onInitializationFailure(throwable);
                    }
                });
    }

    public boolean isSDKInitialized(Context context) throws AWSDKInstantiationException {
        return getAwsdk(context).getConfiguration().isServiceKeyCollected();
    }

    public void getOnDemandSpecialities(Context context, PracticeInfo practiceInfo, String searchItem, final THSOnDemandSpecialtyCallback<List<THSOnDemandSpeciality>, THSSDKError> thsOnDemandSpecialtyCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getPracticeProvidersManager().getOnDemandSpecialties(getPTHConsumer().getConsumer(), practiceInfo, searchItem, new SDKCallback<List<OnDemandSpecialty>, SDKError>() {
            @Override
            public void onResponse(List<OnDemandSpecialty> onDemandSpecialties, SDKError sdkError) {


                List<THSOnDemandSpeciality> listOfThsSpecialities = new ArrayList<>();

                for (OnDemandSpecialty onDemandSpeciality:onDemandSpecialties
                     ) {
                    setMatchMakingVisit(true);
                    THSOnDemandSpeciality thsOnDemandSpeciality = new THSOnDemandSpeciality();
                    thsOnDemandSpeciality.setOnDemandSpecialty(onDemandSpeciality);
                    listOfThsSpecialities.add(thsOnDemandSpeciality);
                }

                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                thsOnDemandSpecialtyCallback.onResponse(listOfThsSpecialities,thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void getVisitContextWithOnDemandSpeciality(Context context, final THSOnDemandSpeciality thsOnDemandSpeciality, final THSVisitContextCallBack<THSVisitContext, THSSDKError> thsVisitContextCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().getVisitContext(getPTHConsumer().getConsumer(), thsOnDemandSpeciality.getOnDemandSpecialty(), new SDKCallback<VisitContext, SDKError>() {
            @Override
            public void onResponse(VisitContext visitContext, SDKError sdkError) {
                THSVisitContext thsVisitContext = new THSVisitContext();
                thsVisitContext.setVisitContext(visitContext);

                setVisitContext(thsVisitContext);

                THSSDKError thsSDKError = new THSSDKError();
                thsSDKError.setSdkError(sdkError);

                thsVisitContextCallback.onResponse(thsVisitContext,thsSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsVisitContextCallback.onFailure(throwable);
            }
        });
    }

    public void getVisitContext(Context context, final THSProviderInfo thsProviderInfo, final THSVisitContextCallBack<THSVisitContext, THSSDKError> THSVisitContextCallBack) throws MalformedURLException, URISyntaxException, AWSDKInstantiationException, AWSDKInitializationException {

        getAwsdk(context).getVisitManager().getVisitContext(getPTHConsumer().getConsumer(), thsProviderInfo.getProviderInfo(), new SDKCallback<VisitContext, SDKError>() {
                    @Override
                    public void onResponse(VisitContext visitContext, SDKError sdkError) {

                        THSVisitContext THSVisitContext = new THSVisitContext();
                        THSVisitContext.setVisitContext(visitContext);

                        THSSDKError THSSDKError = new THSSDKError();
                        THSSDKError.setSdkError(sdkError);

                        THSVisitContextCallBack.onResponse(THSVisitContext, THSSDKError);
                        setVisitContext(THSVisitContext);

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        THSVisitContextCallBack.onFailure(throwable);
                    }
                });
    }


    //TODO: What happens when getConsumer is null
    public void getVitals(Context context, final THSVitalSDKCallback<THSVitals, THSSDKError> thsVitalCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getVitals(getPTHConsumer().getConsumer(),getPthVisitContext().getVisitContext(), new SDKCallback<Vitals, SDKError>() {
            @Override
            public void onResponse(Vitals vitals, SDKError sdkError) {
                THSVitals thsVitals = new THSVitals();
                thsVitals.setVitals(vitals);

                THSSDKError THSSDKError = new THSSDKError();
                THSSDKError.setSdkError(sdkError);

                thsVitalCallBack.onResponse(thsVitals, THSSDKError);

            }

            @Override
            public void onFailure(Throwable throwable) {
                thsVitalCallBack.onFailure(throwable);

            }
        });
    }

    /*public void createVisit(Context context, THSVisitContext pthVisitContext, final THSgetPaymentMethodValidatedCallback pthsdkValidatedCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().createVisit(pthVisitContext.getPthVisitContext(), new SDKValidatedCallback<Visit, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                pthsdkValidatedCallback.onValidationFailure(map);
            }

            @Override
            public void onGetPaymentMethodResponse(Visit visit, SDKError sdkError) {
                THSSDKError pthSDKError = new THSSDKError();
                pthSDKError.setSdkError(sdkError);
                pthsdkValidatedCallback.onGetPaymentMethodResponse(visit,sdkError);
            }

            @Override
            public void onGetPaymentFailure(Throwable throwable) {
                pthsdkValidatedCallback.onGetPaymentFailure(throwable);
            }
        });
    }*/

    public void getAppointments(Context context, SDKLocalDate sdkLocalDate, final THSGetAppointmentsCallback<List<Appointment>, THSSDKError> thsGetAppointmentsCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getAppointments(getPTHConsumer().getConsumer(),sdkLocalDate,new SDKCallback<List< Appointment >, SDKError>(){

            @Override
            public void onResponse(List<Appointment> appointments, SDKError sdkError) {
                thsGetAppointmentsCallback.onResponse(appointments,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsGetAppointmentsCallback.onFailure(throwable);
            }
        });

    }


    public void getConsumerObject(Context context,Authentication authentication,final THSGetConsumerObjectCallBack THSGetConsumerObjectCallBack) throws AWSDKInstantiationException {

        getAwsdk(context).getConsumerManager().getConsumer(authentication, new SDKCallback<Consumer, SDKError>() {
            @Override
            public void onResponse(Consumer consumer, SDKError sdkError) {
                setConsumer(consumer);
                THSGetConsumerObjectCallBack.onReceiveConsumerObject(consumer,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                THSGetConsumerObjectCallBack.onError(throwable);
            }
        });
    }

    public void getConditions(Context context, final THSConditionsCallBack<THSConditionsList,THSSDKError> thsConditionsCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getConditions(getPTHConsumer().getConsumer(), new SDKCallback<List<Condition>, SDKError>() {
            @Override
            public void onResponse(List<Condition> conditions, SDKError sdkError) {
                THSConditionsList thsConditions = new THSConditionsList();
                thsConditions.setConditions(conditions);

                THSSDKError THSSDKError = new THSSDKError();
                THSSDKError.setSdkError(sdkError);

                thsConditionsCallBack.onResponse(thsConditions, THSSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsConditionsCallBack.onFailure(throwable);
            }
        });
    }


    public void getPractices(Context context, final THSPracticesListCallback THSPracticesListCallback) throws AWSDKInstantiationException {


        getAwsdk(context).getPracticeProvidersManager().getPractices(getPTHConsumer().getConsumer(), new SDKCallback<List<Practice>, SDKError>() {
            @Override
            public void onResponse(List<Practice> practices, SDKError sdkError) {
                THSPracticeList pTHPractice = new THSPracticeList();
                pTHPractice.setPractices(practices);
                THSPracticesListCallback.onPracticesListReceived(pTHPractice,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                THSPracticesListCallback.onPracticesListFetchError(throwable);
            }
        });
    }


    public void getProviderList(Context context, Practice practice, String searchTerm, final THSProvidersListCallback THSProvidersListCallback) throws AWSDKInstantiationException{
        getAwsdk(context).getPracticeProvidersManager().findProviders(getPTHConsumer().getConsumer(), practice, null, searchTerm, null, null, null, null, null, new SDKCallback<List<ProviderInfo>, SDKError>() {
            @Override
            public void onResponse(List<ProviderInfo> providerInfos, SDKError sdkError) {
                List<THSProviderInfo> thsProvidersList = new ArrayList<>();
                for (ProviderInfo providerInfo:providerInfos) {
                    THSProviderInfo thsProviderInfo = new THSProviderInfo();
                    thsProviderInfo.setTHSProviderInfo(providerInfo);
                    thsProvidersList.add(thsProviderInfo);
                }
                THSProvidersListCallback.onProvidersListReceived(thsProvidersList, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                THSProvidersListCallback.onProvidersListFetchError(throwable);
            }
        });
    }
    public void getProviderList(Context context, Practice practice, final THSProvidersListCallback THSProvidersListCallback) throws AWSDKInstantiationException {
        getProviderList(context, practice,null,THSProvidersListCallback);
    }

    public void getProviderDetails(Context context, THSProviderInfo thsProviderInfo, final THSProviderDetailsCallback THSProviderDetailsCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getPracticeProvidersManager().getProvider(thsProviderInfo.getProviderInfo(), getPTHConsumer().getConsumer(), new SDKCallback<Provider, SDKError>() {
            @Override
            public void onResponse(Provider provider, SDKError sdkError) {
                THSProviderDetailsCallback.onProviderDetailsReceived(provider,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {

                THSProviderDetailsCallback.onProviderDetailsFetchError(throwable);
            }
        });
    }

    public void updateConsumer(Context context, String updatedPhone, final THSUpdateConsumerCallback<THSConsumerWrapper, THSSDKPasswordError> pthUpdateConsumer) throws AWSDKInstantiationException {
        ConsumerUpdate consumerUpdate = getAwsdk(context).getConsumerManager().getNewConsumerUpdate(getPTHConsumer().getConsumer());
        consumerUpdate.setPhone(updatedPhone);
        getAwsdk(context).getConsumerManager().updateConsumer(consumerUpdate, new SDKValidatedCallback<Consumer, SDKPasswordError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                pthUpdateConsumer.onUpdateConsumerValidationFailure(map);
            }

            @Override
            public void onResponse(Consumer consumer, SDKPasswordError sdkPasswordError) {

                THSConsumerWrapper thsConsumerWrapper = new THSConsumerWrapper();
                thsConsumerWrapper.setConsumer(consumer);
                setConsumer(consumer);

                THSSDKPasswordError pthSDKError = new THSSDKPasswordError();
                pthSDKError.setSdkPasswordError(sdkPasswordError);

                pthUpdateConsumer.onUpdateConsumerResponse(thsConsumerWrapper,pthSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                pthUpdateConsumer.onUpdateConsumerFailure(throwable);
            }
        });
    }

    @VisibleForTesting
    public void setAwsdk(AWSDK awsdk) {
        this.mAwsdk = awsdk;
    }

    public void getMedication(Context context ,  final THSMedicationCallback.PTHGetMedicationCallback pTHGetMedicationCallback ) throws AWSDKInstantiationException{
        getAwsdk(context).getConsumerManager().getMedications(getPTHConsumer().getConsumer(), new SDKCallback<List<Medication>, SDKError>() {
            @Override
            public void onResponse(List<Medication> medications, SDKError sdkError) {
                AmwellLog.i("onGetMedicationReceived", "success");

                THSMedication pTHMedication = new THSMedication();
                pTHMedication.setMedicationList(medications);
                pTHGetMedicationCallback.onGetMedicationReceived(pTHMedication, sdkError);

            }

            @Override
            public void onFailure(Throwable throwable) {
                AmwellLog.i("onGetMedicationReceived", "failure");
            }
        });

    }

    public void searchMedication(Context context, String medicineName, final THSSDKValidatedCallback<THSMedication, SDKError> pTHSDKValidatedCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().searchMedications(getPTHConsumer().getConsumer(), medicineName, new SDKValidatedCallback<List<Medication>, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                pTHSDKValidatedCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(List<Medication> medications, SDKError sdkError) {

                THSMedication pTHMedication = new THSMedication();
                pTHMedication.setMedicationList(medications);
                pTHSDKValidatedCallback.onResponse(pTHMedication, sdkError);

            }

            @Override
            public void onFailure(Throwable throwable) {
                AmwellLog.i("onSearchMedication", "failure");
                pTHSDKValidatedCallback.onFailure(throwable);
            }
        });


    }

    public void updateVitals(Context context, THSVitals thsVitals, final THSUpdateVitalsCallBack thsUpdateVitalsCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().updateVitals(getPTHConsumer().getConsumer(), thsVitals.getVitals(), null , new SDKValidatedCallback<Void, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thsUpdateVitalsCallBack.onUpdateVitalsValidationFailure(map);
            }

            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thsUpdateVitalsCallBack.onUpdateVitalsResponse(sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsUpdateVitalsCallBack.onUpdateVitalsFailure(throwable);
            }
        });
    }

    public void updateConditions(Context context, List<THSCondition> pthConditionList, final THSUpdateConditionsCallback<Void, THSSDKError> thsUpdateConditionsCallback) throws AWSDKInstantiationException {

        List<Condition> conditionList = new ArrayList<>();
        for (THSCondition pthcondition:pthConditionList
             ) {
            conditionList.add(pthcondition.getCondition());
        }
        
        getAwsdk(context).getConsumerManager().updateConditions(getPTHConsumer().getConsumer(), conditionList, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                THSSDKError pthSDKError = new THSSDKError();
                pthSDKError.setSdkError(sdkError);

                thsUpdateConditionsCallback.onUpdateConditonResponse(aVoid,pthSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsUpdateConditionsCallback.onUpdateConditionFailure(throwable);
            }
        });
    }

    public void updateMedication(Context context , THSMedication pTHMedication, final THSMedicationCallback.PTHUpdateMedicationCallback pTHUpdateMedicationCallback) throws AWSDKInstantiationException{
        getAwsdk(context).getConsumerManager().updateMedications(getPTHConsumer().getConsumer(), pTHMedication.getMedicationList(), new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                // sdkError comes null even after successfully updating the medication
                AmwellLog.i("onUpdateMedication","success");
                pTHUpdateMedicationCallback.onUpdateMedicationSent(aVoid,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                AmwellLog.i("onUpdateMedication","failure");

            }
        });
    }

    /*public void getLegaltext(Context context, LegalText legalText, final THSNoticeOfPrivacyPracticesCallBack thsNoticeOfPrivacyPracticesCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getLegalText(legalText, new SDKCallback<String, SDKError>() {
            @Override
            public void onResponse(String s, SDKError sdkError) {
                thsNoticeOfPrivacyPracticesCallBack.onNoticeOfPrivacyPracticesReceivedSuccess(s, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsNoticeOfPrivacyPracticesCallBack.onNoticeOfPrivacyPracticesReceivedFailure(throwable);
            }
        });
    }
*/
    public void getPharmacies(Context context, final THSConsumerWrapper thsConsumerWrapper, String city, State state, String zipCode, final THSGetPharmaciesCallback thsGetPharmaciesCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getPharmacies(thsConsumerWrapper.getConsumer(), null,city, state, zipCode, new SDKValidatedCallback<List<Pharmacy>, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thsGetPharmaciesCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(List<Pharmacy> pharmacies, SDKError sdkError) {
                thsGetPharmaciesCallback.onPharmacyListReceived(pharmacies,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsGetPharmaciesCallback.onFailure(throwable);
            }
        });
    }

    public void getPharmacies(Context context, final THSConsumerWrapper thsConsumerWrapper, float latitude, float longitude, int radius, final THSGetPharmaciesCallback thsGetPharmaciesCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getPharmacies(thsConsumerWrapper.getConsumer(), latitude, longitude, radius, true, new SDKCallback<List<Pharmacy>, SDKError>() {
            @Override
            public void onResponse(List<Pharmacy> pharmacies, SDKError sdkError) {
                thsGetPharmaciesCallback.onPharmacyListReceived(pharmacies,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsGetPharmaciesCallback.onFailure(throwable);
            }
        });
    }

    public void getConsumerPreferredPharmacy(Context context, final THSPreferredPharmacyCallback thsPreferredPharmacyCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getConsumerPharmacy(getPTHConsumer().getConsumer(), new SDKCallback<Pharmacy, SDKError>() {
            @Override
            public void onResponse(Pharmacy pharmacy, SDKError sdkError) {
                thsPreferredPharmacyCallback.onPharmacyReceived(pharmacy, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsPreferredPharmacyCallback.onFailure(throwable);
            }
        });
    }

    public void updateConsumerPreferredPharmacy(Context context, final Pharmacy pharmacy, final THSUpdatePharmacyCallback thsUpdatePharmacyCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().updateConsumerPharmacy(getPTHConsumer().getConsumer(), pharmacy, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thsUpdatePharmacyCallback.onUpdateSuccess(sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsUpdatePharmacyCallback.onUpdateFailure(throwable);
            }
        });
    }

    public void getConsumerShippingAddress(Context context, final THSConsumerShippingAddressCallback thsConsumerShippingAddressCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getShippingAddress(getPTHConsumer().getConsumer(), new SDKCallback<Address, SDKError>() {
            @Override
            public void onResponse(Address address, SDKError sdkError) {
                thsConsumerShippingAddressCallback.onSuccessfulFetch(address, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsConsumerShippingAddressCallback.onFailure(throwable);
            }
        });
    }

    public void updatePreferredShippingAddress(Context context,final Address address,final THSUpdateShippingAddressCallback thsUpdateShippingAddressCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().updateShippingAddress(getPTHConsumer().getConsumer(), address, new SDKValidatedCallback<Address, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thsUpdateShippingAddressCallback.onAddressValidationFailure(map);
            }

            @Override
            public void onResponse(Address address, SDKError sdkError) {
                thsUpdateShippingAddressCallback.onUpdateSuccess(address,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsUpdateShippingAddressCallback.onUpdateFailure(throwable);
            }
        });
    }

    public void getAvailableProvidersBasedOnDate(Context context, Practice thsPractice,
                                                 String searchItem, Language languageSpoken, Date appointmentDate,
                                                 Integer maxresults,
                                                 final THSAvailableProvidersBasedOnDateCallback<THSAvailableProviderList, THSSDKError> thsAvailableProviderCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getPracticeProvidersManager().findFutureAvailableProviders(getPTHConsumer().getConsumer(), thsPractice,
                searchItem, languageSpoken, appointmentDate, maxresults,null, new SDKCallback<AvailableProviders, SDKError>() {
                    @Override
                    public void onResponse(AvailableProviders availableProviders, SDKError sdkError) {
                        THSAvailableProviderList thsAvailableProviderList = new THSAvailableProviderList();
                        thsAvailableProviderList.setAvailableProviders(availableProviders);

                        THSSDKError thsSDKError = new THSSDKError();
                        thsSDKError.setSdkError(sdkError);

                        thsAvailableProviderCallback.onResponse(thsAvailableProviderList,thsSDKError);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        thsAvailableProviderCallback.onFailure(throwable);
                    }
                });

    }

    public void getProviderAvailability(Context context, Provider provider, Date date, final THSAvailableProviderCallback<List<Date>,THSSDKError> thsAvailableProviderCallback) throws AWSDKInstantiationException {
        try {
            getAwsdk(context).getPracticeProvidersManager().getProviderAvailability(getPTHConsumer().getConsumer(), provider,
                    date, null,new SDKCallback<List<Date>, SDKError>() {
                        @Override
                        public void onResponse(List<Date> dates, SDKError sdkError) {
                            THSSDKError thssdkError = new THSSDKError();
                            thssdkError.setSdkError(sdkError);
                            thsAvailableProviderCallback.onResponse(dates,thssdkError);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            thsAvailableProviderCallback.onFailure(throwable);
                        }
                    });
        }catch (Exception ex){
            thsAvailableProviderCallback.onFailure(ex);
        }

    }

    public List<HealthPlan> getHealthPlans(Context context){
        List<HealthPlan> healthplans = null;
        try {
            healthplans = getAwsdk(context).getConsumerManager().getHealthPlans();
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }
        return healthplans;

    }

    public List<Relationship> getSubscriberRelationships(Context context) throws AWSDKInstantiationException {
        List<Relationship> relationships;

        relationships = getAwsdk(context).getConsumerManager().getRelationships();

        return relationships;
    }


    public void getExistingSubscription(Context context, final THSInsuranceCallback.THSgetInsuranceCallBack<THSSubscription, THSSDKError> tHSSDKCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getInsuranceSubscription(getPTHConsumer().getConsumer(), new SDKCallback<Subscription, SDKError>() {
            @Override
            public void onResponse(Subscription subscription, SDKError sdkError) {
                THSSubscription tHSSubscription = new THSSubscription();
                tHSSubscription.setSubscription(subscription);
                THSSDKError tHSSDKError = new THSSDKError();
                tHSSDKError.setSdkError(sdkError);

                tHSSDKCallBack.onGetInsuranceResponse(tHSSubscription, tHSSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                tHSSDKCallBack.onGetInsuranceFailure(throwable);
            }
        });

    }

    public THSSubscriptionUpdateRequest getNewSubscriptionUpdateRequest(Context context) throws AWSDKInstantiationException {
        THSSubscriptionUpdateRequest thsSubscriptionUpdateRequest = new THSSubscriptionUpdateRequest();
        SubscriptionUpdateRequest subscriptionUpdateRequest = getAwsdk(context).getConsumerManager().getNewSubscriptionUpdateRequest(getPTHConsumer().getConsumer(), false);
        thsSubscriptionUpdateRequest.setSubscriptionUpdateRequest(subscriptionUpdateRequest);
        return thsSubscriptionUpdateRequest;
    }

    public void updateInsuranceSubscription(Context context, THSSubscriptionUpdateRequest thsSubscriptionUpdateRequest, final THSSDKValidatedCallback<Void, SDKError> tHSSDKValidatedCallback) throws AWSDKInstantiationException {

        getAwsdk(context).getConsumerManager().updateInsuranceSubscription(thsSubscriptionUpdateRequest.getSubscriptionUpdateRequest(), new SDKValidatedCallback<Void, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                tHSSDKValidatedCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                tHSSDKValidatedCallback.onResponse(aVoid, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                tHSSDKValidatedCallback.onFailure(throwable);
            }
        });

    }

    public void validateSubscriptionUpdateRequest(Context context, THSSubscriptionUpdateRequest thsSubscriptionUpdateRequest, Map<String, ValidationReason> errors) throws AWSDKInstantiationException {


        getAwsdk(context).getConsumerManager().validateSubscriptionUpdateRequest(thsSubscriptionUpdateRequest.getSubscriptionUpdateRequest(), errors);
    }

    public void createVisit(Context context, THSVisitContext thsVisitContext, final CreateVisitCallback<THSVisit, THSSDKError> createVisitCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().createVisit(thsVisitContext.getVisitContext(), new SDKValidatedCallback<Visit, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                createVisitCallback.onCreateVisitValidationFailure(map);
            }

            @Override
            public void onResponse(Visit visit, SDKError sdkError) {
                THSVisit thsVisit = new THSVisit();
                thsVisit.setVisit(visit);
                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                createVisitCallback.onCreateVisitResponse(thsVisit, thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                createVisitCallback.onCreateVisitFailure(throwable);
            }
        });

    }

    public void getPaymentMethod(Context context, final THSPaymentCallback.THSgetPaymentMethodCallBack<THSPaymentMethod, THSSDKError> tHSSDKCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getPaymentMethod(getPTHConsumer().getConsumer(), new SDKCallback<PaymentMethod, SDKError>() {
            @Override
            public void onResponse(PaymentMethod paymentMethod, SDKError sdkError) {
                THSPaymentMethod tHSPaymentMethod = new THSPaymentMethod();
                tHSPaymentMethod.setPaymentMethod(paymentMethod);
                THSSDKError tHSSDKError = new THSSDKError();
                tHSSDKError.setSdkError(sdkError);
                tHSSDKCallBack.onGetPaymentMethodResponse(tHSPaymentMethod, tHSSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                tHSSDKCallBack.onGetPaymentFailure(throwable);
            }
        });
    }


    public THSCreatePaymentRequest getNewCreatePaymentRequest(Context context) throws AWSDKInstantiationException {
        CreatePaymentRequest createPaymentRequest = getAwsdk(context).getConsumerManager().getNewCreatePaymentRequest(getPTHConsumer().getConsumer());
        THSCreatePaymentRequest tHSCreatePaymentRequest = new THSCreatePaymentRequest();
        tHSCreatePaymentRequest.setCreatePaymentRequest(createPaymentRequest);
        return tHSCreatePaymentRequest;
    }

    public THSAddress getAddress(Context context) throws AWSDKInstantiationException {
        THSAddress thsAddress = new THSAddress();
        Address address = getAwsdk(context).getNewAddress();
        thsAddress.setAddress(address);
        return thsAddress;
    }

    public boolean isCreditCardNumberValid(Context context, String cardNumber) throws AWSDKInstantiationException {
        return THSManager.getInstance().getAwsdk(context).getCreditCardUtil().isCreditCardNumberValid(cardNumber);
    }

    public boolean isSecurityCodeValid(Context context, String cardNumber, String cvv) throws AWSDKInstantiationException {
        return THSManager.getInstance().getAwsdk(context).getCreditCardUtil().isSecurityCodeValid(cardNumber, cvv);
    }


    public void validateCreatePaymentRequest(Context context, THSCreatePaymentRequest thsCreatePaymentRequest, Map<String, ValidationReason> errors) throws AWSDKInstantiationException {

        getAwsdk(context).getConsumerManager().validateCreatePaymentRequest(thsCreatePaymentRequest.getCreatePaymentRequest(),errors );


    }
    public void updatePaymentMethod(Context context, THSCreatePaymentRequest thsCreatePaymentRequest, final THSPaymentCallback.THSgetPaymentMethodValidatedCallback<THSPaymentMethod, THSSDKError> tHSSDKValidatedCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().updatePaymentMethod(thsCreatePaymentRequest.getCreatePaymentRequest(), new SDKValidatedCallback<PaymentMethod, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                tHSSDKValidatedCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(PaymentMethod paymentMethod, SDKError sdkError) {
                THSPaymentMethod tHSPaymentMethod = new THSPaymentMethod();
                tHSPaymentMethod.setPaymentMethod(paymentMethod);
                THSSDKError tHSSDKError = new THSSDKError();
                tHSSDKError.setSdkError(sdkError);
                tHSSDKValidatedCallback.onGetPaymentMethodResponse(tHSPaymentMethod, tHSSDKError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                tHSSDKValidatedCallback.onGetPaymentFailure(throwable);
            }
        });
    }

    public void scheduleAppointment(Context context, final THSProviderInfo thsProviderInfo, Date appointmentDate,final RemindOptions consumerRemindOptions, final THSSDKValidatedCallback<Void, SDKError> thssdkValidatedCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().scheduleAppointment(getPTHConsumer().getConsumer(), thsProviderInfo.getProviderInfo(),
                appointmentDate, null,consumerRemindOptions, RemindOptions.FIFTEEN_MIN, new SDKValidatedCallback<Void, SDKError>() {
                    @Override
                    public void onValidationFailure(Map<String, ValidationReason> map) {
                        thssdkValidatedCallback.onValidationFailure(map);
                    }

                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        thssdkValidatedCallback.onResponse(aVoid,sdkError);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        thssdkValidatedCallback.onFailure(throwable);
                    }
                });

    }

    public void uploadHealthDocument(Context context, UploadAttachment uploadAttachment, final THSUploadDocumentCallback thsUploadDocumentCallback) throws AWSDKInstantiationException, IOException {

        getAwsdk(context).getConsumerManager().addHealthDocument(getPTHConsumer().getConsumer(), uploadAttachment, new SDKValidatedCallback<DocumentRecord, SDKError>() {
            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thsUploadDocumentCallback.onUploadValidationFailure(map);
            }

            @Override
            public void onResponse(DocumentRecord documentRecord, SDKError sdkError) {
                thsUploadDocumentCallback.onUploadDocumentSuccess(documentRecord,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsUploadDocumentCallback.onError(throwable);
            }
        });
    }

    public void deletedHealthDocument(Context context, DocumentRecord documentRecord, final THSDeleteDocumentCallback thsDeleteDocumentCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().removeHealthDocumentRecord(getPTHConsumer().getConsumer(), documentRecord, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thsDeleteDocumentCallback.onDeleteSuccess(aVoid,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsDeleteDocumentCallback.onError(throwable);
            }
        });

    }
    public void startVisit(Context context ,Visit visit,  final Intent intent,final THSStartVisitCallback thsStartVisitCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().startVisit(visit, visit.getConsumer().getAddress(), intent,new StartVisitCallback() {
            @Override
            public void onProviderEntered(@NonNull Intent intent) {
                thsStartVisitCallback.onProviderEntered(intent);
            }

            @Override
            public void onStartVisitEnded(@NonNull VisitEndReason visitEndReason) {
                thsStartVisitCallback.onStartVisitEnded(visitEndReason);
            }

            @Override
            public void onPatientsAheadOfYouCountChanged(int i) {
                thsStartVisitCallback.onPatientsAheadOfYouCountChanged(i);
            }

            @Override
            public void onSuggestedTransfer() {
                thsStartVisitCallback.onSuggestedTransfer();
            }

            @Override
            public void onChat(@NonNull ChatReport chatReport) {
                thsStartVisitCallback.onChat(chatReport);
            }

            @Override
            public void onPollFailure(@NonNull Throwable throwable) {
                thsStartVisitCallback.onPollFailure(throwable);
            }

            @Override
            public void onValidationFailure(Map<String, ValidationReason> map) {
                thsStartVisitCallback.onValidationFailure(map);
            }

            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thsStartVisitCallback.onResponse(aVoid,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsStartVisitCallback.onFailure(throwable);
            }
        });
    }

    public void cancelVisit(Context context,Visit visit, final THSCancelVisitCallBack.SDKCallback <Void, SDKError> tHSSDKCallback)  throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().cancelVisit(visit, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                tHSSDKCallback.onResponse(aVoid, sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                tHSSDKCallback.onFailure(throwable);
            }
        });

    }

    public void cancelAppointment(Context context, Appointment appointment, final THSInitializeCallBack<Void, THSSDKError> thsInitializeCallBack) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().cancelAppointment(getPTHConsumer().getConsumer(), appointment, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                thsInitializeCallBack.onInitializationResponse(aVoid,thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsInitializeCallBack.onInitializationFailure(throwable);
            }
        });
    }

    public void abondonCurrentVisit(Context context)throws AWSDKInstantiationException{
        getAwsdk(context).getVisitManager().abandonCurrentVisit();
    }


    public void fetchEstimatedVisitCost(Context context, Provider provider, final THSFetchEstimatedCostCallback thsFetchEstimatedCostCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getPracticeProvidersManager().getEstimatedVisitCost(getPTHConsumer().getConsumer(), provider, new SDKCallback<EstimatedVisitCost, SDKError>() {
            @Override
            public void onResponse(EstimatedVisitCost estimatedVisitCost, SDKError sdkError) {
                thsFetchEstimatedCostCallback.onEstimatedCostFetchSuccess(estimatedVisitCost,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsFetchEstimatedCostCallback.onError(throwable);
            }
        });
    }

    @VisibleForTesting
    public void setUser(User user){
        mUser = user;
    }

    public String getAppName() {
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        Object propertyForKey = (getAppInfra().getConfigInterface().getPropertyForKey(URConfigurationConstants.HSDP_CONFIGURATION_APPLICATION_NAME,
                URConfigurationConstants.UR, configError));
        if(propertyForKey instanceof Map<?,?>){
            HashMap<?,?> map = (HashMap<?,?>) propertyForKey;
            if (map.get("default") != null) {
                return map.get("default").toString();
            }else {
                return " ";
            }
        }
        if(propertyForKey!=null) {
            return propertyForKey.toString();
        }else {
            return " ";
        }
    }

    private AppInfraInterface getAppInfra() {
        return mAppInfra;
    }

    public void setAppInfra(AppInfraInterface mAppInfra) {
        this.mAppInfra = mAppInfra;
        this.mAppTaggingInterface = mAppInfra.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME);// initialize tagging for ths
    }

    public void getVisitHistory(final Context context, SDKLocalDate date, final THSVisitReportListCallback<List<VisitReport>, SDKError> visitReportListCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getVisitReports(getPTHConsumer().getConsumer(), date, null, new SDKCallback<List<VisitReport>, SDKError>() {

            @Override
            public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {
                visitReportListCallback.onResponse(visitReports,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void getVisitReportDetail(Context context, VisitReport visitReport, final THSVisitReportDetailCallback<VisitReportDetail, SDKError> thsVisitReportDetailCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getVisitReportDetail(getPTHConsumer().getConsumer(), visitReport, new SDKCallback<VisitReportDetail, SDKError>() {
            @Override
            public void onResponse(VisitReportDetail visitReportDetail, SDKError sdkError) {
                thsVisitReportDetailCallback.onResponse(visitReportDetail,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsVisitReportDetailCallback.onFailure(throwable);
            }
        });
    }

    public void getVisitSummary(Context context,Visit visit, final THSVisitSummaryCallbacks.THSVisitSummaryCallback<THSVisitSummary, THSSDKError> thsVisitSummaryCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().getVisitSummary(visit, new SDKCallback<VisitSummary, SDKError>() {
            @Override
            public void onResponse(VisitSummary visitSummary, SDKError sdkError) {
                THSVisitSummary thsVisitSummary = new THSVisitSummary();
                thsVisitSummary.setVisitSummary(visitSummary);
                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                thsVisitSummaryCallback.onResponse(thsVisitSummary,thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsVisitSummaryCallback.onFailure(throwable);
            }
        });
    }



    public void getVisitReportAttachment(Context context, VisitReport visitReport, final THSVisitReportAttachmentCallback<FileAttachment, SDKError> thsVisitReportAttachmentCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getConsumerManager().getVisitReportAttachment(getPTHConsumer().getConsumer(), visitReport, new SDKCallback<FileAttachment, SDKError>() {
            @Override
            public void onResponse(FileAttachment fileAttachment, SDKError sdkError) {
                thsVisitReportAttachmentCallback.onResponse(fileAttachment,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsVisitReportAttachmentCallback.onFailure(throwable);
            }
        });

    }

    public void sendRatings(Context context,Visit visit, Integer providerRating, Integer visitRating,final THSSDKCallback<Void, SDKError> thssdkCallback)throws AWSDKInstantiationException{
        getAwsdk(context).getVisitManager().sendRatings(visit, providerRating, visitRating, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thssdkCallback.onResponse(aVoid,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {

                thssdkCallback.onFailure(throwable);
            }
        });
    }

    public void getPractice(Context context, PracticeInfo practiceInfo, final THSPracticeCallback<Practice, SDKError> thsPracticeCallback) throws AWSDKInstantiationException {
        getAwsdk(context).getPracticeProvidersManager().getPractice(practiceInfo, new SDKCallback<Practice, SDKError>() {
            @Override
            public void onResponse(Practice practice, SDKError sdkError) {
                thsPracticeCallback.onResponse(practice,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsPracticeCallback.onFailure(throwable);
            }
        });
    }

    public void doMatchMaking(Context context, THSVisitContext thsVisitContext, final THSMatchMakingCallback thsMatchMakingCallback)throws AWSDKInstantiationException {
        getAwsdk(context).getVisitManager().startMatchmaking(thsVisitContext.getVisitContext(), new MatchmakerCallback() {
            @Override
            public void onProviderFound(Provider provider, VisitContext visitContext) {
                thsMatchMakingCallback.onMatchMakingProviderFound(provider,visitContext);
            }

            @Override
            public void onProviderListExhausted() {
                thsMatchMakingCallback.onMatchMakingProviderListExhausted();
            }

            @Override
            public void onRequestGone() {
                thsMatchMakingCallback.onMatchMakingRequestGone();
            }

            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                thsMatchMakingCallback.onMatchMakingResponse(aVoid,sdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                thsMatchMakingCallback.onMatchMakingFailure(throwable);
            }
        });
    }

    public void cancelMatchMaking(Context context, THSVisitContext thsVisitContext)throws AWSDKInstantiationException {
        try {
            getAwsdk(context).getVisitManager().cancelMatchmaking(thsVisitContext.getVisitContext(), new SDKCallback<Void, SDKError>() {
                @Override
                public void onResponse(Void aVoid, SDKError sdkError) {
                    if (null == sdkError) {
                        AmwellLog.v("cancelMatchMaking", "success");
                    } else {
                        AmwellLog.v("cancelMatchMaking", "failure");
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    AmwellLog.v("cancelMatchMaking", "failure");
                }
            });
        }catch(Exception e){
            AmwellLog.v("cancelMatchMaking", "failure");
        }

    }
    public void applyCouponCode(Context context, THSVisit thsVisit, String couponCode, final ApplyCouponCallback<Void, THSSDKError> applyCouponCallback) throws AWSDKInstantiationException{
        getAwsdk(context).getVisitManager().applyCouponCode(thsVisit.getVisit(), couponCode, new SDKCallback<Void, SDKError>() {
            @Override
            public void onResponse(Void aVoid, SDKError sdkError) {
                THSSDKError thssdkError = new THSSDKError();
                thssdkError.setSdkError(sdkError);
                applyCouponCallback.onApplyCouponResponse(aVoid,thssdkError);
            }

            @Override
            public void onFailure(Throwable throwable) {
                applyCouponCallback.onApplyCouponFailure(throwable);
            }
        });

    }

    public boolean isReturningUser() {
        return mIsReturningUser;
    }

    public void setIsReturningUser(boolean firstTimeUser) {
        mIsReturningUser = firstTimeUser;
    }

    public THSConsumer getThsConsumer() {
        return mThsConsumer;
    }

    public void setThsConsumer(THSConsumer mThsConsumer) {
        this.mThsConsumer = mThsConsumer;
    }

    private void setConsumer(Consumer consumer) {
        THSConsumerWrapper thsConsumerWrapper = new THSConsumerWrapper();
        thsConsumerWrapper.setConsumer(consumer);
        setPTHConsumer(thsConsumerWrapper);

        mThsConsumer.setConsumer(consumer);
    }

    public THSConsumer getThsParentConsumer() {
        return mThsParentConsumer;
    }

    public void setThsParentConsumer(THSConsumer mThsParentConsumer) {
        this.mThsParentConsumer = mThsParentConsumer;
        this.mThsConsumer = mThsParentConsumer;
    }
}
