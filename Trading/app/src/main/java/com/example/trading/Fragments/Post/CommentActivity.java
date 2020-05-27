package com.example.trading.Fragments.Post;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentActivity extends AppCompatActivity {

    String TAG = "CommentActivity";
    String URL = "http://15.165.57.108/Post/";


    EditText blankComment;
    Button insertBtn;
    ConstraintLayout info;
    TextView infoText;
    ImageButton cancelBtn;

    CommentRCAdapter adapter = new CommentRCAdapter(this);

    // 게시물 번호
    int no;
    // 부모댓글 번호
    String parentNum = "-1";
    // 현 댓글의 순서(번호)
    int seq;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .build();

    RetrofitService retrofitService = retrofit.create(RetrofitService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 게시물 액티비티에서 댓글 자세히 보려고 넘어온 상황. 게시물 번호를 받아서, 서버와 통신을 통해 댓글을 불러온다.
        Intent intent = getIntent();
        no = intent.getIntExtra("no", 0);

        blankComment = findViewById(R.id.blank_comment);
        insertBtn = findViewById(R.id.insertBtn);
        info = findViewById(R.id.info);
        infoText = findViewById(R.id.infoText);
        cancelBtn = findViewById(R.id.cancel_btn);

        // 리사이클러뷰 선언.
        RecyclerView recyclerView = findViewById(R.id.recyclerView_comment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);


        // 버튼들의 클릭 이벤트를 모아놓은 메소드
        btnClickListeners();

    }


    private void uploadComment(){
        Log.d(TAG, "uploadComment.. parentNum : "+parentNum);
        // 입력된 댓글을 올리는 메소드이다. 바로 서버통신을 하는건 아니고, 여기서 해줄 작업은
        // 현재 작성자가 댓글을 쓰고 있는지, 대댓글을 쓰고 있는지 조건문으로 확인하고
        // 그 경우에 따라 다른 메소드가 실행되게끔만 해준다.

        // 처음 생각했을 땐, 댓글과 대댓글을 구분해서 서버와 통신해야겠다고 생각했는데
        // 댓글과 대댓글인지는 프론트에서 백으로 정보를 넘길 때 부모댓글 번호로 구분해서 보내주고
        // 처리를 백단에서 하면 되기 때문에 굳이 프론트에서 나눠서 서버로 통신할 필요가 없어짐...


        String comment = blankComment.getText().toString();
        Call<ResponseBody> call = retrofitService.uploadComment(no, UserInfo.getPhoneNum(), comment, parentNum, seq);

//        if ( View.GONE == info.getVisibility() ){
//            // 댓글일 때 부모 번호는 -1
//            call = retrofitService.uploadComment(no, UserInfo.getPhoneNum(), comment, -1);
//        } else {
//            // 대댓글일 때 부모 번호는 부모 댓글의 num
//
//            call = retrofitService.uploadComment(no, UserInfo.getPhoneNum(), comment, 1);
//        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);
                if( response.isSuccessful() ){
                    try {
                        String result = response.body().string();

                        if(result.equals("ok")){
                            Log.d(TAG, "result ok : "+result);
                            Toast.makeText(getApplicationContext(), "댓글이 입력되었습니다.", Toast.LENGTH_SHORT).show();

                            // 화면 다시 띄워주기(갱신)
                            onStart();

                        }else{
                            Log.d(TAG, "result not ok : "+result);
                            Toast.makeText(getApplicationContext(), "서버 통신이 좋지 앖습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "서버 통신이 좋지 앖습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure : "+t);
                Toast.makeText(getApplicationContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 인피니티 페이지네이션 해야함.
        adapter.items.clear();

        getComments(0);
        blankComment.getText().clear();
        info.setVisibility(View.GONE);

    }

    private void getComments(final int pageNum){
        // 서버에는 이 게시글 번호와 페이지넘버가 보내짐.
        Call<ResponseBody> call = retrofitService.getComments(no, pageNum);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);

                if(response.isSuccessful()){
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "result : "+result);

                        if(result.equals("failed")){
                            // 쿼리 실패했을 때 들어오는 부분
                            Toast.makeText(getApplicationContext(), "서버 통신이 좋지 앖습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            // 쿼리값을 받았다. 이제 리사이클러뷰에 뿌려주기 필요!

                            JSONArray jsonArray = new JSONArray(result);

                            for(int i = 0 ; i<jsonArray.length() ; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String image = jsonObject.getString("image");
                                String uId = jsonObject.getString("phonenum");
                                String comment_id = jsonObject.getString("num");
                                String nickname = jsonObject.getString("nickname");
                                String time = jsonObject.getString("time");
                                String comment = jsonObject.getString("comment");

                                String parent = jsonObject.getString("parent");
                                int sequence = jsonObject.getInt("sequence");

                                CommentRCData item = new CommentRCData(image, uId, nickname, time, comment, comment_id, parent, sequence);
                                adapter.addItem(item);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.d(TAG, "not Successful");
                    Toast.makeText(getApplicationContext(), "서버 통신이 좋지 앖습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure : "+t);
                Toast.makeText(getApplicationContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void btnClickListeners(){
        // 댓글 입력 버튼을 눌렀을 때 실행
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "insertBtn clicked");
                // 댓글 입력창이 빈칸인지 아닌지 확인하고 빈칸이 아닐경우 댓글 업로드 실행
                // 빈칸일 경우 안내 문구 띄워주기
                String comment = blankComment.getText().toString();

                if( comment.equals("") ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                    builder.setMessage("댓글을 입력해주세요.");
                    builder.create().show();

                }else{
                    uploadComment();
                }
            }
        });

        // 대댓글 입력 취소 버튼을 눌렀을 때 실행
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancelBtn clicked");
                // 대댓글 입력 안내문구를 gone 처리해준다.
                // 부모댓글 번호를 디폴트값으로 변경해준다.
                info.setVisibility(View.GONE);
                parentNum = "-1";
            }
        });

        adapter.setOnItemClickListener(new OnCommentRCClickListener() {
            @Override
            public void onReplyClick(CommentRCAdapter.ViewHolder viewHolder, View view, int position) {
                // 답글쓰기 버튼을 누르면 해당 답글 쓰는 중 안내문구 띄우고 부모 댓글번호 가져오기
                info.setVisibility(View.VISIBLE);

                String nickname = adapter.getItem(position).getuNickname();
                infoText.setText(nickname+"에게 답글을 쓰는 중입니다.");

                // 댓글의 경우 parentNum 이 null 일테고, 대댓글은 null 이 아니다
                if( !adapter.getItem(position).getParent().equals("null") ){
                    // 대댓글에 댓글을 남길 떈 대댓글의 부모댓글 번호를 가져옴
                    parentNum = adapter.getItem(position).getParent();
                }else{
                    // 댓글에 댓글을 남길 땐 이 댓글의 번호를 부모번호로 가져감.
                    parentNum = adapter.getItem(position).getCommentNum();
                }
                seq = adapter.getItem(position).getSequence();

            }

            @Override
            public void onMoreClick(CommentRCAdapter.ViewHolder viewHolder, View view, final int position) {
                // ... 버튼을 누르면 프로필보기, 수정, 삭제 다이얼로그 띄워주기
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);

                if(UserInfo.getPhoneNum().equals(adapter.getItem(position).uId)){
                    // 로그인 유저와 댓글 작성유저가 같을 때 나오는 기능들..
                    builder.setItems(R.array.myMoreBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String [] taps = getResources().getStringArray(R.array.myMoreBtn);

                            if(taps[which].equals("삭제")){
                                deleteComment(position);
                            }
                        }
                    });

                }else{
                    // 로그인 유저와 댓글 작성유저가 다를 때 나오는 기능들..
                    builder.setItems(R.array.defaultMoreBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String [] taps = getResources().getStringArray(R.array.defaultMoreBtn);

                            if(taps[which].equals("신고하기")){
                                // TODO.. 신고기능??
                            }
                        }
                    });

                }


                builder.create().show();
            }
        });

    }

    private void deleteComment(final int position){
        // 해당 댓글을 지우는 메소드. 현재는 위치만 가져왔기 때문에.. 어뎁터 리스트에서 위치를 가지고 댓글 아이디를 뽑고
        // 서버 통신을 통해 해당 댓글을 삭제한다.
        Log.d(TAG, "deleteCommend");


        String commentId = adapter.getItem(position).getCommentNum();

        // 서버 통신할 때, 이 댓글의 번호와 로그인한 유저 아이디를 같이 넘긴다.
        // 서버단에서 두개를 확인해서 삭제할 예정이다.
        Call<ResponseBody> call = retrofitService.deleteComment(commentId, UserInfo.getPhoneNum());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);

                if(response.isSuccessful()){
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "result is : "+result);

                        if(result.equals("success")){
                            // 삭제 성공
                            Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                            // 댓글을 다시 뿌려주려면 서버 통신을 한번 더 해야함. 하지만 그럴 필요까진 없어보이므로
                            // 해당 댓글 내용만 바꿔주고 클라이언트 단에서 갱신해주자.
                            adapter.getItem(position).setuComment("삭제된 댓글입니다.");
                            // 댓글 내용 변화주기.
                            adapter.notifyItemChanged(position);

                        }else{
                            // sql 단에서 삭제 실패
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "Response not successful");
                    Toast.makeText(getApplicationContext(), "서버와의 통신이 좋지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure : "+t);
                Toast.makeText(getApplicationContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
