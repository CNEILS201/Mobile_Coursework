package com.example.layout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, Serializable, GoogleMap.OnInfoWindowClickListener {

    private String urlSourceRW = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSourcePRW = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlSourceCI = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String xmlSourceRW = "CurrentRoadworks.xml";
    private String xmlSourcePRW="PlannedRoadWorks.xml";
    private String xmlSourceCI="CurrentIncidents.xml";
    RelativeLayout show;
    EditText txtEF,txtET,txtESN;
    TextView txt1,txt2, cRText;
    Button b1,b2,bD,bplan,view,search,plan,btnsearch;
    Date currentTime, date;
    String currentDateString;
    int closeCount;
    List<CurrentIncedents> ci;
    List<RoadWorks> rw,uRw;
    List<PlannedRoadWorks> pRw,uPRw;
    boolean planned = false;
    boolean viewOn = true;
    GoogleMap map;
    Calendar c;

    //get list of PlannedRoadWorks that is happening during user enters date
    public List<PlannedRoadWorks> findDate(Date d){
        Date userDate = d;
        List<PlannedRoadWorks> rPRw = new ArrayList<>();
        for (int i=0; i<pRw.size();i++){
            Date startDate = pRw.get(i).getStartDate();
            Date endDate = pRw.get(i).getEndDate();
            if(userDate.after(startDate) && userDate.before(endDate)){
                rPRw.add(pRw.get(i));
            }
        }
        return rPRw;
    }
    public List<RoadWorks> findRwDate(Date d){
        Date userDate = d;
        List<RoadWorks> rRw = new ArrayList<>();
        for(int i=0; i<rw.size();i++){
            Date startDate = rw.get(i).getStartDate();
            Date endDate = rw.get(i).getEndDate();
            if(userDate.after(startDate) && userDate.before(endDate)){
                rRw.add(rw.get(i));
            }
        }
        return rRw;
    }

    //put markers on map
    public void drawIncidentMap(){
        for (int i=0; i<ci.size();i++){
            BitmapDescriptor icon;
            String title = "Incidents: ";
            title = title + ci.get(i).getTitle();
            String latlng = ci.get(i).getGeorss();
            String [] split = latlng.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(ci.get(i).getTitle().contains("closure")||ci.get(i).getTitle().contains("Closure")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
                closeCount++;
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.incidents);
            }

            LatLng newMarker = new LatLng(lat,lng);
            MarkerOptions option = new MarkerOptions()
                    .zIndex(i)
                    .alpha(0)
                    .position(newMarker)
                    .title(title)
                    .snippet("Click for more Info!!")
                    .icon(icon);

            map.addMarker(option);
        }
    }
    //put markers on map
    public void drawRWMap(List<RoadWorks> crw){
        for (int i=0; i<crw.size();i++){
            BitmapDescriptor icon;
            String title = "RoadWorks: ";
            title = title + crw.get(i).getTitle();
            String latlng = crw.get(i).getGeorss();
            String [] split = latlng.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(crw.get(i).getTitle().contains("closure")||crw.get(i).getTitle().contains("Closure")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
                closeCount++;
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadworks);
            }

            LatLng newMarker = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions()
                    .zIndex(i)
                    .alpha(1)
                    .position(newMarker)
                    .title(title)
                    .snippet("Click for more Info!!")
                    .icon(icon);

                map.addMarker(options);
        }
    }
    //put markers on map
    public void drawPRWMap(){
        for (int i=0; i<uPRw.size();i++){
            BitmapDescriptor icon;
            String title = "Planned RoadWorks: ";
            title = title + uPRw.get(i).getTitle();
            String latlng = uPRw.get(i).getGeorss();
            String [] split = latlng.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(uPRw.get(i).getDescription().contains("Closure")||uPRw.get(i).getDescription().contains("Closures")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
                closeCount++;
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadworks);
            }
            LatLng newMarker = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions()
                    .zIndex(i)
                    .alpha(2)
                    .position(newMarker)
                    .title(title)
                    .snippet("Click for more Info!!")
                    .icon(icon);
            map.addMarker(options);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //if network is available download the Traffic Scotland feed
        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        }
        txtEF = (EditText) findViewById(R.id.txtEFrom);
        txtET = (EditText) findViewById(R.id.txtETo);
        txtESN = (EditText) findViewById(R.id.streetName);
        show = (RelativeLayout) findViewById(R.id.bottom_layout);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        cRText = (TextView) findViewById(R.id.RoadWorks);
        b1 = (Button) findViewById(R.id.topBtn);
        b2 = (Button) findViewById(R.id.bottomBtn);
        btnsearch = (Button) findViewById(R.id.btnSearchR);
        bplan = (Button) findViewById(R.id.btnPlan);
        bD = (Button)findViewById(R.id.btnCalender);
        view = (Button)findViewById(R.id.view);
        search = (Button) findViewById(R.id.search);
        plan = (Button)findViewById(R.id.plan);
        bD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment DatePicker = new DatePickerFragment();
                DatePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        txt1.setVisibility(View.GONE);
        txt2.setVisibility(View.GONE);
        txtEF.setVisibility(View.GONE);
        txtET.setVisibility(View.GONE);
        bplan.setVisibility(View.GONE);
        btnsearch.setVisibility(View.GONE);
        txtESN.setVisibility(View.GONE);

        view.setEnabled(false);
        if (ci == null && rw == null && pRw == null) {
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng Scotland = new LatLng(56.68894,-4.0425997);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Scotland,5f));
        map.setOnInfoWindowClickListener(this);
    }

    //check if network is available
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwrokInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetwrokInfo != null && activeNetwrokInfo.isConnected();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        float i =marker.getZIndex();
        float what = marker.getAlpha();
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("isItPlanned",planned);
        intent.putExtra("ArrayLocation",i);
        intent.putExtra("what",what);
        intent.putExtra("date",DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));
        //
        startActivity(intent);
    }

    //Download the feed and name them appropriately
    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0){
            //download files
            try{
                //current incidents
                Downloader.downloadFromUrl(urlSourceCI, openFileOutput(xmlSourceCI,Context.MODE_PRIVATE));
                //current roadworks
                Downloader.downloadFromUrl(urlSourceRW,openFileOutput(xmlSourceRW,Context.MODE_PRIVATE));
                //planned roadworks
                Downloader.downloadFromUrl(urlSourcePRW,openFileOutput(xmlSourcePRW,Context.MODE_PRIVATE));
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        closeCount = 0;
        c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMMM dd,yyyy");
        try {
            date = df.parse(currentDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentTime = Calendar.getInstance().getTime();
        bD.setText(currentDateString);
        if (viewOn == true){
            map.clear();
            showRoadMap.run();
        }
    }
    //change layout of screen lower more info screen to show only map
    public void b1Clicked(View v)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
        show.getLayoutParams();
        params.weight = 0f;
        show.setLayoutParams(params);
        b1.setVisibility(View.VISIBLE);
    }
    //bring back bottom screen that is currently not on screen
    public void topBtnClicked(View v)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
        show.getLayoutParams();
        params.weight = 3f;
        show.setLayoutParams(params);
        b1.setVisibility(View.GONE);
    }
    //view button will hide everything not involved with the view functionality and show everything that is involved
    public void viewBtnClicked (View v){
        //show inputs that need to be showed for this button
        map.clear();
        view.setEnabled(false);
        search.setEnabled(true);
        plan.setEnabled(true);
        cRText.setText("");
        bD.setVisibility(View.VISIBLE);
        bD.setHintTextColor(Color.WHITE);
        btnsearch.setVisibility(View.GONE);
        cRText.setVisibility(View.VISIBLE);
        txt1.setVisibility(View.GONE);
        txt2.setVisibility(View.GONE);
        txtEF.setVisibility(View.GONE);
        txtET.setVisibility(View.GONE);
        txtESN.setVisibility(View.GONE);
        txtESN.setHintTextColor(Color.WHITE);
        bplan.setVisibility(View.GONE);
        viewOn = true;
    }
    public void searchBtnClicked (View v){
        map.clear();
        plan.setEnabled(true);
        search.setEnabled(false);
        view.setEnabled(true);
        bD.setVisibility(View.GONE);
        bD.setHintTextColor(Color.WHITE);
        btnsearch.setVisibility(View.VISIBLE);
        cRText.setVisibility(View.GONE);
        txt1.setVisibility(View.GONE);
        txt2.setVisibility(View.GONE);
        txtEF.setVisibility(View.GONE);
        txtET.setHintTextColor(Color.WHITE);
        txtET.setVisibility(View.GONE);
        txtEF.setHintTextColor(Color.WHITE);
        txtESN.setVisibility(View.VISIBLE);
        bplan.setVisibility(View.GONE);
        viewOn = false;
    }
    public void planBtnClicked(View v){
        map.clear();
        plan.setEnabled(false);
        search.setEnabled(true);
        view.setEnabled(true);
        bD.setVisibility(View.VISIBLE);
        bD.setHintTextColor(Color.WHITE);
        btnsearch.setVisibility(View.GONE);
        cRText.setVisibility(View.GONE);
        txt1.setVisibility(View.VISIBLE);
        txt2.setVisibility(View.VISIBLE);
        txtEF.setVisibility(View.VISIBLE);
        txtET.setHintTextColor(Color.WHITE);
        txtET.setVisibility(View.VISIBLE);
        txtEF.setHintTextColor(Color.WHITE);
        txtESN.setVisibility(View.GONE);
        txtESN.setHintTextColor(Color.WHITE);
        bplan.setVisibility(View.VISIBLE);
        viewOn = false;
    }

    //show road works where user has entered
    public void btnSearchClicked(View v){
        String street = txtESN.getText().toString().trim();
        int count = 0;
        c = Calendar.getInstance();
        if(street.length() == 0) {
            txtESN.setHintTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Enter something", Toast.LENGTH_SHORT).show();
        }else{
            try {
                if (ci.size() != 0) {
                    for (int i = 0; i < ci.size(); i++) {
                        BitmapDescriptor icon;
                        String title = "Incidents: ";
                        title = title + ci.get(i).getTitle();
                        String latlng = ci.get(i).getGeorss();
                        String[] split = latlng.split(" ");
                        double lat = Double.parseDouble(split[0]);
                        double lng = Double.parseDouble(split[1]);

                        if (ci.get(i).getTitle().contains(street)) {
                            if (ci.get(i).getTitle().contains("closure") || ci.get(i).getTitle().contains("Closure")) {
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
                            } else {
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.incidents);
                            }
                            LatLng newMarker = new LatLng(lat, lng);
                            MarkerOptions option = new MarkerOptions()
                                    .zIndex(i)
                                    .alpha(0)
                                    .position(newMarker)
                                    .title(title)
                                    .snippet("Click for more Info!!")
                                    .icon(icon);

                            map.addMarker(option);
                            count++;
                        }
                    }
                }
                if (rw.size() != 0) {
                    for (int i = 0; i < rw.size(); i++) {
                        BitmapDescriptor icon;
                        String title = "RoadWorks: ";
                        title = title + rw.get(i).getTitle();
                        String latlng = rw.get(i).getGeorss();
                        String[] split = latlng.split(" ");
                        double lat = Double.parseDouble(split[0]);
                        double lng = Double.parseDouble(split[1]);
                        if (rw.get(i).getTitle().contains(street)) {
                            if (rw.get(i).getTitle().contains("closure") || rw.get(i).getTitle().contains("Closure")) {
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
                            } else {
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadworks);
                            }

                            LatLng newMarker = new LatLng(lat, lng);
                            MarkerOptions options = new MarkerOptions()
                                    .zIndex(i)
                                    .alpha(1)
                                    .position(newMarker)
                                    .title(title)
                                    .snippet("Click for more Info!!")
                                    .icon(icon);

                            map.addMarker(options);
                            count++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No Roadworks in this area", Toast.LENGTH_SHORT).show();
            }
        }
        if (count == 0){
            Toast.makeText(getApplicationContext(), "No Roadworks in this area", Toast.LENGTH_SHORT).show();
        }
    }

    //do when plan journey button clicked
    public void btnPlanJourneyClicked(View v){
        //get user input
        String locationTo = txtET.getText().toString().trim();
        String locationFrom = txtEF.getText().toString().trim();
        List<Address> addressTo = null;
        List<Address> addressFrom = null;

        String dateChecker = bD.getText().toString().trim();
        if(dateChecker.length()<1){
            bD.setHintTextColor(Color.RED);
            Toast.makeText(getApplicationContext(),"Enter in a Date",Toast.LENGTH_SHORT).show();
        }else {
            if (!locationFrom.matches("") && !locationTo.matches("")) {
                map.clear();
                showRoadMap.run();
                Geocoder geocoder = new Geocoder(MainActivity.this);
                try {
                    addressFrom = geocoder.getFromLocationName(locationFrom, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Something went wrong with the Geocoder Please try again later",Toast.LENGTH_SHORT).show();
                }
                try{
                    addressTo = geocoder.getFromLocationName(locationTo, 1);
                }catch  (IOException e){
                    e.printStackTrace();
                }

                Address addressF = addressFrom.get(0);
                Address addressT = addressTo.get(0);

                LatLng latLngF = new LatLng(addressF.getLatitude(), addressF.getLongitude());
                LatLng latLngT = new LatLng(addressT.getLatitude(), addressT.getLongitude());
                map.addMarker(new MarkerOptions().position(latLngF).title("Start: " + locationFrom)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.addMarker(new MarkerOptions().position(latLngT).title("End: " + locationTo)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                String url = getUrl(latLngF, latLngT);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngF, 10));
            } else {
                //show user what need to change
                if (locationFrom.matches("")) {
                    txtEF.setHintTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Nothing entered", Toast.LENGTH_SHORT).show();
                } else {
                    txtET.setHintTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Nothing entered", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getUrl(LatLng from, LatLng to){
        String str_origin = "origin=" + from.latitude + "," + from.longitude;
        String str_dest = "destination=" + to.latitude +","+to.longitude;
        String mode = "mode=driving";
        String parameter = str_origin + "&"+ str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"+ parameter +"&key=AIzaSyADSJfmmZEFMWnhOmJI-kuUKukLpkak6qQ" ;
        return url;
    }
    private String requestDirections(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line ="";
            while((line = bufferedReader.readLine()) !=null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return  responseString;
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
        try{
            responseString = requestDirections(strings[0]);
        }catch (IOException e){
            e.printStackTrace();
        }
        return responseString;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse jason here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionParser directionParser = new DirectionParser();
                routes = directionParser.parse(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //get list of routes and display on map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for(List<HashMap<String,String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String,String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null){
                map.addPolyline(polylineOptions);
            }else{
                Toast.makeText(getApplicationContext(),"Directions not found!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            if (rw == null){
                Road.start();
                try {
                    Road.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (pRw == null) {
                pRoad.start();
                try {
                    pRoad.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ci == null){
                Incident.start();
            }
        }
    });
    Thread pRoad = new Thread(new Runnable() {
        @Override
        public void run() {
            pRw = siteXmlPullParser.getPlannedRoadWorksFromFile(MainActivity.this);
        }
    });
    Thread Road = new Thread(new Runnable() {
        @Override
        public void run() {
            rw = siteXmlPullParser.getCurrentRoadWokdsFromFile(MainActivity.this);
        }
    });
    Thread Incident = new Thread(new Runnable() {
        @Override
        public void run() {
            ci = siteXmlPullParser.getCurrentIncedentsFromFile(MainActivity.this);
        }
    });

    Thread showRoadMap = new Thread(new Runnable() {
        @Override
        public void run() {
            //variables
            int numberOfCi = ci.size();
            String stringI;
            int numberOfRw = rw.size();
            String stringR;
            //if datePickers date is today's date do following
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMMM dd,yyyy");
            String pickerDate = sdf.format(date);
            String currentDate = sdf.format(currentTime);
            if(pickerDate.equals(currentDate)){
                planned = false;
                if(numberOfCi == 0){
                    stringI = numberOfCi + " Incidents\n\n";
                }else{
                    drawIncidentMap();
                    stringI = numberOfCi + " Incidents\n\n";
                }
                if(numberOfRw == 0){
                    stringR = numberOfRw + " RoadWorks\n\n "+ closeCount + " Closures";
                }else{
                    drawRWMap(rw);
                    stringR = numberOfRw + " RoadWorks\n\n" + closeCount + " Closures";
                }
                String stringShow = stringI + stringR;
                cRText.setText(stringShow);
            }else{
                planned = true;
                Date userDate = date;
                uPRw = findDate(userDate);
                uRw = findRwDate(userDate);
                int userPRw = uPRw.size();
                int userRw = uRw.size();
                String stringPrw;
                String stringRw;
                if(userRw == 0){
                    stringRw = userRw + " RoadWorks currently happening\n\n";
                }else{
                    drawRWMap(uRw);
                    stringRw = userRw + " RoadWorks currently happening\n\n";
                }
                if (userPRw == 0){
                    stringPrw = userPRw + " Planned RoadWorks\n\n" + closeCount + " Closures";
                }else{
                    drawPRWMap();
                    stringPrw = userPRw + " Planned RoadWorks\n\n" + closeCount + " Closures";
                }
                String stringShow1 = stringRw + stringPrw;
                cRText.setText(stringShow1);
            }
        }
    });
}
