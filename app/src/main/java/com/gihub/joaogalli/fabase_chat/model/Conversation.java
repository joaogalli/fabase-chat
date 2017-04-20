package com.gihub.joaogalli.fabase_chat.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joao.galli on 19/04/2017.
 */
@IgnoreExtraProperties
public class Conversation {

    private String id;

    private List<String> contacts = new ArrayList<>();

    public Conversation() {
    }

    public Conversation(String... cs) {
        contacts.addAll(Arrays.asList(cs));
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
