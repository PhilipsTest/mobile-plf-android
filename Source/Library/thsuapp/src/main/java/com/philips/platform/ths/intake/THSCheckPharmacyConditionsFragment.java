package com.philips.platform.ths.intake;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.cost.THSCostSummaryFragment;
import com.philips.platform.ths.insurance.THSInsuranceConfirmationFragment;
import com.philips.platform.ths.pharmacy.THSPharmacyAndShippingFragment;
import com.philips.platform.ths.pharmacy.THSPharmacyListFragment;
import com.philips.platform.ths.pharmacy.THSSearchPharmacyFragment;
import com.philips.platform.ths.utility.AmwellLog;
import com.philips.platform.ths.utility.THSManager;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class THSCheckPharmacyConditionsFragment extends THSBaseFragment implements THSCheckPharmacyConditonsView, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static String TAG = THSCheckPharmacyConditionsFragment.class.getSimpleName();

    private int REQUEST_LOCATION = 1001;
    private THSCheckPharmacyConditionsPresenter thscheckPharmacyConditionsPresenter;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    Intent gpsSettingsIntent;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
        }
        createLocationRequest();
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        thscheckPharmacyConditionsPresenter = new THSCheckPharmacyConditionsPresenter(this);
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            apiAvailability.getErrorDialog(getActivity(),status,0).show();
            return false;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        AmwellLog.d(TAG, "onStart fired ..............");
        getLocationUpdate();
        if (mGoogleApiClient != null)
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        AmwellLog.d(TAG, "onStop fired ..............");
        disconnectGoogleApiClient();
        AmwellLog.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        super.onStop();
    }

    public void disconnectGoogleApiClient() {
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
            }
        }
    }


    protected void stopLocationUpdates() {
        FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        AmwellLog.d(TAG, "Location update stopped .......................");
    }

    private void checkIfPharmacyRequired() {
        boolean isPharmacyRequired = THSManager.getInstance().getPthVisitContext().isCanPrescribe();
        if (isPharmacyRequired) {
            thscheckPharmacyConditionsPresenter.fetchConsumerPreferredPharmacy();
        } else {  // go to insurance or cost detail
            Consumer consumer = THSManager.getInstance().getPTHConsumer(getContext()).getConsumer();
            getActivity().getSupportFragmentManager().popBackStack();
            if (consumer.getSubscription() != null && consumer.getSubscription().getHealthPlan() != null) {
                final THSCostSummaryFragment fragment = new THSCostSummaryFragment();
                addFragment(fragment, THSCostSummaryFragment.TAG, null, true);
            } else {
                final THSInsuranceConfirmationFragment fragment = new THSInsuranceConfirmationFragment();
                addFragment(fragment, THSInsuranceConfirmationFragment.TAG, null, true);
            }
        }
    }

    public void displayPharmacy() {
        checkPermission();
    }

    /**
     * Ask users permission to fetch location
     */
    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            startLocationUpdates();
        }
    }

    private void callPharmacyListFragment(Location location) {
        if(isFragmentAttached()) {
            getActivity().getSupportFragmentManager().popBackStack();
            THSPharmacyListFragment thsPharmacyListFragment = new THSPharmacyListFragment();
            thsPharmacyListFragment.setConsumerAndAddress(THSManager.getInstance().getPTHConsumer(getContext()), null);
            thsPharmacyListFragment.setLocation(location);
            addFragment(thsPharmacyListFragment, THSPharmacyListFragment.TAG, null,true);
        }
    }

    private void showPharmacySearch() {
        if(isFragmentAttached()) {
            getActivity().getSupportFragmentManager().popBackStack();
            THSSearchPharmacyFragment thsSearchPharmacyFragment = new THSSearchPharmacyFragment();
            addFragment(thsSearchPharmacyFragment, THSSearchPharmacyFragment.TAG, null,true);
        }
    }

    public void displayPharmacyAndShippingPreferenceFragment(Pharmacy pharmacy, Address address) {
        if(isFragmentAttached()) {
            getActivity().getSupportFragmentManager().popBackStack();
            THSPharmacyAndShippingFragment thsPharmacyAndShippingFragment = new THSPharmacyAndShippingFragment();
            thsPharmacyAndShippingFragment.setPharmacyAndAddress(address, pharmacy);
            addFragment(thsPharmacyAndShippingFragment, THSPharmacyAndShippingFragment.TAG, null,true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        AmwellLog.d(TAG, "Firing onLocationChanged...... ::: Lat: " + location.getLatitude() + "Log:::: " + location.getLongitude());
        mCurrentLocation = location;
        callPharmacyListFragment(location);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        AmwellLog.d(TAG, "onConnected - isConnected ........: " + mGoogleApiClient.isConnected());
        checkIfPharmacyRequired();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (checkIfGPSProviderAvailable()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            Toast.makeText(getActivity(), "GPS not enables: going to settings GPS", Toast.LENGTH_SHORT).show();
            gpsSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsSettingsIntent);
        }


        // mGoogleApiClient, mLocationRequest, this);
        AmwellLog.d(TAG, "Location update started ..............: ");
    }

    private boolean checkIfGPSProviderAvailable() {

        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AmwellLog.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdate();
            } else {
                showPharmacySearch();
                showToast("Permission denied : Going to search");
            }
        }
    }

    private void getLocationUpdate() {
        if (mGoogleApiClient.isConnected()) {
            checkIfPharmacyRequired();
            AmwellLog.d(TAG, "Location update resumed .....................");
        }
    }


}
