/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.products;

import java.io.Serializable;

public class CurrentQueryEntity implements Serializable {

    private static final long serialVersionUID = 1778570617984996812L;
    QueryEntity query;
    private String url;

    public QueryEntity getQuery() {
        return query;
    }

    public String getUrl() {
        return url;
    }
}
