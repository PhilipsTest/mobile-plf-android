/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.entity.insurance.Subscription;

import java.util.List;

public class THSConsumer implements Parcelable{
    Consumer consumer;

    public THSConsumer(){

    }

    protected THSConsumer(Parcel in) {
        consumer = in.readParcelable(Consumer.class.getClassLoader());
    }

    public static final Creator<THSConsumer> CREATOR = new Creator<THSConsumer>() {
        @Override
        public THSConsumer createFromParcel(Parcel in) {
            return new THSConsumer(in);
        }

        @Override
        public THSConsumer[] newArray(int size) {
            return new THSConsumer[size];
        }
    };

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Gender getGender(){
        return consumer.getGender();
    }

    public String getAge(){
        return consumer.getAge();
    }

    public String getFormularyRestriction(){
        return consumer.getFormularyRestriction();
    }

    public boolean isEligibleForVisit(){
        return consumer.isEligibleForVisit();
    }

    public boolean isEnrolled(){
        return consumer.isEnrolled();
    }

    Subscription getSubscription(){
        return consumer.getSubscription();
    }

    String getPhone(){
        return consumer.getPhone();
    }

    List<Consumer> getDependents(){
        return consumer.getDependents();
    }

    public boolean isDependent(){
        return consumer.isDependent();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(consumer, i);
    }
}
