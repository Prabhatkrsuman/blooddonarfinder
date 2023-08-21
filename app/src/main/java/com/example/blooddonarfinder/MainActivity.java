package com.example.blooddonarfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final Long SPLASH_DELAY = 2500L;
    private Handler mDelayHandler = new Handler();
    private ImageView imageView;
    String mob;

    private Runnable runnable = new Runnable() {
        Intent intent;
        @Override
        public void run() {

            if (!isFinishing()) {

               if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    intent = new Intent(MainActivity.this, UserLogInActivity.class);
                   finish();
                   overridePendingTransition(R.anim.enter, R.anim.exit);
                   startActivity(intent);
               } else {
                   if (ParseUser.getCurrentUser() == null) {
                       mob = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                       ParseUser.logInInBackground(mob, mob, new LogInCallback() {
                           @Override
                           public void done(ParseUser user, ParseException e) {
                               if(user==null){
                                   intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                                   finish();
                                   overridePendingTransition(R.anim.enter, R.anim.exit);
                                   startActivity(intent);
                                  // Toast.makeText(MainActivity.this, "parseUser null", Toast.LENGTH_SHORT).show();
                               }else{
                                   intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                   finish();
                                   overridePendingTransition(R.anim.enter, R.anim.exit);
                                   startActivity(intent);
                                   //Toast.makeText(MainActivity.this, "parseUser null", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });

                   } else {

                                  intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                  finish();
                                  overridePendingTransition(R.anim.enter, R.anim.exit);
                                  startActivity(intent);


                       /*if ((Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), ParseUser.getCurrentUser().getUsername()))) {


                       } else {

                       }*/
                   }
               }
            }

            //startActivity(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initViews();

        mDelayHandler.postDelayed(runnable, SPLASH_DELAY);

        startAnimation();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

    }

    private void startAnimation() {

        imageView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(imageView, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(imageView, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(imageView, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(1500);
                        mAnimatorSet.start();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelayHandler.removeCallbacks(runnable);
    }
}


