package com.philips.pins.shinelib.datatypes;

import android.util.Log;

import com.philips.pins.shinelib.utility.SHNBluetoothDataConverter;
import com.philips.pins.shinelib.utility.ScalarConverters;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SHNDataWeightMeasurement extends SHNData {

    private static final String TAG = SHNDataWeightMeasurement.class.getSimpleName();

    private final Flags flags;
    private final Date timestamp;
    private final float weight;
    private final int userId;
    private float bmi;
    private float height;

    private float weightKGResolution = 0.005f;
    private float weightLBResolution = 0.01f;

    private float BMIResolution = 0.1f;
    private float heightMeterResolution = 0.001f;
    private float heightInchResolution = 0.1f;

    private int measurementUnsuccessful = 0xFFFF;
    private int unKnownUserId = 255;

    @Override
    public SHNDataType getSHNDataType() {
        return SHNDataType.WeightMeasurement;
    }

    public SHNDataWeightMeasurement(ByteBuffer byteBuffer) {
        try {
            flags = new Flags(byteBuffer.get());

            short weightRaw = byteBuffer.getShort();
            weight = extractWeight(ScalarConverters.ushortToInt(weightRaw));

            timestamp = flags.hasTimestamp() ? SHNBluetoothDataConverter.getDateTime(byteBuffer) : null;
            if (flags.hasTimestamp() && timestamp == null) {
                throw new IllegalArgumentException();
            }

            if (flags.hasUserId()) {
                userId = ScalarConverters.ubyteToInt(byteBuffer.get());
            } else {
                userId = -1;
            }

            if (flags.hasBmiAndHeight) {
                short bmiRaw = byteBuffer.getShort();
                bmi = extractBMI(ScalarConverters.ushortToInt(bmiRaw));
                short heightRaw = byteBuffer.getShort();
                height = extractHeight(ScalarConverters.ushortToInt(heightRaw));
            } else {
                bmi = -1;
                height = -1;
            }
        } catch (BufferUnderflowException e) {
            throw new IllegalArgumentException();
        }
    }

    public Flags getFlags() {
        return flags;
    }

    private float extractWeight(int rawData) {
        if (rawData == measurementUnsuccessful) {
            Log.w(TAG, "Received a measurement with the special weight-value 0xFFFF that represents \"Measurement Unsuccessful\"");
        } else {
            SHNWeightUnit unit = getFlags().getShnWeightUnit();
            float resolution;
            if(unit == SHNWeightUnit.KG ){
                resolution = weightKGResolution;
            }
            else{
                resolution = weightLBResolution;
            }
            return rawData * resolution;
        }
        return 0;
    }

    private float extractBMI(int rawData) {
        return rawData * BMIResolution;
    }

    private float extractHeight(int rawData) {
        SHNHeightUnit unit = getFlags().getShnHeightUnit();
        float resolution;
        if(unit == SHNHeightUnit.Meter){
            resolution = heightMeterResolution;
        }else{
            resolution = heightInchResolution;
        }
        return rawData * resolution;
    }

    public float getWeight() {
        return weight;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isUserIdUnknown() {
        return userId == unKnownUserId;
    }

    public float getBMI() {
        return bmi;
    }

    public float getHeight() {
        return height;
    }

    public static class Flags {
        private static final byte FLAGS_SI = 0x01;
        private static final byte FLAGS_HAS_TIMESTAMP = 0x02;
        private static final byte FLAGS_HAS_USER_ID = 0x04;
        private static final byte FLAGS_HAS_BMI_AND_HEIGHT = 0x08;

        private final SHNWeightUnit shnWeightUnit;
        private final SHNHeightUnit shnHeightUnit;
        private final boolean hasTimestamp;
        private final boolean hasUserId;
        private final boolean hasBmiAndHeight;

        private Flags(byte bitField) {
            boolean isSI = (bitField & FLAGS_SI) == 0;
            shnWeightUnit = isSI ? SHNWeightUnit.KG : SHNWeightUnit.LB;
            shnHeightUnit = isSI ? SHNHeightUnit.Meter : SHNHeightUnit.Inch;

            hasTimestamp = (bitField & FLAGS_HAS_TIMESTAMP) != 0;
            hasUserId = (bitField & FLAGS_HAS_USER_ID) != 0;
            hasBmiAndHeight = (bitField & FLAGS_HAS_BMI_AND_HEIGHT) != 0;
        }

        public SHNWeightUnit getShnWeightUnit() {
            return shnWeightUnit;
        }

        public SHNHeightUnit getShnHeightUnit() {
            return shnHeightUnit;
        }

        public boolean hasTimestamp() {
            return hasTimestamp;
        }

        public boolean hasUserId() {
            return hasUserId;
        }

        public boolean hasBmiAndHeight() {
            return hasBmiAndHeight;
        }
    }
}
