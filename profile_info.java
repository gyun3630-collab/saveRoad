package com.example.mytest;

import android.widget.ImageView;
import android.widget.TextView;

public class profile_info {
    String profile;
    String email;
    String name;
    public profile_info(String name, String email, String url){
        this.email = email;
        this.name = name;
        this.profile = url;
    }

    public profile_info() {
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
