/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package com.philips.platform.core.events;


import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.listeners.DBRequestListener;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class MomentsSaveRequest extends MomentsEvent {
    public MomentsSaveRequest(final List<Moment> moments, DBRequestListener<Moment> dbRequestListener) {
        super(moments,dbRequestListener);
    }
}
