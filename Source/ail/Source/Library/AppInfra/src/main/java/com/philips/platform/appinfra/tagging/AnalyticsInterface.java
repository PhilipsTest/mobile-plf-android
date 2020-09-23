/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appinfra.tagging;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface Ai app tagging interface.
 */
public interface AnalyticsInterface extends Serializable {

    void trackPage(String pageName);
    void trackEventWithInfo(String eventName, Map<String, String> paramMap);

}


