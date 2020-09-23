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

    void trackPageWithInfo(String pageName, String key, String value);
    void trackActionWithInfo(String pageName, Map<String, String> paramDict);

}


