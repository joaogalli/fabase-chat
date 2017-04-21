package com.gihub.joaogalli.fabase_chat.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by joaog on 21/04/2017.
 */
@IgnoreExtraProperties
public class Message {

    private String id;

    private String content;

    private String authorId;

    private long dateCreated;

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
