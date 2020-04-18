package com.example.layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    int i = 0;
    boolean planned;
    float what;
    TextView txtHead,txtBasInfo, txtDate, txtBigInfo;
    Button btnLeft, btnRight,btnUp,btnBack;
    List<CurrentIncedents> ci;
    List<RoadWorks> rw;
    List<PlannedRoadWorks> pRw;
    GoogleMap map;
    Date d;
    String stringDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(ViewActivity.this);

        //get variables
        Intent intent = getIntent();
        i = (int) intent.getFloatExtra("ArrayLocation",0);
        planned = intent.getExtras().getBoolean("isItPlanned");
        what = intent.getFloatExtra("what",3);
        stringDate = intent.getStringExtra("date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM dd,yyyy");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        d = null;
        try {
             d = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pRw = findDate(d);
        //get buttons
        btnLeft = (Button) findViewById(R.id.btnChangeLeft);
        btnRight = (Button) findViewById(R.id.btnChangeRight);
        btnUp = (Button) findViewById(R.id.btnForward);
        btnBack = (Button) findViewById(R.id.btnBack);
        //get TextViews
        txtHead = (TextView) findViewById(R.id.txtHeadTitle);
        txtBasInfo = (TextView) findViewById(R.id.whatAreButtons);
        txtDate = (TextView) findViewById(R.id.txtDateAndPos);
        txtBigInfo = (TextView) findViewById(R.id.txtShowInfo);

    }
    //show specified incident info
    public void showCurrentIncidents(int pos){
        map.clear();
        if (ci.size() == 0){
            txtBigInfo.setText("\nThere are no current Incidents to report on");
        }else{
            txtBasInfo.setText("This is all the current accidents on the Road for Today");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(ci.size()));

            BitmapDescriptor icon;
            String title = ci.get(pos).getTitle();
            String description = ci.get(i).getDescription();
            String latLong = ci.get(i).getGeorss();
            String publish = ci.get(i).getPubDate();
            txtBigInfo.setText("\nTitle: " + title + "\nDescription: " + description +
                    "\nLatitude and Longitude: " + latLong + "\nPublish Date: "+ publish);

            String [] split = latLong.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(ci.get(i).getTitle().contains("closure")||ci.get(i).getTitle().contains("Closure")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.incidents);
            }
            LatLng newMarker = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions()
                    .title(title)
                    .position(newMarker)
                    .icon(icon);

            map.addMarker(options);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newMarker)
                    .zoom(15f).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    //show specified roadwork info
    public void showRoadWorks(int pos){
        map.clear();
        if(rw.size() == 0){
            txtBigInfo.setText("\nThere are no current Roadworks to report on");
        }else {
            txtBasInfo.setText("This is all the current RoadWorks that are affecting the road at the moment but will still be " +
                    "ongoing on this day");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(rw.size()));

            BitmapDescriptor icon;
            String title = rw.get(pos).getTitle();
            String startDate = DateFormat.getDateInstance(DateFormat.FULL).format(rw.get(pos).getStartDate());
            String endDate = DateFormat.getDateInstance(DateFormat.FULL).format(rw.get(pos).getEndDate());
            String description = rw.get(pos).getDelayInfo();
            String latLong = rw.get(pos).getGeorss();
            String publish = rw.get(pos).getPubDate();

            txtBigInfo.setText("\nTitle: " + title + "\nStart Date: " + startDate + "\nEnd Date: " + endDate +
                    "\nDescription: " + description + "\nLatitude and Longitude: " + latLong + "\nPublished date: "
                    + publish);

            String[] split = latLong.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(rw.get(i).getTitle().contains("closure")||rw.get(i).getTitle().contains("Closure")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadworks);
            }
            LatLng newMarker = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions()
                    .title(title)
                    .position(newMarker)
                    .icon(icon);


            map.addMarker(options);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newMarker)
                    .zoom(15f).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    //show specified planed roadworks info
    public void showPlaRoadWorks(int pos){
        map.clear();
        if (pRw.size() == 0){
            txtBigInfo.setText("There is currently no planned roadworks to report");
        }else{
            txtBasInfo.setText("This is Roadworks planned to take place on the specified date");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(pRw.size()));

            BitmapDescriptor icon;
            String title = pRw.get(pos).getTitle();
            String startDate = DateFormat.getDateInstance(DateFormat.FULL).format(pRw.get(pos).getStartDate());
            String endDate = DateFormat.getDateInstance(DateFormat.FULL).format(pRw.get(pos).getEndDate());
            String description = pRw.get(pos).getDescription();
            String latLong = pRw.get(pos).getGeorss();
            String publish = pRw.get(pos).getPubDate();

            txtBigInfo.setText("\nTitle: " + title + "\nStart Date: " + startDate + "\nEnd Date: " + endDate +
                    "\nDescription: " + description + "\nLatitude and Longitude: " + latLong + "\nPublished date: "
                    + publish);

            String[] split = latLong.split(" ");
            double lat = Double.parseDouble(split[0]);
            double lng = Double.parseDouble(split[1]);

            if(pRw.get(i).getDescription().contains("Closure")||pRw.get(i).getDescription().contains("Closures")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.closed);
            }else{
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadworks);
            }
            LatLng newMarker = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions()
                    .title(title)
                    .position(newMarker)
                    .icon(icon);

            map.addMarker(options);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newMarker)
                    .zoom(15f).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    //find specified roadworks using specified date
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng Scotland = new LatLng(56.68894,-4.0425997);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Scotland,5f));
        view.run();
    }

    Runnable view = new Runnable() {
        @Override
        public void run() {
            if(planned == true){
                btnLeft.setText("Planned RoadWorks");
                btnRight.setText("RoadWorks");
                //if roadworks marker was clicked
                if (what == 1){
                    txtHead.setText("\nRoadWorks");
                    btnRight.setEnabled(false);
                    rw = findRwDate(d);
                    showRoadWorks(i);
                }else if (what ==2){
                    txtHead.setText("\nPlanned RoadWorks");
                    btnLeft.setEnabled(false);

                    showPlaRoadWorks(i);
                }
            }else if (planned == false){
                btnLeft.setText("Current Incidents");
                btnRight.setText("RoadWorks");
                if(what == 0){
                    txtHead.setText("\nCurrent Incidents");
                    btnLeft.setEnabled(false);

                    showCurrentIncidents(i);
                }else if(what == 1){
                    txtHead.setText("\nRoadWorks");
                    btnRight.setEnabled(false);

                    showRoadWorks(i);
                }
            }
        }
    };
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            rw = siteXmlPullParser.getCurrentRoadWokdsFromFile(ViewActivity.this);
            pRw = siteXmlPullParser.getPlannedRoadWorksFromFile(ViewActivity.this);
            ci = siteXmlPullParser.getCurrentIncedentsFromFile(ViewActivity.this);
        }
    });

    public void btnLeftClicked(View v){
        if(planned == true){
            i = 0;
            if (pRw.size() == 0){
                i=-1;
            }
            if (pRw.size()==1 || pRw.size() ==0){
                btnBack.setEnabled(false);
                btnUp.setEnabled(false);
            }
            what = 2;
            txtHead.setText("\nPlanned RoadWorks");
            btnLeft.setEnabled(false);
            btnRight.setEnabled(true);
            txtBasInfo.setText("This is Roadworks planned to take place on the specified date");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(pRw.size()));
            showPlaRoadWorks(i);
        }else if (planned == false){
            i = 0;
            if (ci.size() == 0){
                i=-1;
            }
            what = 0;
            txtHead.setText("\nCurrent Incidents");
            btnLeft.setEnabled(false);
            btnRight.setEnabled(true);
            if (ci.size()==1 || ci.size() ==0){
                btnBack.setEnabled(false);
                btnUp.setEnabled(false);
            }
            txtBasInfo.setText("This is all the current accidents on the Road for Today");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(ci.size()+1));
            showCurrentIncidents(i);
        }
    }

    public void btnRightClicked(View v){
        if (planned == true){
            rw = findRwDate(d);
            i = 0;
            what = 1;
            txtHead.setText("\nRoadWorks");
            btnRight.setEnabled(false);
            btnLeft.setEnabled(true);
            btnBack.setEnabled(true);
            btnUp.setEnabled(true);
            txtBasInfo.setText("This is all the current RoadWorks that are affecting the road at the moment but will still be " +
                    "ongoing on this day");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(rw.size()+1));
            showRoadWorks(i);
        }else if (planned == false){
            i = 0;
            what =1;
            txtHead.setText("\nRoadWorks");
            btnBack.setEnabled(true);
            btnUp.setEnabled(true);
            btnRight.setEnabled(false);
            btnLeft.setEnabled(true);
            txtBasInfo.setText("This is all the current RoadWorks that are affecting the road at the moment");
            txtDate.setText("Your entered date: " + stringDate +"\nPosition: "+(i+1)+"/"+(rw.size()+1));
            showRoadWorks(i);
        }
    }

    public void btnForwardClicked(View v) {
        if (what == 0) {
            i++;
            if (i == ci.size()) {
                i = 0;
            }
            showCurrentIncidents(i);
        } else if (what == 1) {
            i++;
            if (i == rw.size()) {
                i = 0;
            }
            showRoadWorks(i);
        } else if (what == 2) {
            i++;
            if (i == pRw.size()) {
                i = 0;
            }
            showPlaRoadWorks(i);
        }
    }

    public void btnBackClicked(View v){
        if (what == 0){
            i--;
            if(i == -1){
                i = ci.size() -1;
            }
            showCurrentIncidents(i);
        }else if(what ==1){
            i--;
            if(i == -1){
                i = rw.size() -1;
            }
            showRoadWorks(i);
        }else if (what == 2){
            i--;
            if (i == -1){
                i = pRw.size() -1;
            }
            showPlaRoadWorks(i);
        }
    }
}
