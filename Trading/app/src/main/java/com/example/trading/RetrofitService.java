package com.example.trading;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitService {


    // POST로 진행하기 때문에 Encoded가 포함되고, GET으로 한다면 필요 없음.
    @FormUrlEncoded
    // 어느 php 파일과 연동할지 정함. 앞의 주소는 구현하는 자바코드에 있음.
    @POST("write.php")
    Call<ResponseOnly> getPost(
            // Field("response")는 서버 단에서 $_POST['response']로 읽히는 부분. 따라서 저 값이 같아야 통신이 가능함.
            // String response는 그저 getPost 파라미터에 어떤 힌트를 줄 지를 정하는 것 뿐..
            @Field("response") String response


            // @Field는 POST로 보낼때 사용하는 것이고, GET을 사용할 땐 @Query를 씀. ex)page=X 여기서 X에 들어가는 게 @Query. @Path는 주소의 최종값이 뭐인지 정하는 것. ex)http://~~~/posts/1 여기서 1
            // 다수의 값을 보내고 싶을 때, GET의 경우 @QueryMap 에 Map을 이용해서 보냄. POST의 경우 @FieldMap을 쓰면됨. Retrofit에서는 Map 보다 HashMap 사용을 권장함. Map은 인터페이스이기 때문.. 더 필요하면 추상클래스와 인터페이스 공부
            // 더 필요한 내용은 https://kor45cw.tistory.com/5

    );

    // 작성된 게시글을 서버로 보내기 위한 post.. 밑에 upload 함수로 쓰이기 때문에 이제 안 쓰임
    @FormUrlEncoded
    @POST("write.php")
    Call<ResponseBody> setPost(
            @FieldMap HashMap<String, String> map
            );

    // 사진을 보내기 위해 POST 방식에서 FormUrlEncoded 가 아닌 Multipart 방식을 사용. post가 여러가지 파트로 이루어져있다는 뜻.
    @Multipart
    @POST("write.php")
    Call<ResponseBody> upload(@Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, String> hashMap, @PartMap HashMap<String, Float> hashMap1);


    // 서버에서 게시글을 가져오기 위한 get
    @GET("posts.php")
    Call<ResponseBody> showPost(
            @QueryMap HashMap<String, String> hashMap, @QueryMap HashMap<String, Float> hashMap1, @Query("radius") int radius
    );

    // 서버에서 게시글 세부내용을 가져오기 위한 get.. 따라서 게시글 넘버를 보낸다.
    @GET("show.php")
    Call<ResponseBody> showThisPost(
            @Query("no") int no
    );

    // 현재 게시물을 서버에서 삭제하기 위한 get.. 게시글 넘버를 보낸다.
    @GET("delete.php")
    Call<ResponseBody> deletePost(
            @Query("no") int no
    );

    // 게시물의 수정값을 서버에 보내기 위한 post 값이 여러개이므로 해쉬맵을 이용함.
    @Multipart
    @POST("edit.php")
    Call<ResponseBody> editPost(@Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, Object> hashmap, @PartMap HashMap<String, Float> hashMap1);

    @GET("updateMark.php")
    Call<ResponseBody> updateMark(
            @Query("no") int no, @Query("phoneNum") String phoneNum, @Query("isMarked") Boolean isMarked
    );

    // 프로필 프래그먼트에서 유저의 사진과 닉네임 정보를 불러오기 위한 서버통신을 함.
    @FormUrlEncoded
    @POST("userInfo.php")
    Call<ResponseBody> userInfo(
            @Field("userId") String userId
    );

    @GET("checkDuplicate.php")
    Call<ResponseBody> checkDuplicate(
            @Query("input") String input
    );

    @Multipart
    @POST("updateProfile.php")
    Call<ResponseBody> updateProfile(
            @Part MultipartBody.Part file, @PartMap HashMap<String, String> hashMap
            );

    @GET("updateProfile.php")
    Call<ResponseBody> updateProfileNick(
            @QueryMap HashMap<String, String> hashMap
    );

    @GET("sellingList.php")
    Call<ResponseBody> sellingList(
            @Query("phonenum") String phonenum, @Query("page") int pageNum, @Query("status") String status
    );

    @GET("changeStatus.php")
    Call<ResponseBody> changeStatus(
            @Query("phonenum") String phoneNum, @Query("no") int postNum, @Query("status") String status
    );

    @GET("markedList.php")
    Call<ResponseBody> markedList(
            @Query("phonenum") String phonenum, @Query("page") int pageNum
    );

    @GET("selectComment.php")
    Call<ResponseBody> getComments(
            @Query("postNum") int postNum, @Query("pageNum") int pageNum
    );

    @FormUrlEncoded
    @POST("uploadComment.php")
    Call<ResponseBody> uploadComment(
            @Field("postnum") int postNum, @Field("phonenum") String phoneNum, @Field("comment") String comment, @Field("parent") String parent, @Field("sequence") int seq
    );

    @GET("deleteComment.php")
    Call<ResponseBody> deleteComment(
            @Query("commentId") String commentId, @Query("uId") String uId
    );

    @GET("checkChatRoom.php")
    Call<ResponseBody> checkChatRoom(
            @Query("user1") String user1, @Query("user2") String user2
    );

    @GET("loadLastText.php")
    Call<ResponseBody> loadLastText(
            @Query("roomId") String roomId
    );

    @FormUrlEncoded
    @POST("uploadChat.php")
    Call<ResponseBody> uploadChat(
            @Field("roomId") String roomId, @Field("userId") String userId, @Field("content") String content
    );

}
