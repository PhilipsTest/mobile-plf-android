package com.philips.cdp.di.ecs.model.summary;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Brand  implements Serializable {

    @SerializedName("brandLogo")
    @Expose
    private String brandLogo;

    /**
     * No args constructor for use in serialization
     *
     */
    public Brand() {
    }

    /**
     *
     * @param brandLogo
     */
    public Brand(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    /**
     *
     * @return
     * The brandLogo
     */
    public String getBrandLogo() {
        return brandLogo;
    }

    /**
     *
     * @param brandLogo
     * The brandLogo
     */
    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

}