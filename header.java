package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class header extends AppCompatActivity {

    Button button;

    private FirebaseAuth user;
// ...
// Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        user = FirebaseAuth.getInstance();

        button = findViewById(R.id.login_out);

        Log.d( "dsf",user.getCurrentUser().toString());
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if( user.getCurrentUser() != null){
                    user.signOut();
                    Intent intent = new Intent(header.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}