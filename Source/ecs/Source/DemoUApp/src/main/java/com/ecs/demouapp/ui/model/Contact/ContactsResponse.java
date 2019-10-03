package com.ecs.demouapp.ui.model.Contact;

import java.io.Serializable;

public class ContactsResponse implements Serializable {

    private ContactData data;
    private boolean success;

    public ContactData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
