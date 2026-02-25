package com.example.mytest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/* 친구
    메시지 message , 전화 번호 phonenumber, 이름 name, 이메일 e-mail, 현재 위치 host_location
    friend_list 친구 목록, 친구 요청 리스트 friend_request_list,
 */
public class introActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DatabaseReference cDatabase;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<LatLng> coordinateList = new ArrayList<>();
    private List<LatLng> cctvList = new ArrayList<>();
    NaverMap naverMap;

    private List<Marker> markers_bell = new ArrayList<>();
    private List<Marker> markers_cctv = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser(); // 파이어 베이스 정보 로그인

     if (currentUser != null) { // 거리뷰
            mDatabase = FirebaseDatabase.getInstance().getReference();
            cDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(currentUser.getUid()).child("email").setValue(currentUser.getEmail());
            mDatabase.child("users").child(currentUser.getUid()).child("name").setValue(currentUser.getDisplayName());
            mDatabase.child("users").child(currentUser.getUid()).child("PhotoUrl").setValue(String.valueOf(currentUser.getPhotoUrl()));

            mDatabase.child("safebell").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if (!task.isSuccessful()) {
                        mapdialog();
                        Log.e("firebase", "실패", task.getException());
                    } else {
                        Log.d("firebase", "성공");
                        int i = 0;
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String WGS84_H = (String) snapshot.child("WGS84_H").getValue();
                            String WGS84_W = (String) snapshot.child("WGS84_W").getValue();
                            if (WGS84_W != null && WGS84_H != null) {
                                double lo = Double.parseDouble(WGS84_H);
                                double la = Double.parseDouble(WGS84_W);

                                coordinateList.add(new LatLng(la, lo));
                                Log.d("localgps", "lo = " + coordinateList.get(i).latitude + " " + coordinateList.get(i).longitude);
                                i++;
                            }
                        }
                        FirebaseDataSingleton.getInstance().setFirebellbaseData(coordinateList);

                        cDatabase.child("safecctv").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                if (!task.isSuccessful()) {
                                    mapdialog();
                                    Log.e("firebase", "실패", task.getException());
                                } else {
                                    Log.d("firebase", "성공");
                                    int i = 0;
                                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                        String longitude = (String) snapshot.child("longitude").getValue();
                                        String latitude = (String) snapshot.child("latitude").getValue();
                                        if (longitude != null && latitude != null) {
                                            double lo = Double.parseDouble(longitude);
                                            double la = Double.parseDouble(latitude);

                                            cctvList.add(new LatLng(la, lo));
                                            Log.d("cctv", "lo = " + cctvList.get(i).latitude + " " + cctvList.get(i).longitude);
                                            i++;
                                        }
                                    }
                                    FirebaseDataSingleton.getInstance().setFirecctvbaseData(cctvList);

                                    int is= 1;
                                    for (LatLng coordinate : coordinateList) {

                                        // Display the marker
                                        Marker newMarker = new Marker();
                                        newMarker.setPosition(coordinate);

                                        newMarker.setIcon(OverlayImage.fromResource(R.drawable.bell));
                                        newMarker.setWidth(80);
                                        newMarker.setHeight(80);

                                        markers_bell.add(newMarker);
                                        Log.d("sdfsd" , String.valueOf(newMarker));
                                    }

                                    // Iterate over coordinateList to find markers within the update radius
                                    for (LatLng coordinate : cctvList) {

                                        // Display the marker
                                        Marker newMarker = new Marker();
                                        newMarker.setPosition(coordinate);
                                        newMarker.setIcon(OverlayImage.fromResource(R.drawable.s));
                                        newMarker.setWidth(80);
                                        newMarker.setHeight(80);
                                        markers_cctv.add(newMarker);

                                    }
                                    // 이 coordinateList의 값을 전달해주는 방법을 찾아야함.
                                    FirebaseDataSingleton.getInstance().setFirebellbasemarkerData(markers_bell);
                                    FirebaseDataSingleton.getInstance().setFirecctvbasemarkerData(markers_cctv);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class); // 거리 뷰
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    }
                }
            });
        }else {
            Intent intent = new Intent(this, MainActivity.class); // 거리 뷰
            startActivity(intent);
            finish();
        }

    }

    private void mapdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 대화상자의 제목과 메시지 설정
        builder.setTitle("")
                .setMessage(R.string.failed_to_load_info)
                .setMessage(R.string.check_internet_try_again)
                // "확인" 버튼 및 클릭 이벤트 설정
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "확인" 버튼이 클릭될 때 실행되는 동작
                    }
                })
                .show(); // 대화상자 표시
    }

}






