package com.example.mytest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


/*
 * 사용자가 메시지를 읽었는지에 대한 정보를 필요로 함.
 * count를 사용해서 1을 넣고 만약 메시지창을 눌렀다면 해당 count가 1이라면 0으로 싹 변환
 * 백그라운드 부분... 아... 어지럽네.. 버전 충돌남;;; 확인 불가;
 * 어?
 *
 *
 */
public class My_service_chat_alarm extends Service {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Thread mThread;
    private int mCount = 0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        Intent notificationIntent = new Intent(this, friend_list_chat.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        // 오레오에서는 알림 채널을 매니저에 생성해야 한다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        db.collection("users")
                .document(currentUser.getEmail())
                .collection("친구목록")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String friendEmail = document.getId();
                        db.collection("users")
                                .document(currentUser.getEmail())
                                .collection("친구목록")
                                .document(friendEmail)
                                .collection("chat")
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        return;
                                    }

                                    if (value != null) {
                                        for (DocumentChange dc : value.getDocumentChanges()) {
                                            // 변경된 채팅 데이터 처리
                                            String message = dc.getDocument().getString("text");
                                            String name = dc.getDocument().getString("name");
                                            String email = dc.getDocument().getString("email");
                                            builder.setSmallIcon(R.mipmap.ic_launcher);
                                            builder.setContentTitle(name + " / " + email);
                                            builder.setContentText(message);

                                            builder.setContentIntent(pendingIntent);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                // Android 12 (API 레벨 31) 이상에서는 Foreground 서비스 유형을 지정해야 함
                                                int type = ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST;
                                                startForeground(1, builder.build(), type);
                                            } else {
                                                startForeground(1, builder.build());
                                            }
                                        }
                                    }
                                });
                    }
                });

        return START_STICKY;
    }
    @Override
    public void onDestroy() {


        // stopService 에 의해 호출 됨
        // 스레드를 정지시킴
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    // 바인드된 컴포넌트에 카운팅 변수 값을 제공
    public int getCount() {
        return mCount;
    }


}