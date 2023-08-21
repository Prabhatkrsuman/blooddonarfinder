package com.example.blooddonarfinder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.jsibbold.zoomage.ZoomageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewImageActivity extends AppCompatActivity {
    ZoomageView imageView;
    TextView profileName;
    TextView dateTime;
    public static Bitmap IMAGE_BITMAP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        getSupportActionBar().hide();

        dateTime = findViewById(R.id.dateTimeImage);
        dateTime.setText(getIntent().getStringExtra("date"));

        profileName = findViewById(R.id.profileName);
        profileName.setText(getIntent().getStringExtra("FullName"));
        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        if(bytes!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(bmp);

            //imageView.setImageBitmap(IMAGE_BITMAP);
        }
    }
}
