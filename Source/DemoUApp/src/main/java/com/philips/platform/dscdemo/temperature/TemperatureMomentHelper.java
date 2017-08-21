package com.philips.platform.dscdemo.temperature;

import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.dscdemo.database.datatypes.MomentDetailType;
import com.philips.platform.dscdemo.database.table.OrmMoment;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class TemperatureMomentHelper {

    String getTemperature(Moment moment) {
        try {

            ArrayList<? extends MeasurementGroup> measurementGroupParent = new ArrayList<>(moment.getMeasurementGroups());
            ArrayList<? extends MeasurementGroup> measurementGroupChild = new ArrayList<>(measurementGroupParent.get(0).getMeasurementGroups());
            ArrayList<? extends Measurement> measurements = new ArrayList<>(measurementGroupChild.get(0).getMeasurements());

            // ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
            return measurements.get(0).getValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "default";
        } catch (IndexOutOfBoundsException e) {
            return "default";
        }catch (Exception e){
            return "default";
        }
        //return -1;
    }

    String getTime(Moment moment) {
        try {
            ArrayList<? extends MomentDetail> momentDetails = new ArrayList<>(moment.getMomentDetails());
            for (MomentDetail detail : momentDetails) {
                if (detail.getType().equalsIgnoreCase(MomentDetailType.PHASE))
                    return detail.getValue();
            }
            return "default";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "default";
        } catch (IndexOutOfBoundsException e) {
            return "default";
        }catch (Exception e){
            return "default";
        }
    }


    String getNotes(Moment moment) {
        try {

            ArrayList<? extends MeasurementGroup> measurementGroupParent = new ArrayList<>(moment.getMeasurementGroups());
            ArrayList<? extends MeasurementGroup> measurementGroupChild = new ArrayList<>(measurementGroupParent.get(0).getMeasurementGroups());
            ArrayList<? extends Measurement> measurements = new ArrayList<>(measurementGroupChild.get(0).getMeasurements());

            // ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
            //return measurements.get(0).getValue();

            // ArrayList<? extends Measurement> measurements = new ArrayList<>(moment.getMeasurements());
            Measurement measurement = measurements.get(0);
            ArrayList<? extends MeasurementDetail> measurementDetails = new ArrayList<>(measurement.getMeasurementDetails());
            return measurementDetails.get(0).getValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "default";
        } catch (IndexOutOfBoundsException e) {
            return "default";
        }catch (Exception e){
            return "default";
        }
        //return null;
    }

    String getExpirationDate(Moment moment) {
        DateTime expirationDate = ((OrmMoment)moment).getExpirationDate();
        if(expirationDate != null){
            return expirationDate.toString();
        }else{
            return "never expires";
        }
    }

}
