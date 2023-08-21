package com.example.blooddonarfinder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyPreviousCustomAdapter extends BaseAdapter implements ListAdapter, View.OnClickListener {
    private ArrayList<String> arrayList;
    private Context context;
    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;
    Date date;


    public MyPreviousCustomAdapter(ArrayList<String> list, Context context) {
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

    public View getView(final int position, View view, final ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_previous_layout, parent, false);


        // get the reference of textView and button
        final TextView textView = (TextView) view.findViewById(R.id.textView);

        textView.setText(arrayList.get(position));

        ParseQuery<ParseObject> queryAuto = new ParseQuery<>("RequstBlood");
        queryAuto.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        // Toast.makeText(context,ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
        queryAuto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

                            date = object.getCreatedAt();
                            countTime(date);
                            // Toast.makeText(context, date.toString(), Toast.LENGTH_SHORT).show();
                            ArrayList<Integer> pozition = null;

                            if (mm <= 30) {
                                try {
                                    //  objects.get(position).deleteInBackground();
                                    Toast.makeText(context, "Your request has been expired... :" + mm, Toast.LENGTH_SHORT).show();

                                } catch (ArrayIndexOutOfBoundsException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        }

                    }
                }
            }
        });

        return view;
    }

        @Override
        public void onClick (View v){
            Intent intent = new Intent(context, RequestMapViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public String countTime (Date date1){

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
            if (ss <= 60 && mm != 0) {
                if (mm <= 60 && hh != 0) {
                    if (hh <= 60 && day != 0) {
                        return day + " DAYS AGO";
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
    }


