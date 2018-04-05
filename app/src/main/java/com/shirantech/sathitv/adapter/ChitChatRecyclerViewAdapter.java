package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.model.ChitChat;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.util.List;


public class ChitChatRecyclerViewAdapter extends RecyclerView.Adapter<ChitChatRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ChitChatRecyclerViewAdapter.class.getName();
    private final List<ChitChat> chitChatList;
    private Context context;

    public ChitChatRecyclerViewAdapter(List<ChitChat> items, Context context) {
        chitChatList = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_chitchat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = chitChatList.get(position);
        boolean isInitiate = holder.mItem.isInitiate();
        if(!TextUtils.isEmpty(PreferencesHelper.getProfileImageUrl(context))){
            String senderImage = PreferencesHelper.getProfileImageUrl(context);
            AppLog.showLog("senderImage url :::  " + senderImage);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(senderImage))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.imageViewSender.getController())
                    .build();
            holder.imageViewSender.setController(controller);
        }

        if(!TextUtils.isEmpty(PreferencesHelper.getImageUrl(context))){
            String receiverImage = PreferencesHelper.getImageUrl(context);
            AppLog.showLog("receiver url :::  "+receiverImage);
            ImageRequest requestReceiver = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(receiverImage))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controllerReceiver = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(requestReceiver)
                    .setOldController(holder.imageViewReceiver.getController())
                    .build();
            holder.imageViewReceiver.setController(controllerReceiver);
        }



        if (!isInitiate) {
            holder.linearLayoutSender.setVisibility(View.GONE);
            holder.linearLayoutReceiver.setVisibility(View.VISIBLE);
            holder.textViewReceiver.setText(holder.mItem.getMessage());
        } else {
            holder.linearLayoutReceiver.setVisibility(View.GONE);
            holder.linearLayoutSender.setVisibility(View.VISIBLE);
            holder.textViewSender.setText(holder.mItem.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return chitChatList.size();
    }


    public void addLatestMessage(ChitChat chitChat) {
        chitChatList.add(chitChat);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout linearLayoutSender, linearLayoutReceiver;
        public final CustomFontTextView textViewReceiver, textViewSender;
        public final SimpleDraweeView imageViewReceiver, imageViewSender;
        public ChitChat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            linearLayoutReceiver = (LinearLayout) view.findViewById(R.id.linearLayoutReceiver);
            linearLayoutSender = (LinearLayout) view.findViewById(R.id.linearLayoutSender);
            textViewSender = (CustomFontTextView) view.findViewById(R.id.textViewChatSender);
            textViewReceiver = (CustomFontTextView) view.findViewById(R.id.textViewChatReceiver);
            imageViewReceiver = (SimpleDraweeView) view.findViewById(R.id.imageViewReciever);

            imageViewSender = (SimpleDraweeView) view.findViewById(R.id.imageViewSender);
        }
    }
}
