package com.example.trading.Fragments.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trading.Fragments.Profile.SetTownActivity;
import com.example.trading.R;
import com.example.trading.RealPathFromURI;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteActivity extends AppCompatActivity {

    String URL = "http://15.165.57.108/Post/";
    String TAG = "WriteActivity";
    ImageView addBtn;
    TextView imageNum;
    EditText title, price, content;
    Spinner category_spinner;

    String selectedCategory;

    String mShortAddress;
    Float mLat, mLng;
    int mRadius;

    int REQUEST_CODE_FOR_IMAGES = 0;
    ArrayList<String> imagePath = new ArrayList<>();

    WriteRCAdapter adapter = new WriteRCAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        addBtn = findViewById(R.id.fake);
        imageNum = findViewById(R.id.imageNum);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        content = findViewById(R.id.content);
        category_spinner = findViewById(R.id.category_spinner);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_image);
        // 레이아웃을 관리하는 매니저를 만들어주고, 이를 리사이클러뷰에 적용해준다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 위에서 선언한 어뎁터를 리사이클러뷰에 적용해준다.
        recyclerView.setAdapter(adapter);

        // 스피너에 대한 초기화와 기능이 들어가 있는 메소드
        categorySpinnerMethod();

        // 이미지 추가 버튼
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMultiImages();
            }
        });

        // 이미지 리사이클러뷰 아이템마다 붙어있는 삭제 버튼을 눌렀을 때, 해당 아이템이 삭제되는 기능
        adapter.setOnItemClickListener(new OnWriteRCClickListener() {
            @Override
            public void onItemClick(WriteRCAdapter.ViewHolder viewHolder, View view, int position) {
                // 어뎁터에서 해당 아이템을 삭제함.
                adapter.items.remove(position);
                // 서버에 보내기 위해 마련해 놓은 imagePath에서도 해당 사진의 path를 삭제해줌.
                imagePath.remove(position);
                // 이미지의 갯수를 카운트하는 부분을 업데이트 해줌.
                imageNum.setText(adapter.getItemCount()+"/10");
                adapter.notifyDataSetChanged();
            }
        });

        getAddressFromSharedPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 저장소에 접근하는 권한을 확인함.
        permissionCheck();
    }

    public void upload(){
        // 레트로핏을 사용한다는 말임. URL은 연결할 서버 URL이고, Gson을 이용해 Json 값을 변환한다는 뜻.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 서버에 보낼 이미지를 담을 어레이 리스트를 만듦.
        ArrayList<MultipartBody.Part> imageList = new ArrayList<>();

        // 이미지 path로부터 파일을 만들고, 이것을 서버에 보낼 형식으로 만들고 어레이리스트로 묶어놓는다.
        for(int i = 0 ; i < imagePath.size() ; i++){
            // 이미지의 path를 가지고 서버에 보낼 파일을 만듦.
            File file = new File(imagePath.get(i));

            // 요청을 보낼 때 이것은 multipart/form-data 라는 것을 인지시킴. 사진을 보내기 위함. 그리고 위에서 만든 파일을 첨부한다.
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody의 part로 업로드 파일을 만든다. FromData에 들어가는 name은 서버단에서 받을 변수명이고, 사진을 여러장 보낼 것이기 때문에 배열로 선언한다.
            // 중간 매개변수는 파일이름. 마지막 매개변수는 요청보낼 내용이다.
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", imagePath.get(i), requestBody);
            imageList.add(uploadFile);
        }

//        Map<String, RequestBody> map = new HashMap<>();
//        RequestBody act = RequestBody.create(MediaType.parse("text/plain"), "upload");
//        map.put("act", act);

        HashMap hashMap = new HashMap();
        hashMap.put("phoneNum", UserInfo.phoneNum); //연습용 "821079647128"
        hashMap.put("title", title.getText().toString());
        hashMap.put("address", mShortAddress);
        hashMap.put("category", selectedCategory);
        hashMap.put("price", price.getText().toString());
        hashMap.put("content", content.getText().toString());

        // 좌표값은 float 자료형으로 들어가서 해당 해쉬맵을 다시 만들어줌.
        HashMap hashMap1 = new HashMap();
        hashMap1.put("lat", mLat);
        hashMap1.put("lng", mLng);

        // 레트로서비스 인터페이스를 객체화하고 위에서 선언한 레트로핏에 연결. 인터페이스에 선언해둔 함수를 사용하기 위함.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        // 서버에 보낼 콜을 만듦. 포스트 방식으로 보낼 것이고, 포스트 안에 들어가는 파라미터가 서버로 전송되는 값임.
        Call<ResponseBody> call = retrofitService.upload(imageList, hashMap, hashMap1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            // 전송이 성공했고 응답을 받았을 때
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");

                if(response.isSuccessful()){
                    //서버로부터 응답 확인하려고 만들어 놓은 구문.. 나중에는 필요없으므로 주석처리 필요
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "Response: "+result);
                        JSONObject jsonObject = new JSONObject(result);
                        Log.d(TAG, "Response: "+jsonObject.getString("response"));

                        if(jsonObject.getString("response").equals("success")){
                            Toast.makeText(WriteActivity.this, "등록 성공!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WriteActivity.this, "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 뒤로가기
                    onBackPressed();
                }else{
                    Toast.makeText(WriteActivity.this, "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                }
            }

            // 응답에 뭔가 오류가 났을 때.
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "서버 접근 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    public void categorySpinnerMethod(){
        // 어레이 어뎁터를 만든다. 카테고리에 들어갈 값들을 선언해 놓은 R.array.category에서 값을 가져온다.
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        // 어뎁터를 어떤 형식으로 보여줄지 선택한다. layout 옵션이 있는데 크게 다른 점은 없다.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 이전에 선언한 스피너에 이 어뎁터를 달아준다.
        category_spinner.setAdapter(adapter);

        // 스피너가 어떠한 값을 선택했는지 리스너를 달아준다.
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 해당 포지션에서 값을 가져온다.
                selectedCategory = (String) category_spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // 액션바를 추가하는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 리소스에 만들어 놓은 menu_write.xml을 인플레이트(덮기) 한다.
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 추가된 액션바에 기능을 넣는 메소드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 선택된 아이템의 아이디에 따라서 작동하도록 구현. 여기서는 어차피 완료 버튼 하나임.
        switch (item.getItemId()){
            case R.id.post_btn:
                // 빈칸 검사
                if(!adapter.items.isEmpty() && !title.getText().toString().equals("") && !price.getText().toString().equals("") && !content.getText().toString().equals("")){
                    // 카테고리 선택 검사
                    if(selectedCategory.equals("카테고리")){
                        Toast.makeText(WriteActivity.this, "카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show();
                    }else{
                        // 조건 만족하면 서버로 보내기.
                        upload();
                    }
                }else{
                    Toast.makeText(WriteActivity.this, "빈칸이나 사진을 채워주세요!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 앨범에서 이미지를 가져오도록 하는 인텐트
    public void selectMultiImages(){

        Log.d(TAG, "selectMultiImages");
        // 선택을 할 수 있는 인텐트를 만듦
        Intent intent = new Intent(Intent.ACTION_PICK);
        // 이미지 선택을 하겠다는 의미
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        // 아래 줄로 인해서 한번에 여러장을 받아올 수 있다.
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // 선택된 이미지의 uri를 받아오겠다는 의미
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_FOR_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+data);
        Log.d(TAG, "requestCode: "+requestCode);
        Log.d(TAG, "resultCode: "+resultCode);

        if(requestCode == REQUEST_CODE_FOR_IMAGES && resultCode == RESULT_OK){

            if(data.getClipData() == null){
                Toast.makeText(getApplicationContext(), "다중선택이 불가능한 기기에요.", Toast.LENGTH_SHORT).show();
            }else{
                ClipData clipData = data.getClipData();
                Log.d(TAG, "clipData: "+clipData.getItemCount());

                // 총 사진의 갯수가 10개가 초과되지 못하게 한다.
                if(clipData.getItemCount() > 10 || (clipData.getItemCount()+adapter.getItemCount()) > 10){
                    Toast.makeText(getApplicationContext(), "사진은 10장 이하만 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "사진 선택완료!");
                    for(int i = 0 ; i < clipData.getItemCount() ; i++){

//                        imageUriList.add(getPathFromURI(clipData.getItemAt(i).getUri()));
//                        Log.d(TAG, "URI: "+imageUriList.get(i));

                        // 파일의 path를 불러옴
                        String path = RealPathFromURI.getRealPathFromURI(this, clipData.getItemAt(i).getUri());
                        Log.d(TAG, "Path: "+path);

                        // 리사이클러뷰에 넣을 객체를 생성하고 path를 부여해줌
                        WriteRCData item = new WriteRCData(path);

                        // 리사이클러뷰에 추가
                        adapter.addItem(item);
                        adapter.notifyDataSetChanged();

                        // 서버에 보내기 위해 path를 가지고 있는 어레이리스트를 만들어줌.
                        imagePath.add(path);

                    }
                }
            }
        }
    }

    // Tedpermission 라이브러리를 사용하는 부분... 내/외부 저장소에 접근할 때 권한요청이 없어진 것 같으므로 현재는 필요 없음.
    public void permissionCheck(){
        PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
//                        Toast.makeText(getApplicationContext(), "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(getApplicationContext(), "권한이 거부되었습니다.\n"+deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };

                TedPermission.with(this)
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setDeniedMessage("접근권한을 승인하지 않으면, 이 앱의 이용에 제한이 있을 수 있습니다. 설정에서 접근권한을 승인해주세요.")
                        .check();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageNum.setText(adapter.getItemCount()+"/10");
    }

    public void getAddressFromSharedPreferences(){
        Log.d(TAG, "getAddressFromSharedPreferencse");
        // 쉐어드프리퍼런스에서 이 유저가 설정해놓은 메인 주소와, 물품 탐색반경을 가져오고, 이 정보를 토대로 서버에 요청을 한다! 여기서는 서버 요청 전에 정보(주소, 반경) 불러오는 것만 함
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String addressesString = sharedPreferences.getString("addresses", "");
        int checkedID = sharedPreferences.getInt("isChecked", 0);
        mRadius = sharedPreferences.getInt("radius", 0);

        try {
            String mAddress = null;
            // 제이슨 어레이에서 저장된 값을 빼오고, 그 값의 양에 따라 주소어레이리스트에 넣어준다.
            JSONArray jsonArray = new JSONArray(addressesString);
            // 제이슨어레이가 하나의 값만 갖고 있다면 바로 내 주소로 지정해줌
            if (1 == jsonArray.length() && checkedID != -1 ) {
                mAddress = jsonArray.getString(0);
            } else if (2 == jsonArray.length()) {
                // 제이슨어레이가 두개의 주소를 갖고 있다면, 쉐어드프리퍼런스에 선택되어 있는 주소 아이디를 확인하고, 그 아이디에 따라서 내 주소를 지정해줌.
                if (1 == checkedID) {
                    mAddress = jsonArray.getString(0);
                } else if (2 == checkedID) {
                    mAddress = jsonArray.getString(1);
                }
            }

            if(mAddress != null || checkedID != -1){
                // 위에서 불러온 내 주소를 가지고 경위도로 바꿔줌. 서버에 보낼 경위도임.
                changeLocationToLatlng(mAddress);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("글을 게시하려면 위치등록이 필요해요.");
                builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SetTownActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                builder.create().show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeLocationToLatlng(String address) {
        // 어레이리스트에서 받아온 스트링이 /을 기준으로 행정동과 전체주소로 이루어져 있기 때문에 이를 사용하기 위해서 나눠준다.
        String[] splitedAddress = address.split("/");
        mShortAddress = splitedAddress[0];
        String fullAddress = splitedAddress[1];

        Log.d(TAG, "CheckedAddress is: " + fullAddress);

        // 주소를 경위도로 바꿔주기 위해 사용할 지오코더를 선언한다.
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

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
            Log.d(TAG, "Addresses is: " + addresses + "shortly: " + mShortAddress);
            Address address1 = addresses.get(0);
            // 이 주소에서 경위도를 가져와서 맵에 넣어주고 포커스를 이동시킨다.
            Double dLat = address1.getLatitude();
            mLat = dLat.floatValue();
            Double dLng = address1.getLongitude();
            mLng = dLng.floatValue();

        }
        Log.d(TAG, "mLatlng: " + mLat + "/" + mLng);
    }


}
