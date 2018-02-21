/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.core.events;

import com.philips.platform.core.datatypes.Insight;

import java.util.List;

public class DeleteSyncedInsightsRequest extends Event {
    private final List<? extends Insight> insights;

    public DeleteSyncedInsightsRequest(List<? extends Insight> insights) {
        this.insights = insights;
    }

    public List<? extends Insight> getInsights() {
        return insights;
    }
}
