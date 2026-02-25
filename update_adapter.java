package com.example.mytest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class update_adapter extends  RecyclerView.Adapter<update_adapter.ViewHolder> {

    private static ArrayList<update_list> f_c_datas = new ArrayList<>();

    update_adapter(ArrayList<update_list> update_list){
        this.f_c_datas = update_list;

    }

    @NonNull
    @Override
    public update_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.update_icon, parent, false);
        return new update_adapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull update_adapter.ViewHolder holder, int position) {
        update_list profile_data  = f_c_datas.get(position);
        holder.setItem(profile_data);
    }
    public void addItem(update_list item){
        f_c_datas.add(item);
    }
    public update_list getItem(int position) {

        return f_c_datas.get(position);
    }

    public static ArrayList<update_list> getData() {

        return f_c_datas;
    }

    public void setItem(ArrayList<update_list> data) {
        this.f_c_datas = data;
    }

    @Override
    public int getItemCount() {
        return f_c_datas.size();
    }

    public int getItemViewType(int position) {
        return position;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vs;
        TextView text;
        public ViewHolder(@NonNull View itemView ){
            super(itemView);
            vs = itemView.findViewById(R.id.textView1s);
            text = itemView.findViewById(R.id.textView2s);
        }

        public void setItem(update_list item) {
            vs.setText(item.getVs());
            String itemText = item.getText();

            // 하이픈을 줄 바꿈 문자로 대체
            String modifiedText = itemText.replace("-", "\n");
            text.setText(modifiedText);
        }
    }
}
