package com.example.mytest;

import com.google.firebase.auth.FirebaseAuth;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataSingleton {

    private static FirebaseDataSingleton instance; // 인스턴스 변수
    private List<LatLng> coordinateList = new ArrayList<>(); // 파이어베이스 데이터
    private List<LatLng> cctvList = new ArrayList<>();

    private List<Marker> coordinateListmarker = new ArrayList<>(); // 파이어베이스 데이터
    private List<Marker> cctvListmarker  = new ArrayList<>();


    // 다른 클래스에서의 인스턴스화를 방지하기 위한 private 생성자
    private FirebaseDataSingleton() {

    }

    // 싱글톤 인스턴스를 가져오기 위한 정적 메서드
    public static synchronized FirebaseDataSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseDataSingleton();
        }
        return instance;
    }

    // 파이어베이스 데이터 설정 메서드
    public void setFirebellbaseData(List<LatLng> data) {
        this.coordinateList = data;
    }
    public void setFirecctvbaseData(List<LatLng> data) {
        this.cctvList = data;
    }
    // 파이어베이스 데이터 가져오기 메서드
    public List<LatLng> getFirebellbaseData() {
        return coordinateList;
    }

    public List<LatLng> getFirecctvbaseData() {
        return cctvList;
    }

    public void setFirebellbasemarkerData(List<Marker> data) {
        this.coordinateListmarker = data;
    }
    public void setFirecctvbasemarkerData(List<Marker> data) {
        this.cctvListmarker = data;
    }
    // 파이어베이스 데이터 가져오기 메서드
    public List<Marker> getFirebellbasemarkerData() {
        return coordinateListmarker;
    }

    public List<Marker> getFirecctvbasemarkerData() {
        return cctvListmarker;
    }


}
