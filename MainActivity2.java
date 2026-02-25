package com.example.mytest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.Manifest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity{

    SearchView searchView;
    Street_view streetView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    BottomNavigationView bottomNavigationView;

    FloatingActionButton myFab;


    private static final int REQUEST_CALL_PHONE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        streetView = new Street_view();

        FloatingActionButton myFab = findViewById(R.id.main_floating_add_btn);
        Drawable icon = getResources().getDrawable(R.drawable.exit);
        icon.setTint(Color.WHITE);
        myFab.setImageDrawable(icon);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.tou_container, streetView).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.street_view_fragement) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.tou_container, streetView).commit();
                    return true;
                    }
                else if (item.getItemId() == R.id.find_street_fragement) {
                    Intent intent = new Intent(getApplicationContext(), findStreet.class);
                    startActivity(intent);
                    return true;

                }
                else if(item.getItemId() == R.id.message_fragement){
                    Intent intent = new Intent(getApplicationContext(), friend_activity.class);
                    startActivity(intent);
                    return true;
                }
                else if (item.getItemId() == R.id.set_up_fragement) {
                    Intent intent = new Intent(getApplicationContext(), set_activity.class);
                    startActivity(intent);
                    return true;
                }
                else if (item.getItemId() == R.id.main_floating_add_btn) {
                    makePhoneCall();
                    return true;
                }
                return false;
            }
        });

    }
    private void makePhoneCall() {
        String number = "112";
        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, R.string.phone_permission_needed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
