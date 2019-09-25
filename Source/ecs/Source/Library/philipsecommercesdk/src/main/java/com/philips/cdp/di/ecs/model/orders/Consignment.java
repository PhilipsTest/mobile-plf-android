/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.model.orders;


import com.philips.cdp.di.ecs.model.address.ECSAddress;

import java.io.Serializable;
import java.util.List;

public class Consignment  implements Serializable {
    private String code;

    private ECSAddress shippingAddress;
    private String status;
    private String statusDate;
    private List<ConsignmentEntries> entries;

    public String getCode() {
        return code;
    }


    public ECSAddress getShippingAddress() {
        return shippingAddress;
    }


    public String getStatus() {
        return status;
    }


    public String getStatusDate() {
        return statusDate;
    }

    public List<ConsignmentEntries> getEntries() {
        return entries;
    }


}
