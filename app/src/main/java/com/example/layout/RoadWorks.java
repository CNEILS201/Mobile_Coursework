package com.example.layout;

import java.util.Date;

public class RoadWorks {
    private String title;
    private Date startDate;
    private Date endDate;
    private String delayInfo;
    private String Georss;
    private String pubDate;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {this.title = title;}

    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {this.endDate = endDate;}

    public String getDelayInfo() {
        return delayInfo;
    }
    public void setDelayInfo(String delayInfo) {this.delayInfo = delayInfo;}

    public String getGeorss() {
        return Georss;
    }
    public void setGeorss(String georss) {Georss = georss;}

    public String getPubDate() {
        return pubDate;
    }
    public void setPubDate(String pubDate) {this.pubDate = pubDate;}
}
