package com.philips.platform.appinfra.contentloader.model;

/**
 * Created by 310238114 on 11/7/2016.
 */
public class Tag {

    public Boolean isVisibleOnWeb;
    public String name;
    public String key;
    public String id;

    public Boolean getVisibleOnWeb() {
        return isVisibleOnWeb;
    }

    public void setVisibleOnWeb(Boolean visibleOnWeb) {
        isVisibleOnWeb = visibleOnWeb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

}
