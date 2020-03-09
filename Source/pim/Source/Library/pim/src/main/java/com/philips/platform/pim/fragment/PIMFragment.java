package com.philips.platform.pim.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.UserLoginListener;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.R;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.errors.PIMErrorCodes;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.manager.PIMConfigManager;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.utilities.PIMInitState;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Launch CLP page for authentication and Profile page if authenticated,
 * Exchange authorization code.
 */
public class PIMFragment extends Fragment implements PIMLoginListener, Observer<PIMInitState> {
    private PIMLoginManager pimLoginManager;
    private PIMOIDCConfigration pimoidcConfigration;
    private Context mContext;
    private LoggingInterface mLoggingInterface;
    private String TAG = PIMFragment.class.getSimpleName();
    private ProgressBar pimLoginProgreassBar;
    private boolean isInitRequiredAgain = true;
    private MutableLiveData<PIMInitState> liveData;
    private UserLoginListener mUserLoginListener;
    private final String USER_PROFILE_URL = "userreg.janrainoidc.userprofile";
    private HashMap consentParameterMap;
    private boolean isTokenReqInProcess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        liveData = PIMSettingManager.getInstance().getPimInitLiveData();
        liveData.observe(this, this::onChanged);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @
            Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pim, container, false);
        pimLoginProgreassBar = view.findViewById(R.id.pbPimRequest);
        Bundle bundle = getArguments();
        if (bundle != null) {
            consentParameterMap = (HashMap) bundle.get(PIMInterface.PIM_KEY_CONSENTS);
        }
        return view;
    }

    public void setActionbarListener(ActionBarListener actionbarListener, UserLoginListener userLoginListener) {
        mUserLoginListener = userLoginListener;
    }

    @Override
    public void onChanged(@Nullable PIMInitState pimInitState) {
        mLoggingInterface.log(DEBUG, TAG, "Init State : " + pimInitState.ordinal() + " isInitRequiredAgain : " + isInitRequiredAgain);
        if (pimInitState == PIMInitState.INIT_FAILED) {
            if (isInitRequiredAgain) {
                enablProgressBar();
                new PIMConfigManager(PIMSettingManager.getInstance().getPimUserManager()).init(mContext, PIMSettingManager.getInstance().getAppInfraInterface().getServiceDiscovery());
                isInitRequiredAgain = false;
            } else {
                disableProgressBar();
            }
        } else if (pimInitState == PIMInitState.INIT_SUCCESS) {
            pimoidcConfigration = PIMSettingManager.getInstance().getPimOidcConfigration();
            pimLoginManager = new PIMLoginManager(mContext, pimoidcConfigration, consentParameterMap);
            isInitRequiredAgain = false;
            enablProgressBar();
            launch();
        }
    }

    private void launch() {
        if (PIMSettingManager.getInstance().getPimUserManager().getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            mLoggingInterface.log(DEBUG, TAG, "OIDC Login skipped, as user is already logged in");
            downloadUserProfileUrlFromSD();
        } else {
            pimLoginProgreassBar.setVisibility(View.VISIBLE);
            launchLoginPage();
        }
    }

    /**
     * Launch web page for authentication.
     */
    private void launchLoginPage() {
        try {
            Intent authReqIntent = pimLoginManager.getAuthReqIntent(this);
            startActivityForResult(authReqIntent, 100);
        } catch (Exception ex) {
            mLoggingInterface.log(DEBUG, TAG, "Launching login page failed.");
        }
    }

    private void downloadUserProfileUrlFromSD() {
        ArrayList<String> serviceIdList = new ArrayList<>();
        serviceIdList.add(USER_PROFILE_URL);
        PIMSettingManager.getInstance().getAppInfraInterface().getServiceDiscovery().getServicesWithCountryPreference(serviceIdList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                ServiceDiscoveryService serviceDiscoveryService = urlMap.get(USER_PROFILE_URL);
                String userProfileUrl = serviceDiscoveryService.getConfigUrls();
                String locale = serviceDiscoveryService.getLocale();
                PIMSettingManager.getInstance().setLocale(locale);
                mLoggingInterface.log(DEBUG, TAG, "downloadUserProfileUrlFromSD onSuccess. Url : " + userProfileUrl + " Locale : " + locale);
                launchUserProfilePage(userProfileUrl);
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                mLoggingInterface.log(DEBUG, TAG, "downloadUserProfileUrlFromSD failed.");
            }
        }, null);
    }

    /**
     * Launch user profile page if user is logged in.
     */
    private void launchUserProfilePage(String userProfileUrl) {
        String clientId;
        PIMSettingManager pimSettingManager = PIMSettingManager.getInstance();
        if (pimSettingManager.getPimUserManager().getLoginFlow() == PIMUserManager.LOGIN_FLOW.MIGRATION) {
            clientId = pimoidcConfigration.getMigrationClientId();
        } else
            clientId = pimoidcConfigration.getClientId();
        String urlString = "http://www.philips.com";
        String[] urlStringWithVisitorId = pimSettingManager.getTaggingInterface().getVisitorIDAppendToURL(urlString).split("=");
        mLoggingInterface.log(DEBUG, TAG, "External URL with Adobe_mc : " + urlStringWithVisitorId[1]);

        try {
            String[] userprofileBaseString = userProfileUrl.split("\\?");
            Uri userrofileURI = Uri.parse(userprofileBaseString[0]).buildUpon().appendQueryParameter("client_id", clientId).build();
            String locale = pimSettingManager.getLocale();
            userrofileURI = Uri.parse(userrofileURI.toString()).buildUpon().appendQueryParameter("ui_locales", locale != null ? locale : "en-US").build();
            userrofileURI = Uri.parse(userrofileURI.toString()).buildUpon().appendQueryParameter("adobe_mc", urlStringWithVisitorId[1]).build();
            Intent authReqIntent = new Intent(Intent.ACTION_VIEW);
            authReqIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            authReqIntent.setData(userrofileURI);
            mLoggingInterface.log(DEBUG, TAG, "Launching user profile : " + userrofileURI.toString());
            startActivityForResult(authReqIntent, 200);
        } catch (Exception ex) {
            mLoggingInterface.log(DEBUG, TAG, "Launching user profile page failed."
                    + " url: " + userProfileUrl + " exception: " + ex.getMessage());
            disableProgressBar();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onLoginSuccess() {
        disableProgressBar();
        isTokenReqInProcess = false;
        mUserLoginListener.onLoginSuccess();
    }

    @Override
    public void onLoginFailed(Error error) {
        disableProgressBar();
        isTokenReqInProcess = false;
        mUserLoginListener.onLoginFailed(error);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoggingInterface.log(DEBUG, TAG, "onActivityResult => requestCode : " + requestCode + "  resultCode : " + resultCode + "    isTokenReqInProcess : " + isTokenReqInProcess);
        if (isTokenReqInProcess)
            return;

        if (requestCode == 100 && resultCode == RESULT_OK && pimLoginManager.isAuthorizationSuccess(data)) {
            isTokenReqInProcess = true;
            pimLoginManager.exchangeAuthorizationCode(data);
        } else if (requestCode == 100 && resultCode == RESULT_CANCELED) {
            disableProgressBar();
            Error error = new Error(PIMErrorCodes.USER_CANCELED_AUTH_FLOW, PIMErrorEnums.getLocalisedErrorDesc(mContext, PIMErrorCodes.USER_CANCELED_AUTH_FLOW));
            mUserLoginListener.onLoginFailed(error);
        } else {
            disableProgressBar();
        }
    }

    private void enablProgressBar() {
        pimLoginProgreassBar.setVisibility(View.VISIBLE);
    }

    private void disableProgressBar() {
        pimLoginProgreassBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoggingInterface.log(DEBUG, TAG, "onDestroy Called");
        if (liveData != null)
            liveData.removeObserver(this::onChanged);
    }
}
