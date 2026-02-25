package com.example.mytest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class friend_list_adapter extends RecyclerView.Adapter<friend_list_adapter.ViewHolder> {
    Context context;
    private static ArrayList<profile_info> fdata = new ArrayList<>();


    public friend_list_adapter(ArrayList<profile_info> request_data, Context context){
        this.fdata = request_data;
        this.context = context;
    }

    @NonNull
    @Override
    public friend_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.frenid_list_icon, parent, false);
        return new friend_list_adapter.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull friend_list_adapter.ViewHolder holder, int position) {
        profile_info profile_data  = fdata.get(position);
        holder.setItem(profile_data);

    }

    public void addItem(profile_info item){
        fdata.add(item);
    }
    public profile_info getItem(int position) {
        return fdata.get(position);
    }

    public static ArrayList<profile_info> getData() {
        return fdata;
    }

    public void setItem(ArrayList<profile_info> data) {
        this.fdata = data;
    }

    @Override
    public int getItemCount() {
        return fdata.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        TextView name;
        ImageView proflie_image;
        LinearLayout linearLayout;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            email = itemView.findViewById(R.id.f_l_i_e);
            name = itemView.findViewById(R.id.f_l_i_n);
            proflie_image = itemView.findViewById(R.id.f_l_i_p);
            /*친구 삭제 꾹 눌렀을때 */
            linearLayout = itemView.findViewById(R.id.f_l_i_l);
            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(!fdata.get(position).email.equals(currentUser.getEmail())) {
                        showAlertDialog();
                    }
                    return true;
                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, friend_list_chat_r.class);
                        intent.putExtra("name", fdata.get(position).name);
                        intent.putExtra("email", fdata.get(position).email);
                        intent.putExtra("profile", fdata.get(position).profile);
                        context.startActivity(intent);
                    }
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

        private void showAlertDialog() {
            // AlertDialog.Builder 인스턴스 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // 대화상자의 제목과 메시지 설정
            AlertDialog builder1 = builder.setTitle(R.string.delete_friend_title)
                    .setMessage(R.string.delete_friend_message)
                    // "확인" 버튼 및 클릭 이벤트 설정
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();

                            if (position != RecyclerView.NO_POSITION) {
                                Log.d("sdf", "onClick: " + fdata.get(position).email);
                                // 내 친구 목록에서 친구 정보 제거
                                db.collection("users").document(currentUser.getEmail()).collection("친구목록").document(fdata.get(position).email)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                // 친구 -> 친구 목록에서 내 정보 삭제
                                db.collection("users").document(fdata.get(position).email).collection("친구목록").document(currentUser.getEmail())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }
                    }).show();
        }

    }
}
