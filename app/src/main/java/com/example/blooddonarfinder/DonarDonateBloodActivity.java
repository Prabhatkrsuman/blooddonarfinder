package com.example.blooddonarfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TimeUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonarfinder.fragment.MapsDonarDetailsActivity;
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
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.parse.Parse.getApplicationContext;

public class DonarDonateBloodActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Intent intent;

    LatLng donarLocation;
    LatLng requestLocation;
    String bGroup;
    String fullName;
    String geoPoint;
    String requesterLocation;
    String bQuantity;
    String mob;
    Bitmap bitmap;
    BitmapDescriptor smallMarkerIcon;
    View dialogView;
    private String m_Text = "";
    private String a_Text = "";
    String verifyCode;
    String objectId;
    String reqObjectId;
    EditText acceptCodeEditText;
    Button button;
    Button cancelButton;
    Date createdDate;
    String reportDate;
    int month = 0;
    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;
    String fullNameReq;
    String currentUserId;

    public void acceptRequest(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DonarDonateBloodActivity.this);

        // set title
        alertDialogBuilder.setTitle("Are you sure?");
        alertDialogBuilder.setIcon(R.drawable.ic_blood_drop_24dp);
        // set dialog message
        alertDialogBuilder
                .setMessage("you want to serve your country, society and needy people with selflessly.")
                .setCancelable(false)
                .setPositiveButton("YES,I will.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        button.setVisibility(View.INVISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);


                        ParseQuery<ParseObject> query = ParseQuery.getQuery("RequstBlood");

                        query.whereEqualTo("username", intent.getStringExtra("username"));
                        query.whereEqualTo("BloodGroup",bGroup);
                        query.whereEqualTo("objectId",objectId);

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                                if (e == null) {

                                    if (objects.size() > 0) {
                                        verifyCode = objects.get(0).getString("RequestCode");
                                         String checkExists = objects.get(0).getString("driverUsername");
                                         if(checkExists==null) {
                                             for (ParseObject object : objects) {

                                                 object.put("driverUsername", ParseUser.getCurrentUser().getUsername());

                                                 object.saveInBackground(new SaveCallback() {
                                                     @Override
                                                     public void done(ParseException e) {

                                                         if (e == null) {

                                                             Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
                                                                     Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("donarLatitude", 0) + "," + intent.getDoubleExtra("donarLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
                                                             startActivity(directionsIntent);

                                                         }

                                                     }
                                                 });

                                                 final AlertDialog.Builder builder = new AlertDialog.Builder(DonarDonateBloodActivity.this);
                                                 builder.setTitle("Verify code");
                                                 builder.setMessage("Take code from Requester and verify.");
                                                 builder.setIcon(R.drawable.ic_baseline_lock_24);

// Set up the input
                                                 final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                                 input.setInputType(InputType.TYPE_CLASS_TEXT);
                                                 input.setHint("Enter Requested code");
                                                 input.setPadding(120, 0, 100, 20);
                                                 builder.setView(input);

// Set up the buttons
                                                 builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         m_Text = input.getText().toString();
                                                         if (m_Text.equals(verifyCode)) {
                                                             ParseObject object1 = new ParseObject("DonateBlood");
                                                             object1.put("username",ParseUser.getCurrentUser().getUsername());
                                                             object1.put("DonationCount","Donate");
                                                             object1.put("fullname",fullNameReq);
                                                             object1.saveInBackground(new SaveCallback() {
                                                                 @Override
                                                                 public void done(ParseException e) {
                                                                     Toast.makeText(DonarDonateBloodActivity.this, "code Verified, Blood Donation confirmed.", Toast.LENGTH_SHORT).show();
                                                                     acceptCodeEditText.setText("");
                                                                 }
                                                             });
                                                         } else {
                                                             Toast.makeText(DonarDonateBloodActivity.this, "Code does'nt matched, try again!", Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
                                                 builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         dialog.cancel();
                                                         acceptCodeEditText.setVisibility(View.VISIBLE);
                                                         //cancelButton.setVisibility(View.VISIBLE);
                                                     }
                                                 });

                                                 builder.show();

                                             }
                                         }else{
                                             Toast.makeText(DonarDonateBloodActivity.this, "you have already accepted request,please help!", Toast.LENGTH_SHORT).show();
                                             acceptCodeEditText.setVisibility(View.VISIBLE);
                                             cancelButton.setVisibility(View.VISIBLE);
                                             button.setVisibility(View.INVISIBLE);
                                             Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
                                                     Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("donarLatitude", 0) + "," + intent.getDoubleExtra("donarLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
                                             startActivity(directionsIntent);

                                             acceptCodeEditText.setOnTouchListener(new View.OnTouchListener() {

                                                 @Override
                                                 public boolean onTouch(View v, MotionEvent event) {
                                                     final int DRAWABLE_LEFT = 0;
                                                     final int DRAWABLE_TOP = 1;
                                                     final int DRAWABLE_RIGHT = 2;
                                                     final int DRAWABLE_BOTTOM = 3;

                                                     if(event.getAction() == MotionEvent.ACTION_UP) {
                                                         if(event.getRawX() >= (acceptCodeEditText.getRight() - acceptCodeEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                                             if(acceptCodeEditText.getText().toString().equals(verifyCode)){

                                                                 ParseObject object1 = new ParseObject("DonateBlood");
                                                                 object1.put("username",ParseUser.getCurrentUser().getUsername());
                                                                 object1.put("DonationCount","Donate");
                                                                 object1.put("fullname",fullNameReq);
                                                                 object1.saveInBackground(new SaveCallback() {
                                                                     @Override
                                                                     public void done(ParseException e) {
                                                                         Toast.makeText(DonarDonateBloodActivity.this, "code Verified, Blood Donation confirmed.", Toast.LENGTH_SHORT).show();
                                                                         acceptCodeEditText.setText("");
                                                                     }
                                                                 });
                                                             } else {
                                                                 Toast.makeText(DonarDonateBloodActivity.this, "Code does'nt matched, try again!", Toast.LENGTH_SHORT).show();
                                                             }

                                                             return true;
                                                         }
                                                     }
                                                     return false;
                                                 }
                                             });


                                         }

                                    }



                                }

                            }
                        });


                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Donate once, you will feel wealthy.", Toast.LENGTH_SHORT).show();

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void cancelRequest(View view){

        final boolean deleteAttributesOnly = true;

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("RequstBlood");

        query.whereEqualTo("username", intent.getStringExtra("username"));
        query.whereEqualTo("BloodGroup",bGroup);
        query.whereEqualTo("objectId",objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        for(ParseObject object:objects){
                            // Retrieve the object by id
                            query.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
                                public void done(ParseObject entity, ParseException e) {
                                    if (e == null) {
                                        if (deleteAttributesOnly) {
                                            // If you want to undefine a specific field, do this:
                                            entity.remove("driverUsername");
                                            // Then save the changes
                                            entity.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Toast.makeText(DonarDonateBloodActivity.this, "Successfully removed!", Toast.LENGTH_SHORT).show();
                                                    cancelButton.setVisibility(View.INVISIBLE);
                                                    button.setVisibility(View.VISIBLE);
                                                    acceptCodeEditText.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        } else {
                                            // Otherwise, you can delete the entire ParseObject from the database
                                            entity.deleteInBackground();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_donate_blood);
        
        intent = getIntent();

        fullName = intent.getStringExtra("fullName");
        bGroup = intent.getStringExtra("bGroup");
        mob = intent.getStringExtra("mob");
        requesterLocation = intent.getStringExtra("location");
        geoPoint = intent.getStringExtra("requestLocation");
        objectId = intent.getStringExtra("objectId");



        //imageRetrived(fullName,mob);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ConstraintLayout mapLayout = (ConstraintLayout)findViewById(R.id.mapConstraintLayout);
        button = findViewById(R.id.acceptRequestButton);
        cancelButton = findViewById(R.id.cancelRequestButton);
        acceptCodeEditText = findViewById(R.id.acceptCodeEditText);
        acceptCodeEditText.setVisibility(View.INVISIBLE);

        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        donarLocation = new LatLng(intent.getDoubleExtra("donarLatitude", 0), intent.getDoubleExtra("donarLongitude", 0));

                        requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude", 0), intent.getDoubleExtra("requestLongitude", 0));

                       // button.setText(requestLocation + " " + driverLocation);

                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
                        //query1.whereEqualTo("FullName",fullName);
                        query1.whereEqualTo("MobileNumber",mob);


                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if(objects.size()>0){
                                        for(final ParseObject object:objects){
                                            ParseFile file = (ParseFile) object.get("image");
                                            reqObjectId = object.getObjectId();
                                            if(file!=null) {
                                                file.getDataInBackground(new GetDataCallback() {
                                                    @Override
                                                    public void done(byte[] data, ParseException e) {

                                                        if (e == null && data != null) {
                                                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                                        }
                                                    }
                                                });
                                                smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(DonarDonateBloodActivity.this, bitmap, "Blood Requester"));
                                            }else{
                                                smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(DonarDonateBloodActivity.this, R.drawable.profile_pic_change, "Blood Requester"));
                                            }

                                            ArrayList<Marker> markers = new ArrayList<>();

                                            //int height = 200;
                                            //int width = 200;
                                            // Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logopit);
                                            // Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);



                                            // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_blood_request_24dp);

                                            markers.add(googleMap.addMarker(new MarkerOptions().position(donarLocation).title("Your Location")));
                                            Marker requestMarker = googleMap.addMarker(new MarkerOptions().position(requestLocation).title("Request Location")
                                                    .snippet("Search for blood...")
                                                    .icon(smallMarkerIcon));
                                            markers.add(requestMarker);
                                            //  requestMarker.showInfoWindow();
                                            // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));



                                            //  MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location")
                                            // mMarker = googleMap.addMarker(markerOptions);

                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            for (Marker marker : markers) {
                                                builder.include(marker.getPosition());
                                            }
                                            LatLngBounds bounds = builder.build();

                                            Log.i("Bounds", String.valueOf(bounds));

                                            int padding = 300; // offset from edges of the map in pixels
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                            //mMap.moveCamera(cu);
                                            googleMap.animateCamera(cu);

                                            mMap.setMapStyle(
                                                    MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));


                                        }
                                    }
                                } else {
                                    Toast.makeText(DonarDonateBloodActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                        ParseQuery<ParseObject> parseQuery1 = ParseQuery.getQuery("UserDetails");
                        parseQuery1.whereEqualTo("MobileNumber",ParseUser.getCurrentUser().getUsername());
                        parseQuery1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null){
                                    if(objects.size()>0){
                                        fullNameReq = objects.get(0).getString("FullName");
                                        currentUserId = objects.get(0).getObjectId();
                                    }
                                }
                            }
                        });



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
                        parseQuery.whereGreaterThanOrEqualTo("createdAt",date);
                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null){
                                    if(objects.size()>0){
                                        createdDate = objects.get(0).getCreatedAt();
                                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        reportDate = df.format(createdDate);
                                    }
                                }
                            }
                        });

                    }

                        });

                    }
                });



    }

    private Bitmap createCustomMarker(Context context, int profile_pic_change, String blood_requester) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(profile_pic_change);
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(blood_requester);

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

    public String countTime(Date date1){
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

      /*  if (ss < 60) {
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


        if(ss<=60 && mm!= 0) {
            if (mm <= 60 && hh != 0) {
                if (hh <= 60 && day != 0) {
                    if (day<=30) {
                        return day + " DAYS AGO";
                    }else{
                        month = day/30;
                        return  month+ " MONTHS AGO";
                    }
                } else {
                    return hh + " HOUR AGO";
                }
            } else {
                return mm + " MIN AGO";
            }
        }else{
            return ss + "Seconds AGO";
        }
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

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public static Bitmap createCustomMarker(Context context, Bitmap resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        if(resource!=null) {
            markerImage.setImageBitmap(resource);
        }else{
            markerImage.setImageResource(R.drawable.logopitt);
        }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals("Request Location")) {
            showCustomDialog();
            Toast.makeText(getApplicationContext(), "Prabhat", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void showCustomDialog() {


        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog,viewGroup,false);


        TextView name = dialogView.findViewById(R.id.name);
        TextView blGroup = dialogView.findViewById(R.id.blood_requested);
        TextView rLocation = dialogView.findViewById(R.id.location);
        TextView pGeoPoint = dialogView.findViewById(R.id.geopoint);
        TextView donationDate = dialogView.findViewById(R.id.donationDate);
        ImageView profile = dialogView.findViewById(R.id.profile);

        ImageButton callButton = (ImageButton) dialogView.findViewById(R.id.buttonCall);
        ImageButton chatButton = (ImageButton) dialogView.findViewById(R.id.buttonChat);
        ImageButton aboutButton = (ImageButton) dialogView.findViewById(R.id.buttonAbout);

        name.setText(fullName);
         blGroup.setText("Blood Requested: "+bGroup);
         rLocation.setText("Location: "+requesterLocation);
         pGeoPoint.setText("GeoPoint: "+requestLocation);
         if(createdDate!=null){
             if(countTime(createdDate)!=null) {
                 donationDate.setText("Donate Blood at " + reportDate + " before :" + countTime(createdDate));
         }
         }else {
             donationDate.setVisibility(View.GONE);
         }
         if(bitmap!=null) {
             profile.setImageBitmap(bitmap);
         }else{
             profile.setImageResource(R.drawable.profile_pic_change);
         }

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


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("mob",mob);

                if(isPermissionGranted()){
                    call_action();
                }



                Toast.makeText(DonarDonateBloodActivity.this, "Call Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DonarDonateBloodActivity.this, "Chat Button Clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DonarDonateBloodActivity.this, ChatActivity.class);

                if (bitmap != null) {
                    Bitmap converetdImage = getResizedBitmap(bitmap, 750);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    intent.putExtra("bitmapbytes", bytes);
                    intent.putExtra("fullName",fullName);
                    intent.putExtra("reqObjId",reqObjectId);
                    intent.putExtra("currObjId",currentUserId);
                    Log.i("Bitmapcount", String.valueOf(bytes.length));
                    startActivity(intent);
                } else {
                    intent.putExtra("fullName",fullName);
                    intent.putExtra("reqObjId",reqObjectId);
                    intent.putExtra("currObjId",currentUserId);
                    startActivity(intent);
                }
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DonarDonateBloodActivity.this, "About Button Clicked", Toast.LENGTH_SHORT).show();
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.importphoto");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(DonarDonateBloodActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



   /* public String phoeNumberWithOutCountryCode(String phoneNumberWithCountryCode) {
        Pattern complie = Pattern.compile(" ");
        String[] phonenUmber = complie.split(phoneNumberWithCountryCode);
        Log.e("number is", phonenUmber[1]);
        return phonenUmber[1];
    }*/
}
