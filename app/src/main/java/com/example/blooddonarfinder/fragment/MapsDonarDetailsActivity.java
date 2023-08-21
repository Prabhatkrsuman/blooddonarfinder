package com.example.blooddonarfinder.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.blooddonarfinder.ProfileActivity;
import com.example.blooddonarfinder.R;
import com.example.blooddonarfinder.other.CircleTransform;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsDonarDetailsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private boolean canGetLocation;
    ParseGeoPoint parseGeoPoint;
    TextView textView;
    String names = null;
    ParseGeoPoint donarLocation;
    Intent intent;
    LatLng donarLocationOnMap;
    LatLng requestLocationOnMap;
    String blGroup;
    ParseGeoPoint geoPoint1;
    LatLng latLng;
    String fullnameD;
    String blGroupD;
    String locationD;
    String donarDistance;
    String mob;
    TextView name;
    TextView bGroup;
    TextView rLocation;
    TextView pGeoPoint;
    Bitmap bitmap;
    ArrayList<Marker> markers;
    ArrayList<ParseGeoPoint> geoPointArrayList = new ArrayList<>();
    ArrayList<Double> donarLatitudes = new ArrayList<Double>();
    ArrayList<Double> donarLongitudes = new ArrayList<Double>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<String> namess = new ArrayList<>();
    Bitmap bitmap1;
    ParseFile file;
    Bitmap bp = null;
    String dName;
    ImageView profilePic;
    Date createdDate;
    String reportDate;
    int month = 0;
    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;


    public String countTime(Date date1) {
        String convertTime = null;
        String suffix = "ago";

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatted = dateFormat.format(date1);
                Date oldDate = dateFormat.parse(formatted);
                Date cDate = new Date();
                Long timeDiff = cDate.getTime() - oldDate.getTime();
                day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
                hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day));
                mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
                ss = (int) (TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)));

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        /*if (ss < 60) {
            if (ss == 1) {
                convertTime = ss + " second " + suffix;
                return convertTime;
            } else {
                convertTime = ss + " seconds " + suffix;
                return convertTime;
            }
        } else if (mm < 60) {
            if (mm == 1) {
                convertTime = mm + " minute " + suffix;
                return convertTime;
            } else {
                convertTime = mm + " minutes " + suffix;
                return convertTime;
            }
        } else if (hh < 24) {
            if (hh == 1) {
                convertTime = hh + " hour " + suffix;
                return convertTime;
            } else {
                convertTime = hh + " hours " + suffix;
                return convertTime;
            }
        } else if (day >= 7) {
            if (day >= 365) {
                long tempYear = day / 365;
                if (tempYear == 1) {
                    convertTime = tempYear + " year " + suffix;
                    return convertTime;
                } else {
                    convertTime = tempYear + " years " + suffix;
                    return convertTime;
                }
            } else if (day >= 30) {
                long tempMonth = day / 30;
                if (tempMonth == 1) {
                    convertTime = (day / 30) + " month " + suffix;
                    return convertTime;
                } else {
                    convertTime = (day / 30) + " months " + suffix;
                    return convertTime;
                }
            } else {
                long tempWeek = day / 7;
                if (tempWeek == 1) {
                    convertTime = (day / 7) + " week " + suffix;
                    return convertTime;
                } else {
                    convertTime = (day / 7) + " weeks " + suffix;
                    return convertTime;
                }
            }
        } else {
            if (day == 1) {
                convertTime = day + " day " + suffix;
                return convertTime;
            } else {
                convertTime = day + " days " + suffix;
                return convertTime;
            }
        }*/


            if (ss <= 60 && mm != 0) {
                if (mm <= 60 && hh != 0) {
                    if (hh <= 60 && day != 0) {
                        if (day <= 30) {
                            return day + " DAYS AGO";
                        } else {
                            month = day / 30;
                            return month + " MONTHS AGO";
                        }
                    } else {
                        return hh + " HOUR AGO";
                    }
                } else {
                    return mm + " MIN AGO";
                }
            } else {
                return ss + "Seconds AGO";
            }
        }
      
    


    public void updateLocationInfo (Location location){

        Log.i("Location Info", location.toString());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            String address = "Could not find address ";
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


            if (addressList != null && addressList.size() > 0) {

                Log.i("PlaceInfo", addressList.get(0).toString());

                address = "";
                if (addressList.get(0).getAddressLine(0) != null) {

                    address = addressList.get(0).getAddressLine(0);
                }
                Log.i("Address",address);
                textView.setText(blGroup+" Blood Group Lists");
            }
        } catch (IOException e) {

            e.printStackTrace();
        }


    }


    public void startListening() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsDonarDetailsActivity.this);
        // set title
        alertDialogBuilder.setTitle("Are you sure?");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);

        // set dialog message
        alertDialogBuilder
                .setMessage("you want to exit.")
                .setCancelable(false)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MapsDonarDetailsActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
               .setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "CANCEL", Toast.LENGTH_SHORT).show();

                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_donar_details);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        intent =getIntent();
        blGroup=intent.getStringExtra("BloodGroup");



        mapFragment.getMapAsync(this);
        textView = findViewById(R.id.titleShows);
        ConstraintLayout mapLayout = (ConstraintLayout) findViewById(R.id.userConstraintLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean canGetLocation;

            @Override
            public void onGlobalLayout() {


                locationManager = (LocationManager) MapsDonarDetailsActivity.this.getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateLocationInfo(location);
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

                if (Build.VERSION.SDK_INT < 23) {

                    startListening();

                } else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsDonarDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if (location != null) {
                                            parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                                            updateLocationInfo(location);
                                            pointLocation(parseGeoPoint);
                                        } else if (isNetworkEnabled) {
                                            locationManager.requestLocationUpdates(
                                                    LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                                            Log.d("Network", "Network Enabled");
                                            if (locationManager != null) {
                                                location = locationManager
                                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                                if (location != null) {
                                                    parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                                                    updateLocationInfo(location);
                                                    pointLocation(parseGeoPoint);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        //Toast.makeText(this,blGroup, Toast.LENGTH_SHORT).show();

    }

    private void pointLocation(final ParseGeoPoint parseGeoPoint) {
        //Toast.makeText(this,parseGeoPoint.toString(), Toast.LENGTH_SHORT).show();
        ParseQuery<ParseObject> donarQuery = ParseQuery.getQuery("UserDetails");
        //donarQuery.whereEqualTo("BloodGroup",blGroup);
        donarQuery.whereNear("GeoPointLocation",parseGeoPoint);
        //donarQuery.whereNotEqualTo("MobileNumber", ParseUser.getCurrentUser().getUsername());
        donarQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    donarLongitudes.clear();
                    donarLatitudes.clear();
                    bitmapArrayList.clear();
                    if(objects.size()>0){
                        for(ParseObject object:objects) {
                            donarLocation = (ParseGeoPoint) object.get("GeoPointLocation");
                            if (donarLocation != null) {
                                Double distanceInKilometer = parseGeoPoint.distanceInKilometersTo((ParseGeoPoint) object.get("GeoPointLocation"));
                                float distanceOneDp = Math.round(distanceInKilometer * 10) / 10;

                                // donarDistances.add(distanceOneDp + " KM");
                                donarLatitudes.add(donarLocation.getLatitude());
                                donarLongitudes.add(donarLocation.getLongitude());
                                geoPointArrayList.add(donarLocation);
                            }
                            names = object.getString("FullName");
                            namess.add(names);
                        }
                        showDetails(namess,parseGeoPoint,donarLatitudes,donarLongitudes,geoPointArrayList);
                    }else{
                        Toast.makeText(MapsDonarDetailsActivity.this, "No any Donar Found for this Blood Group..", Toast.LENGTH_SHORT).show();
                        LatLng userLocation = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    }
                }
            }
        });

    }

    private void showDetails(ArrayList<String> name, ParseGeoPoint requestPoint, ArrayList<Double> donarLatitudes, ArrayList<Double> donarLongitudes, ArrayList<ParseGeoPoint> geoPointArrayList) {
        //Toast.makeText(this,name+" "+donarLocation+" "+donarLatitudes+" "+donarLongitudes, Toast.LENGTH_SHORT).show();
        requestPoint = new ParseGeoPoint(location.getLatitude(),location.getLongitude());
        markers = new ArrayList<>();
        for(int i = 0 ;i<geoPointArrayList.size(); i++) {
            donarLocationOnMap = new LatLng(geoPointArrayList.get(i).getLatitude(), geoPointArrayList.get(i).getLongitude());
            requestLocationOnMap = new LatLng(requestPoint.getLatitude(), requestPoint.getLongitude());

            // Bitmap bitmap = bitmapArrayList.get(i);

            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsDonarDetailsActivity.this,R.drawable.logopitt, "Blood Requester"));

            Marker donarMarker = mMap.addMarker(new MarkerOptions().position(donarLocationOnMap).title("Donar Location")
                    .snippet("Click one more time please...")
                    .icon(smallMarkerIcon));
            markers.add(donarMarker);
            markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationOnMap).title("Your Location")));

        } // requestMarker.showInfoWindow();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        Log.i("Bounds", String.valueOf(bounds));

        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        // mMap.moveCamera(cu);
        mMap.animateCamera(cu);

        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));


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
        mMap.setOnMarkerClickListener(this);
