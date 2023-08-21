package com.example.blooddonarfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseUser;

public class UserLogInActivity extends AppCompatActivity {

    private EditText editTextMobile;
    Button buttonContinue;

    public void continueProcess(View view){
        String mobile = editTextMobile.getText().toString().trim();
        if(mobile.isEmpty() || mobile.length() < 10){
            editTextMobile.setError("Enter a valid mobile");
            editTextMobile.requestFocus();
            return;
        }
        Intent intent = new Intent(getApplicationContext(),VerifyPhoneActivity.class);
        intent.putExtra("mobile",mobile);

        startActivity(intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_in);

        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);

    }
}
/*firebase.auth().signOut().then(function() {
  // Sign-out successful.
}).catch(function(error) {
  // An error happened.
});*/