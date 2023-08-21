package com.example.blooddonarfinder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.parse.Parse.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YourRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourRequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Date date;
    String reportDate;

    int day = 0;
    int hh = 0;
    int mm = 0;
    int ss = 0;


    public YourRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YourRequestFragment newInstance(String param1, String param2) {
        YourRequestFragment fragment = new YourRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final MyCustomAdapter arrayAdapter;
       // final Button cancelButton = (Button) getView().findViewById(R.id.btn);
        // Inflate the layout for this fragment
       final View view = inflater.inflate(R.layout.fragment_your_request, container, false);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        FloatingActionButton floatingActionRefreshButton = view.findViewById(R.id.floatingActionRefreshButton);
        Button previousButton = view.findViewById(R.id.previousButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        floatingActionRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(YourRequestFragment.this).attach(YourRequestFragment.this).commit();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),YourPreviousRequestActivity.class);
                startActivity(intent);
            }
        });


        final ListView listView = view.findViewById(R.id.listView);
       final ArrayList<String> arrayList = new ArrayList<String>();

        Date cDate = new Date();
        int days = 7 ;
        int time = ( days*24*3600 * 1000);
        Date expirationDate = new Date(cDate.getTime() - (time));

        arrayList.clear();
        ParseQuery<ParseObject> query = new ParseQuery <ParseObject>("RequstBlood");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereGreaterThanOrEqualTo("createdAt",expirationDate);
         arrayAdapter = new MyCustomAdapter(arrayList,getActivity().getApplicationContext());
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
                            String reqCode = (String) object.get("RequestCode");
                            arrayList.add("Requested at:"+reportDate+"\n"+"before :"+countTime(date)+"\n\n\nYou have requested to "+bgroup+"\nblood group of "+bQuantity+"\nfor the purpose of "+purpose+"\n"+" at "+address+"\n"+" location."+"\n\n"+"Request Code: "+reqCode);

                        }

                    }else{
                        arrayList.add("You have no any request.");
                      //  cancelButton.setVisibility(View.GONE);
                    }
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

       return  view;
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