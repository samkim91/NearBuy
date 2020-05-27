package com.example.trading.Fragments.Post;

import android.view.View;

public interface OnCommentRCClickListener {

    void onReplyClick(CommentRCAdapter.ViewHolder viewHolder, View view, int position);

    void onMoreClick(CommentRCAdapter.ViewHolder viewHolder, View view, int position);
}
