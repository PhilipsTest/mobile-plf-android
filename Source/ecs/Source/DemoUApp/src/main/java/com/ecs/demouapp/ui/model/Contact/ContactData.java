package com.ecs.demouapp.ui.model.Contact;

import java.util.List;

public class ContactData {
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
