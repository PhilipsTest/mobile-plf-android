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

    /**
     * Create instance for component ai app tagging interface.
     * This method to be used by all component to get their respective tagging
     *
     * @param componentId      the component id
     * @param componentVersion the component version
     * @return the appinfra app tagging interface
     * @since 1.0.0
     */
    AnalyticsInterface createInstanceForComponent(String componentId,
                                                  String componentVersion);

    void trackPageWithInfo(String pageName, String key, String value);

    void trackPageWithInfo(String pageName, Map<String, String> paramDict);

    void trackActionWithInfo(String pageName, String key, String value);

    void trackActionWithInfo(String pageName, Map<String, String> paramDict);

}


