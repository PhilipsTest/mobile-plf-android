/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.datasync.moments;

import android.support.annotation.NonNull;

import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.MeasurementGroupDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.platform.core.trackers.DataServicesManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/*
 * *
 *  * (C) Koninklijke Philips N.V., 2015.
 *  * All rights reserved.
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MomentsConverter {

    @Inject
    BaseAppDataCreator baseAppDataCreater;

    @Inject
    public MomentsConverter() {
        DataServicesManager.getInstance().getAppComponant().injectMomentsConverter(this);
    }

    @NonNull
    public List<Moment> convert(@NonNull final List<UCoreMoment> uCoreMoments) {

        List<Moment> momentList = new ArrayList<>();
        try {
            for (UCoreMoment uCoreMoment : uCoreMoments) {
                Moment moment = createMoment(uCoreMoment);
                momentList.add(moment);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return momentList;
    }

    @NonNull
    private Moment createMoment(@NonNull final UCoreMoment uCoreMoment) {
        Moment moment = baseAppDataCreater.createMoment(uCoreMoment.getCreatorId(), uCoreMoment.getSubjectId(),
                uCoreMoment.getType());

        moment.setDateTime(new DateTime(uCoreMoment.getTimestamp()));

        addSynchronisationData(moment, uCoreMoment);

        List<UCoreDetail> details = uCoreMoment.getDetails();
        if (details != null) {
            addToUCoreMomentDetails(moment, details);
        }

        List<UCoreMeasurementGroups> measurementGroups = uCoreMoment.getMeasurementGroups();
        if (measurementGroups != null && measurementGroups.size() != 0) {
            //DSLog.i(DSLog.LOG, "BEFORE addMeasurementGroupsToMomentNoRecursion" + moment.toString() + "and UcoreMoment = " + uCoreMoment);
            addMeasurementGroupsToMomentNoRecursion(true,moment, null,measurementGroups);
        }
        return moment;
    }

    private void addMeasurementGroupsToMomentNoRecursion(final boolean isRoot, final Moment moment, final MeasurementGroup parents, final List<UCoreMeasurementGroups> measurementGroups) {
       // DSLog.i(DSLog.LOG, "BEFORE addMeasurementGroupsToMomentNoRecursion" + moment.toString() + "and list of uCoremeaurement = " + measurementGroups.toString());
        for (UCoreMeasurementGroups uCoreMeasurementGroups : measurementGroups) {

        //    DSLog.i(DSLog.LOG, "Inside for loop for uCoreMeasurementGroup = " + uCoreMeasurementGroups);
            MeasurementGroup parentOrm;
            if(!isRoot){
                parentOrm = baseAppDataCreater.createMeasurementGroup(parents);
            }else {
                parentOrm = baseAppDataCreater.createMeasurementGroup(moment);
            }
            addMeasurementsAndDeatilsToMeasurementGroup(uCoreMeasurementGroups, parentOrm);

            if(!isRoot)
            parents.addMeasurementGroup(parentOrm);
            if(uCoreMeasurementGroups.getMeasurementGroups()!=null && uCoreMeasurementGroups.getMeasurementGroups().size()>0)
            {
            //    MeasurementGroup childOrm = baseAppDataCreater.createMeasurementGroup(parentOrm);
                addMeasurementGroupsToMomentNoRecursion(false, moment, parentOrm,uCoreMeasurementGroups.getMeasurementGroups());
               // addMeasurementsAndDeatilsToMeasurementGroup(uCoreMeasurementGroups, childOrm);

          //      DSLog.i(DSLog.LOG, "inside if condition" + parentOrm.toString() + "for uCoreMeasurement = " + uCoreMeasurementGroups.getMeasurementGroups());
            }

          //  addMeasurementGroupsToMomentNoRecursion(moment,parentOrm,uCoreMeasurementGroups.getMeasurementGroups());

            if(isRoot)
            moment.addMeasurementGroup(parentOrm);
            //DSLog.i(DSLog.LOG, "AFETR for loop" + moment.toString());
        }
    }

    private MeasurementGroup addMeasurementGroupsRecursively(MeasurementGroup parentOrmToAttachMoment, MeasurementGroup parentOrm, final UCoreMeasurementGroups parentUCore) {
        List<UCoreMeasurementGroups> childUCoreList = parentUCore.getMeasurementGroups();
        if(childUCoreList == null){
            return parentOrmToAttachMoment;
        }

        for(UCoreMeasurementGroups uCoreMeasurementGroup : childUCoreList){

            if(uCoreMeasurementGroup ==null){
                return parentOrmToAttachMoment;
            }

            MeasurementGroup childOrm = baseAppDataCreater.createMeasurementGroup(parentOrm);
            addMeasurementsAndDeatilsToMeasurementGroup(uCoreMeasurementGroup, childOrm);
            parentOrm.addMeasurementGroup(childOrm);
            parentOrmToAttachMoment = addMeasurementGroupsRecursively(parentOrm,childOrm,uCoreMeasurementGroup);
        }
        return parentOrmToAttachMoment;
    }


    @NonNull
    private void addMeasurementGroupsToMoment(Moment moment, List<UCoreMeasurementGroups> measurementGroups) {
        MeasurementGroup parentOrmToAttachMoment = null;
        for (UCoreMeasurementGroups uCoreMeasurementGroups : measurementGroups) {
            MeasurementGroup parentOrm = baseAppDataCreater.createMeasurementGroup(moment);
            parentOrmToAttachMoment = parentOrm;

            UCoreMeasurementGroups parentUCore = uCoreMeasurementGroups;

            addMeasurementsAndDeatilsToMeasurementGroup(parentUCore, parentOrm);

            addMeasurementGroupsRecursively(parentOrm, parentOrm, parentUCore);
        }

        moment.addMeasurementGroup(parentOrmToAttachMoment);
    }

    private void addMeasurementsAndDeatilsToMeasurementGroup(UCoreMeasurementGroups uCoreMeasurementGroups, MeasurementGroup parent) {
        if (uCoreMeasurementGroups.getMeasurementGroupDetails() != null) {
            addToMeasurementGroupDeatil(uCoreMeasurementGroups, parent);
        }

        if (uCoreMeasurementGroups.getMeasurements() != null) {
            addMeasurements(parent, uCoreMeasurementGroups.getMeasurements());
        }
    }

    private void addToMeasurementGroupDeatil(UCoreMeasurementGroups uCoreMeasurementGroups, MeasurementGroup measurementGroup) {
        for (UCoreMeasurementGroupDetail uCoreDetail : uCoreMeasurementGroups.getMeasurementGroupDetails()) {
            // MeasurementGroupDetailType type = momentTypeMap.getMeasurementGroupDeatilType(uCoreDetail.getType());
//            if (!MomentDetailType.UNKNOWN.equals(uCoreDetail.getType())) {
            MeasurementGroupDetail measurementGroupDetail = baseAppDataCreater.createMeasurementGroupDetail(uCoreDetail.getType(), measurementGroup);
            measurementGroupDetail.setValue(uCoreDetail.getValue());
            measurementGroup.addMeasurementGroupDetail(measurementGroupDetail);
//            }
        }
    }

    private void addSynchronisationData(@NonNull final Moment moment, @NonNull final UCoreMoment uCoreMoment) {
        SynchronisationData synchronisationData = baseAppDataCreater.createSynchronisationData(uCoreMoment.getGuid(), uCoreMoment.getInactive(), new DateTime(uCoreMoment.getLastModified()), uCoreMoment.getVersion());
        moment.setSynchronisationData(synchronisationData);
    }

    private void addToUCoreMomentDetails(@NonNull final Moment moment, @NonNull final List<UCoreDetail> uCoreDetailList) {
        for (UCoreDetail uCoreDetail : uCoreDetailList) {
            //MomentDetailType type = momentTypeMap.getMomentDetailType(uCoreDetail.getType());
            //if (!MomentDetailType.UNKNOWN.equals(type)) {
            MomentDetail momentDetail = baseAppDataCreater.createMomentDetail(uCoreDetail.getType(), moment);
            momentDetail.setValue(uCoreDetail.getValue());
            moment.addMomentDetail(momentDetail);
//            }
        }
    }

    private void addMeasurements(@NonNull final MeasurementGroup measurementGroup, @NonNull final List<UCoreMeasurement> uCoreMeasurementList) {
        for (UCoreMeasurement uCoreMeasurement : uCoreMeasurementList) {
            //MeasurementType type = momentTypeMap.getMeasurementType(uCoreMeasurement.getType());
            //if (!MeasurementType.UNKNOWN.equals(type)) {
            Measurement measurement = baseAppDataCreater.createMeasurement(uCoreMeasurement.getType(), measurementGroup);
            measurement.setDateTime(new DateTime(uCoreMeasurement.getTimestamp()));
            measurement.setValue(uCoreMeasurement.getValue());
            measurement.setUnit(uCoreMeasurement.getUnit());

            List<UCoreDetail> uCoreDetailList = uCoreMeasurement.getDetails();
            if (uCoreDetailList != null) {
                addMeasurementDetails(measurement, uCoreDetailList);
            }

            measurementGroup.addMeasurement(measurement);
            // }
        }
    }

    private void addMeasurementDetails(@NonNull final Measurement measurement, @NonNull final List<UCoreDetail> uCoreDetailList) {
        for (UCoreDetail uCoreDetail : uCoreDetailList) {
//            MeasurementDetailType type = momentTypeMap.getMeasurementDetailType(uCoreDetail.getType());
//            if (!MeasurementDetailType.UNKNOWN.equals(type)) {
            final MeasurementDetail measurementDetail = baseAppDataCreater.createMeasurementDetail(uCoreDetail.getType(), measurement);
            String value = uCoreDetail.getValue();
            // if (!value.equals(MeasurementDetailValueMap.UNKNOWN_NAME)) {
            measurementDetail.setValue(value);
            measurement.addMeasurementDetail(measurementDetail);
            // }
//            }
        }
    }


    public UCoreMoment convertToUCoreMoment(Moment moment) {
        UCoreMoment uCoreMoment = new UCoreMoment();

        try {
            uCoreMoment.setTimestamp(moment.getDateTime().toString());
            String momentTypeString = moment.getType();
            uCoreMoment.setType(momentTypeString);

            List<UCoreMeasurementGroups> uCoreMeasurementGroupList = new ArrayList<>();
            List<UCoreMeasurementGroupDetail> uCoreMeasurementGroupDetails = new ArrayList<>();
            List<UCoreDetail> uCoreMomentList = new ArrayList<>();

            addToUCoreMomentDetails(moment.getMomentDetails(), uCoreMomentList);
            //addToUCoreMeasurements(moment.getMeasurements(), uCoreMeasurementList);
            uCoreMoment.setDetails(uCoreMomentList);

            uCoreMeasurementGroupList = addToUCoreMeasurementGroups(moment.getMeasurementGroups(), uCoreMeasurementGroupList);
            uCoreMoment.setMeasurementGroups(uCoreMeasurementGroupList);

            setVersion(uCoreMoment, moment.getSynchronisationData());
        }catch (Exception e){
            e.printStackTrace();
        }
        return uCoreMoment;
    }

    private List<UCoreMeasurementGroups> addToUCoreMeasurementGroups(Collection<? extends MeasurementGroup> measurementGroups, List<UCoreMeasurementGroups> uCoreMeasurementGroupList) {
        ArrayList<MeasurementGroup> measurementGroupsArray = new ArrayList(measurementGroups);
        int size = measurementGroups.size();
        for (int i = 0, j = 0; i < size; i++) {
            UCoreMeasurementGroups uCoreMeasurementGroup = new UCoreMeasurementGroups();
            uCoreMeasurementGroup = convertMeasurementGroupToUCoreMeasurementGroup(measurementGroupsArray.get(i), uCoreMeasurementGroup);
            uCoreMeasurementGroupList.add(uCoreMeasurementGroup);

            ArrayList<MeasurementGroup> childGroupsArray = new ArrayList(measurementGroupsArray.get(i).getMeasurementGroups());
            int size1 = childGroupsArray.size();
            while (childGroupsArray != null && size1 > 0) {
                for (int i1 = 0, j1 = 0; i1 < size1; i++) {
                    UCoreMeasurementGroups uCoreMeasurementGroup1 = new UCoreMeasurementGroups();
                    uCoreMeasurementGroup1 = convertMeasurementGroupToUCoreMeasurementGroup(childGroupsArray.get(i1), uCoreMeasurementGroup1);
                    List<UCoreMeasurementGroups> uCoreMeasurementGroupses = new ArrayList<>();
                    uCoreMeasurementGroupses.add(uCoreMeasurementGroup1);
                    uCoreMeasurementGroupList.get(i).setMeasurementGroups(uCoreMeasurementGroupses);

                    //TODO: Check the size before doing a get(i)
                    childGroupsArray = new ArrayList(childGroupsArray.get(i).getMeasurementGroups());
                    size1 = childGroupsArray.size();
                }
            }
        }
        return uCoreMeasurementGroupList;
    }

    private UCoreMeasurementGroups convertMeasurementGroupToUCoreMeasurementGroup(MeasurementGroup measurementGroup, UCoreMeasurementGroups uCoreMeasurementGroups) {
        //Added Details
        List<UCoreMeasurementGroupDetail> measurementGroupDetails = new ArrayList<>();
        List<UCoreMeasurementGroupDetail> uCoreMeasurementGroupDetails = addToUCoreMeasurementGroupDetails(measurementGroup.getMeasurementGroupDetails(), measurementGroupDetails);
        uCoreMeasurementGroups.setDetails(uCoreMeasurementGroupDetails);

        //Added Measurements
        List<UCoreMeasurement> measurements = new ArrayList<>();
        List<UCoreMeasurement> uCoreMeasurements = addToUCoreMeasurements(measurementGroup.getMeasurements(), measurements);
        uCoreMeasurementGroups.setMeasurements(uCoreMeasurements);

        return uCoreMeasurementGroups;
    }


    private void setVersion(final UCoreMoment uCoreMoment, final SynchronisationData synchronisationData) {
        if (synchronisationData != null) {
            uCoreMoment.setVersion(synchronisationData.getVersion());
        }
    }

    private void addToUCoreMomentDetails(Collection<? extends MomentDetail> momentDetails, List<UCoreDetail> momentDetailList) {

        for (MomentDetail momentDetail : momentDetails) {
            UCoreDetail detail = new UCoreDetail();
            detail.setType(momentDetail.getType());
            detail.setValue(momentDetail.getValue());

            momentDetailList.add(detail);
        }
    }

    private List<UCoreMeasurementGroupDetail> addToUCoreMeasurementGroupDetails(Collection<? extends MeasurementGroupDetail> measurementGroupDetails,
                                                                                List<UCoreMeasurementGroupDetail> uCoreMeasurementGroupDetails) {

        for (MeasurementGroupDetail measurementGroupDetail : measurementGroupDetails) {
            UCoreMeasurementGroupDetail detail = new UCoreMeasurementGroupDetail();
            detail.setType(measurementGroupDetail.getType());
            detail.setValue(measurementGroupDetail.getValue());

            uCoreMeasurementGroupDetails.add(detail);
        }
        return uCoreMeasurementGroupDetails;
    }

    private List<UCoreMeasurement> addToUCoreMeasurements(Collection<? extends Measurement> measurements, List<UCoreMeasurement> uCoreMeasurementList) {
        for (Measurement measurement : measurements) {
            UCoreMeasurement uCoreMeasurement = new UCoreMeasurement();
            uCoreMeasurement.setTimestamp(measurement.getDateTime().toString());
            uCoreMeasurement.setType(measurement.getType());
            uCoreMeasurement.setValue(measurement.getValue());
            uCoreMeasurement.setUnit(measurement.getUnit());
            addToUCoreMeasurementDetails(measurement.getMeasurementDetails(), uCoreMeasurement);
            uCoreMeasurementList.add(uCoreMeasurement);
        }
        return uCoreMeasurementList;
    }

    private void addToUCoreMeasurementDetails(Collection<? extends MeasurementDetail> measurementDetails, com.philips.platform.datasync.moments.UCoreMeasurement uCoreMeasurement) {
        List<UCoreDetail> uCoreDetailList = new ArrayList<>();

        for (MeasurementDetail measurementDetail : measurementDetails) {
            UCoreDetail uCoreDetail = new UCoreDetail();
            uCoreDetail.setType(measurementDetail.getType());

            uCoreDetail.setValue(measurementDetail.getValue());
            uCoreDetail.setValue(measurementDetail.getValue());

            uCoreDetailList.add(uCoreDetail);
        }
        uCoreMeasurement.setDetails(uCoreDetailList);
    }

}