//donationDate.setText("Available for blood Doantion");
        // Add a marker in Sydney and move the camera
       // LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
       // mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
    }

    public static Bitmap createCustomMarker(Context context, int resource, String _name) {

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
        Bitmap bitmap1 = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        marker.draw(canvas);

        return bitmap1;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Double lat = marker.getPosition().latitude;
        Double lon = marker.getPosition().longitude;
        final ParseGeoPoint geoPoint = new ParseGeoPoint(lat,lon);
        Log.i("Änything Print",marker.getId()+marker.getPosition()+geoPoint);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDetails");
        query.whereEqualTo("GeoPointLocation",geoPoint);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("geoPoint", String.valueOf(geoPoint));

                    if (objects.size() > 0) {
                            geoPoint1 = (ParseGeoPoint) objects.get(0).get("GeoPointLocation");
                            latLng = new LatLng(geoPoint1.getLatitude(),geoPoint1.getLongitude());
                            fullnameD = objects.get(0).getString("FullName");
                            blGroupD = objects.get(0).getString("BloodGroup");
                            locationD = objects.get(0).getString("Location");
                            mob = objects.get(0).getString("MobileNumber");
                            if (donarLocation != null) {
                                Double distanceInKilometer = parseGeoPoint.distanceInKilometersTo((geoPoint1));
                                float distanceOneDp = Math.round(distanceInKilometer * 10) / 10;

                                donarDistance = (distanceOneDp + " KM");
                            }
                    }
                }
            }
        });

        Log.i("geoPoint1", String.valueOf(geoPoint1));
        Log.i("geoPointDistance", String.valueOf(donarDistance));
        Log.i("requestPoint", String.valueOf(parseGeoPoint));

        if (marker.getTitle().equals("Donar Location") && marker.getPosition().equals(latLng)) {

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
            query1.whereEqualTo("MobileNumber",mob);
            query1.whereEqualTo("FullName",fullnameD);

            query1.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if(objects.size()>0){
                            ParseFile file = (ParseFile) objects.get(0).getParseFile("image");
                            if(file!=null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {

                                        if (e == null && data != null) {
                                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            showCustomDialog(bitmap);
                                        }
                                    }
                                });
                            }else{
                                showCustomDialogDefault();
                            }

                        }
                    } else {
                        Toast.makeText(MapsDonarDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


            Toast.makeText(getApplicationContext(), "Prabhat", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void showCustomDialogDefault() {


        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_donar_dialog,viewGroup,false);


        name = dialogView.findViewById(R.id.name);
        bGroup = dialogView.findViewById(R.id.donar_blood);
        rLocation = dialogView.findViewById(R.id.location);
        pGeoPoint = dialogView.findViewById(R.id.geopoint);
        TextView donarDistanceD = dialogView.findViewById(R.id.donar_distance);
        profilePic = dialogView.findViewById(R.id.profilePic);
        final TextView donationDate = dialogView.findViewById(R.id.onationDate);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd");
        String dateOutput = format.format(date);

       /* Date cDate = new Date();
        int weeks = 4;
        int days = 7;
        int time = (weeks*days*24*3600 * 1000);
        Date expirationDate = new Date(cDate.getTime() - (time));*/

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("DonateBlood");
        parseQuery.whereEqualTo("username",mob);
        parseQuery.whereEqualTo("fullname",fullnameD);
        parseQuery.whereEqualTo("DonationCount","Donate");
        parseQuery.whereGreaterThanOrEqualTo("createdAt",date);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        createdDate = objects.get(0).getCreatedAt();
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        reportDate = df.format(createdDate);

                            if(countTime(createdDate)!=null) {
                                donationDate.setText("Donate Blood at " + reportDate + " before :" + countTime(createdDate));

                        }else {
                            donationDate.setVisibility(View.GONE);
                        }
                    }else {
                        donationDate.setText("Available for blood donation.");
                    }
                }
            }
        });





        // Log.i("fullnameD", fullnameD.get(i));
        // Log.i("blGroupD", blGroupD.get(i));
        // Log.i("locationD", String.valueOf(locationD));
        // Log.i("donarDistD", donarDistance.get(i));

        name.setText(fullnameD);
        bGroup.setText("Blood Group: "+blGroupD);
        rLocation.setText("Location: "+locationD);
        pGeoPoint.setText("Co-ordinate: "+latLng.toString());
        donarDistanceD.setText("Distance: "+donarDistance);

        profilePic.setImageResource(R.drawable.profile_pic_change);



        final Button requestButton = (Button) dialogView.findViewById(R.id.buttonRequest);
        ImageButton callButton = (ImageButton) dialogView.findViewById(R.id.buttonCall);
        ImageButton chatButton = (ImageButton) dialogView.findViewById(R.id.buttonChat);
        ImageButton aboutButton = (ImageButton) dialogView.findViewById(R.id.buttonAbout);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        // alertDialog.getWindow().setLayout(300, 400); //Controlling width and height.
        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = 560;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestButton.setText("Requested Sent");
                Toast.makeText(MapsDonarDetailsActivity.this, "Request Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted()){
                    call_action();
                }
                Toast.makeText(MapsDonarDetailsActivity.this, "Call Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsDonarDetailsActivity.this, "Chat Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsDonarDetailsActivity.this, "About Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCustomDialog(Bitmap bitmap) {


        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_donar_dialog,viewGroup,false);


        name = dialogView.findViewById(R.id.name);
        bGroup = dialogView.findViewById(R.id.donar_blood);
        rLocation = dialogView.findViewById(R.id.location);
        pGeoPoint = dialogView.findViewById(R.id.geopoint);
        TextView donarDistanceD = dialogView.findViewById(R.id.donar_distance);
        profilePic = dialogView.findViewById(R.id.profilePic);
        final TextView donationDate = dialogView.findViewById(R.id.onationDate);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd");
        String dateOutput = format.format(date);


       /* Date cDate = new Date();
        int days = 365 ;
        int time = (days*24*3600 * 1000);
        Date expirationDate = new Date(cDate.getTime() - (time));*/

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("DonateBlood");
        parseQuery.whereEqualTo("username",mob);
        parseQuery.whereEqualTo("fullname",fullnameD);
        parseQuery.whereEqualTo("DonationCount","Donate");
        parseQuery.whereGreaterThanOrEqualTo("createdAt",date);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        createdDate = objects.get(0).getCreatedAt();
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        reportDate = df.format(createdDate);

                            if(countTime(createdDate)!=null) {
                                donationDate.setText("Donate Blood at " + reportDate + " before :" + countTime(createdDate));

                        }else {
                            donationDate.setVisibility(View.GONE);
                        }
                    }else {
                        donationDate.setText("Available for blood donation.");
                    }
                }
            }
        });




        // Log.i("fullnameD", fullnameD.get(i));
        // Log.i("blGroupD", blGroupD.get(i));
        // Log.i("locationD", String.valueOf(locationD));
        // Log.i("donarDistD", donarDistance.get(i));

        name.setText(fullnameD);
        bGroup.setText("Blood Group: "+blGroupD);
        rLocation.setText("Location: "+locationD);
        pGeoPoint.setText("Co-ordinate: "+latLng.toString());
        donarDistanceD.setText("Distance: "+donarDistance);

        profilePic.setImageBitmap(bitmap);




        final Button requestButton = (Button) dialogView.findViewById(R.id.buttonRequest);
        ImageButton callButton = (ImageButton) dialogView.findViewById(R.id.buttonCall);
        ImageButton chatButton = (ImageButton) dialogView.findViewById(R.id.buttonChat);
        ImageButton aboutButton = (ImageButton) dialogView.findViewById(R.id.buttonAbout);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        // alertDialog.getWindow().setLayout(300, 400); //Controlling width and height.
        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = 560;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestButton.setText("Requested Sent");
                Toast.makeText(MapsDonarDetailsActivity.this, "Request Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted()){
                    call_action();
                }
                Toast.makeText(MapsDonarDetailsActivity.this, "Call Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsDonarDetailsActivity.this, "Chat Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsDonarDetailsActivity.this, "About Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void call_action(){
        String mobileNumber = mob;
        //phoeNumberWithOutCountryCode(mobileNumber);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL); // Action for what intent called for
        intent.setData(Uri.parse("tel: " + mobileNumber)); // Data with intent respective action on intent
        startActivity(intent);
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
}