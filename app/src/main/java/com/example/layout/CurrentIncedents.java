package com.example.layout;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentIncedents {
    private String title;
    private String description;
    private String Georss;
    private String pubDate;

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){ this.title = title ; }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeorss() {
        return Georss;
    }
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }
    public void setGeorss(String georss) {
        Georss = georss;
    }

}
