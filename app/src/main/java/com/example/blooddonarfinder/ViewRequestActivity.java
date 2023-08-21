package com.example.blooddonarfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.blooddonarfinder.other.CircleTransform;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    ListView requestListView;
    ArrayAdapter arrayAdapter;

    ArrayList<String> request = new ArrayList<String>();
    ArrayList<Double> requestLatitudes = new ArrayList<Double>();
    ArrayList<Double> requestLongitudes = new ArrayList<Double>();
    ArrayList<String> usernames = new ArrayList<String>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    Bitmap bitmap;

    LocationManager locationManager;
    LocationListener locationListener;

    ParseGeoPoint parseGeoPoint;
    ParseGeoPoint requestLocation;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private boolean canGetLocation;
    String bGroup;
    //ArrayList<String> bGroupD = new ArrayList<>();
    ArrayList<String> fullName = new ArrayList<>();
    ArrayList<String> requesterLocation = new ArrayList<>();
    ArrayList<String> bQuantity = new ArrayList<>();
    ArrayList<String> mobileNum = new ArrayList<>();
    String description;
    ArrayList<String> objectID = new ArrayList<>();

    public void updateListView(Location location){

        if(location != null) {

           // query.whereDoesNotExist("driverUsername");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("RequstBlood");

            final ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(),location.getLongitude());

            query.whereNear("GeoPointLocation",parseGeoPoint);
           // query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("BloodGroup",bGroup);

            //query.setLimit(10);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        request.clear();
                        requestLongitudes.clear();
                        requestLatitudes.clear();
                        float distanceOneDp=0;
                        if(objects.size() > 0){
                            for (int i=0;i<objects.size();i++){

                                requestLocation = (ParseGeoPoint) objects.get(i).get("GeoPointLocation");

                                if (requestLocation != null) {
                                    Double distanceInKilometer = parseGeoPoint.distanceInKilometersTo((ParseGeoPoint) objects.get(i).get("GeoPointLocation"));
                                    distanceOneDp = Math.round(distanceInKilometer * 10) / 10;
                                    fullName.add(objects.get(i).getString("FullName"));
                                    bQuantity.add(objects.get(i).getString("BloodQuantity"));
                                    description = objects.get(i).getString("Description");
                                    requesterLocation.add(objects.get(i).getString("Location"));
                                    mobileNum.add(objects.get(i).getString("username"));
                                    objectID.add(objects.get(i).getObjectId());
                                    request.add(fullName.get(i)+"       "+distanceOneDp + " KM"+"\n\n"+"Blood Quantity required: "+bQuantity.get(i)+"\n"+"Purpose:-"+description);


                                    requestLatitudes.add(requestLocation.getLatitude());
                                    requestLongitudes.add(requestLocation.getLongitude());
                                    usernames.add(objects.get(i).getString("username"));
                                }

                            }

                        }else{
                            request.add("No active requests Nearby");
                        }
                        requestListView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        setTitle("Nearby Requests");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        bGroup = intent.getStringExtra("BloodGroup");

        requestListView = (ListView)findViewById(R.id.requestListView);

        arrayAdapter = new ArrayAdapter(this,R.layout.request_listview_text, request);
       // request.clear();
        request.add("Getting Nearby Location...");
       // requestListView.setAdapter(arrayAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (requestLatitudes.size() > i && requestLongitudes.size() > i && usernames.size() > i && lastKnownLocation != null && mobileNum.size()>i && fullName.size()>i && requesterLocation.size()>i && objectID.size()>i) {

                        Intent intent = new Intent(getApplicationContext(),DonarDonateBloodActivity.class);

                        intent.putExtra("requestLatitude", requestLatitudes.get(i));
                        intent.putExtra("requestLongitude", requestLongitudes.get(i));
                        intent.putExtra("donarLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("donarLongitude", lastKnownLocation.getLongitude());
                        intent.putExtra("fullName",fullName.get(i));
                        intent.putExtra("mob",mobileNum.get(i));
                        intent.putExtra("location",requesterLocation.get(i));
                        intent.putExtra("requestLocation",requestLocation);
                        intent.putExtra("bGroup",bGroup);
                        intent.putExtra("username", usernames.get(i));
                        intent.putExtra("objectId",objectID.get(i));

                        startActivity(intent);

                    }

                }

            }
        });


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateListView(location);
                ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

                ParseUser.getCurrentUser().saveInBackground();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
        try {
            // locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d("GPS", String.valueOf(isGPSEnabled));

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("Network", String.valueOf(isNetworkEnabled));

            if (isGPSEnabled || isNetworkEnabled) {
                // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                // no network provider is enabled
                this.canGetLocation = true;

                //here is the if-else change so code avoids falling into both loops
                // if GPS Enabled get lat/long using GPS Services
               if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                       Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastKnownLocation != null) {
                            parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                            updateListView(lastKnownLocation);
                        } else {
                            if (isNetworkEnabled) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                Log.d("Network", "Network Enabled");
                                if (locationManager != null) {
                                    Location lastLocation = locationManager
                                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    if (lastLocation != null) {
                                        parseGeoPoint = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());

                                        updateListView(lastLocation);
                                    }
                                }
                            }
                            }
                        }
                    }
                }
           // }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
}
