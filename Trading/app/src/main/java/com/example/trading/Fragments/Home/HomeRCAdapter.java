package com.example.trading.Fragments.Home;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trading.R;

import java.util.ArrayList;


public class HomeRCAdapter extends RecyclerView.Adapter<HomeRCAdapter.ViewHolder> implements OnHomeRCClickListener {

    // 리사이클러 뷰 아이템들을 담을 어레이리스트
    public ArrayList<HomeRCData> items = new ArrayList<>();
    // 아이템 클릭 리스너 인터페이스
    OnHomeRCClickListener listener;
    // Glide 를 사용하기 위한 컨텍스트를 어뎁터 선언할 때 받아온다.
    Context context;

    // Context를 포함하기 위한 어뎁터 생성자.
    public HomeRCAdapter(Context context){
        this.context = context;
    }

    // 아이템 클릭 리스너 메소드를 만들고 리스너를 달아줌.
    public void setOnItemClickListener(OnHomeRCClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, View view, int position) {
        // 인터페이스에서 만들어준 아이템클릭 메소드를 오버라이드함.
        if(listener!=null){
            listener.onItemClick(viewHolder, view, position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더를 만드는 메소드, 레이아웃 인플레이터를 통해 뷰를 하나 만들고, 이 뷰의 양식을 리사이클러뷰 아이템으로 정한다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_home_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 뷰홀더를 바인딩(기존 아이템에 새 아이템을 연결)해 주는 메소드.
        HomeRCData item = items.get(position);

        // 아이템에 메인 사진을 넣는 글라이드. 컨텍스트를 받아오고, url 주소를 통해 이미지뷰에 연결
        if(!item.getUri().equals("")){
            Glide.with(context).load(item.uri).into(holder.imageView);
        }

        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        // 리사이클러뷰의 크기를 세는 메소드
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // 리사이클러 뷰에 사용할 뷰홀더 클래서 선언.. 각각의 개체들을 선언
        ImageView imageView;
        TextView title, location, time, price, comment, marked, status;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 생성자를 통해 각 개체들을 xml 에서 만들어 놓은 개체들과 연결
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            location = itemView.findViewById(R.id.location);
            time = itemView.findViewById(R.id.time);
            price = itemView.findViewById(R.id.price);
            comment = itemView.findViewById(R.id.comment);
            marked = itemView.findViewById(R.id.marked);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(HomeRCData item){
            // 이 클래스의 셋터
            title.setText(item.getTitle());
            location.setText(item.getLocation());
            time.setText(item.getTime());
            price.setText(item.getPrice());
            comment.setText(item.getComment());
            marked.setText(item.getMarked());

            // 게시물의 판매상태가 판매완료이면, 판매완료 문구를 보여주고, 가격 텍스트뷰의 색을 회색으로 바꿔준다.
//            if (item.getStatus().equals("soldout")) {
//                status.setVisibility(View.VISIBLE);
//                price.setTextColor(Color.GRAY);
//                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            }

        }
    }

    // 이 밑으론 아이템을 추가하거나, 셋터 겟터
    public void addItem(HomeRCData item){
        items.add(item);
    }

    public ArrayList<HomeRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<HomeRCData> items) {
        this.items = items;
    }

    public HomeRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, HomeRCData item){
        this.items.set(position, item);
    }
}
