package com.example.workcollab;

import android.net.Uri;

public class UserData {
    private String id;
    private String username;
    private Uri uri;

    public UserData(String id, String username, Uri uri) {
        this.id = id;
        this.username = username;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
