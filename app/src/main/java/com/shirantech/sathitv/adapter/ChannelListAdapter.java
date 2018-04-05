package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ChannelListActivity;
import com.shirantech.sathitv.model.TvChannel;

import java.util.List;

/**
 * Adapter for showing channels in grid in {@link ChannelListActivity}
 */
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder> {
    private final List<TvChannel> mChannelList;
    private final OnClickListener mClickListener;

    public ChannelListAdapter(List<TvChannel> channelList, OnClickListener clickListener) {
        this.mChannelList = channelList;
        mClickListener = clickListener;
    }

    @Override
    public ChannelListAdapter.ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new ChannelViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(ChannelListAdapter.ChannelViewHolder holder, int position) {
        final TvChannel currentChannel = getChannelAt(position);
        holder.channelLogo.setTag(position);
        holder.channelLogo.setImageURI(Uri.parse(currentChannel.getLogoUrl()));
        holder.infoButton.setImageResource(currentChannel.isFree() ? R.drawable.ic_info_free :
                R.drawable.ic_info_paid);
        holder.channelNameView.setText(currentChannel.getName());
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    private TvChannel getChannelAt(int position) {
        return mChannelList.get(position);
    }

    /**
     * ViewHolder to hold channel logos
     */
    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        public final SimpleDraweeView channelLogo;
        public final ImageButton infoButton;
        public final TextView channelNameView;

        public ChannelViewHolder(View itemView, OnClickListener mClickListener) {
            super(itemView);
            channelLogo = (SimpleDraweeView) itemView.findViewById(R.id.photo_view);
            channelLogo.setOnClickListener(mClickListener);
            infoButton = (ImageButton) itemView.findViewById(R.id.info_button);
            infoButton.setClickable(false);
            channelNameView = (TextView) itemView.findViewById(R.id.photo_title);
            channelNameView.setVisibility(View.VISIBLE);
        }
    }
}
