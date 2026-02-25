package com.example.mytest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class friend_list_chat_adapter extends  RecyclerView.Adapter<friend_list_chat_adapter.ViewHolder> {

    private static ArrayList<friend_list_chat> f_c_data = new ArrayList<>();
    private String currentUserEmail;
    friend_list_chat_adapter(ArrayList<friend_list_chat> friend_list_chat, String currentUserEmail){
        this.f_c_data = friend_list_chat;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public friend_list_chat_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.friend_list_chat_r_icon, parent, false);
        return new friend_list_chat_adapter.ViewHolder(itemView, currentUserEmail);
    }

    @Override
    public void onBindViewHolder(@NonNull friend_list_chat_adapter.ViewHolder holder, int position) {
        friend_list_chat profile_data  = f_c_data.get(position);
        holder.setItem(profile_data);
    }
    public void addItem(friend_list_chat item){
        f_c_data.add(item);
    }
    public friend_list_chat getItem(int position) {

        return f_c_data.get(position);
    }

    public static ArrayList<friend_list_chat> getData() {

        return f_c_data;
    }

    public void setItem(ArrayList<friend_list_chat> data) {
        this.f_c_data = data;
    }

    @Override
    public int getItemCount() {
        return f_c_data.size();
    }

    public int getItemViewType(int position) {
        return position;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView f_l_i_d;
        TextView f_l_i_n;
        ImageView f_l_i_p;
        TextView f_l_i_r;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int count = 0;
        int sum = 2;
        public ViewHolder(@NonNull View itemView, String cuemail) {
            super(itemView);
            f_l_i_p = itemView.findViewById(R.id.f_l_i_p);
            f_l_i_n = itemView.findViewById(R.id.f_l_i_n);
            text = itemView.findViewById(R.id.textView9);
            f_l_i_d = itemView.findViewById(R.id.f_l_i_d);

        }

        public void setItem(friend_list_chat item) {
            f_l_i_d.setText(item.getDate());
            f_l_i_n.setText(item.getName());
            text.setText(item.getText());


            String imageUrl = item.getProfileurl();
            Picasso.get()
                    .load(imageUrl)
                    .transform(new circle())
                    .into(f_l_i_p);

            count = 0;
        }
    }
}
