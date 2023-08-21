package com.example.blooddonarfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserDetailsActivity extends AppCompatActivity {

    DatePickerDialog picker;
    EditText eText;
    EditText fName;
    EditText lName;
    EditText eMail;
    Calendar myCalendar;
    private Spinner bgroup;
    private RadioGroup radioGroup;
    private RadioButton male, female, transgender;
    Button saveDetails;
    String gender = "Male";
    LocationManager locationManager;
    LocationListener locationListener;
    EditText locationPicker;
    TextView mobileNumber;
    ParseGeoPoint parseGeoPoint;
    Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private boolean canGetLocation;


    public void updateLocationInfo(Location location) {

        Log.i("Location Info", location.toString());

        locationPicker = findViewById(R.id.tv_location_picker);

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        eText.setText(sdf.format(myCalendar.getTime()));
    }

    public void addListenerOnSpinnerItemSelection() {
        bgroup = (Spinner) findViewById(R.id.blood_group_drop_down);
        bgroup.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        setTitle("User Details");
        // ParseUser.getCurrentUser().deleteInBackground();
      //  Toast.makeText(this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();

        bgroup = (Spinner) findViewById(R.id.blood_group_drop_down);
        saveDetails = (Button) findViewById(R.id.create_profile_button);
        eText = (EditText) findViewById(R.id.select_birth_date_EditText);
        fName = (EditText) findViewById(R.id.first_name);
        lName = (EditText) findViewById(R.id.last_name);
        eMail = (EditText) findViewById(R.id.email);
        mobileNumber = (TextView) findViewById(R.id.mobile_number);
        radioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        male = (RadioButton) findViewById(R.id.rb_male);
        female = (RadioButton) findViewById(R.id.rb_female);
        transgender = (RadioButton) findViewById(R.id.rb_transgender);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        myCalendar = Calendar.getInstance();
        addListenerOnSpinnerItemSelection();

        final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mobileNumber.setText(phone);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        eText.setOnClickListener(new View.OnClickListener() {
            private boolean canGetLocation;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UserDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                logIn();
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                // find which radio button is selected
                if(checkedId == R.id.rb_male) {
                    gender = "Male";
                } else if(checkedId == R.id.rb_female) {
                    gender = "Female";
                } else {
                    gender = "TransGender";
                }
            }

        });



            saveDetails.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(TextUtils.isEmpty(fName.getText().toString())) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Enter your FirstName.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(lName.getText().toString())) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Enter your LastName.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(eMail.getText().toString())) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Enter your EmailId.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if((bgroup.getSelectedItem().toString()).equals("Select Your Blood Group")) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Select your Blood Group.\nThis is essential for me.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(eText.getText().toString())) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Select Your Date of Birth.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(locationPicker.getText().toString())) {
                        Toast.makeText(UserDetailsActivity.this, "Please!,Enter your Address.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        if (ParseUser.getCurrentUser() == null) {

                            ParseUser user = new ParseUser();
                            user.setUsername(mobileNumber.getText().toString());
                            user.setPassword(mobileNumber.getText().toString());
                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.i("SignUp with Parse", "Successful");
                                }
                            });
                        }
                  /*  }else if(ParseUser.getCurrentUser()!=null){
                        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>();
                        ParseUser.logInInBackground(, "prabhat08", new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if(user != null){
                                    Log.i("LogIn","Successfull");
                                }else{
                                    Log.i("LogIn", "Failed, Error: "+e.toString());
                                }
                            }
                        });
                    }*/

                      /*  Toast.makeText(UserDetailsActivity.this,
                                "OnClickListener : " +
                                        "\nfName : " + fName.getText() +
                                        "\nlName : " + lName.getText() +
                                        "\nemail : " + eMail.getText() +
                                        "\nMobile Number :" + mobileNumber.getText() +
                                        "\nBlood Group : " + bgroup.getSelectedItem() +
                                        "\nDOB : " + eText.getText() +
                                        "\nGender : " + gender +
                                        "\nGeoPoint : " + parseGeoPoint +
                                        "\nLocation :" + locationPicker.getText(),
                                Toast.LENGTH_SHORT).show();
*/
                            //Data goes to Server now if all condition is true
                           ParseObject object = new ParseObject("UserDetails");
                            object.put("FirstName", fName.getText().toString());
                            object.put("LastName", lName.getText().toString());
                            String fullName = fName.getText()+" "+lName.getText();
                            object.put("FullName",fullName);
                            object.put("Email", eMail.getText().toString());
                            object.put("MobileNumber", mobileNumber.getText().toString());
                            object.put("BloodGroup", bgroup.getSelectedItem());
                            object.put("DOB", eText.getText().toString());
                            object.put("Gender", gender);
                            object.put("GeoPointLocation", parseGeoPoint);
                            object.put("Location", locationPicker.getText().toString());

                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        Toast.makeText(UserDetailsActivity.this, "Your Details has been successfully saved.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(UserDetailsActivity.this, "Oops!Something went wrong.", Toast.LENGTH_SHORT).show();
                                        Log.i("SaveInBackground", e.getMessage());
                                    }
                                }
                            });



                        }
/*
                    if(fName.getText() == null || lName.getText()==null || eMail.getText()==null || bgroup.getSelectedItem() == null
                            || eText.getText()==null || eText.getText()==null || gender==null || locationPicker.getText()==null) {

                        Toast.makeText(this, "Please,fill the Required Option. ", Toast.LENGTH_SHORT).show();
                    }else{
                        ParseUser user = new ParseUser();
                        user.put("FirstName", fName.getText().toString());
                        user.put("LastName", lName.getText().toString());
                        user.put("Email", eMail.getText().toString());
                        user.put("BloodGroup", bgroup.getSelectedItem().toString());
                        user.put("DOB", eText.getText().toString());
                        user.put("Gender", gender);
                        user.put("Location", locationPicker.getText().toString());
                    }*/
                }

            });


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



/*
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String userid=user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        usersRef.orderByChild("userid").equalTo(userid).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    Toast.makeText(UserDetailsActivity.this, datas.getKey(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(UserDetailsActivity.this, "It can't be fetched .", Toast.LENGTH_SHORT).show();

            }
        });
*/

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
       // Log.i("Selected Date",eText.getText().toString());
    }
    public void logIn(){
        ParseUser.logInInBackground(mobileNumber.getText().toString(),mobileNumber.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.i("LogIn", "Successful");
                } else {
                    Log.i("LogIn", "Failed");
                }
            }
        });
    }


}