package com.example.layout;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//class to download the file.
public class Downloader {
    private static String myTag ="Download XML";
    //download a file from the internet and store it locally
    //@param URL - the url of the file to download
    //@param fos - a FileOutputStream to save the download file to
    public static void downloadFromUrl(String URL, FileOutputStream fos){
        try{
            URL url = new URL(URL);

            long startTime = System.currentTimeMillis();
            Log.d(myTag,"download begining");

            //open connection to the url
            URLConnection ucon = url.openConnection();

            Log.i(myTag,"Opened Connection");

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            Log.i(myTag,"Got inputstream and BufferedInputStream");

            BufferedOutputStream bos = new BufferedOutputStream(fos);
            Log.i(myTag,"Got FileOutputStream and BufferedOutputStream");

            //start reading the file
            byte data[] = new byte[1024];
            int count;
            while ((count = bis.read(data)) != -1){
                bos.write(data, 0, count);
            }
            //have to call flush or chance of file getting corrupted
            bos.flush();
            bos.close();

            Log.d(myTag, "download ready in "
                    + ((System.currentTimeMillis() - startTime))
                    + " milisec");
        }catch (IOException e){
            Log.d(myTag,"Error: "+e);
        }
    }
}
