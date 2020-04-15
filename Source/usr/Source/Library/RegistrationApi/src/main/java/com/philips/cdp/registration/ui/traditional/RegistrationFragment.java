/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserLoginState;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.myaccount.UserDetailsFragment;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.social.AlmostDoneFragment;
import com.philips.cdp.registration.ui.social.MergeAccountFragment;
import com.philips.cdp.registration.ui.social.MergeSocialToSocialAccountFragment;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.traditional.mobile.ResetPasswordWebView;
import com.philips.cdp.registration.ui.utils.CountDownEvent;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkStateReceiver;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.NotificationBarHandler;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import javax.inject.Inject;


public class RegistrationFragment extends Fragment implements NetworkStateListener, BackEventListener {

    @Inject
    NetworkUtility networkUtility;

    private FragmentManager mFragmentManager;


    private ActionBarListener mActionBarListener;

    private RegistrationLaunchMode mRegistrationLaunchMode;

    private RegistrationContentConfiguration registrationContentConfiguration;

    public MyCountDownTimer myCountDownTimer;

    private int titleResourceID = -99;

    private NetworkStateReceiver mNetworkReceiver;

    private boolean isCounterRunning;

    private static final String TAG = "RegistrationFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RLog.d(TAG, "onCreate");

        RegistrationBaseFragment.setHeightWidthToZero();
        Bundle bundle = getArguments();
        RLog.d(TAG, "onCreate : Bundle is null" + bundle);

