package com.example.trading.Fragments.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trading.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetTownActivity extends AppCompatActivity implements OnMapReadyCallback {

    ChipGroup chipGroup;
    Chip chip1, chip2, addChip;
    TextView locationText, rangeText;
    SeekBar seekBar;

    GoogleMap mMap;
    Marker mMarker;
    Circle mCircle;

    String TAG = "SetTown_Activity";

//    int UPDATE_INTERVAL_MS = 1000; // 업데이트 간격 1초
//    int FASTEST_UPDATE_INTERVAL_MS = 500; // 빠른 업데이트 간격 5

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    // 최초에 위치를 요청하고 담아두기 위함
    Location location;

    // 요청한 위치에서 확인된 내 위치를 담아두기 위함.
    Location mLocation;
    LatLng mLatlng;

    LocationCallback locationCallback;

    int REQUEST_CODE = 1;

    ArrayList<String> addressList = new ArrayList<>();

    int checkedId;
    int mRadius = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_town);
        Log.d(TAG, "onCreate");

        chipGroup = findViewById(R.id.chipGroup);
        addChip = findViewById(R.id.addChipBtn);
        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        locationText = findViewById(R.id.locationText);
        rangeText = findViewById(R.id.rangeText);
        seekBar = findViewById(R.id.seekBar);

        // 주소 접근권한을 확인/요청하는 메소드
        permissionCheck();

        // 위치 요청을 하는 객체 선언.. 우선순위를 높은 정확도로 둔다.
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                .setInterval(UPDATE_INTERVAL_MS)
//                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)

        // 위치 요청을 설정하는 빌더를 만들고, 위에서 선언한 위치요청 내용을 넣어준다.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        // 위치를 받아오기 위해 사용하는 클라이언트.. 이것을 사용해서 위치를 업데이트하거나, 업데이트 정지 등을 처리할 수 있음.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // xml에 선언해둔 fragment에 구글 맵을 붙여주는 부분
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);

        getAddressFromSharedPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // '다음'을 이용해서 주소를 받아온 다음 실행되는 곳.. 주소를 chip에 담아주는 역할을 할 것임.
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + data.getStringExtra("data"));

            addressList.add(data.getStringExtra("data"));
            setInitChips();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");

        // 구글에서 제공해주는 지도가 준비되면, 해당 객체를 내가 이전에 선언해준 객체에 넣는다.
        mMap = googleMap;

        // 내 위치로 이동하는 버튼을 사용할 것인가?
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

//        defaultLocation();

        // 맵에 클릭리스너를 달아준다.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng);
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult in LocationCallback: " + locationResult);

                List<Location> locations = locationResult.getLocations();

                if (locations.size() > 0) {
                    location = locations.get(locations.size() - 1);

//                    mLatlng = new LatLng(location.getLatitude(), location.getLongitude());

//                    String markerTitle = getCurrentAddress(mLatlng);
//                    String markerSnippet = "위도: "+location.getLatitude()+" / 경도: "+location.getLongitude();

//                    Log.d(TAG, "marker info: " + markerTitle);
                    setCurrentLocation(location);
                    mLocation = location;
                    mLatlng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                    setInitChips();
                    chipsClickListener();
                }
            }
        };

        // locationCallback을 통해 받은 위치로 업데이트를 시작한다. onCreate에 선언한 위치요청 객체와, 위에서 선언한 위치에 대한 콜백함수를 파라미터로 받고, 백그라운드에서 계속 돌게 함.
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }

    public void defaultLocation() {
        // 위치를 받아오는데 실패했을 때, 서울(역) 경위도를 찍어준다.
        LatLng SEOUL = new LatLng(37.56, 126.97);

        // 위에서 선언한 경위도에 마커를 씌우기 위해 선언.
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("seoul");
//        markerOptions.snippet("main city");
//        mMap.addMarker(markerOptions);

        // 지도 화면을 위에서 선언한 서울 경위도로 이동시킴
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));

        // 지도 화면에 '10'까지 줌을 한 상태로 보이게 함. (숫자가 커지면 더 확대 됨)
        // move와 animate의 차이는 move는 즉각적으로 카메라 화면을 보여주고, animate는 시점이 이동하는 것을 보여준다.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
    }

    public void setCurrentLocation(Location location) {
        Log.d(TAG, "setCurrentLocation: " + location);
        // 현재 위치를 설정해주는 메소드

        // 기존 마커가 있다면, 다시 설정해주기 위해서 삭제!
//        if (mMarker != null) {
//            mMarker.remove();
//        }

        // 현재 위치에서 경위도를 가져온다.
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        // 새로운 마커를 찍기위한 마켜 옵션을 선언
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLatLng);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true); // 마커를 드래그할 수 있는지 설정

        // 내 위치를 나타내는 마커에 위에서 만든 마커를 추가
