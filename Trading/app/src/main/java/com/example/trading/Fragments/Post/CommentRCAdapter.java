package com.example.trading.Fragments.Post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.trading.R;

import java.util.ArrayList;

public class CommentRCAdapter extends RecyclerView.Adapter<CommentRCAdapter.ViewHolder> implements OnCommentRCClickListener {

    ArrayList<CommentRCData> items = new ArrayList<>();

    Context context;

    String TAG = "CommentRCAdapter";

    OnCommentRCClickListener listener;

    @Override
    public long getItemId(int position) {
        return Long.parseLong(items.get(position).getCommentNum());
    }

    public CommentRCAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_comment_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentRCData item = items.get(position);

        if (!item.getuImage().equals("")) {
            Glide.with(context).load(item.getuImage()).apply(new RequestOptions().circleCrop()).into(holder.userImg);
        }

        // 만약 아이템이 대댓글일 경우 유저이미지뷰를 오른쪽으로 약간 말어준다.
        // 원래는 부모댓글 유무로 조건을 세우려 했으나, null 값이 존재하므로 json화 하는데 에러가 발생함. 그래서 대체방법으로 sequence를 조건으로 세움
        // seq 값 1 이상의 값은 모두 대댓글임
        if ( item.getSequence() > 1){
            // 자바단에서 레이아웃을 수정하기 위한 작업

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.userImg.getLayoutParams();
            layoutParams.leftMargin = 100;      // 100 pixel
            holder.userImg.setLayoutParams(layoutParams);    // 이미지뷰에 다시 파라미터값을 셋 해줌.
        } else {
            // 뷰홀더에서 데이터 꼬임 문제가 나타나서 만들어준 부분
            // 댓글의 경우에는 왼쪽 마진을 없애준다.

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.userImg.getLayoutParams();
            layoutParams.leftMargin = 0;      // 0 pixel
            holder.userImg.setLayoutParams(layoutParams);    // 이미지뷰에 다시 파라미터값을 셋 해줌.
        }

        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnCommentRCClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMoreClick(ViewHolder viewHolder, View view, int position) {
        if (listener != null) {
            listener.onMoreClick(viewHolder, view, position);
        }
    }

    @Override
    public void onReplyClick(ViewHolder viewHolder, View view, int position) {
        if (listener != null) {
            listener.onMoreClick(viewHolder, view, position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImg;
        ImageView moreBtn;
        TextView userNickname;
        TextView writeTime;
        TextView userComment;
        TextView replyBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.userImg);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            userNickname = itemView.findViewById(R.id.userNickname);
            writeTime = itemView.findViewById(R.id.writeTime);
            userComment = itemView.findViewById(R.id.userComment);
            replyBtn = itemView.findViewById(R.id.replyBtn);

            // TODO 뷰홀더에 두개의 클릭 기능이 되는가 ??

            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "moreBtn Clicked");
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onMoreClick(ViewHolder.this, v, position);
                    }
                }
            });

            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "replyBtn Clicked");
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onReplyClick(ViewHolder.this, v, position);
                    }
                }
            });


        }

        public void setItem(CommentRCData item){
            userNickname.setText(item.getuNickname());
            writeTime.setText(item.getuTime());
            userComment.setText(item.getuComment());
        }

    }

    public void addItem(CommentRCData item){
        items.add(item);
    }

    public ArrayList<CommentRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<CommentRCData> items) {
        this.items = items;
    }

    public CommentRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, CommentRCData item){
        this.items.set(position, item);
    }
}

