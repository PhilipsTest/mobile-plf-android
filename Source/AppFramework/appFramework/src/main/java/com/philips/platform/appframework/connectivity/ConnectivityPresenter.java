package com.philips.platform.appframework.connectivity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.registration.User;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.connectivity.appliance.BleReferenceAppliance;
import com.philips.platform.appframework.connectivity.appliance.DeviceMeasurementPort;
import com.philips.platform.appframework.connectivity.models.Measurement;
import com.philips.platform.appframework.connectivity.models.MomentDetail;
import com.philips.platform.appframework.connectivity.models.UserMoment;
import com.philips.platform.appframework.connectivity.network.GetMomentRequest;
import com.philips.platform.appframework.connectivity.network.PostMomentRquest;

import java.util.ArrayList;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ConnectivityPresenter implements ConnectivityContract.UserActionsListener {
    private ConnectivityContract.View connectivityViewListener;
    private Context context;

    public ConnectivityPresenter(final ConnectivityContract.View connectivityViewListener, Context context) {
        this.connectivityViewListener = connectivityViewListener;
        this.context = context;
    }

    @Override
    public void postMoment(final User user, final String momentValue) {
        PostMomentRquest postMomentRquest = new PostMomentRquest(getDummyUserMoment(momentValue), user, postMomentResponseListener);
        postMomentRquest.executeRequest(context.getApplicationContext());
    }

    @Override
    public void getMoment(final User user, final String momentId) {
        GetMomentRequest getMomentRequest = new GetMomentRequest(getMomentResponseListener,user,momentId);
        getMomentRequest.executeRequest(context.getApplicationContext());
    }

    @Override
    public void setUpApplicance(@NonNull BleReferenceAppliance appliance) {
        if (appliance == null) {
            throw new IllegalArgumentException("Cannot create bleReferenceAppliance for provided NetworkNode.");
        }


        // Setup device measurement port
        appliance.getDeviceMeasurementPort().addPortListener(new DICommPortListener<DeviceMeasurementPort>() {

            @Override
            public void onPortUpdate(DeviceMeasurementPort deviceMeasurementPort) {
                connectivityViewListener.updateConnectionStateText(context.getString(R.string.disconnected));
                if(deviceMeasurementPort!=null && deviceMeasurementPort.getPortProperties()!=null) {
                    connectivityViewListener.updateDeviceMeasurementValue(Integer.toString(deviceMeasurementPort.getPortProperties().measurementvalue));
                }else{
                    connectivityViewListener.onDeviceMeasurementError(Error.NOT_AVAILABLE,"");
                }
            }

            @Override
            public void onPortError(DeviceMeasurementPort deviceMeasurementPort, Error error, final String s) {
                connectivityViewListener.updateConnectionStateText(context.getString(R.string.disconnected));
                connectivityViewListener.onDeviceMeasurementError(error,s);
            }
        });

    }

    private PostMomentRquest.PostMomentResponseListener postMomentResponseListener = new PostMomentRquest.PostMomentResponseListener() {
        @Override
        public void onPostMomentSuccess(final String momentId) {
            connectivityViewListener.updateUIOnPostMomentSuccess(momentId);
        }

        @Override
        public void onPostMomentError(final VolleyError error) {
            connectivityViewListener.updateUIOnPostMomentError(error);
        }
    };

    private GetMomentRequest.GetMomentResponseListener getMomentResponseListener = new GetMomentRequest.GetMomentResponseListener() {
        @Override
        public void onGetMomentSuccess(final String momentValue) {
            connectivityViewListener.updateUIOnGetMomentSuccess(momentValue);
        }

        @Override
        public void onGetMomentError(final VolleyError error) {
            connectivityViewListener.updateUIOnGetMomentError(error);
        }
    };

    /**
     * make dummy user moment
     *
     * @return
     */
    public UserMoment getDummyUserMoment(String editTextValue) {
        MomentDetail momentDetail = new MomentDetail();
        momentDetail.setType("Example");
        momentDetail.setValue(editTextValue);

        Measurement measurement = new Measurement();
        ArrayList<MomentDetail> momentDetailList = new ArrayList<MomentDetail>();
        momentDetailList.add(momentDetail);
        measurement.setMomentDetailArrayList(momentDetailList);
        measurement.setTimestamp("2015-08-14T07:07:14.000Z");
        measurement.setType("Duration");
        measurement.setUnit("sec");
        measurement.setValue(editTextValue);

        ArrayList<Measurement> measurementsList = new ArrayList<Measurement>();
        measurementsList.add(measurement);

        UserMoment userMoment = new UserMoment();
        userMoment.setMomentDetailArrayList(momentDetailList);
        userMoment.setMeasurementArrayList(measurementsList);
        userMoment.setTimestamp("2015-08-13T14:54:25+0200");
        userMoment.setType("Example");
        return userMoment;
    }
}
