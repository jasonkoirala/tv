package com.shirantech.sathitv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.Comment;

import java.util.List;

/**
 * Adapter for showing comment list.
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {
    private final List<Comment> mCommentList;

    public CommentListAdapter(List<Comment> mCommentList) {
        this.mCommentList = mCommentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        final Comment currentComment = getCommentAt(position);
        holder.usernameView.setText(currentComment.getUserName());
        holder.commentView.setText(currentComment.getComment());
        holder.commentStatusView.setVisibility(currentComment.isPostingComment() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    private Comment getCommentAt(int position) {
        return mCommentList.get(position);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        public final TextView usernameView, commentView, commentStatusView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            usernameView = (TextView) itemView.findViewById(R.id.username_view);
            commentView = (TextView) itemView.findViewById(R.id.comment_view);
            commentStatusView = (TextView) itemView.findViewById(R.id.comment_status_view);
        }
    }
}
