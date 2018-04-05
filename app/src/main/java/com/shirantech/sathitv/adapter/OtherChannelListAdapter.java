package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.TvChannel;

import java.util.List;

/**
 * Created by jyoshna on 4/19/16.
 * adapter that shows channel list below video surface view
 */
public class OtherChannelListAdapter extends RecyclerView.Adapter<OtherChannelListAdapter.ChannelViewHolder> {

    private final List<TvChannel> channelList;
    private final OnClickListener mClickListener;

    public OtherChannelListAdapter(List<TvChannel> channelList,OnClickListener clickListener) {
        this.channelList = channelList;
        mClickListener = clickListener;
    }
    @Override
    public OtherChannelListAdapter.ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_list_item, parent, false);
        return new ChannelViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        final TvChannel currentChannel = getChannelAt(position);
        holder.imageViewLogo.setTag(position);
        holder.imageViewLogo.setImageURI(Uri.parse(currentChannel.getLogoUrl()));
    }


    private TvChannel getChannelAt(int position) {
        return channelList.get(position);
    }
    @Override
    public int getItemCount() {
        return channelList.size();
    }
    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        public final SimpleDraweeView imageViewLogo;

        public ChannelViewHolder(View itemView, OnClickListener mClickListener) {
            super(itemView);
            imageViewLogo = (SimpleDraweeView) itemView.findViewById(R.id.imageViewChannel);
            imageViewLogo.setOnClickListener(mClickListener);
        }
    }
}
