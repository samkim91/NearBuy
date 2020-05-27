package com.example.trading.Fragments.Post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.trading.R;

import java.util.ArrayList;

public class ShowVPAdapter extends PagerAdapter {

    String TAG = "ShowVPAdapter";
    Context context;
    ArrayList<String> imageUriList;

    public ShowVPAdapter(){

    }

    // 컨텍스트를 받아오기 위한 생성자 선언
    public ShowVPAdapter(Context context, ArrayList<String> imageUriList){
        this.context = context;
        this.imageUriList = imageUriList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.viewpager_page, container, false);

        ImageView imageView = view.findViewById(R.id.imageOfViewPager);

        // 아이템에 사진을 넣는 글라이드. 생성자를 통해 받아온 컨텍스트와 url 주소를 통해 이미지뷰에 연결
        Glide.with(context).load(imageUriList.get(position)).into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "destroyItem : "+position);
//        super.destroyItem(container, position, object);
        // 지난 아이템은 뷰페이져에서 삭제함으로 리소스 낭비를 방지한다. 뷰 페이저는 현재 보고 있는 페이지와 양 옆의 페이지를 미리 로드해놓는다.
        container.removeView((View) object);
    }

    // 뷰페이져의 페이지 수를 카운트 하는 것. 사진을 애초에 최대 10개만 저장하기에 이 값은 10 이하의 숫자가 나온다.
    @Override
    public int getCount() {
        return imageUriList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        Log.d(TAG, "isViewFromObject");
        return view == object;
    }
}
