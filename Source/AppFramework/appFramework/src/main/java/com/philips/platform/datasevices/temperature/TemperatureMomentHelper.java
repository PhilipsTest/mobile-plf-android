package com.philips.platform.datasevices.temperature;

import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.MomentDetailType;
import com.philips.platform.datasevices.listener.DBChangeListener;
import com.philips.platform.datasevices.listener.EventHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class TemperatureMomentHelper {

    double getTemperature(Moment moment){
        try {
            ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
            return measurements.get(0).getValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0.0;
        } catch (IndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    String getTime(Moment moment){
        try {
            ArrayList<? extends MomentDetail> momentDetails = new ArrayList<>(moment.getMomentDetails());
            for(MomentDetail detail : momentDetails){
                if(detail.getType() == MomentDetailType.PHASE)
                    return detail.getValue();
            }
            return "default";
        }catch (ArrayIndexOutOfBoundsException e){
            return "default";
        }catch (IndexOutOfBoundsException e){
            return "default";
        }
    }

    String getNotes(Moment moment){
        try {
            ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
            Measurement measurement = measurements.get(0);
            ArrayList<? extends MeasurementDetail> measurementDetails = new ArrayList<>(measurement.getMeasurementDetails());
            return measurementDetails.get(0).getValue();
        }catch (ArrayIndexOutOfBoundsException e){
            return "default";
        }catch (IndexOutOfBoundsException e){
            return "default";
        }
    }

    public void notifySuccessToAll(final ArrayList<? extends Object> ormMoments) {
        final ArrayList<DBChangeListener> dbChangeListeners = EventHelper.getInstance().getEventMap().get(EventHelper.MOMENT);
        if (dbChangeListeners != null) {
            for (DBChangeListener listener : dbChangeListeners) {
                listener.onSuccess(ormMoments);
            }
        }
    }

    public void notifyAllSuccess(Object ormMoments) {
        final ArrayList<DBChangeListener> dbChangeListeners = EventHelper.getInstance().getEventMap().get(EventHelper.MOMENT);
        if (dbChangeListeners != null) {
            for (DBChangeListener listener : dbChangeListeners) {
                listener.onSuccess(ormMoments);
            }
        }
    }

    public void notifyAllFailure(Exception e) {
        Map<Integer, ArrayList<DBChangeListener>> eventMap = EventHelper.getInstance().getEventMap();
        Set<Integer> integers = eventMap.keySet();
        if (integers.contains(EventHelper.MOMENT)) {
            ArrayList<DBChangeListener> dbChangeListeners = EventHelper.getInstance().getEventMap().get(EventHelper.MOMENT);
            for (DBChangeListener listener : dbChangeListeners) {
                listener.onFailure(e);
            }
        }
    }

    Moment updateMoment(Moment moment,String phase, String temperature, String notes){
        ArrayList<? extends MomentDetail> momentDetails = new ArrayList<>(moment.getMomentDetails());
        int momentDetailsSize=momentDetails.size();
        MomentDetail momentDetail = momentDetails.get(momentDetailsSize-1);
        momentDetail.setValue(phase);

        ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
        Measurement measurement = measurements.get(0);
        measurement.setValue(Double.parseDouble(temperature));

        ArrayList<? extends MeasurementDetail> measurementDetails = new ArrayList<>(measurement.getMeasurementDetails());
        MeasurementDetail measurementDetail = measurementDetails.get(0);
        measurementDetail.setValue(notes);

        return moment;
    }
}
