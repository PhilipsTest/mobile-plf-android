/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.products;

import java.io.Serializable;

public class QueryEntity implements Serializable {
    private static final long serialVersionUID = -1837839317046618189L;
    private String value;

    public String getValue() {
        return value;
    }
}
