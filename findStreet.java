package com.example.mytest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.location.Location;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
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
import com.naver.maps.map.util.FusedLocationSource;
import java.util.List;


public class findStreet extends AppCompatActivity implements OnMapReadyCallback {


    SearchView fir_searchView;
    SearchView sec_searchView;
    LatLng[] searchResults = new LatLng[2];
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    int currentIndex = 0;
    private NaverMap naverMap;
    private LatLng previousLocation;
    private boolean isCameraMoved = false;
    private LocationManager locationManager;

    private List<LatLng> previousMarkers = new ArrayList<>();

    private static final double UPDATE_RADIUS = 300;

    private List<LatLng> cctvList = new ArrayList<>();

    private List<LatLng> coordinateList = new ArrayList<>();
    private FusedLocationSource locationSource;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    //private static final int REQUEST_LOCATION_PERMISSION = 1;
    List<LatLng> coords = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    List<PathOverlay> pathOverlays = new ArrayList<>();

    private List<Marker> markers_bell = new ArrayList<>();
    private List<Marker> markers_cctv = new ArrayList<>();

    Boolean checklist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_street);

        coordinateList = FirebaseDataSingleton.getInstance().getFirebellbaseData();
        cctvList = FirebaseDataSingleton.getInstance().getFirecctvbaseData();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_find_fragments);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_find_fragments, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }


        ImageButton btn = findViewById(R.id.fts);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchResults[0] != null && searchResults[1] != null){
                    checklist = true;
                    getLocation();
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
                            Toast.makeText(getApplicationContext(), R.string.toast_location_not_found, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 위치 정보를 사용할 수 없는 경우 처리합니다.
                        Toast.makeText(getApplicationContext(), R.string.location_unavailable, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 지도가 아직 준비되지 않은 경우 처리합니다.
                    Toast.makeText(getApplicationContext(), R.string.map_not_ready, Toast.LENGTH_SHORT).show();
                }
             }else{
                    Toast.makeText(getApplicationContext(),R.string.set_path_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);






        fir_searchView = findViewById(R.id.first_street_search_view);
        sec_searchView = findViewById(R.id.sec_street_search_view);

        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView trans = findViewById(R.id.imageView4);
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempfQuery = fir_searchView.getQuery().toString();
                String tempsQuery = sec_searchView.getQuery().toString();
                if(tempfQuery != null && tempsQuery != null){
                    fir_searchView.setQuery(tempsQuery, false);
                    sec_searchView.setQuery(tempfQuery, false);
                    toggleMarkerIcons(); // Call clearMaps to swap the icons

                }
            }
        });

        /* 현재 내 위치를 default 값으로 가져와야함. 역지오코딩을 사용하던 어떤 조치를 통해서
            내 위치가 기본이 되게 만들어야함. 출발지를 애초에 설정해 놔야할 듯 함.
            왜냐하면 arrayList가 오류를 발생시킴.. 하지만 이걸 바꾸기는 구조적으로 무리가 있음.
            네이버또한 현재 위치리를 지우면 기본 값인 나의 위치가 딸려나옴
            arrayLIST에서 출발지, 목적지를 위한 NULL 값을 할당해줌 -> 문제 도착지에서 발생함. 특정 인덱스가
            NULL 값이라서 에러 발생


            1. 기초적으로 일단 시작이 되면 위에 출발지는 역지오 코딩을 통한 나의 위치를 주소로 변환
            2. 만약 fir_searchView가 null이 되었을 경우에 기본 코딩으로 나의 위치를 주소로 변환
         */
        fir_searchView.setSubmitButtonEnabled(true);
        // Set up SearchView
        fir_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentIndex = 0;

                new AddressToCoordinatesConverter().execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        sec_searchView.setSubmitButtonEnabled(true);
        // Set up SearchView
        sec_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentIndex = 1;


                new AddressToCoordinatesConverter().execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getLocation() {
        // 위치 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없으면 요청합니다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 위치 권한이 있는 경우
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                    locationOverlay.setVisible(true);
                    Log.d("localgps Latitude " , latitude + " " + longitude);
                    locationOverlay.setPosition(new LatLng(latitude, longitude));
                    if(checklist == true){
                        updateMarkers(new LatLng(location.getLatitude(), location.getLongitude()));
                    }

                    // 위치 업데이트가 있을 때마다 내 위치를 로그에 출력합니다.
                    Log.d("Current Location", "Latitude: " + latitude + ", Longitude: " + longitude);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            // GPS 프로바이더를 사용하여 위치 업데이트를 요청합니다. 최소 시간 간격은 1000ms, 최소 거리 간격은 10m로 설정하였습니다.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 위치 업데이트 중지
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    private void toggleMarkerIcons() {
        // Ensure there are at least 2 markers in the list
        if (markers.size() < 2) {
            Log.e("Toggle Marker Icons", "Marker가 2개가 아님 다시 수정이 필요함");
            return;
        }

        // Swap the icons
        Marker marker0 = markers.get(0);
        Marker marker1 = markers.get(1);

        // Get the current icons
        OverlayImage currentIcon0 = marker0.getIcon();
        OverlayImage currentIcon1 = marker1.getIcon();

        // Assign new icons based on the current icons
        if (currentIcon0.equals(OverlayImage.fromResource(R.drawable.start))) {
            marker0.setIcon(OverlayImage.fromResource(R.drawable.goal));
            marker1.setIcon(OverlayImage.fromResource(R.drawable.start));
        } else {
            marker0.setIcon(OverlayImage.fromResource(R.drawable.start));
            marker1.setIcon(OverlayImage.fromResource(R.drawable.goal));
        }
    }

    private void clearMap() {
        if (searchResults[currentIndex] != null) {
            Marker marker = markers.get(currentIndex);
            if (marker != null) {
                marker.setMap(null);
                markers.remove(currentIndex);
                // Reset the corresponding search result
                searchResults[currentIndex] = null;
            }
        }

        // Remove all path overlays from the map
        for (PathOverlay pathOverlay : pathOverlays) {
            pathOverlay.setMap(null);
        }
        pathOverlays.clear();

        coords.clear();

    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setLocationSource(locationSource);
        naverMap.getUiSettings().setLocationButtonEnabled(true);

    }



    @SuppressLint("StaticFieldLeak")
    private void check() {
        if (searchResults[0] != null && searchResults[1] != null) {
            LatLng startlatLng = searchResults[0].toLatLng();
            LatLng goallatLng = searchResults[1].toLatLng();
            Log.d("Coordinate 1: ", String.valueOf(startlatLng));
            Log.d("Coordinate 2: ", String.valueOf(goallatLng));

            String urlStr = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving" +
                    "?start=" + startlatLng.latitude + "," + startlatLng.longitude +
                    "&goal=" + goallatLng.latitude + "," + goallatLng.longitude
                    +"&option=trafast";

            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    try {
                        URL url = new URL(params[0]);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "y0hp7us7tv");
                        connection.setRequestProperty("X-NCP-APIGW-API-KEY", "Szahn24YwL7o1ZxSpRUeCSBMJPMmqhFBUSV4r1JW");
                        Log.d("ss: ", "?SSSSSS");
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            return response.toString();
                        }
                        else{
                            Log.d("HttpURLConnection", String.valueOf(responseCode));
                        }
                    } catch (IOException e) {
                        Log.d("못 불러오는데?", "실행 안됨");
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String directionsJson) {
                    if (directionsJson != null) {
                        /* JSONObject {} , JSONArray = [] */
                        try {

                            JSONObject responseJson = new JSONObject(directionsJson);
                            Log.d("responseJson?", responseJson.toString());

                            JSONObject routeObject = responseJson.getJSONObject("route");

                            Log.d("routes?", routeObject.toString());


                            JSONObject traOptimalRoutes = (JSONObject) routeObject.getJSONArray("trafast").get(0);
                            Log.d("traoptimal?", traOptimalRoutes.toString());

                            JSONArray pathArray = traOptimalRoutes.getJSONArray("path");
                            Log.d("path?", pathArray.toString());

                            for (int j = 0; j < pathArray.length(); j++) {
                                int s = pathArray.length();
                                JSONArray point = pathArray.getJSONArray(j);
                                Log.d("point?", point.getDouble(0) + " " +  point.getDouble(1) +"/" + pathArray.length());
                                double latitude = point.getDouble(0);
                                double longitude = point.getDouble(1);

                                coords.add(new LatLng(longitude, latitude));
                                /* 둘의 바꿔야 pathOverlay에 입력 가능하다. */
                            }

                     /*
                            for (int j = 0; j < pathArray.length(); j++) {

                                double distance = coords.get(0).distanceTo(coords.get(j));
                                if (distance >= 500) {
                                    이거...돌리는데 개 오래걸림;; 팅기기도 하고 짧은 거리는 상관 없음..
                                }
                            }
                      */

                            // Draw path
                            PathOverlay pathOverlay = new PathOverlay();
                            pathOverlay.setCoords(coords);
                            pathOverlay.setColor(Color.GREEN);
                            pathOverlay.setWidth(20);
                            pathOverlay.setMap(naverMap);
                            pathOverlays.add(pathOverlay);
                            coords.clear();

                            naverMap.moveCamera(CameraUpdate.fitBounds(pathOverlay.getBounds()));




                            // 내 위치 표시
                            if (ContextCompat.checkSelfPermission(findStreet.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                getLocation();
                            } else {
                                ActivityCompat.requestPermissions(findStreet.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Directions", "Failed to fetch directions");
                    }




                }

            }.execute(urlStr);
        } else {
            Log.e("Error", "One or both coordinates are null");
        }
    }
    public class AddressToCoordinatesConverter extends AsyncTask<String, Void, LatLng> {

        private static final String NAVER_MAPS_GEOCODING_API_URL_LOAD = "https://openapi.naver.com/v1/search/local.json";
        private static final String NAVER_MAPS_GEOCODING_API_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

        private static final String X_NAVER_CLIENT_ID = "KVrulXMCr7uPfrrIs8Rg";
        private static final String X_NAVER_CLIENT_SECRET = "mmzxnGn4Tk";
        private static final String NAVER_CLIENT_ID = "y0hp7us7tv";
        private static final String NAVER_CLIENT_SECRET = "Szahn24YwL7o1ZxSpRUeCSBMJPMmqhFBUSV4r1JW";

        @Override
        protected LatLng doInBackground(String... strings) {
            Log.d("doInBackground: ", "test");
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
                clearMap();
                moveCameraToCoordinates(latLng.latitude, latLng.longitude);
                searchResults[currentIndex] = latLng;
                check();
            }
        }
    }

    private void updateMarkers(LatLng currentLocation) {
        // 현재 위치랑 비교 해서 갱신의 텀을 줄 예정임 위에 위치 업데이트 요청도 비슷한 느낌으로 설정해야함
        // 거리 10 거리 50?  2초마다 갱신해도 의미가 없어보임... 흠.. 고민 중

        if (previousLocation != null && currentLocation.distanceTo(previousLocation) == 50) {
            return; // 사용자의 위치거리가 전보다 50 차이가 난다면 벨을 갱신함.
        }

        // Clear previous markers
        for (Marker marker : markers_bell) {
            marker.setMap(null);
        }
        markers_bell.clear();

        for (Marker marker : markers_cctv) {
            marker.setMap(null);
        }
        markers_cctv.clear();

        // Iterate over coordinateList to find markers within the update radius
        for (LatLng coordinate : coordinateList) {
            double distance = currentLocation.distanceTo(coordinate);
            if (distance <= UPDATE_RADIUS) {
                // Display the marker
                Marker newMarker = new Marker();
                newMarker.setPosition(coordinate);
                newMarker.setIcon(OverlayImage.fromResource(R.drawable.bell));
                newMarker.setMap(naverMap);
                newMarker.setWidth(80);
                newMarker.setHeight(80);
                markers_bell.add(newMarker);
            }
        }

        // Iterate over coordinateList to find markers within the update radius
        for (LatLng coordinate : cctvList) {
            double distance = currentLocation.distanceTo(coordinate);
            if (distance <= UPDATE_RADIUS) {
                // Display the marker
                Marker newMarker = new Marker();
                newMarker.setPosition(coordinate);
                newMarker.setIcon(OverlayImage.fromResource(R.drawable.s));
                newMarker.setMap(naverMap);
                newMarker.setWidth(80);
                newMarker.setHeight(80);
                markers_cctv.add(newMarker);
            }
        }

        // Update previous location
        previousLocation = currentLocation;
    }
    private void moveCameraToCoordinates(double latitude, double longitude) {
        if (naverMap != null) {
            // Move the camera to the specified coordinates
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(longitude, latitude));
            naverMap.moveCamera(cameraUpdate);
            Marker marker = new Marker();
            marker.setPosition(new LatLng(longitude, latitude));
            if(currentIndex == 0){
                marker.setIcon(OverlayImage.fromResource(R.drawable.start));
            }
            else if(currentIndex == 1){
                marker.setIcon(OverlayImage.fromResource(R.drawable.goal));
            }

            marker.setWidth(150);
            marker.setHeight(150);;
            /*여기 출발 아이콘 + 도착 아이콘 추가해야함*/
            marker.setMap(naverMap);
            markers.add(currentIndex, marker);

            // Adjust zoom level without changing camera position
            CameraUpdate zoomUpdate = CameraUpdate.zoomTo(10);
            naverMap.moveCamera(zoomUpdate);
        } else {
            Log.e("Street_view", "NaverMap is null");
        }
    }
    /* 안내 시작을 누르면 그때부터 내 위치를 기반으로 위치를 계속 따 줘야 함 */
}