/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.ads.listener.AdListener;
import com.philips.cdp.registration.ads.listener.NativeAdListener;
import com.philips.cdp.registration.ads.modal.HouseAdsNativeView;
import com.philips.cdp.registration.ads.views.HouseAdsNative;
import com.philips.cdp.registration.ads.views.PromotionsAdsDialog;
import com.philips.cdp.registration.ads.views.PromotionsAdsDialogFull;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTaggingErrors;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.ClientIDConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.handlers.LoginHandler;
import com.philips.cdp.registration.handlers.ResendVerificationEmailHandler;
import com.philips.cdp.registration.restclient.URRequest;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.RegistrationSettingsURL;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.URNotification;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.AccountActivationFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.cdp.registration.ui.traditional.mobile.MobileForgotPassVerifyCodeFragment;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.LoginFailureNotification;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.philips.cdp.registration.app.tagging.AppTagging.trackAction;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.FIREBASE_SUCCESSFUL_REGISTRATION_DONE;
import static com.philips.cdp.registration.errors.ErrorCodes.JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE;
import static com.philips.cdp.registration.ui.utils.UIFlow.FLOW_B;

public class PromotionalFragment extends RegistrationBaseFragment  {

    PromotionsAdsDialog houseAds;// = new HouseAdsDialog(this.getActivity(), "https://lz-houseads.firebaseapp.com/houseAds/ads.json");  //Context & URL to Json File.
    PromotionsAdsDialogFull houseAdsFull;// = new HouseAdsDialog(this.getActivity(), "https://lz-houseads.firebaseapp.com/houseAds/ads.json");  //Context & URL to Json File.
    private static final String TAG = "HomeFragment";

    Context context;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.reg_promtions_test, null);

        ProgressBarButton banner = view.findViewById(R.id.usr_promotion_banner);
        ProgressBarButton full = view.findViewById(R.id.usr_promotion_full);
        ProgressBarButton dialog = view.findViewById(R.id.usr_promotion_dialog);

        banner.setOnClickListener(v -> showBanner());
        full.setOnClickListener(v -> showFull());
        dialog.setOnClickListener(v -> showDialog());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void notificationInlineMsg(String msg) {

    }

    @Override
    protected void setViewParams(Configuration config, int width) {

    }

    @Override
    protected void handleOrientation(View view) {

    }

    @Override
    public int getTitleResourceId() {
        return  R.string.USR_DLS_SigIn_TitleTxt;
    }

    void showBanner(){
        final LinearLayout adLayout = view.findViewById(R.id.bannerView); //Ad Assets inside a ViewGroup
        adLayout.setVisibility(View.GONE);

        HouseAdsNativeView nativeView = new HouseAdsNativeView();
        nativeView.setTitleView((TextView) view.findViewById(R.id.bannerText));
        nativeView.setIconView((ImageView) view.findViewById(R.id.bannerImg));

        HouseAdsNative houseAdsNative = new HouseAdsNative(this.getContext(), "https://lz-houseads.firebaseapp.com/houseAds/ads.json");
        houseAdsNative.setNativeAdView(adLayout); //HouseAdsNativeView Object
        houseAdsNative.setNativeAdView(nativeView); //View Object
        houseAdsNative.setNativeAdListener(new NativeAdListener() {
            @Override
            public void onAdLoadFailed(@NotNull Exception exception) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                RLog.d(TAG, "getViewFromRegistrationFunction : onAdLoadFailed");
            }

            @Override
            public void onAdLoaded() {
                adLayout.setVisibility(View.VISIBLE);
                RLog.d(TAG, "getViewFromRegistrationFunction : onAdLoaded");
            }

//            @Override
//            public void onAdLoadFailed() {
//                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//            }
        });
        houseAdsNative.loadAds();

    }

    void showFull(){

        houseAdsFull = new PromotionsAdsDialogFull(this.getActivity(), "https://lz-houseads.firebaseapp.com/houseAds/ads.json");  //Context & URL to Json File.
        houseAdsFull.hideIfAppInstalled(true); //An App's Ad won't be shown if it is Installed on the Device.
        houseAdsFull.setCardCorners(100); // Set CardView's corner radius.
        houseAdsFull.setCtaCorner(100); //Set CTA Button's background radius.
        houseAdsFull.showHeaderIfAvailable(false); //Show Header Image if available, true by default
        houseAdsFull.loadAds();
        houseAdsFull.setAdListener(new AdListener() {
            @Override
            public void onAdLoadFailed(@NotNull Exception exception) {
                RLog.i(TAG, " URNotification handleBtnClickableStates111");

            }

            @Override
            public void onAdLoaded() {
                RLog.i(TAG, " URNotification handleBtnClickableStates222");
                houseAdsFull.showAd();


            }

            @Override
            public void onAdClosed() {
                RLog.i(TAG, " URNotification handleBtnClickableStates333");

            }

            @Override
            public void onAdShown() {
                RLog.i(TAG, " URNotification handleBtnClickableStates444");

            }

            @Override
            public void onApplicationLeft() {
                RLog.i(TAG, " URNotification handleBtnClickableStates555");

            }
        });

    }


    void showDialog(){

                houseAds = new PromotionsAdsDialog(this.getActivity(), "https://lz-houseads.firebaseapp.com/houseAds/ads.json");  //Context & URL to Json File.
        houseAds.hideIfAppInstalled(true); //An App's Ad won't be shown if it is Installed on the Device.
        houseAds.setCardCorners(100); // Set CardView's corner radius.
        houseAds.setCtaCorner(100); //Set CTA Button's background radius.
        houseAds.showHeaderIfAvailable(false); //Show Header Image if available, true by default
        houseAds.loadAds();
        houseAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoadFailed(@NotNull Exception exception) {
                RLog.i(TAG, " URNotification handleBtnClickableStates111");

            }

            @Override
            public void onAdLoaded() {
                RLog.i(TAG, " URNotification handleBtnClickableStates222");
                houseAds.showAd();


            }

            @Override
            public void onAdClosed() {
                RLog.i(TAG, " URNotification handleBtnClickableStates333");

            }

            @Override
            public void onAdShown() {
                RLog.i(TAG, " URNotification handleBtnClickableStates444");

            }

            @Override
            public void onApplicationLeft() {
                RLog.i(TAG, " URNotification handleBtnClickableStates555");

            }
        });

    }


}