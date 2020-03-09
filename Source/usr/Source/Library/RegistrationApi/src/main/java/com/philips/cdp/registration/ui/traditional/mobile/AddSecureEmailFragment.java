package com.philips.cdp.registration.ui.traditional.mobile;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.AccountActivationFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.cdp.registration.ui.utils.LoginIdValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.ValidLoginId;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;


public class AddSecureEmailFragment extends RegistrationBaseFragment implements AddSecureEmailContract {

    private String TAG = "AddSecureEmailFragment";

    @BindView(R2.id.btn_reg_secure_data_email)
    ProgressBarButton addRecoveryEmailButton;

    @BindView(R2.id.btn_reg_secure_data_email_later)
    Button maybeLaterButton;

    @BindView(R2.id.rl_reg_securedata_email_field)
    ValidationEditText recoveryEmail;

   @BindView(R2.id.rl_reg_securedata_email_field_inputValidation)
    InputValidationLayout rl_reg_securedata_email_field_inputValidation;

    @BindView(R2.id.reg_error_msg)
    XRegError recoveryErrorTextView;

    private AddSecureEmailPresenter addSecureEmailPresenter;

    @BindView(R2.id.ll_reg_root_container)
    LinearLayout regRootContainer;

    private boolean isValidEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerInlineNotificationListener(this);
        RLog.i(TAG,"Screen name is "+ TAG);
        addSecureEmailPresenter = new AddSecureEmailPresenter(this);
        View view = inflater.inflate(R.layout.reg_fragment_secure_email, container, false);
        ButterKnife.bind(this, view);
        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        setUpRecoveryEmail();
        return view;
    }

    private void setUpRecoveryEmail() {
        rl_reg_securedata_email_field_inputValidation.setValidator(emailValidator);
        recoveryEmail.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    LoginIdValidator emailValidator = new LoginIdValidator(new ValidLoginId() {
        @Override
        public int isValid(boolean valid) {
            isValidEmail = valid;
            if(valid){
                addRecoveryEmailButton.setEnabled(true);
                if(!rl_reg_securedata_email_field_inputValidation.isShowingError())
                    rl_reg_securedata_email_field_inputValidation.hideError();
            }else{
                addRecoveryEmailButton.setEnabled(false);
                rl_reg_securedata_email_field_inputValidation.setErrorMessage(R.string.USR_InvalidOrMissingEmail_ErrorMsg);
                if(!rl_reg_securedata_email_field_inputValidation.isShowingError())
                    rl_reg_securedata_email_field_inputValidation.showError();
            }
            return 0;
        }

        @Override
        public int isEmpty(boolean emptyField) {
            if(emptyField)
                addRecoveryEmailButton.setEnabled(false);
            isValidEmail = false;
            return 0;
        }
    });


    @Override
    protected void setViewParams(Configuration config, int width) {
       // applyParams(config,regRootContainer,width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.USR_DLS_URCreateAccount_NavTitle;
    }


    @OnClick(R2.id.btn_reg_secure_data_email)
    public void addEmailButtonClicked() {
        RLog.i(TAG,TAG+".addEmailButton clicked");
        recoveryErrorTextView.setVisibility(GONE);
        addSecureEmailPresenter.addEmailClicked(recoveryEmail.getText().toString());
        AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.KEY_SECURE_DATA_WITH_EMAIL);
    }

    @OnClick(R2.id.btn_reg_secure_data_email_later)
    public void maybeLaterButtonClicked() {
        RLog.i(TAG,TAG+".maybeLaterButton clicked");
        addSecureEmailPresenter.maybeLaterClicked();
        AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.KEY_SKIP_SECURE_DATA);
    }

    @Override
    public void registrationComplete() {
        getRegistrationFragment().userRegistrationComplete();
    }

    @Override
    public void showInvalidEmailError() {
        rl_reg_securedata_email_field_inputValidation.setErrorMessage(
                getString(R.string.USR_InvalidEmailAdddress_ErrorMsg));
        rl_reg_securedata_email_field_inputValidation.showError();
    }

    @Override
    public void onAddRecoveryEmailSuccess() {
        ((RegistrationFragment) getParentFragment()).hideKeyBoard();

        getRegistrationFragment().addFragment(new AccountActivationFragment());
    }

    @Override
    public void onAddRecoveryEmailFailure(String error) {
        recoveryErrorTextView.setError(error);
        recoveryErrorTextView.setVisibility(VISIBLE);
    }

    @Override
    public void enableButtons() {
        if(isValidEmail) {
            addRecoveryEmailButton.setEnabled(true);
        }else {
            addRecoveryEmailButton.setEnabled(false);
        }
        maybeLaterButton.setEnabled(true);

    }

    @Override
    public void disableButtons() {
        addRecoveryEmailButton.setEnabled(false);
        maybeLaterButton.setEnabled(false);
    }

    @Override
    public void showNetworkUnavailableError() {
        recoveryErrorTextView.setError(getResources().getString(R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg));
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
        addRecoveryEmailButton.setEnabled(false);
        addSecureEmailPresenter.registerNetworkListener();
        ((RegistrationFragment) getParentFragment()).hideKeyBoard();
        recoveryEmail.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addSecureEmailPresenter.cleanUp();
    }

    @Override
    public void showProgress() {
        addRecoveryEmailButton.showProgressIndicator();
        maybeLaterButton.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        addRecoveryEmailButton.hideProgressIndicator();
        maybeLaterButton.setEnabled(true);
    }

    @Override
    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(
                getRegistrationFragment().getContext(), RegConstants.TERMS_N_CONDITIONS_ACCEPTED,emailOrMobileNumber);
    }

    @Override
    public void notificationInlineMsg(String msg) {
        recoveryErrorTextView.setError(msg);
    }
}
