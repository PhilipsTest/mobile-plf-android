package com.philips.platform.ths.providerdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class THSProviderEntity implements Parcelable{

    public THSProviderEntity(){

    }

    protected THSProviderEntity(Parcel in) {
    }

    public static final Creator<THSProviderEntity> CREATOR = new Creator<THSProviderEntity>() {
        @Override
        public THSProviderEntity createFromParcel(Parcel in) {
            return new THSProviderEntity(in);
        }

        @Override
        public THSProviderEntity[] newArray(int size) {
            return new THSProviderEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
