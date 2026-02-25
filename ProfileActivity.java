package com.example.mytest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private EditText editTextNickname, editTextStatus;
    TextView editTextEmail;
    private Button buttonSave, buttonChangeProfile;
    private ImageView imageViewProfile;
    private Bitmap defaultProfileBitmap; // 기본 프로필 이미지를 저장하는 변수

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity_layout);

        // 툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile_title);


        // XML 레이아웃에서 뷰들을 찾아 변수에 바인딩합니다.
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextStatus = findViewById(R.id.editTextStatus);
        buttonSave = findViewById(R.id.buttonSave);
        buttonChangeProfile = findViewById(R.id.buttonChangeProfile);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextEmail = findViewById(R.id.editTextEmail);


        editTextNickname.setText(currentUser.getDisplayName());

        editTextEmail.setText(currentUser.getEmail());


        Picasso.get()
                .load(currentUser.getPhotoUrl())
                .transform(new circle())
                .into(imageViewProfile);
        // 기본 프로필 이미지를 설정합니다.
        defaultProfileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);

        // 저장 버튼 클릭 리스너 설정
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로필 변경 내용 저장 메서드 호출
                saveProfileChanges();
            }
        });

        // 프로필 사진 변경 버튼 클릭 리스너 설정
        buttonChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리에서 사진을 선택하는 Intent를 실행합니다.
                dispatchTakePictureIntent();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back 키 눌렀을 때 동작
                // 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), set_activity.class);
                startActivity(intent);
                return true;
            //default:
            //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    // 프로필 변경 내용 저장 메서드
    private void saveProfileChanges() {
        // 수정된 닉네임과 상태 메시지를 가져옵니다.
        String nickname = editTextNickname.getText().toString();
        String status = editTextStatus.getText().toString();

        // 변경된 정보를 저장하는 로직을 구현하세요.
        // 예시로 변경된 정보를 출력하는 것으로 대체합니다.
        System.out.println(R.string.nickname + nickname);
        System.out.println(R.string.status_message + status);
    }

    // 갤러리에서 이미지를 가져오는 Intent를 실행하는 메서드
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // 갤러리에서 이미지를 선택한 후 결과를 처리하는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewProfile.setImageBitmap(imageBitmap);
        }
    }
}



