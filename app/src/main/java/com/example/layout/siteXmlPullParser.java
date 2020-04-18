package com.example.layout;

import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class siteXmlPullParser {
    static final String KEY_SITE = "item";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_GEORSS = "georss:point";
    static final String KEY_PUBDATE = "pubDate";

    //get current road works from file
    public static ArrayList<RoadWorks> getCurrentRoadWokdsFromFile(Context ctx){
        ArrayList<RoadWorks> cI = new ArrayList<>();
        RoadWorks curCi = null;

        String curText = "";
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            FileInputStream fis = ctx.openFileInput("CurrentRoadworks.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            //point the parser to the file
            xpp.setInput(reader);

            //get intital eventType
            int eventType = xpp.getEventType();
            //loop through the pull event until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT){
                //get current tag
                String tagname = xpp.getName();

                //react to different event types appropriately
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagname.equalsIgnoreCase(KEY_SITE)){
                            //if we are starting a new <item> block we need a new CurrentIncedent to represent it
                            curCi = new RoadWorks();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //get current text so we can us it in the END_TAG event
                        curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (curCi != null) {
                            if (tagname.equalsIgnoreCase(KEY_SITE)){
                                //if </item> then we are done with current item add it to the list
                                cI.add(curCi);
                            } else if (tagname.equalsIgnoreCase(KEY_TITLE)) {
                                curCi.setTitle(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION)){
                                String description = curText;
                                //separate the description into appropriate areas
                                String [] separated = description.split("<br />");
                                //convert bot starting date and ending date into Date
                                Date startingDate = convertDate(separated[0]);
                                Date endingDate = convertDate(separated[1]);

                                curCi.setStartDate(startingDate);
                                curCi.setEndDate(endingDate);
                                curCi.setDelayInfo(separated[2]);
                            }else if (tagname.equalsIgnoreCase(KEY_GEORSS)){
                                curCi.setGeorss(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_PUBDATE)){
                                curCi.setPubDate(curText);
                            }
                        }
                        break;

                    default:
                        break;
                }
                //move to next iteration
                eventType = xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cI;
    }

    //get all planed roadworks from file
    public static ArrayList<PlannedRoadWorks> getPlannedRoadWorksFromFile(Context ctx){
        ArrayList<PlannedRoadWorks> cI = new ArrayList<>();
        PlannedRoadWorks curCi = null;

        String curText="";
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            FileInputStream fis = ctx.openFileInput("PlannedRoadWorks.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            //point the parser to the file
            xpp.setInput(reader);

            //get intital eventType
            int eventType = xpp.getEventType();
            //loop through the pull event until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT){
                //get current tag
                String tagname = xpp.getName();

                //react to different event types appropriately
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagname.equalsIgnoreCase(KEY_SITE)){
                            //if we are starting a new <item> block we need a new CurrentIncedent to represent it
                            curCi = new PlannedRoadWorks();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //get current text so we can us it in the END_TAG event
                        curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (curCi != null) {
                            if (tagname.equalsIgnoreCase(KEY_SITE)){
                                //if </item> then we are done with current item add it to the list
                                cI.add(curCi);
                            } else if (tagname.equalsIgnoreCase(KEY_TITLE)) {
                                curCi.setTitle(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION)){
                                String description = curText;
                                //separate the description into appropriate areas
                                String [] separated = description.split("<br />");
                                //convert bot starting date and ending date into Date
                                Date startingDate = convertDate(separated[0]);
                                Date endingDate = convertDate(separated[1]);

                                String d = separated[2].trim();
                                curCi.setStartDate(startingDate);
                                curCi.setEndDate(endingDate);
                                curCi.setDescription(d);
                            }else if (tagname.equalsIgnoreCase(KEY_GEORSS)){
                                curCi.setGeorss(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_PUBDATE)){
                                curCi.setPubDate(curText);
                            }
                        }
                        break;

                    default:
                        break;
                }
                //move to next iteration
                eventType = xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cI;
    }

    //get current incidents from file
    public static ArrayList<CurrentIncedents> getCurrentIncedentsFromFile(Context ctx){

        ArrayList<CurrentIncedents> cI= new ArrayList<>();
        CurrentIncedents curCi = null;

        String curText = "";
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            FileInputStream fis = ctx.openFileInput("CurrentIncidents.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            //point the parser to the file
            xpp.setInput(reader);

            //get intital eventType
            int eventType = xpp.getEventType();

            //loop through the pull event until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT){
                //get current tag
                String tagname = xpp.getName();

                //react to different event types appropriately
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagname.equalsIgnoreCase(KEY_SITE)){
                            //if we are starting a new <item> block we need a new CurrentIncedent to represent it
                            curCi = new CurrentIncedents();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //get current text so we can us it in the END_TAG event
                        curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (curCi != null) {
                            if (tagname.equalsIgnoreCase(KEY_SITE)){
                                //if </item> then we are done with current item add it to the list
                                cI.add(curCi);
                            } else if (tagname.equalsIgnoreCase(KEY_TITLE)) {
                                curCi.setTitle(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION)){
                                curCi.setDescription(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_GEORSS)){
                                curCi.setGeorss(curText);
                            }else if (tagname.equalsIgnoreCase(KEY_PUBDATE)){
                                curCi.setPubDate(curText);
                            }
                        }
                        break;

                    default:
                        break;
                }
                //move to next iteration
                eventType = xpp.next();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        //return the populated list
        return cI;
    }

    //method to convert String taken from file to Date
    private static Date convertDate(String s) {
        Date rDate = null;
        //keep splitting until we get "1 January 2020"
        String [] date = s.split(": ");
        String [] actualDate = date[1].split("-");
        String [] dDate = actualDate[0].split(",");
        //split "1 January 2020"
        String[] split = dDate[1].split(" ");

        //combine the splits back together so that it can be converted into a Date
        String d = split[1] +"/"+split[2]+"/"+split[3];

        SimpleDateFormat format = new SimpleDateFormat("dd/MMMM/yyyy");
        try{
            rDate = format.parse(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return rDate;
    }
}
