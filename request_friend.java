package com.example.mytest;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class request_friend extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_request_friend, container, false);

        // 툴바 생성
        Toolbar toolbar = rootview.findViewById(R.id.toolbar);

        return rootview;
    }
}