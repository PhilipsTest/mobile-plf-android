
package com.philips.platform.flowmanager.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AppFlowState {

    private String state;
    private List<AppFlowEvent> events = new ArrayList<AppFlowEvent>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @return The events
     */
    public List<AppFlowEvent> getEvents() {
        return events;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

}
