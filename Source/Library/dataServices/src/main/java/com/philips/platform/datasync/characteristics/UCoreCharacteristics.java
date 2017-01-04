/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.datasync.characteristics;

import java.util.List;

public class UCoreCharacteristics{
    private String type;
    private String value;

    private List<UCoreCharacteristics> characteristics;

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCharacteristics(List<UCoreCharacteristics> characteristics) {
        this.characteristics = characteristics;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public List<UCoreCharacteristics> getCharacteristics() {
        return characteristics;
    }
}