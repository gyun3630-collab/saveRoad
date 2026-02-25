package com.example.mytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class friend_list_chat_r extends AppCompatActivity {

    private static final String TAG = "friend_list_chat_r";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();
    EditText editText;

    ImageButton imageButton;

    ArrayList<friend_list_chat> friend_list_chat = new ArrayList<>();
    friend_list_chat_adapter friend_list_chat_adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Timestamp timestamp = Timestamp.now();


    // UUID를 사용하여 고유한 채팅 문서 ID 생성
    String chatDocumentId = UUID.randomUUID().toString();

    String name;
    String email;
    String profile;

    Date currentDate = new Date();

    String formattedDate;
    SimpleDateFormat dateFormat;

    /*읽음과 읽지 않은에 대해서 작성하면 됨...
    * 스레드를 여기서 돌려 버리면?
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_chat_r);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

// 현재 시간 가져오기
        Date currentDate = new Date();

// SimpleDateFormat을 사용하여 형식 지정 및 한국 표준시간대 설정
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);

// 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 변환
        formattedDate = dateFormat.format(currentDate);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        editText = findViewById(R.id.editTextText);
        imageButton = findViewById(R.id.imageButton);

        Intent intent = getIntent();
        name = intent.getStringExtra("name"); // "name"이라는 이름으로 전달된 String 데이터 추출
        email = intent.getStringExtra("email"); // "email"이라는 이름으로 전달된 String 데이터 추출
        profile = intent.getStringExtra("profile");
        friend_list_chat_adapter = new friend_list_chat_adapter(friend_list_chat, email);
        Log.d(TAG, "onCreate: " + name + email);

        // 스레드를 사용하여 시간을 체크하고 업데이트하는 작업을 수행
        Thread updateTimeThread = new Thread(new timeRun());

        // 스레드 시작
        updateTimeThread.start();

        db.collection("users")
                .document(currentUser.getEmail())
                .collection("친구목록")
                .document(email)
                .collection("chat")
                .orderBy("date")// "cities" 컬렉션을 선택합니다.
                .addSnapshotListener(new EventListener<QuerySnapshot>() { // 실시간 리스너를 추가합니다.
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) { // 에러가 발생한 경우
                            Log.w(TAG, "listen:error", e); // 에러 로그를 출력합니다.
                            return; // 함수 실행을 종료합니다.
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) { // 문서 변경 사항들을 반복합니다.
                            switch (dc.getType()) { // 변경 타입을 확인합니다.
                                case ADDED: // 문서가 추가된 경우
                                    String documentId = dc.getDocument().getId(); //
                                    String date = dc.getDocument().getString("date");
                                    String name = dc.getDocument().getString("name");
                                    String photoUrl = dc.getDocument().getString("photoUrl");
                                    String text = dc.getDocument().getString("text");
                                    String email = dc.getDocument().getString("email");
                                    f_l_c_addItem(name, date, text, photoUrl, email);


                                    friend_list_chat_adapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:

                                    break;
                                case REMOVED: // 문서가 삭제된 경우
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData()); // 삭제된 문서의 데이터를 로그로 출력합니다.
                                    break;
                            }
                        }

                    }
                });


        RecyclerView list_chat = findViewById(R.id.chat);
        list_chat.setLayoutManager(new LinearLayoutManager(this));
        list_chat.setAdapter(friend_list_chat_adapter);


        Toolbar toolbar = findViewById(R.id.f_t);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle(name); // 툴바 제목 설정


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                editText.setText("");
                db.collection("users").document(currentUser.getEmail()).collection("친구목록").document(email).collection("chat").document(chatDocumentId)
                .set(
                     new HashMap<String, Object>() {{
                         put("email", currentUser.getEmail());
                         put("name", currentUser.getDisplayName());
                         put("photoUrl", currentUser.getPhotoUrl());
                         put("date", formattedDate);
                         put("text",userInput );
                                }})
                        .addOnSuccessListener(aVoid -> {


                        })
                        .addOnFailureListener(e -> {
                            // Handle failure

                        });
                db.collection("users").document(email).collection("친구목록").document(currentUser.getEmail()).collection("chat").document(chatDocumentId)
                        .set(
                                new HashMap<String, Object>() {{
                                    put("email", currentUser.getEmail());
                                    put("name", currentUser.getDisplayName());
                                    put("photoUrl", currentUser.getPhotoUrl());
                                    put("date", formattedDate);
                                    put("text",userInput);

                                }})
                        .addOnSuccessListener(aVoid -> {


                        })
                        .addOnFailureListener(e -> {
                            // Handle failure

                        });
                chatDocumentId = UUID.randomUUID().toString();

            }
        });



    }
public void check(){

    db.collection("users")
            .document(email)
            .collection("친구목록")
            .document(currentUser.getEmail())
            .collection("chat")
            .orderBy("date")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {

                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot document : snapshots.getDocuments()) {

                            String currentUserEmail = currentUser.getEmail();
                            Map<String, Object> readUpdate = new HashMap<>();
                            readUpdate.put(currentUserEmail, true); // Add the current user's email to the 'read' collection

                            document.getReference().collection("read")
                             .document(currentUserEmail)
                                    .set(readUpdate)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Read status successfully updated!"))
                                    .addOnFailureListener(ed -> Log.w(TAG, "Error updating read status", ed));

                        }
                    } else {

                    }
                }
            });
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back 키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            //default:
            //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }



    private void f_l_c_addItem(String name, String date, String text, String Profile, String email) {
        friend_list_chat item = new friend_list_chat();

        item.setName(name);
        item.setDate(date);
        item.setProfileurl(Profile);
        item.setText(text);

        item.setEmail(email);
        friend_list_chat.add(item);
    }

    private class timeRun implements Runnable{

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 현재 시간 가져오기
                    Date currentDate = new Date();

                    // 한국 표준시간대 설정
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                    // 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 변환
                    formattedDate = dateFormat.format(currentDate);

                    // 1분마다 스레드 실행
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 스레드 인터럽트 시 예외 처리
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}