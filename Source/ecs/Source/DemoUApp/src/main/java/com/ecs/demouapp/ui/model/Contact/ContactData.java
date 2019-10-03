package com.ecs.demouapp.ui.model.Contact;

import java.io.Serializable;
import java.util.List;

public class ContactData implements Serializable {


    private List<ContactPhone> phone;
    private List<ChatData> chat;
    private List<EmailData> email;

    public List<ContactPhone> getPhone() {
        return phone;
    }

    public List<ChatData> getChat() {
        return chat;
    }

    public List<EmailData> getEmail() {
        return email;
    }
}
