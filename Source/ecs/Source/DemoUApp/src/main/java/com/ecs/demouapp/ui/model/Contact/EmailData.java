package com.ecs.demouapp.ui.model.Contact;

import java.io.Serializable;

public class EmailData implements Serializable {
    private String label;
    private String contentPath;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getLabel() {
        return label;
    }

    public String getContentPath() {
        return contentPath;
    }
}
