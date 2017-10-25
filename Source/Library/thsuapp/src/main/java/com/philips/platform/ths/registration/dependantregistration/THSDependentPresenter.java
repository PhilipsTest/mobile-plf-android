/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.registration.dependantregistration;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.practice.THSPractice;
import com.philips.platform.ths.practice.THSPracticeFragment;
import com.philips.platform.ths.registration.THSCheckConsumerExistsCallback;
import com.philips.platform.ths.registration.THSRegistrationFragment;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.settings.THSScheduledVisitsFragment;
import com.philips.platform.ths.settings.THSVisitHistoryFragment;
import com.philips.platform.ths.utility.AmwellLog;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class THSDependentPresenter implements THSBasePresenter {
    THSDependantHistoryFragment mThsDependantHistoryFragment;

    public THSDependentPresenter(THSDependantHistoryFragment thsDependantHistoryFragment) {
        mThsDependantHistoryFragment = thsDependantHistoryFragment;
    }

    @Override
    public void onEvent(int componentID) {

    }

    protected void checkIfUserExists() {
        Bundle bundle = new Bundle();
        bundle.putInt(THSConstants.THS_LAUNCH_INPUT, mThsDependantHistoryFragment.mLaunchInput);

        if (isConsumerEnrolled()) {

            switch (mThsDependantHistoryFragment.mLaunchInput) {
                case THSConstants.THS_SCHEDULED_VISITS:
                    mThsDependantHistoryFragment.addFragment(new THSScheduledVisitsFragment(), THSScheduledVisitsFragment.TAG, null, false);
                    break;
                case THSConstants.THS_VISITS_HISTORY:
                    mThsDependantHistoryFragment.addFragment(new THSVisitHistoryFragment(), THSScheduledVisitsFragment.TAG, null, false);
                    break;
                case THSConstants.THS_PRACTICES:
                    mThsDependantHistoryFragment.addFragment(new THSPracticeFragment(), THSPracticeFragment.TAG, null, false);
                    break;
            }
        } else {
            mThsDependantHistoryFragment.addFragment(new THSRegistrationFragment(), THSRegistrationFragment.TAG, bundle, false);
        }
    }

    private boolean isConsumerEnrolled() {
        final THSConsumer thsConsumer = THSManager.getInstance().getThsConsumer();
        Consumer consumer = thsConsumer.getConsumer();

        if(thsConsumer == null || consumer ==null){
            return false;
        }

        if(thsConsumer.isDependent() && consumer.isDependent()){
            return true;
        }else {
            return consumer.isEnrolled();
        }
    }

    protected void updateDependents() {

        final List<Consumer> server = THSManager.getInstance().getThsParentConsumer().getConsumer().getDependents();
         List<THSConsumer> local = THSManager.getInstance().getThsParentConsumer().getDependents();

        List<THSConsumer> newObject = new ArrayList<>();

        for(Consumer consumer: server){
            for(THSConsumer thsConsumer:local){
                if(thsConsumer.getFirstName()!=null && thsConsumer.getFirstName().equalsIgnoreCase(consumer.getFirstName())){
                    thsConsumer.setConsumer(consumer);
                    newObject.add(thsConsumer);
                }else {
                    addDependentToConsumer(consumer,newObject);
                }
            }
        }

        THSManager.getInstance().getThsParentConsumer().setDependents(local);

    }

    private List addDependentToConsumer(Consumer consumer, List<THSConsumer> localDependents) {

        for (THSConsumer thsConsumer : localDependents){
            if(thsConsumer.getFirstName().equalsIgnoreCase(consumer.getFirstName())){
                return localDependents;
            }
        }

        THSConsumer baby = new THSConsumer();
        baby.setFirstName("Vardhan");
        baby.setLastName("Hosur");
        final com.americanwell.sdk.entity.consumer.Gender gender = consumer.getGender();
        if(gender == com.americanwell.sdk.entity.consumer.Gender.MALE){
            baby.setGender(Gender.MALE);
        }else {
            baby.setGender(Gender.FEMALE);
        }

        final SDKLocalDate dob = consumer.getDob();
        if(dob!=null) {
            baby.setDob(dob.toDate());
        }

        baby.setDependent(true);
        baby.setBloodPressureSystolic("80");
        baby.setBloodPressureDiastolic("120");
        baby.setTemperature(90.0);
        baby.setWeight(56);


        localDependents.add(baby);
        return localDependents;
    }
}
