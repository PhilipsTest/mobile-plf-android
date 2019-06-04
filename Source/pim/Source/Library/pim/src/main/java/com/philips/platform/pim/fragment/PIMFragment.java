package com.philips.platform.pim.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.R;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;

import java.util.Formatter;

import static android.app.Activity.RESULT_CANCELED;
import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Launch CLP page for authentication and Profile page if authenticated,
 * Exchange authorization code.
 */
public class PIMFragment extends Fragment implements PIMLoginListener {
    private PIMLoginManager pimLoginManager;
    private PIMOIDCConfigration pimoidcConfigration;
    private Context mContext;
    private LoggingInterface mLoggingInterface;
    private String TAG = PIMFragment.class.getSimpleName();
    private ProgressBar pimLoginProgreassBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        pimoidcConfigration = PIMSettingManager.getInstance().getPimOidcConfigration();
        pimLoginManager = new PIMLoginManager(mContext,pimoidcConfigration);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pim, container, false);

        pimLoginProgreassBar = view.findViewById(R.id.pbPimRequest);
        PIMUserManager pimUserManager = PIMSettingManager.getInstance().getPimUserManager();
        if (pimUserManager.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            mLoggingInterface.log(DEBUG, TAG, "OIDC Login skipped, as user is already logged in");
            launchUserProfilePage();
        } else if (pimoidcConfigration == null)
            mLoggingInterface.log(DEBUG, TAG, "Login is not initiated as OIDC configuration not found.");
        else {
            pimLoginProgreassBar.setVisibility(View.VISIBLE);
            launchLoginPage();
        }
        return view;
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

    /**
     * Launch user profile page if user is logged in.
     */
    private void launchUserProfilePage() {

        //TODO : Temp:  The url will be uploaded and fetched from Service Discovery
        final String USER_PROFILE_URL_STG = "https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/auth-ui/profile?client_id=%s&ui_locales=%s";

        StringBuilder url = new StringBuilder();
        try {
            Formatter fmt = new Formatter(url);
            fmt.format(USER_PROFILE_URL_STG, new PIMOIDCConfigration().getClientId(),  PIMSettingManager.getInstance().getLocale());
            Intent authReqIntent = new Intent(Intent.ACTION_VIEW);
            authReqIntent.setData(Uri.parse(url.toString()));
            startActivityForResult(authReqIntent, 200);
        } catch (Exception ex) {
            mLoggingInterface.log(DEBUG, TAG, "Launching user profile page failed."
                    +" url: "+url+" exception: "+ex.getMessage());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onLoginSuccess() {
        pimLoginProgreassBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoginFailed(Error error) {
        pimLoginProgreassBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoggingInterface.log(DEBUG, TAG, "onActivityResult : " + requestCode);
        if (resultCode == RESULT_CANCELED) {
            pimLoginProgreassBar.setVisibility(View.GONE);
        } else if(requestCode == 100) {
            pimLoginProgreassBar.setVisibility(View.VISIBLE);
            pimLoginManager.exchangeAuthorizationCode(data);
        }
    }
}