        if (bundle != null) {
            mRegistrationLaunchMode = (RegistrationLaunchMode) bundle.get(RegConstants.REGISTRATION_LAUNCH_MODE);
            registrationContentConfiguration = (RegistrationContentConfiguration) bundle.get(RegConstants.REGISTRATION_CONTENT_CONFIG);
       }
        myCountDownTimer = new MyCountDownTimer(60 * 1000, 1000);
    }

    public RegistrationContentConfiguration getContentConfiguration() {
        return registrationContentConfiguration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        View view = inflater.inflate(R.layout.reg_fragment_registration, container, false);
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        RLog.d(TAG, "onCreateView : registered NetworkStateListener");
        mFragmentManager = getChildFragmentManager();
        if (mFragmentManager.getBackStackEntryCount() < 1) {
            loadFirstFragment();
            RLog.d(TAG, "onCreateView : loadFirstFragment is called");
        }
        mNetworkReceiver = new NetworkStateReceiver();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        networkUtility.registerNetworkListener(mNetworkReceiver);
        RLog.d(TAG, "onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        networkUtility.unRegisterNetworkListener(mNetworkReceiver);
        RLog.d(TAG, "onPause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(TAG, "onStop");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        if(getFragmentCount() == 1 && mFragmentManager.getFragments().get(0) instanceof RegistrationBaseFragment)
           ((RegistrationBaseFragment) mFragmentManager.getFragments().get(0)).hideNotificationBarView();
        RegistrationBaseFragment.setHeightWidthToZero();
        setPrevTiltle();
    }

    private void setPrevTiltle() {
        if (mPreviousResourceId != -99)
            mActionBarListener.updateActionBar(getPreviousResourceId(), true);
    }


    public boolean onBackPressed() {
        hideKeyBoard();
        return handleBackStack();
    }

    private boolean handleBackStack() {
        if (mFragmentManager == null) mFragmentManager = getChildFragmentManager();
        if (mFragmentManager != null) {
            int count = mFragmentManager.getBackStackEntryCount();
            if (count == 0) {
                return true;
            }
            Fragment fragment = mFragmentManager.getFragments().get(count);
            if (fragment instanceof AlmostDoneFragment) {
                ((AlmostDoneFragment) (fragment)).clearUserData();
            }

            if (fragment instanceof ForgotPasswordFragment) {
                ((ForgotPasswordFragment) (fragment)).backPressed();
            }
            trackHandler();
            int stackCount = count - 1;
            if (fragment instanceof ResetPasswordWebView) {
                currentFragment = mFragmentManager.getFragments().get(stackCount);
                while (!(currentFragment instanceof HomeFragment)) {

                    mFragmentManager.popBackStack();
                    stackCount--;
                    currentFragment = mFragmentManager.getFragments().get(stackCount);

                }
                mFragmentManager.popBackStack();
                return true;
            }


            try {
                currentFragment = mFragmentManager.getFragments().get(count - 1);
                mFragmentManager.popBackStack();
                if (currentFragment instanceof HomeFragment && networkUtility.isNetworkAvailable()) {
                    ((HomeFragment) currentFragment).enableControlsOnNetworkStatusOnHomeFragment();
                }
            } catch (IllegalStateException e) {
                /**
                 * Ignore - No way to avoid this if some action is performed
                 * and the fragment is put into background before that action is completed
                 * See defect - 92539
                 */

            }
            if (fragment instanceof AccountActivationFragment) {
                RegUtility.setCreateAccountStartTime(System.currentTimeMillis());
            }
        } else {
            getActivity().finish();
        }
        return false;
    }

    private void trackHandler() {
        int count = mFragmentManager.getBackStackEntryCount();
        if (count > 0) {
            String curPage;
            if (mFragmentManager.getFragments() != null) {
                Fragment preFragment = mFragmentManager.getFragments().get(count - 1);
                curPage = getTackingPageName(preFragment);
                trackPage(curPage);
            }
        }

    }

    private String getTackingPageName(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            return AppTaggingPages.HOME;

        } else if (fragment instanceof CreateAccountFragment) {
            return AppTaggingPages.CREATE_ACCOUNT;

        } else if (fragment instanceof SignInAccountFragment) {
            return AppTaggingPages.CREATE_ACCOUNT;

        } else if (fragment instanceof AccountActivationFragment) {
            return AppTaggingPages.ACCOUNT_ACTIVATION;

        } else if (fragment instanceof AlmostDoneFragment) {
            return AppTaggingPages.ALMOST_DONE;

        } else if (fragment instanceof MarketingAccountFragment) {
            return AppTaggingPages.MARKETING_OPT_IN;

        } else if (fragment instanceof UserDetailsFragment) {
            return AppTaggingPages.USER_PROFILE;

        } else {
            return AppTaggingPages.MERGE_ACCOUNT;
        }
    }

    private void loadFirstFragment() {
        try {

            boolean hsdpSkipLoginConfigurationAvailable = RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable();
            User mUser = new User(getParentActivity().getApplicationContext());
            handleUseRLoginStateFragments(hsdpSkipLoginConfigurationAvailable, mFragmentManager, mUser);
        } catch (IllegalStateException e) {
            RLog.e(TAG, "loadFirstFragment :FragmentTransaction " +
                    "Exception occured in loadFirstFragment  :" + e.getMessage());
        }
    }

    @VisibleForTesting
    protected Fragment handleUseRLoginStateFragments(boolean hsdpSkipLoginConfiguration, FragmentManager pFragmentManager, User pUser) {

        boolean isUserSignIn = ((hsdpSkipLoginConfiguration && pUser.getUserLoginState().ordinal() >= UserLoginState.PENDING_HSDP_LOGIN.ordinal()) || pUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN);
        Fragment fragment;
        if (isUserSignIn) {
            if (mRegistrationLaunchMode != null && RegistrationLaunchMode.MARKETING_OPT.equals(mRegistrationLaunchMode)) {
                fragment = launchMarketingAccountFragment(pFragmentManager);
                RLog.d(TAG, "handleUseRLoginStateFragments : launchMarketingAccountFragment");
            } else {
                fragment = launchMyAccountFragment(pFragmentManager);
                RLog.d(TAG, "handleUseRLoginStateFragments : launchMyAccountFragment");
            }

        } else if (pUser.getUserLoginState().ordinal() == UserLoginState.PENDING_VERIFICATION.ordinal()) {

            if (FieldsValidator.isValidEmail(pUser.getEmail())) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegConstants.IS_SOCIAL_PROVIDER, true);
                AccountActivationFragment marketingAccountFragment = new AccountActivationFragment();
                marketingAccountFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = pFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_reg_fragment_container, marketingAccountFragment);
                fragmentTransaction.commitAllowingStateLoss();
                fragment = marketingAccountFragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegConstants.IS_SOCIAL_PROVIDER, true);
                MobileVerifyCodeFragment marketingAccountFragment = new MobileVerifyCodeFragment();
                marketingAccountFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = pFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_reg_fragment_container, marketingAccountFragment);
                fragmentTransaction.commitAllowingStateLoss();
                fragment = marketingAccountFragment;
            }


        }else {
            RLog.d(TAG, "handleUseRLoginStateFragments : launchHomeFragment");
            AppTagging.trackFirstPage(AppTaggingPages.HOME);
            fragment = replaceWithHomeFragment(pFragmentManager);
        }
        currentFragment = fragment;

        return fragment;
    }

    protected Fragment launchMyAccountFragment(FragmentManager pFragmentManager) {
        UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
        FragmentTransaction fragmentTransaction = pFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_reg_fragment_container, userDetailsFragment);
        fragmentTransaction.commitAllowingStateLoss();
        currentFragment = userDetailsFragment;

        return userDetailsFragment;
    }

    public void userRegistrationComplete() {
        if (RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener() != null) {
            RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener().
                    onUserRegistrationComplete(getParentActivity());
        } else {
            RegUtility.showErrorMessage(getParentActivity());
        }
    }

    private Fragment launchMarketingAccountFragment(FragmentManager pFragmentManager) {
        AppTagging.trackFirstPage(AppTaggingPages.MARKETING_OPT_IN);
        return replaceMarketingAccountFragment(pFragmentManager);
    }

    private void trackPage(String currPage) {
        AppTagging.trackPage(currPage);
    }

    public Fragment replaceWithHomeFragment(FragmentManager pFragmentManager) {
        try {
            if (null != pFragmentManager) {
                currentFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = pFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_reg_fragment_container, currentFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (IllegalStateException e) {
            RLog.d(TAG, "replaceWithHomeFragment :FragmentTransaction " +
                    "Exception occurred in addFragment  :" + e.getMessage());
        }
        return currentFragment;
    }

    public boolean isHomeFragment() {
        if (currentFragment instanceof HomeFragment) {
            return true;
        }
        return false;
    }

    public Fragment getCurrentFragment(){
        return  currentFragment;
    }

    Fragment currentFragment;

    public void addFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_reg_fragment_container, fragment, fragment.getTag());
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
        currentFragment = fragment;
        hideKeyBoard();
    }

    public void addFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_reg_fragment_container, fragment, fragment.getTag());
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
        currentFragment = fragment;
        hideKeyBoard();
    }

    private Fragment replaceMarketingAccountFragment(FragmentManager pFragmentManager) {
        MarketingAccountFragment marketingAccountFragment = new MarketingAccountFragment();
        FragmentTransaction fragmentTransaction = pFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_reg_fragment_container, marketingAccountFragment);
        fragmentTransaction.commitAllowingStateLoss();
        currentFragment = marketingAccountFragment;
        return marketingAccountFragment;
    }


    public void addAlmostDoneFragment(JSONObject preFilledRecord, String provider,
                                      String registrationToken) {

        RLog.d(TAG, "addAlmostDoneFragment : is called");

        AlmostDoneFragment socialAlmostDoneFragment = new AlmostDoneFragment();
        Bundle socialAlmostDoneFragmentBundle = new Bundle();
        socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_TWO_STEP_ERROR,
                preFilledRecord.toString());
        socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_PROVIDER, provider);
        socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_REGISTRATION_TOKEN,
                registrationToken);
        socialAlmostDoneFragmentBundle.putBoolean(RegConstants.IS_FOR_TERMS_ACCEPATNACE, true);
        socialAlmostDoneFragment.setArguments(socialAlmostDoneFragmentBundle);
        addFragment(socialAlmostDoneFragment);
    }

    public void addAlmostDoneFragmentforTermsAcceptance() {
        RLog.d(TAG, "addAlmostDoneFragmentforTermsAcceptance : is called");
        AlmostDoneFragment almostDoneFragment = new AlmostDoneFragment();
        addFragment(almostDoneFragment);
    }

    public void addPhilipsNewsFragment() {
        RLog.d(TAG, "addPhilipsNewsFragment : is called");
        PhilipsNewsFragment philipsNewsFragment = new PhilipsNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RegConstants.PHILIPS_NEWS_TITLE, getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt));
        bundle.putString(RegConstants.PHILIPS_NEWS_DISCRETION, getString(R.string.USR_DLS_PhilipsNews_Description_Text));
        addFragment(philipsNewsFragment, bundle);
    }

    public void addPersonalConsentFragment() {
        RLog.d(TAG, "addPersonalConsentFragment : is called");
        PhilipsNewsFragment philipsNewsFragment = new PhilipsNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RegConstants.PHILIPS_NEWS_TITLE, getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt));
        bundle.putString(RegConstants.PHILIPS_NEWS_DISCRETION, getString(getContentConfiguration().getPersonalConsentDefinition().getHelpText()));
        addFragment(philipsNewsFragment, bundle);
    }

    public void addMergeAccountFragment(String registrationToken, String provider, String emailId) {
        RLog.d(TAG, "addMergeAccountFragment : is called");
        MergeAccountFragment mergeAccountFragment = new MergeAccountFragment();
        Bundle mergeFragmentBundle = new Bundle();
        mergeFragmentBundle.putString(RegConstants.SOCIAL_PROVIDER, provider);
        mergeFragmentBundle.putString(RegConstants.SOCIAL_MERGE_TOKEN, registrationToken);
        mergeFragmentBundle.putString(RegConstants.SOCIAL_MERGE_EMAIL, emailId);
        mergeAccountFragment.setArguments(mergeFragmentBundle);
        addFragment(mergeAccountFragment);
    }

    public void addMergeSocialAccountFragment(Bundle bundle) {
        RLog.d(TAG, "addMergeSocialAccountFragment : is called");
        MergeSocialToSocialAccountFragment mergeAccountFragment
                = new MergeSocialToSocialAccountFragment();
        mergeAccountFragment.setArguments(bundle);
        addFragment(mergeAccountFragment);
    }


    public void launchAccountActivationFragmentForLogin() {
        RLog.d(TAG, "launchAccountActivationFragmentFoRLogin : is called");
        Bundle bundle = new Bundle();
        bundle.putBoolean(RegConstants.IS_SOCIAL_PROVIDER, true);
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
        AccountActivationFragment accountActivationFragment = new AccountActivationFragment();
        accountActivationFragment.setArguments(bundle);
        addFragment(accountActivationFragment);
    }


    public void addResetPasswordFragment() {
        RLog.d(TAG, "addResetPasswordFragment : is called");
        ForgotPasswordFragment resetPasswordFragment = new ForgotPasswordFragment();
        addFragment(resetPasswordFragment);
    }

    public void hideKeyBoard() {
        if (getParentActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getParentActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getParentActivity().getWindow() != null && getParentActivity().getWindow().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getParentActivity().getWindow().
                        getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void showKeyBoard() {
        if (getParentActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.d(TAG, "onNetWorkStateReceived : is called" + isOnline);
        if (!isOnline && !UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            UserRegistrationInitializer.getInstance().resetInitializationState();
        }
        if (!UserRegistrationInitializer.getInstance().isJanrainIntialized() &&
                !UserRegistrationInitializer.getInstance().isJumpInitializationInProgress()) {
            RegistrationHelper registrationSettings = RegistrationHelper.getInstance();
            registrationSettings
                    .initializeUserRegistration(getParentActivity()
                            .getApplicationContext());
            RLog.d(TAG,
                    "onNetWorkStateReceived : Janrain reinitialization with locale : "
                            + RegistrationHelper.getInstance().getLocale());
        }
    }

    public Activity getParentActivity() {
        return getActivity();
    }

    public int getFragmentCount() {
        FragmentManager fragmentManager = getChildFragmentManager();
        int fragmentCount = fragmentManager.getFragments().size();
        return fragmentCount;
    }

    public ActionBarListener getUpdateTitleListener() {
        return mActionBarListener;
    }

    public void setOnUpdateTitleListener(ActionBarListener listener) {
        this.mActionBarListener = listener;
    }

    public void setResourceID(int titleResourceId) {
        titleResourceID = titleResourceId;
    }

    public int getResourceID() {
        return titleResourceID;
    }

    int mPreviousResourceId = -99;

    public int getPreviousResourceId() {
        return mPreviousResourceId;
    }

    int currentTitleResource;

    public void setCurrentTitleResource(int currentTitleResource) {
        this.currentTitleResource = currentTitleResource;
    }

    private void setCounterState(boolean isCounting) {
        this.isCounterRunning = isCounting;
    }

    public boolean getCounterState() {
        return isCounterRunning;

    }

    @Override
    public boolean handleBackEvent() {
        return !(onBackPressed());
    }


    public void startCountDownTimer() {
        myCountDownTimer.start();
    }

    public void stopCountDownTimer() {
        myCountDownTimer.onFinish();
        myCountDownTimer.cancel();
    }

    public class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long timeLeft) {
            RLog.d(TAG, "onTick COUNTER_TICK : timeLeft : " + timeLeft);
            EventBus.getDefault().post(new CountDownEvent(RegConstants.COUNTER_TICK, timeLeft));
            setCounterState(true);
        }

        @Override
        public void onFinish() {
            EventBus.getDefault().post(new CountDownEvent(RegConstants.COUNTER_FINISH, 0));
            setCounterState(false);
        }
    }

    public View getNotificationContentView(String title, String message) {
        RLog.d(TAG, "getNotificationContentView : isCalled");
        View view = View.inflate(getContext(), R.layout.reg_notification_bg_accent, null);
        ((TextView) view.findViewById(R.id.uid_notification_title)).setText(title + " " + message);
        view.findViewById(R.id.uid_notification_title).setVisibility(View.VISIBLE);
        view.findViewById(R.id.uid_notification_content).setVisibility(View.VISIBLE);
        view.findViewById(R.id.uid_notification_icon).setVisibility(View.VISIBLE);
        view.findViewById(R.id.uid_notification_icon).setOnClickListener(v -> EventBus.getDefault().post(new NotificationBarHandler()));
        return view;
    }

}
