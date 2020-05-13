/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;

public class Sorts implements Serializable {
    private static final long serialVersionUID = -8340458217380642204L;
    private String code;
    private boolean selected;

    public String getCode() {
        return code;
    }

    public boolean isSelected() {
        return selected;
    }

}