//        mMarker = mMap.addMarker(markerOptions);

        // 카메라 시점을 이동시켜줌.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
    }

    public void moveToCheckedLocation(String address) {
        // 어레이리스트에서 받아온 스트링이 /을 기준으로 행정동과 전체주소로 이루어져 있기 때문에 이를 사용하기 위해서 나눠준다.
        String[] splitedAddress = address.split("/");
        String fullAddress = splitedAddress[1];

        Log.d(TAG, "CheckedAddress is: " + fullAddress);

        // 주소를 경위도로 바꿔주기 위해 사용할 지오코더를 선언한다.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // 지오코더로 불러오는 주소를 담아줄 리스트
        List<Address> addresses = null;

        try {
            // 지오코더를 사용해 주소이름을 통해 전체 주소정보를 가져온다.
            addresses = geocoder.getFromLocationName(fullAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        }

        // 주소가 잘 가져와졋는지 검사하고,
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getApplicationContext(), "주소가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 주소정보리스트에서 첫번째 값을 가져온다.
            Log.d(TAG, "Addresses is: " + addresses);
            Address address1 = addresses.get(0);
            // 이 주소에서 경위도를 가져와서 맵에 넣어주고 포커스를 이동시킨다.
            LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());

            if (mMarker != null) {
                mMarker.remove();
            }
            // 해당 위치에 마커를 찍어준다.
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            mMarker = mMap.addMarker(markerOptions);
            // 해당 위치로 이동한다.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    //    public String getCurrentAddress(LatLng latLng) {
//        Log.d(TAG, "getCurrentAddress: " + latLng);
//
//        // 지오코더! GPS를 주소로 변환해줌. 컨텍스트를 주고, 지역을 디폴트로 설정(현재 기기에서 사용하는 지역을 가져온다는 뜻)
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        // 주소를 담아두는 리스트
//        List<Address> addresses;
//
//        try {
//            // 지오코더를 이용해서 받아온 경위도 좌표를 위치주소로 바꿔준다.
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "IOException");
//            return "주소를 가져올 수 없습니다.";
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            Log.d(TAG, "IllegalArgumentException");
//            return "잘못된 GPS 좌표입니다.";
//        }
//
//        if (addresses == null || addresses.size() == 0) {
//            // 주소가 없다고 하면, 없다고 반환한다.
//            Toast.makeText(getApplicationContext(), "주소가 없습니다.", Toast.LENGTH_SHORT).show();
//            return "주소가 없습니다.";
//        } else {
//            // 발견된 주소가 있다고 하면, 해당 주소리스트에서 주소부분을 가져와서 반환한다.
//            Address address = addresses.get(0);
//            Log.d(TAG, "Address is: " + address);
//            return address.getAddressLine(0);
//        }
//    }


    public void permissionCheck() {
        Log.d(TAG, "permissionCheck");
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한이 있다면 실행.. 띄울 게 없다.
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한이 없다면 토스트를 통해 안내한다.
                Toast.makeText(getApplicationContext(), "권한이 거부되었습니다." + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        // 정밀위치와 대략적인 위치 접근 권한이 승인되었는지 확인하는 구문
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .setDeniedMessage("접근권한을 승인하지 않으면, 이 앱의 이용에 제한이 있을 수 있습니다. 설정에서 접근권한을 승인해주세요.")
                .check();
    }

    public void setSeekBar(String fullAddress) {
        // 주소를 경위도로 바꿔주기 위해 사용할 지오코더를 선언한다.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // 지오코더로 불러오는 주소를 담아줄 리스트
        List<Address> addresses = null;

        try {
            // 지오코더를 사용해 주소이름을 통해 전체 주소정보를 가져온다.
            addresses = geocoder.getFromLocationName(fullAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        }

        Log.d(TAG, "seekBarAddress: "+addresses);

        // 주소가 잘 가져와졋는지 검사하고,
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getApplicationContext(), "주소가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 주소정보리스트에서 첫번째 값을 가져온다.
            Address address1 = addresses.get(0);
            // 이 주소에서 경위도를 가져와서 맵에 넣어주고 포커스를 이동시킨다.
            final LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());

            // 기존에 설정한 값이 있다면 seekbar를 초기화 해주고 값 크기만큼 원을 그려줌.
            if(mRadius != 0){
                rangeText.setText("근처 " + mRadius + "km");
                seekBar.setProgress(mRadius);

                // 기존에 원이 있다면 지워준다. 안 그러면 원이 중첩되기 때문에!
                if (mCircle != null) {
                    mCircle.remove();
                }

                // 원을 만듦. 반지름은 seekbar에서 설정하는 숫자에 1000을 곱함. 기본 m 단위임. 경계선 굵기는 0으로 하고 색은 투명하게 보이게 함.
                CircleOptions circleOptions = new CircleOptions().center(latLng)
                        .radius(mRadius * 1000)
                        .strokeWidth(0f)
                        .fillColor(Color.parseColor("#880000FF"));

                // 맵에 원을 추가
                mCircle = mMap.addCircle(circleOptions);
            }

            // seekbar를 컨트롤하는 부분. 이 바의 위치에 따라서 내 위치를 중심으로 하는 반경이 커지고, 그만큼 여러 제품을 얻어올 수 있게 할 것임.
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rangeText.setText("근처 " + progress + "km");

                    // 기존에 원이 있다면 지워준다. 안 그러면 원이 중첩되기 때문에!
                    if (mCircle != null) {
                        mCircle.remove();
                    }

                    // 원을 만듦. 반지름은 seekbar에서 설정하는 숫자에 1000을 곱함. 기본 m 단위임. 경계선 굵기는 0으로 하고 색은 투명하게 보이게 함.
                    CircleOptions circleOptions = new CircleOptions().center(latLng)
                            .radius(progress * 1000)
                            .strokeWidth(0f)
                            .fillColor(Color.parseColor("#880000FF"));

                    // 맵에 원을 추가하고, 저장을 위해 전역변수로 선언해둔 radius에 progress 값을 넣음
                    mCircle = mMap.addCircle(circleOptions);
                    mRadius = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    public void getAddressFromSharedPreferences(){
        // 쉐어드프리퍼런스에서 값을 불러온다.
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String addressesString = sharedPreferences.getString("addresses", "");
        checkedId = sharedPreferences.getInt("isChecked", 0);
        mRadius = sharedPreferences.getInt("radius", 0);
        Log.d(TAG, "onStart: "+addressesString+"/checkedId: "+checkedId);
        try {
            // 제이슨 어레이에서 저장된 값을 빼오고, 그 값의 양에 따라 주소어레이리스트에 넣어준다. 그러면, 이 어레이리스트를 가지고 칩을 만들어줄 것임.
            JSONArray jsonArray = new JSONArray(addressesString);
            for(int i = 0 ; i<jsonArray.length() ; i++){
                String address = jsonArray.getString(i);
                addressList.add(address);
            }
            Log.d(TAG, "addressList From JSONArray: "+addressList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setInitChips(){
        //초기에 칩을 모두 없앴다가, 현재의 arrayAddress 의 상태를 보고 칩을 다시 만들어준다.
        chip1.setVisibility(View.GONE);
        chip2.setVisibility(View.GONE);

        // arrayAddress가 담고 있는 갯수에 따라서 칩을 배치해준다. 사이즈가 0일 때는 (+)칩이 나오게 하고, 1일 때는 정보를 담고 있는 칩 1개와 (+)칩이 나오게 한다.
        // 2일 때는 (+)을 없애고, 정보를 담고 있는 칩 2개가 나오게 한다.
        if (0 == addressList.size()) {
            addChip.setVisibility(View.VISIBLE);
            setCurrentLocation(mLocation);
        } else if (1 == addressList.size()) {
            // 어레이리스트 안에 있는 값이 행정동과 전체주소로 이루어져 있기 때문에 /을 기준으로 잘라준다.
            String rawAddress = addressList.get(0);
            String[] arrayAddress = rawAddress.split("/");
            chip1.setText(arrayAddress[0]);
            chip1.setVisibility(View.VISIBLE);
            moveToCheckedLocation(rawAddress);

            // 이전에 칩이 체크되었었는지 확인하고, 맞다면 칩을 체크상태로 만들어준다.
            if(1 == checkedId || 2 == checkedId){
                chip1.setChecked(true);
                locationText.setText(chip1.getText());
                // seekBar에 대한 클릭 리스너를 선언한 메소드
                setSeekBar(rawAddress);
            }
            addChip.setVisibility(View.VISIBLE);

        } else if (2 == addressList.size()) {
            // 주소어레이리스트의 길이가 2라면, 값을 모두 가져와서 행정동과 전체주소의 구분자인 /를 기준으로 값을 잘라서 사용
            for (int i = 0; i < addressList.size(); i++) {
                String rawAddress = addressList.get(i);
                String[] arrayAddress = rawAddress.split("/");
                // 첫번째 칩에 값을 넣어주고, 두번째 칩에도 값을 넣어준다.
                if (0 == i) {
                    chip1.setText(arrayAddress[0]);
                    chip1.setVisibility(View.VISIBLE);
                } else if (1 == i) {
                    chip2.setText(arrayAddress[0]);
                    chip2.setVisibility(View.VISIBLE);
                }
            }

            // 어떤 칩이 체크되어 있었는지 확인하고 해당값에 따라서 체크해준다.
            if(1 == checkedId){
                Log.d(TAG, "checkedID: 1");
                chip1.setChecked(true);
                locationText.setText(chip1.getText());
                moveToCheckedLocation(addressList.get(0));
                setSeekBar(addressList.get(0));
            }else if(2 == checkedId){
                Log.d(TAG, "checkedID: 2");
                chip2.setChecked(true);
                locationText.setText(chip2.getText());
                moveToCheckedLocation(addressList.get(1));
                setSeekBar(addressList.get(1));
            }

            // (+)칩은 없애준다.
            addChip.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        // 이 액티비티가 꺼지면, 위치요청을 중단함.
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void chipsClickListener() {
        // 칩그룹에서 칩을 하나 선택할 때, 텍스트뷰가 변화되게 하는 곳이다. 아무것도 선택하지 않았을 때는 기본값이 나오게 한다.
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Log.d(TAG, "onCheckedChanged: " + i);

                if (chip1.isChecked()) {
                    locationText.setText(chip1.getText());
                    // 체크된 칩에 따라 맵의 위치를 변경해주기 위한 메소드를 실행한다.
                    moveToCheckedLocation(addressList.get(0));
                    setSeekBar(addressList.get(0));
                } else if (chip2.isChecked()) {
                    locationText.setText(chip2.getText());
                    // 체크된 칩에 따라 맵의 위치를 변경해주기 위한 메소드를 실행한다. 칩2 의 경우에는 주소 어레이리스트의 사이즈에 따라서 주소가 다르기 때문에 아래와 같이 조건문을 달아줌.
                    // 칩이 1개 남았는데 이게 두번째 칩이라면, 0의 인덱스를 갖는게 맞고, 칩이 2개 남았을 때 이게 두번째 칩이라면, 1의 인덱스를 갖는게 맞음.
                    if (1 == addressList.size()) {
                        moveToCheckedLocation(addressList.get(0));
                        setSeekBar(addressList.get(0));
                    } else {
                        moveToCheckedLocation(addressList.get(1));
                        setSeekBar(addressList.get(1));
                    }
                } else {
                    // 아무 칩도 선택하지 않았을 때, 초기화를 해준다.
                    locationText.setText("읍/면/동");
                    rangeText.setText("근처 0km");
                    // 현재 본인의 위치로 이동.
                    setCurrentLocation(mLocation);
                    // 마커를 삭제
                    if(mMarker != null){
                        mMarker.remove();
                    }
                    if(mCircle != null){
                        mCircle.remove();
                    }

                }

                Log.d(TAG, "addressList in clickListener: "+addressList.toString());
            }
        });

        // 추가를 담고 있는 칩에 클릭 리스너를 달아준다.
        addChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetAddressActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // 칩의 삭제 버튼을 누르면 그룹에서 삭제되게 한다.
        chip1.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 체크를 해제해주고, 칩을 없애주고, 어레이리스트에서 정보를 삭제해주고, (+)칩을 보여주고, 기본위치를 내 위치로 이동한다.
                chip1.setChecked(false);
                chip1.setVisibility(View.GONE);
                addressList.remove(0);
                addChip.setVisibility(View.VISIBLE);
                setCurrentLocation(mLocation);
            }
        });

        // 칩2가 혼자 남았을 때는 어레이리스트 사이즈가 1이기 때문에, 조건을 확인하고 해당 내용을 삭제해준다.
        chip2.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 체크를 해제해주고, 칩을 없애주고, 어레이리스트 사이즈에 따라서 1개이면 0번째를 삭제, 2개이면 1번째를 삭제, (+)칩을 보여주고, 기본 위치를 내 위치로 이동
                chip2.setChecked(false);
                chip2.setVisibility(View.GONE);
                if (1 == addressList.size()) {
                    addressList.remove(0);
                } else {
                    addressList.remove(1);
                }
                addChip.setVisibility(View.VISIBLE);
                setCurrentLocation(mLocation);
            }
        });
    }

    public void saveLocationInSharedPreferences(){
        // 위치 정보를 로그인유저 정보를 저장한 쉐어드프리퍼런스에 저장한다.
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 어레이를 그대로 제이슨어레일 형태로 넣어준다.
        JSONArray jsonArray = new JSONArray(addressList);

        if(chip1.isChecked() || chip2.isChecked()){

            // 어레이리스트와, 어느 칩이 체크되었는지, 반경은 얼마인지 등 확인하고 넣어준다.
            editor.putString("addresses", jsonArray.toString());
            editor.putInt("radius", mRadius);
            if(chip1.isChecked()){
                editor.putInt("isChecked", 1);
            }else if(chip2.isChecked()){
                editor.putInt("isChecked", 2);
            }
            // 저장
            editor.commit();
            Log.d(TAG, "saveLocationInSharedPreferences");
            Toast.makeText(getApplicationContext(), "저장되었어요.", Toast.LENGTH_SHORT).show();
        }else{
            // 칩이 체크가 안 되었을 때는, 어레이리스트에 저장된 값을 쉐어드에 저장해주고, 체크표시와 반경은 없는 것으로 쉐어드프리퍼런스에 저장해준다.
            editor.putString("addresses", jsonArray.toString());
            editor.putInt("isChecked", 0);
            editor.putInt("radius", 0);
            editor.commit();
            Toast.makeText(getApplicationContext(), "지역을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 액션바에 저장 버튼을 활성화 시킴
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_write, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 저장버튼을 눌렀을 때, 쉐어드프리퍼런스에 해당 내용을 저장하는 메소드를 작동시킴
        if(item.getItemId() == R.id.post_btn){
            saveLocationInSharedPreferences();
        }
        return super.onOptionsItemSelected(item);
    }
}

