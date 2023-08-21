package com.example.blooddonarfinder;

import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.blooddonarfinder.fragment.MapsDonarDetailsActivity.createCustomMarker;

public class RequestMapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;

    LocationListener locationListener;

    Button callUberButton;

    Boolean requestActive = false;

    Handler handler = new Handler();

    Button infoTextView;

    Boolean driverActive = true;

    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private boolean canGetLocation;
    Location lastKnownLocation;
    ParseGeoPoint parseGeoPoint;
    String bloodGroup;

    public void checkForUpdates() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("RequstBlood");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        //query.whereEqualTo("BloodGroup",bloodGroup);
        query.whereExists("driverUsername");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0) {

                    driverActive = true;

                    ParseQuery<ParseUser> query = ParseUser.getQuery();

                    query.whereEqualTo("username", objects.get(0).getString("username"));

                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            if (e == null && objects.size() > 0) {

                                ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("location");

                                    if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(RequestMapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                        if (lastKnownLocation != null) {

                                            ParseGeoPoint userLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                            Double distanceInMiles = driverLocation.distanceInMilesTo(userLocation);
                                            Double distanceInKm = driverLocation.distanceInKilometersTo(userLocation);

                                            if (distanceInMiles < 0.01) {

                                                infoTextView.setText("Donar is now here!");

                                                LatLng donarLocationLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

                                                LatLng requestLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                                                ArrayList<Marker> markers = new ArrayList<>();

                                                mMap.clear();
                                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(RequestMapViewActivity.this, R.drawable.thinking, "Blood Requester"));

                                                Marker donarMarker = mMap.addMarker(new MarkerOptions().position(donarLocationLatLng).title("Donar Location")
                                                        .snippet("Always help needy People..")
                                                        .icon(smallMarkerIcon));
                                                markers.add(donarMarker);

                                                markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location")));
                                                // markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                for (Marker marker : markers) {
                                                    builder.include(marker.getPosition());
                                                }
                                                LatLngBounds bounds = builder.build();


                                                int padding = 0; // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                // mMap.moveCamera(cu);
                                                mMap.animateCamera(cu);
                                                mMap.setMapStyle(
                                                        MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));


                                           /* callUberButton.setVisibility(View.INVISIBLE);

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    checkForUpdates();

                                                }
                                            }, 2000);*/












                                          /*  ParseQuery<ParseObject> query = ParseQuery.getQuery("RequstBlood");
                                            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null) {

                                                        for (ParseObject object : objects) {

                                                            object.deleteInBackground();

                                                        }


                                                    }

                                                }
                                            });*/

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    infoTextView.setText("");
                                                    driverActive = false;

                                                }
                                            }, 5000);

                                            } else {

                                                Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;
                                                Double distanceOneDPK = (double) Math.round(distanceInKm * 10) / 10;
                                                if (distanceOneDPK < 1) {
                                                    infoTextView.setText("Your Donar is  " + distanceOneDP.toString() + "  miles away!");
                                                } else {
                                                    infoTextView.setText("Your Donar is  " + distanceOneDPK.toString() + "  Km away!");
                                                }
                                                LatLng donarLocationLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

                                                LatLng requestLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                                                ArrayList<Marker> markers = new ArrayList<>();

                                                mMap.clear();
                                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(RequestMapViewActivity.this, R.drawable.thinking, "Blood Requester"));

                                                Marker donarMarker = mMap.addMarker(new MarkerOptions().position(donarLocationLatLng).title("Donar Location")
                                                        .snippet("Always help needy People..")
                                                        .icon(smallMarkerIcon));
                                                markers.add(donarMarker);

                                                markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location")));
                                                // markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                for (Marker marker : markers) {
                                                    builder.include(marker.getPosition());
                                                }
                                                LatLngBounds bounds = builder.build();


                                                int padding = 100; // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                // mMap.moveCamera(cu);
                                                mMap.animateCamera(cu);
                                                mMap.setMapStyle(
                                                        MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));


                                           // callUberButton.setVisibility(View.INVISIBLE);

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    checkForUpdates();

                                                }
                                            }, 2000);

                                            }

                                        }

                                    }

                                }
                            }

                    });




                }else{
                    driverActive=false;
                    updateMap(lastKnownLocation);
                    infoTextView.setVisibility(View.GONE);
                    Toast.makeText(RequestMapViewActivity.this, "No any Donar has accepted your Request.\n Just wait!", Toast.LENGTH_LONG).show();
                }



            }
        });


    }


    public void updateMap(Location location) {

        if (driverActive == false) {

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_request_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        infoTextView = (Button) findViewById(R.id.infoTextView);

       // Intent intent = getIntent();
        //bloodGroup = intent.getStringExtra("BloodGroup");





        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RequstBlood");

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        requestActive = true;

                        checkForUpdates();

                    }

                }

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateMap(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {

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
                                    LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (lastKnownLocation != null) {
                                    parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                    updateMap(lastKnownLocation);
                                } else if (isNetworkEnabled) {
                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                                    Log.d("Network", "Network Enabled");
                                    if (locationManager != null) {
                                        lastKnownLocation = locationManager
                                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                        if (lastKnownLocation != null) {
                                            parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                            updateMap(lastKnownLocation);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {

                updateMap(lastKnownLocation);

            }


        }



        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }


}