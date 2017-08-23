/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.registration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.cdp.registration.User;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.pharmacy.THSSpinnerAdapter;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uid.view.widget.EditText;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class THSRegistrationFragment extends THSBaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = THSRegistrationFragment.class.getSimpleName();
    private THSRegistrationPresenter mThsRegistrationPresenter;
    private RelativeLayout mRelativeLayout;
    private ProgressBarButton mContinueButton;
    private  EditText mEditTextFirstName;
    private EditText mEditTextLastName;
    private EditText mDateOfBirth;
    private EditText mEditTextStateSpinner;
    private RadioButton mCheckBoxMale;
    private RadioButton mCheckBoxFemale;
    private CustomSpinner mStateSpinner;
    private THSSpinnerAdapter spinnerAdapter;
    private List<State> mValidStates = null;
    private Date mDob;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_registration_form, container, false);

        if (null != getActionBarListener()) {
            getActionBarListener().updateActionBar(getString(R.string.ths_your_details), true);
        }

        setView(view);
        mThsRegistrationPresenter = new THSRegistrationPresenter(this);
        return view;
    }

    private void setView(ViewGroup view) {
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.registration_form_container);
        mContinueButton = (ProgressBarButton) view.findViewById(R.id.ths_continue);
        mContinueButton.setOnClickListener(this);
        mEditTextFirstName = (EditText) view.findViewById(R.id.ths_edit_first_name);
        mEditTextLastName = (EditText) view.findViewById(R.id.ths_edit_last_name);
        mDateOfBirth = (EditText) view.findViewById(R.id.ths_edit_dob);
        mDateOfBirth.setFocusable(false);
        mDateOfBirth.setClickable(true);
        mDateOfBirth.setOnClickListener(this);
        mEditTextStateSpinner = (EditText) view.findViewById(R.id.ths_edit_location_container);
        mEditTextStateSpinner.setFocusable(false);
        mEditTextStateSpinner.setClickable(true);
        mEditTextStateSpinner.setOnClickListener(this);
        mCheckBoxMale = (RadioButton) view.findViewById(R.id.ths_checkbox_male);
        mCheckBoxFemale = (RadioButton) view.findViewById(R.id.ths_checkbox_female);
        mStateSpinner = new CustomSpinner(getContext(),null);

        try {
            final List<Country> supportedCountries = THSManager.getInstance().getAwsdk(getActivity().getApplicationContext()).getSupportedCountries();
            mValidStates = THSManager.getInstance().getAwsdk(getActivity().getApplicationContext()).getConsumerManager().getValidPaymentMethodStates(supportedCountries.get(0));
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }


        spinnerAdapter = new THSSpinnerAdapter(getActivity(), R.layout.ths_pharmacy_spinner_layout, mValidStates);
        mStateSpinner.setAdapter(spinnerAdapter);
        mStateSpinner.setSelection(0);
        mStateSpinner.setOnItemSelectedEvenIfUnchangedListener(this);

        prepopilateData();
    }

    private void prepopilateData() {
        final User user = THSManager.getInstance().getUser(getContext());
        if(user == null)
            return;
        if(user.getGivenName()!=null) {
            mEditTextFirstName.setText(user.getGivenName());
        }
        if(user.getFamilyName()!=null) {
            mEditTextLastName.setText(user.getFamilyName());
        }
        if(user.getDateOfBirth()!=null) {
            setDate(user.getDateOfBirth());
        }

        final com.philips.cdp.registration.ui.utils.Gender gender = user.getGender();
        if(gender == null)
            return;

        if(gender == com.philips.cdp.registration.ui.utils.Gender.FEMALE){
            mCheckBoxFemale.setChecked(true);
        }else {
            mCheckBoxMale.setSelected(true);
        }
    }

    @Override
    public void finishActivityAffinity() {
        getActivity().finishAffinity();
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return getActivity();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.ths_continue){
            mThsRegistrationPresenter.enrollUser(mDob,mEditTextFirstName.getText().toString(),
                    mEditTextLastName.getText().toString(), Gender.MALE,mValidStates.get(mStateSpinner.getSelectedItemPosition()));
        }if(id == R.id.ths_edit_dob){
            mThsRegistrationPresenter.onEvent(R.id.ths_edit_dob);
        }if(id == R.id.ths_edit_location_container){
            mStateSpinner.performClick();
        }
    }

    public void updateDobView(Date date) {
        mDob = date;
        setDate(date);
    }

    private void setDate(Date date) {
        mDateOfBirth.setText(new SimpleDateFormat(THSConstants.DATE_FORMATTER, Locale.getDefault()).
                format(date));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mEditTextStateSpinner.setText(mValidStates.get(i).getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
