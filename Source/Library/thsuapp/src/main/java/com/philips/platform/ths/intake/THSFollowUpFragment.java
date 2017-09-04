/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.pharmacy.THSPharmacyAndShippingFragment;
import com.philips.platform.ths.pharmacy.THSPharmacyListFragment;
import com.philips.platform.ths.pharmacy.THSSearchPharmacyFragment;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.CheckBox;
import com.philips.platform.uid.view.widget.EditText;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;

import static com.philips.platform.ths.utility.THSConstants.THS_ADD_VITALS_PAGE;
import static com.philips.platform.ths.utility.THSConstants.THS_FOLLOW_UP_PAGE;

public class THSFollowUpFragment extends THSBaseFragment implements View.OnClickListener {
    public static final String TAG = THSFollowUpFragment.class.getSimpleName();
    protected EditText mPhoneNumberEditText;
    private CheckBox mNoppAgreeCheckBox;
    ProgressBarButton mFollowUpContinueButton;
    private THSFollowUpPresenter mTHSFollowUpPresenter;
    private ActionBarListener actionBarListener;
    protected String updatedPhone;
    private Label nopp_label;
    private int REQUEST_LOCATION = 1001;
    private LocationManager mLocationManager = null;
    private String provider = null;
    protected Location updatedLocation = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_intake_follow_up, container, false);

        actionBarListener = getActionBarListener();
        if (null != actionBarListener) {
            actionBarListener.updateActionBar(R.string.ths_prepare_your_visit, true);
        }
        mTHSFollowUpPresenter = new THSFollowUpPresenter(this);
        mPhoneNumberEditText = (EditText) view.findViewById(R.id.pth_intake_follow_up_phone_number);
        mFollowUpContinueButton = (ProgressBarButton) view.findViewById(R.id.pth_intake_follow_up_continue_button);
        mFollowUpContinueButton.setOnClickListener(this);
        mFollowUpContinueButton.setEnabled(false);
        mNoppAgreeCheckBox = (CheckBox) view.findViewById(R.id.pth_intake_follow_up_nopp_agree_check_box);
        mNoppAgreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFollowUpContinueButton.setEnabled(true);
                } else {
                    mFollowUpContinueButton.setEnabled(false);
                }
            }
        });
        nopp_label = (Label) view.findViewById(R.id.pth_intake_follow_up_i_agree_link_text);
        nopp_label.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        THSConsumer THSConsumer = THSManager.getInstance().getPTHConsumer();
        if (null != THSConsumer && null != THSConsumer.getConsumer() && null != THSConsumer.getConsumer().getPhone() && !THSConsumer.getConsumer().getPhone().isEmpty()) {
            mPhoneNumberEditText.setText(THSConsumer.getConsumer().getPhone());
        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pth_intake_follow_up_continue_button) {

            mTHSFollowUpPresenter.onEvent(R.id.pth_intake_follow_up_continue_button);

        } else if (v.getId() == R.id.pth_intake_follow_up_i_agree_link_text) {

            mTHSFollowUpPresenter.onEvent(R.id.pth_intake_follow_up_i_agree_link_text);
        }

    }

    public void displayPharmacyAndShippingPreferenceFragment(Pharmacy pharmacy, Address address) {
        mFollowUpContinueButton.hideProgressIndicator();
        THSPharmacyAndShippingFragment thsPharmacyAndShippingFragment = new THSPharmacyAndShippingFragment();
        thsPharmacyAndShippingFragment.setConsumer(THSManager.getInstance().getPTHConsumer());
        thsPharmacyAndShippingFragment.setPharmacyAndAddress(address, pharmacy);
        thsPharmacyAndShippingFragment.setFragmentLauncher(getFragmentLauncher());
        addFragment(thsPharmacyAndShippingFragment, THSPharmacyAndShippingFragment.TAG, null);
    }

    public void displaySearchPharmacy() {
        mFollowUpContinueButton.hideProgressIndicator();
        if(checkGooglePlayServices()){
            checkPermission();
        }
    }


    /**
     * Location services code below. Checking if google play services is installed/latest/supported version?
     * @return
     */
    private boolean checkGooglePlayServices() {
        int result = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        switch (result) {
            case ConnectionResult.SUCCESS:
                return true;

            case ConnectionResult.SERVICE_INVALID:
                GooglePlayServicesUtil.getErrorDialog(
                        ConnectionResult.SERVICE_INVALID, getActivity(), 0).show();
                break;

            case ConnectionResult.SERVICE_MISSING:
                GooglePlayServicesUtil.getErrorDialog(
                        ConnectionResult.SERVICE_MISSING, getActivity(), 0).show();
                break;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GooglePlayServicesUtil.getErrorDialog(
                        ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
                        getActivity(), 0).show();
                break;

            case ConnectionResult.SERVICE_DISABLED:
                GooglePlayServicesUtil.getErrorDialog(
                        ConnectionResult.SERVICE_DISABLED, getActivity(), 0).show();
                break;
        }
        return false;
    }


    /**
     * Ask users permission to fetch location
     */
    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            getLocation();
        }
    }

    /**
     * This methos checks is the proivider is enabled to fetch the location
     */
    public void getLocation() {
        if (isProviderAvailable() && (provider != null)) {
            fetchCurrentLocation();
        }
    }

    /**
     * Calls the location fetch method of the location manager
     */
    private void fetchCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        long minTime = 5000;// ms
        float minDist = 5.0f;// meter
        mLocationManager.requestLocationUpdates(provider, minTime, minDist, locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v("Location", "Lon : " + location.getLongitude() + "::::Lat : " + location.getLatitude());
            updatedLocation = location;
            mLocationManager = null;
            callPharmacyListFragment(location);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void callPharmacyListFragment(Location location) {
        THSPharmacyListFragment thsPharmacyListFragment = new THSPharmacyListFragment();
        thsPharmacyListFragment.setConsumerAndAddress(THSManager.getInstance().getPTHConsumer(), null);
        thsPharmacyListFragment.setLocation(location);
        thsPharmacyListFragment.setFragmentLauncher(getFragmentLauncher());
        addFragment(thsPharmacyListFragment,THSPharmacyListFragment.TAG,null);

    }

    /**
     * checks is provider is available
     * @return
     */
    private boolean isProviderAvailable() {
        mLocationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        if (mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            return true;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        return provider != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                showPharmacySearch();
                showToast("Permission denied : Going to search");
            }
        }
    }

    private void showPharmacySearch() {

        THSSearchPharmacyFragment thsSearchPharmacyFragment = new THSSearchPharmacyFragment();
        thsSearchPharmacyFragment.setFragmentLauncher(getFragmentLauncher());
        addFragment(thsSearchPharmacyFragment, THSSearchPharmacyFragment.TAG, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        THSManager.getInstance().getThsTagging().trackPageWithInfo(THS_FOLLOW_UP_PAGE,null,null);
    }
}
