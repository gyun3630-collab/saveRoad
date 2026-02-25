package com.example.mytest;

public class update_list {
    String vs;
    String text;

    update_list(String vs, String text){
        this.vs = vs;
        this.text  = text;
    }

    public update_list() {

    }


    public String getVs() {
        return vs;
    }

    public void setVs(String vs) {
        this.vs = vs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
