package com.gihub.joaogalli.fabase_chat.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao.galli on 20/04/2017.
 */
@IgnoreExtraProperties
public class UserConversations {

    private List<String> conversations = new ArrayList<>();

    public UserConversations() {
    }

    public void addConversation(String conversation) {
        conversations.add(conversation);
    }

    public List<String> getConversations() {
        return conversations;
    }

    public void setConversations(List<String> conversations) {
        this.conversations = conversations;
    }
}
