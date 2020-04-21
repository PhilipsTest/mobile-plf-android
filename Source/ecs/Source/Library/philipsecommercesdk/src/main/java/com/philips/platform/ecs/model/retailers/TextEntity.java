/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import java.io.Serializable;

public class TextEntity implements Serializable {
    private String Key;
    private String Value;

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }
}
