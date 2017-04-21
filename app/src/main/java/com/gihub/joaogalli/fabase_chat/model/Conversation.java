package com.gihub.joaogalli.fabase_chat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joao.galli on 19/04/2017.
 */
@IgnoreExtraProperties
public class Conversation implements Parcelable {

    private String id;

    private String name;

    private List<String> contacts = new ArrayList<>();

    public Conversation() {
    }

    public Conversation(String name, String... cs) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeStringList(this.contacts);
    }

    protected Conversation(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.contacts = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel source) {
            return new Conversation(source);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
}
