package com.example.blooddonarfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YourPreviousRequestActivity extends AppCompatActivity {

    Date date;
    String reportDate;

    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_previous_request);

        setTitle("Previous Request");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final MyPreviousCustomAdapter arrayAdapter;
        // Inflate the layout for this fragment
        final ListView listView = findViewById(R.id.listView);
        final ArrayList<String> arrayList = new ArrayList<String>();
        Date cDate = new Date();
        int days = 7 ;
        int time = ( days*24*3600 * 1000);
        Date expirationDate = new Date(cDate.getTime() - (time));

        arrayList.clear();
        ParseQuery<ParseObject> query = new ParseQuery <ParseObject>("RequstBlood");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereLessThanOrEqualTo("createdAt",expirationDate);
        arrayAdapter = new MyPreviousCustomAdapter(arrayList,YourPreviousRequestActivity.this);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object:objects){
                            date = object.getCreatedAt();
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            reportDate = df.format(date);
                            String bgroup = (String) object.get("BloodGroup");
                            String bQuantity = (String) object.get("BloodQuantity");
                            String purpose = (String) object.get("Description");
                            String address = (String) object.get("Location");
                            arrayList.add("Requested at:"+reportDate+"\n"+"before :"+countTime(date)+"\n\n\nYou have requested to "+bgroup+"\nblood group of "+bQuantity+"\nfor the purpose of "+purpose+"\n"+" at "+address+"\n"+" location.");

                        }

                    }else{
                        arrayList.add("You have no any Previous request.");
                        //  cancelButton.setVisibility(View.GONE);
                    }
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public String countTime(Date date1){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatted = dateFormat.format(date1);
            Date oldDate = dateFormat.parse(formatted);
            Date cDate = new Date();
            // int days = 7;
            int time = ( 1800 * 1000);
            Date expirationDate = new Date(cDate.getTime() + (time));

            Toast.makeText(getApplicationContext(),expirationDate.toString(), Toast.LENGTH_SHORT).show();
            Long timeDiff = cDate.getTime() - oldDate.getTime();
            day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
            hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day));
            mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
            ss = (int) (TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)));

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if(ss<=60 && mm!= 0) {
            if (mm <= 60 && hh != 0) {
                if (hh <= 60 && day != 0) {
                    return day + " DAYS AGO";
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
}