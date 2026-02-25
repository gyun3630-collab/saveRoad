package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class set_activity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingactivity_layout);


        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle(R.string.settings_title);


        //클릭시 해당 페이지로 넘어가
        LinearLayout profileLayout = findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        LinearLayout withdrawal = findViewById(R.id.withLayout);
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWithdrawalActivity();
            }
        });

        LinearLayout reportlayout = findViewById(R.id.reportLayout);
        reportlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReportActivity();
            }
        });

        LinearLayout languageLayout = findViewById(R.id.languageLayout);
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageActivity();
            }
        });

        LinearLayout updateLayout = findViewById(R.id.updateLayout);
        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateActivity();
            }
        });

        LinearLayout soundLayout = findViewById(R.id.soundLayout);
        soundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSoundActivity();
            }
        });



    }

    // 프로필 수정 화면으로 이동하는 메서드
    private void openProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
    private void openWithdrawalActivity() {
        Intent intent = new Intent(getApplicationContext(), WithdrawalActivity.class);
        startActivity(intent);
    }

    private void openReportActivity() {
        Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
        startActivity(intent);
    }
    private void openLanguageActivity() {
        Intent intent = new Intent(getApplicationContext(), LanguageActivity.class);
        startActivity(intent);
    }

    private void openUpdateActivity() {
        Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        startActivity(intent);
    }

    private void openSoundActivity() {
        Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
        startActivity(intent);
    }

    private void openInformLayoutActivity() {
        Intent intent = new Intent(getApplicationContext(), InformActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back 키 눌렀을 때 동작
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
                return true;
            //default:
            //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}