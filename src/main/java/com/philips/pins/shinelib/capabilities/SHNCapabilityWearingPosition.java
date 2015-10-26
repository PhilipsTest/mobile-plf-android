package com.philips.pins.shinelib.capabilities;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNResultListener;
import com.philips.pins.shinelib.SHNSetResultListener;
import com.philips.pins.shinelib.SHNWearingPositionResultListener;

/**
 * Created by 310188215 on 10/06/15.
 */
public interface SHNCapabilityWearingPosition extends SHNCapability {
    enum  SHNWearingPosition {
        Unknown,
        Wrist,
        LeftWrist,
        RightWrist,
        Pocket,
        Keycord,
        Chest,
        Waist        
    }

    void getSupportedWearingPositions(SHNSetResultListener<SHNWearingPosition> shnSetResultListener);
    void setWearingPosition(SHNWearingPosition shnWearingPosition, SHNResultListener shnResultListener);
    void getWearingPosition(SHNWearingPositionResultListener shnWearingPositionResultListener);
}
