/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;



import java.io.Serializable;

/**
 * The type Ecs orders contains orders which is passed as input to fetch order details.
 */
public class ECSOrders implements Serializable {
    private String code;
    private String guid;
    private String placed;
    private String status;
    private String statusDisplay;
    private ECSOrderDetail orderDetail;


    public ECSOrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(ECSOrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    private Total total;

    /**
     * Gets code.
     *
     * @return the code is the unique order id
     */
    public String getCode() {
        return code;
    }

    public String getGuid() {
        return guid;
    }

    public String getPlaced() {
        return placed;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public Total getTotal() {
        return total;
    }


}

