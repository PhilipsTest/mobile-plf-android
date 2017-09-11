package com.philips.cdp.registration.ui.customviews.countrypicker;

public class Country {
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Country other = (Country) obj;

        return name.equalsIgnoreCase(other.name);

    }

    @Override
    public int hashCode() {
        return code.hashCode()+name.hashCode();
    }
}