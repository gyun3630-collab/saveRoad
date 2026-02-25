package com.example.mytest;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class profile_info_adapter extends RecyclerView.Adapter<profile_info_adapter.ViewHolder>{
    private static ArrayList<profile_info> data;
    Context context;
    public profile_info_adapter(ArrayList<profile_info> profile_info){
        this.data = profile_info;
    }
    public profile_info_adapter(ArrayList<profile_info> profile_info, Context context){
        this.data = profile_info;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.frenid_icon, parent, false);
        return new ViewHolder(itemView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        profile_info profile_data  = data.get(position);
        holder.setItem(profile_data);


    }
    public void addItem(profile_info item){
        data.add(item);
    }
    public profile_info getItem(int position) {
        return data.get(position);
    }

    public static ArrayList<profile_info> getData() {
        return data;
    }

    public void setItem(ArrayList<profile_info> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getItemViewType(int position) {
        return position;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView email;
        TextView name;
        ImageView proflie_image;
        ImageView btn;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            email = itemView.findViewById(R.id.textView10);
            name = itemView.findViewById(R.id.textView11);
            proflie_image = itemView.findViewById(R.id.imageView6);
            btn = itemView.findViewById(R.id.plusimg);



            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
                }
            });


        }

        public void setItem(profile_info item){
            email.setText(item.getEmail());
            name.setText(item.getName());
            String imageUrl = item.getProfile();


            Picasso.get()
                    .load(imageUrl)
                    .transform(new circle())
                    .into(proflie_image);

        }

        private void showAlertDialog() {
            // AlertDialog.Builder 인스턴스 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // 대화상자의 제목과 메시지 설정
            AlertDialog builder1 = builder.setTitle(R.string.friend_request_title)
                    .setMessage(R.string.friend_request_message)
                    // "확인" 버튼 및 클릭 이벤트 설정
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                DocumentReference requestCollectionRef = db.collection("users").document(currentUser.getEmail()).collection("친구목록").document(data.get(position).email);
                                DocumentReference washingtonRef = db.collection("users").document(data.get(position).email).collection("친구요청목록").document(currentUser.getEmail());
                                requestCollectionRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        AlertDialog builder1 = builder
                                                .setMessage( data.get(position).email+ R.string.already_friends)
                                                // "확인" 버튼 및 클릭 이벤트 설정
                                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).show();
                                    }else {
                                        washingtonRef.set(
                                                        new HashMap<String, Object>() {{
                                                            put("request_email", currentUser.getEmail());
                                                            put("request_name", currentUser.getDisplayName());
                                                            put("request_photoUrl", currentUser.getPhotoUrl());
                                                        }})
                                                .addOnSuccessListener(aVoid -> {
                                                    AlertDialog builder1 = builder
                                                            .setMessage(data.get(position).email + R.string.friend_request_sent)
                                                            // "확인" 버튼 및 클릭 이벤트 설정
                                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            }).show();


                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle failure

                                                });
                                    }
                                });
                            }
                        }

                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            }


        }
    }

