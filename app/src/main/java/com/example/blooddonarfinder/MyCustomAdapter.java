package com.example.blooddonarfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
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

import static com.parse.Parse.getApplicationContext;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter, View.OnClickListener {
    private ArrayList<String> arrayList;
    private Context context;
    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;
    Date date;



    public MyCustomAdapter(ArrayList<String> list, Context context) {
        super();
        this.arrayList = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    // getView method is called for each item of ListView
    public View getView(final int position, View view, final ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_layout, parent, false);



        // get the reference of textView and button
        final TextView textView = (TextView) view.findViewById(R.id.textView);
        final Button cancelButton = (Button) view.findViewById(R.id.btn);
        final Button viewButton = (Button) view.findViewById(R.id.viewBtn);

        // Set the title and button name
        textView.setText(arrayList.get(position));
        if(textView.getText() == "You have no any request."){
            cancelButton.setVisibility(View.GONE);
            viewButton.setVisibility(View.GONE);
        }else{
            cancelButton.setVisibility(View.VISIBLE);
            viewButton.setVisibility(View.VISIBLE);
        }

            ParseQuery<ParseObject> queryAuto = new ParseQuery<>("RequstBlood");
            queryAuto.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
       // Toast.makeText(context,ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
            queryAuto.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {

                            for(ParseObject object: objects){

                                date = object.getCreatedAt();
                                countTime(date);
                               // Toast.makeText(context, date.toString(), Toast.LENGTH_SHORT).show();
                             }
                               
                        }
                    }
                }
            });
        // Click listener of button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());

                // set title
                alertDialogBuilder.setTitle("Are you sure?");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);

                // set dialog message
                alertDialogBuilder
                        .setMessage("you want to cancel your request.")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RequstBlood");
                                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                                // query.whereDoesNotExist("Cancelled");
                                //query.whereExists("Cancelled");
                                query.orderByDescending("createdAt");
                                //query.whereEqualTo("BloodGroup", "O+");
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if(e == null ){
                                            if(objects.size()>0){
                                                // for(ParseObject object: objects){
                                                //   object.put("Cancelled","cancel");
                                                objects.get(position).deleteInBackground();
                                                Toast.makeText(getApplicationContext(), "Your request has been successfully cancelled.", Toast.LENGTH_SHORT).show();

                                                // }
                                            }
                                        }
                                    }
                                });
                                // Logic goes here
                                arrayList.remove(position);
                                notifyDataSetChanged();


                            }
                        })
                        .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        viewButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
                Intent intent = new Intent(context, RequestMapViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
    }

    public String countTime(Date date1){

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

