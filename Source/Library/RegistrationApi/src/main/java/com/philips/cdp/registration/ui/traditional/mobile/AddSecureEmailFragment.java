package com.philips.cdp.registration.ui.traditional.mobile;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.ui.customviews.LoginIdEditText;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.AccountActivationFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;


public class AddSecureEmailFragment extends RegistrationBaseFragment implements AddSecureEmailContract {

    @BindView(R2.id.btn_reg_secure_data_email)
    Button addRecoveryEmailButton;

    @BindView(R2.id.btn_reg_secure_data_email_later)
    Button maybeLaterButton;

    @BindView(R2.id.rl_reg_securedata_email_field)
    LoginIdEditText recoveryEmail;

    @BindView(R2.id.reg_error_msg)
    XRegError recoveryErrorTextView;

    @BindView(R2.id.add_email_progress)
    ProgressBar addEmailProgress;

    private AddSecureEmailPresenter addSecureEmailPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MobileActivationFragment : onCreateView");
        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        addSecureEmailPresenter = new AddSecureEmailPresenter(this);
        View view = inflater.inflate(R.layout.reg_fragment_secure_email, container, false);
        ButterKnife.bind(this, view);
        setUpRecoveryEmail();
        return view;
    }

    private void setUpRecoveryEmail() {
        recoveryEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        recoveryEmail.setHint(getString(R.string.reg_EmailAddPlaceHolder_txtField));
    }

    @Override
    protected void setViewParams(Configuration config, int width) {

    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_RegCreateAccount_NavTitle;
    }


    @OnClick(R2.id.btn_reg_secure_data_email)
    public void addEmailButtonClicked() {
        recoveryErrorTextView.setVisibility(GONE);
        addSecureEmailPresenter.addEmailClicked(recoveryEmail.getEmailId());
    }

    @OnClick(R2.id.btn_reg_secure_data_email_later)
    public void maybeLaterButtonClicked() {
        addSecureEmailPresenter.maybeLaterClicked();
    }

    @Override
    public void registrationComplete() {
        getRegistrationFragment().userRegistrationComplete();
    }

    @Override
    public void showInvalidEmailError() {
        recoveryEmail.setErrDescription(getString(R.string.reg_InvalidEmailAdddress_ErrorMsg));
        recoveryEmail.showInvalidAlert();
    }

    @Override
    public void onAddRecoveryEmailSuccess() {
        getRegistrationFragment().addFragment(new AccountActivationFragment());
    }

    @Override
    public void onAddRecoveryEmailFailure(String error) {
        recoveryErrorTextView.setError(error);
        recoveryErrorTextView.setVisibility(VISIBLE);
    }

    @Override
    public void enableButtons() {
        addRecoveryEmailButton.setEnabled(true);
        maybeLaterButton.setEnabled(true);

    }

    @Override
    public void disableButtons() {
        addRecoveryEmailButton.setEnabled(false);
        maybeLaterButton.setEnabled(false);
    }

    @Override
    public void showNetworkUnavailableError() {
        recoveryErrorTextView.setError(getResources().getString(R.string.reg_Generic_Network_Error));
        recoveryErrorTextView.setVisibility(VISIBLE);
    }

    @Override
    public void hideError() {
        recoveryErrorTextView.setError(null);
        recoveryErrorTextView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        addSecureEmailPresenter.registerNetworkListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addSecureEmailPresenter.cleanUp();
    }

    @Override
    public void showProgress() {
        addEmailProgress.setVisibility(VISIBLE);
    }

    @Override
    public void hideProgress() {
        addEmailProgress.setVisibility(GONE);
    }

    @Override
    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(getRegistrationFragment().getContext(), emailOrMobileNumber, true);
    }
}
