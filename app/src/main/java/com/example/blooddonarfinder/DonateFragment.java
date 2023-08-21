package com.example.blooddonarfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DonateFragment extends Fragment {
    private Spinner bgroup;
    Button donate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        bgroup = (Spinner) view.findViewById(R.id.blood_group_drop_down);
        donate = (Button) view.findViewById(R.id.donate_blood);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addListenerOnSpinnerItemSelection();
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((bgroup.getSelectedItem().toString()).equals("Select Blood Group")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please!,Select Blood Group.\nThis is essential for me.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), ViewRequestActivity.class);
                    intent.putExtra("BloodGroup",bgroup.getSelectedItem().toString());
                    startActivity(intent);
                }
            }
        });

    }
    public void addListenerOnSpinnerItemSelection() {
       // bgroup = (Spinner) getView().findViewById(R.id.blood_group_drop_down);
        bgroup.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
}
