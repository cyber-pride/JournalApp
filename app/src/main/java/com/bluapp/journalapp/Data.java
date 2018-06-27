package com.bluapp.journalapp;

/**
 * Created by Emmanuel on 25/06/18.
 */

public class Data {

    private String title, content, date;



    public Data(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Data(){

    }
    public void setDate(String date) {

        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
