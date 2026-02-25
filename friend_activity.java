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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class friend_activity extends AppCompatActivity {

    ArrayList<profile_info> friend_list = new ArrayList<>();
    friend_list_adapter friend_list_adapter;
    private static final String TAG = "friend_activity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friend_list_adapter = new friend_list_adapter(friend_list, this);
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle(R.string.friend_list); // 툴바 제목 설정

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        RecyclerView flist = findViewById(R.id.f_l);
        flist.setLayoutManager(new LinearLayoutManager(this));
        flist.setAdapter(friend_list_adapter);

        db.collection("users").document(currentUser.getEmail()).collection("친구목록").addSnapshotListener(new EventListener<QuerySnapshot>() { // 실시간 리스너를 추가합니다.
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) { // 에러가 발생한 경우
                    Log.w(TAG, "listen:error", e); // 에러 로그를 출력합니다.
                    return; // 함수 실행을 종료합니다.
                }
                String Email;
                String Name;
                String PhotoUrl;
                for (DocumentChange dc : snapshots.getDocumentChanges()) { // 문서 변경 사항들을 반복합니다.
                    switch (dc.getType()) { // 변경 타입을 확인합니다.
                        case ADDED:
                        case MODIFIED:
                             Email = dc.getDocument().getString("email");
                             Name = dc.getDocument().getString("name");
                             PhotoUrl = dc.getDocument().getString("photoUrl");
                            fl_addItem(Name, Email, PhotoUrl);
                            friend_list_adapter.notifyDataSetChanged();
                            break;

                        case REMOVED:
                            String removedEmail = dc.getDocument().getString("email");
                            // Remove the corresponding item from the list
                            for (int i = 0; i < friend_list.size(); i++) {
                                if (friend_list.get(i).getEmail().equals(removedEmail)) {
                                    friend_list.remove(i);
                                    // Notify the adapter of the removal
                                    friend_list_adapter.notifyItemRemoved(i);
                                    break; // Exit loop once item is removed
                                }
                            }
                            break;
                        // 문서가 삭제된 경우
                    }
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

    private void fl_addItem(String name, String email, String Profile) {
        profile_info item = new profile_info();

        item.setName(name);
        item.setEmail(email);
        item.setProfile(Profile);


        friend_list.add(item);
    }

}

