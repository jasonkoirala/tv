package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.FullscreenPlayerActivity;
import com.shirantech.sathitv.activity.YoutubePlayerActivity;
import com.shirantech.sathitv.model.VOD;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomFontBoldTextView;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class VODRecyclerViewAdapter extends RecyclerView.Adapter<VODRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = VODRecyclerViewAdapter.class.getName();
    private final List<VOD> vodList;
    private Context context;
    private final boolean type = true;
//    private final HoroscopeFragment.OnListFragmentInteractionListener mListener;

    public VODRecyclerViewAdapter(List<VOD> items, Context context) {
        vodList = items;
//        mListener = listener;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vod_list_item, parent, false);
        Fresco.initialize(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = vodList.get(position);
        String currentPhoto = holder.mItem.getImage();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(currentPhoto))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.imageView.getController())
                .build();
        holder.imageView.setController(controller);

        holder.textViewTitle.setText(holder.mItem.getTitle());
        holder.textViewDuration.setText(holder.mItem.getDuration());
        holder.textViewTypeName.setText(holder.mItem.getTypeName());
        convertDateFormat(holder);
        if (vodList.get(position).getType()) {
            holder.textViewTypeName.setVisibility(View.GONE);
        } else {
            holder.textViewTypeName.setVisibility(View.VISIBLE);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vodList.get(position).getType()) {
//                    context.startActivity(YoutubePlayerActivity.getStartIntent(context, "https://www.youtube.com/watch?v=SweCMjgu1_A"));
                    context.startActivity(YoutubePlayerActivity.getStartIntent(context, vodList.get(position).getUrl()));
//                    context.startActivity(FullscreenPlayerActivity.getStartIntent(context, vodList.get(position).getUrl()));
                } else {
                    context.startActivity(FullscreenPlayerActivity.getStartIntent(context, vodList.get(position).getVideo()));
                }
            }
        });

    }

    private void convertDateFormat(ViewHolder holder) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
        Date date = null;
        String changedDate = null;

        try {
            date = inputFormat.parse(holder.mItem.getCreated());
            changedDate = outputFormat.format(date);
            holder.textViewDate.setText(changedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return vodList.size();
    }

    public void addMoreVideos(List<VOD> vodDtoList) {
        vodList.addAll(vodDtoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RelativeLayout relativeLayout;
        public final SimpleDraweeView imageView;
        public final CustomFontTextView textViewDate, textViewTypeName, textViewDuration;
        public final CustomFontBoldTextView textViewTitle;
        public VOD mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutVod);
            imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewVod);
            textViewTitle = (CustomFontBoldTextView) view.findViewById(R.id.textViewVodTitle);
            textViewDate = (CustomFontTextView) view.findViewById(R.id.textviewVodDate);
            textViewTypeName = (CustomFontTextView) view.findViewById(R.id.textViewVodTypeName);
            textViewDuration = (CustomFontTextView) view.findViewById(R.id.textViewVodDuration);
        }
    }


}
