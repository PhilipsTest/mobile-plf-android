/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1127731998782174251L;
    private String name;
    private String uid;

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }
}
