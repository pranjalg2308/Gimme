package com.kalabhedia.gimme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class newUserActivity extends AppCompatActivity {

    EditText mUsername;
    Button mSubmit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        mUsername = (EditText) findViewById(R.id.newUserName);
        mSubmit = (Button) findViewById(R.id.submit_username_button);



        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUsername.getText().toString().trim() != null) {
                    Log.v("Username", mUsername.getText().toString());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mUsername.getText().toString())
                            .build();
                    FirebaseUser currentUser;
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();
                    assert currentUser != null;
                    currentUser.updateProfile(profileUpdates);
                    startActivity(new Intent(newUserActivity.this, MainActivity.class));
                    finish();
                } else
                    Toast.makeText(newUserActivity.this, "Username can not be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

