package com.example.mytest;





import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import static com.google.gson.internal.$Gson$Types.arrayOf;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;


import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.map.overlay.OverlayImage;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import ted.gun0912.clustering.clustering.TedClusterItem;
import ted.gun0912.clustering.naver.TedNaverClustering;
import ted.gun0912.clustering.naver.TedNaverMarker;


public class Street_view extends Fragment implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    double seoulSouth = 37.428017; // 남쪽 경계
    double seoulNorth = 37.701749; // 북쪽 경계
    double seoulWest = 126.764582; // 서쪽 경계
    double seoulEast = 127.183868; // 동쪽 경계
    private static final String TAG = "Street_view";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SearchView searchView;
    private static final int PERMISSION_REQUEST_CODE = 250;
    int prevZoom;
    private NaverMap naverMap;
    private List<LatLng> coordinateList = new ArrayList<>();
    private boolean isCameraMoved = false;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private Context mContext;
    private List<LatLng> previousMarkers = new ArrayList<>();
    private List<Marker> markers_bell = new ArrayList<>();
    private List<Marker> markers_cctv = new ArrayList<>();
    private static final double UPDATE_RADIUS = 300;
    private LatLng previousLocation;

    /*RecyclerView 프로필 목록 필요한 변수 선언*/
    ArrayList<profile_info> list = new ArrayList<>();
    profile_info_adapter profileInfoAdapter;


    /*RecyclerView 친구 수락 목록 필요한 변수 선언*/
    ArrayList<profile_info> request_list = new ArrayList<>();
    request_friend_adapter request_friend_adapter;


    /*RecyclerView 친구 목록 필요한 변수 선언*/
    ArrayList<profile_info> friend_list = new ArrayList<>();
    friend_list_adapter friend_list_adapter;

    private List<LatLng> cctvList = new ArrayList<>();

    RecyclerView flist;
    RecyclerView request;
    View header;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main2_puls, container, false);



        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragments);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragments, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);



        profileInfoAdapter = new profile_info_adapter(list, getContext());
        request_friend_adapter = new request_friend_adapter(request_list, getContext());
        friend_list_adapter = new friend_list_adapter(friend_list, getContext());
        mAuth = FirebaseAuth.getInstance();



        markers_bell = FirebaseDataSingleton.getInstance().getFirebellbasemarkerData();
        markers_cctv = FirebaseDataSingleton.getInstance().getFirecctvbasemarkerData();

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }



        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mContext = getContext();



        Button logoutButton = (Button) header.findViewById(R.id.login_out);
        RecyclerView profile_recyc =  header.findViewById(R.id.profileRecyclerView);
        profile_recyc.setLayoutManager(new LinearLayoutManager(requireContext()));
        profile_recyc.setAdapter(profileInfoAdapter);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        if(currentUser != null){
            TextView textView =  header.findViewById(R.id.email);
            textView.setText(currentUser.getEmail());
            TextView nametext =  header.findViewById(R.id.name);
            nametext.setText(currentUser.getDisplayName());
        }


        Toolbar toolbar = rootView.findViewById(R.id.toolbarf);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the drawer when the menu icon is clicked
                openDrawer();
            }
        });

        TextView text = rootView.findViewById(R.id.search_hint);


        searchView = rootView.findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);
        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                new AddressToCoordinatesConverter().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    text.setVisibility(View.VISIBLE);
                } else {
                    text.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });



        Button gps = rootView.findViewById(R.id.gpslocal);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 위치로 카메라 이동
                if (naverMap != null) {

                    // 현재 위치를 확인합니다.
                    LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                    if (locationOverlay != null) {
                        LatLng currentLocation = locationOverlay.getPosition();
                        if (currentLocation != null) {
                            // 카메라를 현재 위치로 이동합니다.
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(currentLocation);
                            naverMap.moveCamera(cameraUpdate);
                            CameraUpdate zoomUpdate = CameraUpdate.zoomTo(30);
                            naverMap.moveCamera(zoomUpdate);
                        } else {
                            // 현재 위치를 찾을 수 없는 경우 처리합니다.
                            Toast.makeText(getContext(), R.string.toast_location_not_found, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 위치 정보를 사용할 수 없는 경우 처리합니다.
                        Toast.makeText(getContext(), R.string.location_unavailable, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 지도가 아직 준비되지 않은 경우 처리합니다.
                    Toast.makeText(getContext(), R.string.map_not_ready, Toast.LENGTH_SHORT).show();
                }
            }
        });


        /* 검색창을 눌렀을 때의 나오게 되는 recyclerview*/
        profile_recyc.setVisibility(View.GONE);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {
                    Log.e("firebase", "실패", task.getException());
                } else {
                    Log.d("firebase", "성공");
                    int i = 0;
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        if (! currentUser.getEmail().equals(snapshot.child("email").getValue())) {
                        String email  = (String) snapshot.child("email").getValue();
                        String name  = (String) snapshot.child("name").getValue();
                        String photoUrl  = (String) snapshot.child("PhotoUrl").getValue();
                            addItem(name, email, photoUrl);
                        }

                        profileInfoAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        SearchView searchfriend = header.findViewById(R.id.searchView2);

        searchfriend.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile_recyc.getVisibility() == View.GONE) {
                    profile_recyc.setVisibility(View.GONE);
                } else {
                    profile_recyc.setVisibility(View.GONE);
                }
            }
        });

        searchfriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                profile_recyc.setVisibility(View.VISIBLE);
                ArrayList<profile_info> filteredList = new ArrayList<>();

                for (profile_info item : list) {
                    // Adjust this condition based on your search criteria
                    if (item.getName().toLowerCase().contains(newText.toString().trim().toLowerCase()) ||
                            item.getEmail().toLowerCase().contains(newText.toString().trim().toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                profileInfoAdapter.setItem(filteredList);
                profileInfoAdapter.notifyDataSetChanged();
                return false;
            }
        });

        /* 친구 요청 recyclerview 친구 요청을 보낸 목록
        * collection는 문서들의 그룹이며,
        * document는 데이터를 포함하는 개별 단위입니다
        *
        * */

        ImageView exclamation = header.findViewById(R.id.exclamation);


        CollectionReference requestCollectionRef = db.collection("users").document(currentUser.getEmail()).collection("친구요청목록");

        requestCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() { // 실시간 리스너를 추가합니다.
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) { // 에러가 발생한 경우
                    Log.w(TAG, "listen:error", e); // 에러 로그를 출력합니다.
                    return; // 함수 실행을 종료합니다.
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) { // 문서 변경 사항들을 반복합니다.
                    switch (dc.getType()) { // 변경 타입을 확인합니다.
                        case ADDED:
                        case MODIFIED:


                            String requestEmail = dc.getDocument().getString("request_email");
                            String requestName = dc.getDocument().getString("request_name");
                            String  requestPhotoUrl = dc.getDocument().getString("request_photoUrl");
                            re_addItem(requestName, requestEmail, requestPhotoUrl);


                            request_friend_adapter.notifyDataSetChanged();
                            if(request_list != null){
                                exclamation.setVisibility(View.VISIBLE);
                            }
                            break;
                        case REMOVED:
                            String removedEmail = dc.getDocument().getString("request_email");
                            // Remove the corresponding item from the list
                            for (int i = 0; i < request_list.size(); i++) {
                                if (request_list.get(i).getEmail().equals(removedEmail)) {
                                    request_list.remove(i);
                                    // Notify the adapter of the removal
                                    request_friend_adapter.notifyItemRemoved(i);
                                    break; // Exit loop once item is removed
                                }
                            }
                            if(request_list == null){
                                exclamation.setVisibility(View.GONE);
                            }
                            break;
                    }
                }

            }
        });







        db.collection("users").document(currentUser.getEmail()).collection("친구목록").addSnapshotListener(new EventListener<QuerySnapshot>() { // 실시간 리스너를 추가합니다.
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) { // 에러가 발생한 경우
                    Log.w(TAG, "listen:error", e); // 에러 로그를 출력합니다.
                    return; // 함수 실행을 종료합니다.
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) { // 문서 변경 사항들을 반복합니다.
                    switch (dc.getType()) { // 변경 타입을 확인합니다.
                        case ADDED:
                        case MODIFIED:
                            String Email = dc.getDocument().getString("email");
                            String Name = dc.getDocument().getString("name");
                            String PhotoUrl = dc.getDocument().getString("photoUrl");

                            fl_addItem(Name, Email, PhotoUrl);
                            friend_list_adapter.notifyDataSetChanged();
                       // 문서가 수정된 경우
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

        Button requset_friend = (Button) header.findViewById(R.id.request);
        requset_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requset_friend();
            }
        });


        Button friend_list_btn = (Button) header.findViewById(R.id.friend_list);
        friend_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_list();
            }
        });







        return rootView;
    }

    private void friend_list() {
        flist = header.findViewById(R.id.request_cycler);
        flist.setLayoutManager(new LinearLayoutManager(requireContext()));
        flist.setAdapter(friend_list_adapter);

    }


    public void requset_friend() {
        request = header.findViewById(R.id.request_cycler);
        request.setLayoutManager(new LinearLayoutManager(requireContext()));
        request.setAdapter(request_friend_adapter);

    }

    // Method to open the drawer
    private void openDrawer() {
        // Get the activity and its DrawerLayout
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

        // Open the drawer
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /* gps 검사하는 문장임 이건 삭제하던가.. 추후 조치 필요*/


    private void updateDrawerHeader() {
        // Update drawer header if needed
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.addOnCameraChangeListener((reason, animated) -> {
            int zoom = (int) naverMap.getCameraPosition().zoom;
            handleZoomChange(zoom);
        });

        if(markers_cctv != null){

            List<JavaItem> customMarkerList = new ArrayList<>();

            for (Marker coordinate : markers_cctv) {
                LatLng position = coordinate.getPosition();
                JavaItem customMarker = new JavaItem(position);
                customMarkerList.add(customMarker);
            }

            TedNaverClustering.with(getContext(), naverMap)
                    .customMarker(new Function1<TedClusterItem, Marker>() {
                        @Override
                        public Marker invoke(TedClusterItem tedClusterItem) {
                            Marker marker = new Marker();
                            marker.setIcon(OverlayImage.fromResource(R.drawable.s));
                            marker.setWidth(60);
                            marker.setHeight(86);
                            return marker;
                        }
                    })
                    .items(customMarkerList)
                    .make();
        }
    }

    private void handleZoomChange(int zoom) {

        if (prevZoom == -1) {
            prevZoom = zoom;
            return;
        }

        if (zoom == 18 && prevZoom == 17) {
            makeFixedMarker();
        } else if (zoom == 17 && prevZoom == 18) {
            removeFixedMarkers();
        }

        prevZoom = zoom;
    }

    private void makeFixedMarker() {
        if(markers_bell != null){
            for (Marker coordinate : markers_bell) {
                coordinate.setMap(naverMap);
            }
        }
    }

    private void removeFixedMarkers() {
        for (Marker marker : markers_bell) {
            marker.setMap(null); // 마커를 맵에서 제거
        }

    }
    private void getLocation() {


        LocationManager locationManager = getLocationManager();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (!isCameraMoved) {
                    // Set the flag to true to indicate camera movement
                    if (latitude >= seoulSouth && latitude <= seoulNorth &&
                            longitude >= seoulWest && longitude <= seoulEast) {
                        // Move the camera to the current location
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
                        naverMap.moveCamera(cameraUpdate);
                    } else {
                        // Move the camera to the coordinates of Seoul
                        LatLng seoulCenter = new LatLng(37.5665, 126.9780);
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(seoulCenter);
                        naverMap.moveCamera(cameraUpdate);
                    }
                    isCameraMoved = true;
                }
                LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                locationOverlay.setVisible(true);
                Log.d("localgps Latitude " , latitude + " " + longitude);
                locationOverlay.setPosition(new LatLng(latitude, longitude));
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        // 위치 업데이트 요청
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없으면 요청합니다.
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, locationListener);
            //1초 거리 10이 자나거나 변경되면 위치정보를 초기화 함
        }
    }






    private LocationManager getLocationManager() {
        return (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Tlqkf");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
                // 권한 요청
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                }
        }
    }


    private void showAlertDialog() {
        // AlertDialog.Builder 인스턴스 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // 대화상자의 제목과 메시지 설정
        builder.setTitle(R.string.alert_title)
                .setMessage(R.string.alert_message)
                // "확인" 버튼 및 클릭 이벤트 설정
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "확인" 버튼이 클릭될 때 실행되는 동작
                    }
                })

                .show(); // 대화상자 표시
    }


    private void moveCameraToCoordinates(double latitude, double longitude) {
        if (naverMap != null) {
            // Move the camera to the specified coordinates
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(longitude, latitude));
            naverMap.moveCamera(cameraUpdate);
            Marker marker = new Marker();
            marker.setPosition(new LatLng(longitude, latitude));
            marker.setMap(naverMap);

            // Adjust zoom level without changing camera position
            CameraUpdate zoomUpdate = CameraUpdate.zoomTo(20);
            naverMap.moveCamera(zoomUpdate);
        } else {
            Log.e("Street_view", "NaverMap is null");
        }
    }




    public class AddressToCoordinatesConverter extends AsyncTask<String, Void, LatLng> {

        // 일반 검색을 통해서 도로명 주소를 가져오는 url 네이버 api이다.
        private static final String NAVER_MAPS_GEOCODING_API_URL_LOAD = "https://openapi.naver.com/v1/search/local.json";


        //geocoding으로 반환된 도로명 주소의 값을 좌표값으로 변환시켜 주는 api이다 (네이버 클라우드 위치)
        private static final String NAVER_MAPS_GEOCODING_API_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

        // 여기 밑은 비밀번호와 아이디 라고 생각하면 편하다.
        private static final String X_NAVER_CLIENT_ID = "KVrulXMCr7uPfrrIs8Rg";
        private static final String X_NAVER_CLIENT_SECRET = "mmzxnGn4Tk";
        private static final String NAVER_CLIENT_ID = "y0hp7us7tv";
        private static final String NAVER_CLIENT_SECRET = "Szahn24YwL7o1ZxSpRUeCSBMJPMmqhFBUSV4r1JW";

        @Override
        protected LatLng doInBackground(String... strings) {
            // 검색어를 입력 받았는데 그 검색어의 길이가 0이라면 반환
            // 아니라면 실행시켜주면 됨.
            if (strings.length == 0) return null;
            String address = strings[0];

            try {
                // 네이버 로컬 검색 API 호출
                URL url = new URL(NAVER_MAPS_GEOCODING_API_URL_LOAD + "?query=" + address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Naver-Client-Id", X_NAVER_CLIENT_ID);
                connection.setRequestProperty("X-Naver-Client-Secret", X_NAVER_CLIENT_SECRET);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();

                    // JSON 응답 처리
                    JSONObject responseJson = new JSONObject(stringBuilder.toString());
                    JSONArray items = responseJson.getJSONArray("items");
                    if (items.length() > 0) {
                        JSONObject firstItem = items.getJSONObject(0);
                        String roadAddress = firstItem.getString("roadAddress");
                        Log.d("Coordinates", roadAddress);

                        // 도로명 주소를 이용하여 네이버 지도 Geocoding API 호출
                        url = new URL(NAVER_MAPS_GEOCODING_API_URL + "?query=" + roadAddress);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_CLIENT_ID);
                        connection.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_CLIENT_SECRET);

                        responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getInputStream();
                            reader = new BufferedReader(new InputStreamReader(inputStream));
                            stringBuilder = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            inputStream.close();

                            // JSON 응답 처리
                            responseJson = new JSONObject(stringBuilder.toString());
                            JSONArray addresses = responseJson.getJSONArray("addresses");
                            if (addresses.length() > 0) {
                                JSONObject firstAddress = addresses.getJSONObject(0);
                                double latitude = firstAddress.getDouble("x");
                                double longitude = firstAddress.getDouble("y");
                                return new LatLng(latitude, longitude);
                            }
                        }
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    // 대화상자의 제목과 메시지 설정
                    builder.setTitle("")
                            .setMessage(R.string.search_failure_message)
                            .setMessage(R.string.search_failure_message2)
                            // "확인" 버튼 및 클릭 이벤트 설정
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // "확인" 버튼이 클릭될 때 실행되는 동작
                                }
                            })
                            .show(); // 대화상자 표시
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            if (latLng != null) {
                moveCameraToCoordinates(latLng.latitude, latLng.longitude);
            }

        }
    }

    private void addItem(String name, String email, String Profile) {
        profile_info item = new profile_info();

        item.setName(name);
        item.setEmail(email);
        item.setProfile(Profile);


        list.add(item);
    }

    private void re_addItem(String name, String email, String Profile) {
        profile_info item = new profile_info();

        item.setName(name);
        item.setEmail(email);
        item.setProfile(Profile);


        request_list.add(item);
    }

    private void fl_addItem(String name, String email, String Profile) {
        profile_info item = new profile_info();

        item.setName(name);
        item.setEmail(email);
        item.setProfile(Profile);


        friend_list.add(item);
    }

}