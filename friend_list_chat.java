package com.example.mytest;

public class friend_list_chat {
    String profileurl;
    String date;
    String name;
    String text;
    String read;
    String email;
    friend_list_chat(){

    }
    friend_list_chat( String profileurl, String date, String name, String text, String read, String email){
        this.profileurl = profileurl;
        this.date = date;
        this.name = name;
        this.text = text;
        this.read = read;
        this.email = email;
    }
    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
}
