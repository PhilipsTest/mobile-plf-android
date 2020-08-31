
package com.philips.cdp.registration.ads.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.philips.cdp.registration.ads.model.App;

@SuppressWarnings("unused")
public class Response {

    @SerializedName("apps")
    private List<App> mApps;

    public List<App> getApps() {
        return mApps;
    }

    public void setApps(List<App> apps) {
        mApps = apps;
    }

}
