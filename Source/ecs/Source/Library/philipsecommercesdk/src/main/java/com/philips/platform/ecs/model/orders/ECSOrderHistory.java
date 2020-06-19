/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;
import java.util.List;

/**
 * The type Ecs order history contains details of order placed.
 * This object is returned when fetchOrderHistory
 */
public class ECSOrderHistory implements Serializable {


    private static final long serialVersionUID = 8680915286804314559L;
    private Pagination pagination;
    private List<ECSOrders> orders;

    private List<Sorts> sorts;

    public Pagination getPagination() {
        return pagination;
    }

    public List<ECSOrders> getOrders() {
        return orders;
    }

    public void setOrders(List<ECSOrders> orders) {
        this.orders = orders;
    }

    public List<Sorts> getSorts() {
        return sorts;
    }


    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
