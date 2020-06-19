/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;
import java.util.List;

public class ConsignmentEntries implements Serializable {
    private static final long serialVersionUID = -7350936420007269250L;
    private int entryNumber;

    private int quantity;

    private OrderEntry orderEntry;

    private Cost totalPrice;
    private List<String> trackAndTraceIDs;
    private List<String> trackAndTraceUrls;


    public int getEntryNumber() {
        return entryNumber;
    }



    public int getQuantity() {
        return quantity;
    }

    public Cost getTotalPrice() {
        return totalPrice;
    }

    public List<String> getTrackAndTraceIDs() {
        return trackAndTraceIDs;
    }

    public List<String> getTrackAndTraceUrls() {
        return trackAndTraceUrls;
    }

    public OrderEntry getOrderEntry() {
        return orderEntry;
    }

    public void setOrderEntry(OrderEntry orderEntry) {
        this.orderEntry = orderEntry;
    }

    public void setTrackAndTraceIDs(List<String> trackAndTraceIDs) {
        this.trackAndTraceIDs = trackAndTraceIDs;
    }

    public void setTrackAndTraceUrls(List<String> trackAndTraceUrls) {
        this.trackAndTraceUrls = trackAndTraceUrls;
    }
}
