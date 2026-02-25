package com.example.mytest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class request_friend_adapter extends RecyclerView.Adapter<request_friend_adapter.ViewHolder> {
    Context context;
    private static ArrayList<profile_info> request_data = new ArrayList<>();


    public request_friend_adapter(ArrayList<profile_info> request_data,  Context context){
        this.request_data = request_data;
        this.context = context;
    }

    @NonNull
    @Override
    public request_friend_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.frenid_request_icon, parent, false);
        return new request_friend_adapter.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull request_friend_adapter.ViewHolder holder, int position) {
        profile_info profile_data  = request_data.get(position);
        holder.setItem(profile_data);

    }

    public void addItem(profile_info item){
        request_data.add(item);
    }
    public profile_info getItem(int position) {
        return request_data.get(position);
    }

    public static ArrayList<profile_info> getData() {
        return request_data;
    }

    public void setItem(ArrayList<profile_info> data) {
        this.request_data = data;
    }

    @Override
    public int getItemCount() {
        return request_data.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        TextView name;
        ImageView proflie_image;

        ImageButton close;
        ImageButton check;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            email = itemView.findViewById(R.id.textView10);
            name = itemView.findViewById(R.id.textView11);
            proflie_image = itemView.findViewById(R.id.imageView6);
            check = itemView.findViewById(R.id.imageButton5);
            close = itemView.findViewById(R.id.imageButton6);

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    showAlertDialog(position);
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // 대화상자의 제목과 메시지 설정
                    AlertDialog builder1 = builder.setTitle(R.string.friend_request_title)
                            .setMessage(R.string.delete_friend_message)
                            // "확인" 버튼 및 클릭 이벤트 설정
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = getAdapterPosition();
                                    db.collection("users").document(currentUser.getEmail()).collection("친구요청목록").document(request_data.get(position).email)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            }).show();
                         }
                      });
        }

        public void setItem(profile_info item) {
            email.setText(item.getEmail());
            name.setText(item.getName());
            String imageUrl = item.getProfile();
            Picasso.get()
                    .load(imageUrl)
                    .transform(new circle())
                    .into(proflie_image);

        }


        private void showAlertDialog(int position) {
            // AlertDialog.Builder 인스턴스 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // 대화상자의 제목과 메시지 설정
            AlertDialog builder1 = builder.setTitle(R.string.friend_request_title)
                    .setMessage(R.string.confirm_friend_request)
                    // "확인" 버튼 및 클릭 이벤트 설정
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (position != RecyclerView.NO_POSITION) {
                                DocumentReference washingtonRef = db.collection("users").document(currentUser.getEmail()).collection("친구목록").document(request_data.get(position).email);
                                washingtonRef.set(
                                                new HashMap<String, Object>() {{
                                                    put("email", request_data.get(position).email);
                                                    put("name", request_data.get(position).name);
                                                    put("photoUrl", request_data.get(position).profile);
                                                }})
                                        .addOnSuccessListener(aVoid -> {
                                            AlertDialog builder1 = builder
                                                    .setMessage( request_data.get(position).email+ R.string.became_friends_with)
                                                    // "확인" 버튼 및 클릭 이벤트 설정
                                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            db.collection("users").document(currentUser.getEmail()).collection("친구요청목록").document(request_data.get(position).email)
                                                                    .delete()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });
                                                        }
                                                    }).show();



                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure

                                        });
                                DocumentReference setfriend = db.collection("users").document(request_data.get(position).email).collection("친구목록").document(currentUser.getEmail());
                                setfriend.set(
                                        new HashMap<String, Object>() {{
                                            put("email", currentUser.getEmail());
                                            put("name", currentUser.getDisplayName());
                                            put("photoUrl", currentUser.getPhotoUrl());
                                        }}).addOnSuccessListener(aVoid -> {

                                }).addOnFailureListener(e ->{

                                });

                            }
                        }

                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }
    }
}
