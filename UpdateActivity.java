package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {


    ArrayList<update_list> update_list = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    update_adapter update_adapter = new update_adapter(update_list);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateactivity_layout);
        RecyclerView updata_list = findViewById(R.id.updata_list);



        db = FirebaseFirestore.getInstance();


        updata_list.setLayoutManager(new LinearLayoutManager(this));
        updata_list.setAdapter(update_adapter);

        // update 컬렉션 참조
        CollectionReference updateCollection = db.collection("update");

        // 데이터 가져오기
        updateCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String version = document.getString("버전");
                        String content = document.getString("주요 변경 사항");
                        Log.d("Sdgfsdg", version);
                        update_lists(version, content);
                        update_adapter.notifyDataSetChanged();
                    }
                } else {

                }
            }
        });
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle(R.string.update_info_title); // 툴바 제목 설정

    }

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

    private void update_lists(String vs, String text) {
        update_list item = new update_list();

        item.setVs(vs);
        item.setText(text);

        update_list.add(item);

    }
}
