package com.example.blooddonarfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.blooddonarfinder.fragment.MapsDonarDetailsActivity;
import com.example.blooddonarfinder.fragment.NotificationFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.parse.Parse.getApplicationContext;

public class RequestFragment extends Fragment {

    private Spinner bgroup;
    Button request;
    LocationManager locationManager;
    LocationListener locationListener;
    EditText locationPicker;
    EditText description;
    EditText bloodQuant;
    ParseGeoPoint parseGeoPoint;
    ImageView imageView;
    Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private boolean canGetLocation;
    ParseGeoPoint donarLocation;
    String firstName;
    String lastName;
    String fullName;
    ArrayList<String> firstNameD;
    ArrayList<String> lastNameD;
    ArrayList<String> fullNameDs = new ArrayList<>();
    ArrayList<String> blGroupDs = new ArrayList<>();
    ArrayList<String> locationDs = new ArrayList<>();
    ArrayList<Double> donarLatitudes = new ArrayList<Double>();
    ArrayList<Double> donarLongitudes = new ArrayList<Double>();
    ArrayList<String> donarDistances = new ArrayList<>();
    String reqCode;
   // RandomString tickets;
    String easy;
   // RandomString gen;


    public RequestFragment() {
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
                    locationPicker.setText(address);
                }
            } catch (IOException e) {

                e.printStackTrace();
            }


        }


    public void startListening() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();
        }
    }

   /* public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(900000) + 100000;

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }*/


   /* public static class RandomString {

        /**
         * Generate a random string.
         */
      /*  public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }

        public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static String lower = upper.toLowerCase(Locale.ROOT);

        public static final String digits = "0123456789";

        public static final String alphanum = upper + lower + digits;

        private final Random random;

        private final char[] symbols;

        private final char[] buf;

        public RandomString(int length, Random random, String symbols) {
            if (length < 1) throw new IllegalArgumentException();
            if (symbols.length() < 2) throw new IllegalArgumentException();
            this.random = Objects.requireNonNull(random);
            this.symbols = symbols.toCharArray();
            this.buf = new char[length];
        }

        /**
         * Create an alphanumeric string generator.
         */
       /* public RandomString(int length, Random random) {
            this(length, random, alphanum);
        }

        /**
         * Create an alphanumeric strings from a secure generator.
         */
       /* public RandomString(int length) {
            this(length, new SecureRandom());
        }

        /**
         * Create session identifiers.
         */
       /* public RandomString() {
            this(21);
        }

    }*/

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        imageView = view.findViewById(R.id.imageView);
        bgroup = (Spinner) view.findViewById(R.id.blood_group_drop_down);
        request = (Button) view.findViewById(R.id.request_blood);
        description = (EditText) view.findViewById(R.id.description);
        bloodQuant = (EditText) view.findViewById(R.id.blood_quant);
        locationPicker = (EditText) view.findViewById(R.id.tv_location_picker);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        addListenerOnSpinnerItemSelection();

        reqCode = getSaltString();

        //for sharing invite code
      //  easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
       // tickets = new RandomString(23, new SecureRandom(), easy);

       // gen = new RandomString(8, ThreadLocalRandom.current());


        ParseQuery<ParseObject> queryDetails = ParseQuery.getQuery("UserDetails");
        queryDetails.whereEqualTo("MobileNumber",ParseUser.getCurrentUser().getUsername());
        queryDetails.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        for(ParseObject object : objects){
                            firstName = object.getString("FirstName");
                            lastName = object.getString("LastName");
                           fullName = firstName+" "+lastName;
                        }
                    }
                }


            }
        });



        request.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                 if((bgroup.getSelectedItem().toString()).equals("Select Blood Group")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please!,Select your Blood Group.\nThis is essential for me.", Toast.LENGTH_SHORT).show();
                    return;
                 }else if(TextUtils.isEmpty(bloodQuant.getText().toString())) {
                     Toast.makeText(getActivity().getApplicationContext(), "Please!,Enter Quantity of blood.", Toast.LENGTH_SHORT).show();
                     return;
                }else if(TextUtils.isEmpty(locationPicker.getText().toString())) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please!,Enter your Address.", Toast.LENGTH_SHORT).show();
                    return;
                }


                   /* Toast.makeText(getActivity().getApplicationContext(),
                            "OnClickListener : " +
                                    "\nUserName : " + ParseUser.getCurrentUser().getUsername() +
                                    "\nbloodQuant : " + bloodQuant.getText().toString() +
                                    "\ndesc : " + description.getText().toString() +
                                    "\nBlood Group : " + bgroup.getSelectedItem() +
                                    "\nGeoPoint : " + parseGeoPoint +
                                    "\nLocation :" + locationPicker.getText().toString()
                            , Toast.LENGTH_SHORT).show();*/

                     //Data goes to Server now if all condition is true
                            ParseObject object = new ParseObject("RequstBlood");
                            object.put("username", ParseUser.getCurrentUser().getUsername());
                            object.put("BloodQuantity", bloodQuant.getText().toString());
                            object.put("Description", description.getText().toString());
                            object.put("BloodGroup", bgroup.getSelectedItem());
                            object.put("GeoPointLocation", parseGeoPoint);
                            object.put("FullName", fullName);
                            object.put("Location", locationPicker.getText().toString());
                            object.put("RequestCode",reqCode);

                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        Toast.makeText(getApplicationContext(), "Your Request has been successfully send.", Toast.LENGTH_SHORT).show();


                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);

                                        // set title
                                        alertDialogBuilder.setTitle("Request Code generated");
                                        alertDialogBuilder.setIcon(R.drawable.ic_baseline_vpn_key_24);
                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("                      "+reqCode+"\n\n"+"Share your code with Donar which will serve you.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        Intent intent = new Intent(getApplicationContext(),MapsDonarDetailsActivity.class);
                                                        intent.putExtra("BloodGroup", bgroup.getSelectedItem().toString());
                                                        startActivity(intent);

                                                    }
                                                });

                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // show it
                                        alertDialog.show();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Oops!Something went wrong.", Toast.LENGTH_SHORT).show();
                                        Log.i("SaveInBackground", e.getMessage());
                                    }
                                }
                            });

                Log.i("ParseGeoPoint", String.valueOf(parseGeoPoint));
                Log.i("BloodGroup",bgroup.getSelectedItem().toString());
              /*  if(parseGeoPoint != null) {
                    Log.i("ParseGeoPoint", String.valueOf(parseGeoPoint));
                    // query.whereDoesNotExist("driverUsername");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDetails");

                  //  final ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(),location.getLongitude());

                    query.whereNear("GeoPointLocation",parseGeoPoint);
                     //query.whereEqualTo("BloodGroup",bgroup.getSelectedItem().toString());


                    //query.setLimit(10);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null){
                                donarLongitudes.clear();
                                donarLatitudes.clear();
                                if(objects.size() > 0){
                                    for(ParseObject object:objects) {
                                        donarLocation = (ParseGeoPoint) object.get("GeoPointLocation");
                                        fullNameDs.add(object.getString("FullName"));
                                        blGroupDs.add(object.getString("BloodGroup"));
                                        locationDs.add(object.getString("Location"));
                                        Log.i("LocationDonar", String.valueOf(donarLocation)+locationDs);

                                        if (donarLocation != null) {
                                            Double distanceInKilometer = parseGeoPoint.distanceInKilometersTo((ParseGeoPoint) object.get("GeoPointLocation"));
                                            float distanceOneDp = Math.round(distanceInKilometer * 10) / 10;

                                            donarDistances.add(distanceOneDp + " KM");

                                            donarLatitudes.add(donarLocation.getLatitude());
                                            donarLongitudes.add(donarLocation.getLongitude());

                                        }


                                    }
                                    Log.i("donarLatitudes", String.valueOf(donarLatitudes));
                                    Log.i("donarLongitudes", String.valueOf(donarLongitudes));

                                    if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                        Log.i("donarLatitudes", String.valueOf(donarLatitudes));
                                        Log.i("donarLongitudes", String.valueOf(donarLongitudes));

                                        for(int i = 0 ;i<donarLatitudes.size() && i<donarLongitudes.size()  && lastKnownLocation != null;i++){

                                            Intent intent = new Intent(getApplicationContext(),UserLocationRequest.class);

                                            intent.putExtra("donarLatitude", donarLatitudes);
                                            intent.putExtra("donarLongitude", donarLongitudes);
                                            intent.putExtra("requestLatitude", lastKnownLocation.getLatitude());
                                            intent.putExtra("requestLongitude", lastKnownLocation.getLongitude());
                                            intent.putExtra("BloodGroup", bgroup.getSelectedItem().toString());
                                            intent.putExtra("fullNameD",fullNameDs);
                                            intent.putExtra("blGroupD",blGroupDs);
                                            intent.putExtra("donarDistance",donarDistances);
                                            intent.putExtra("locationD",locationDs);

                                            startActivity(intent);


                                        }

                                    }

                                }

                            }
                        }
                    });

                }*/






                }

        });

      //  locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
          locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        locationPicker.setOnClickListener(new View.OnClickListener() {
            private boolean canGetLocation;

            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Toast.makeText(getActivity(), "EditText Clicked", Toast.LENGTH_SHORT).show();
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
        });



        ParseAnalytics.trackAppOpenedInBackground(getActivity().getIntent());
        // Log.i("Selected Date",eText.getText().toString());
    }

    public void addListenerOnSpinnerItemSelection() {
        bgroup = (Spinner) getView().findViewById(R.id.blood_group_drop_down);
        bgroup.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }

}
