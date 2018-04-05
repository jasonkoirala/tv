package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
import com.shirantech.sathitv.activity.FullscreenPlayerActivity;
import com.shirantech.sathitv.activity.YoutubePlayerActivity;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.model.VOD;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.util.List;


public class ModelAllPhotosRecyclerViewAdapter extends RecyclerView.Adapter<ModelAllPhotosRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ModelAllPhotosRecyclerViewAdapter.class.getName();

    public interface  OnPhotoClickListener{
        void onItemClick(int position);
    }
    private final List<ModelProfile> modelProfileList;
    private Context context;
    private final boolean type = true;
    OnPhotoClickListener listener;
//    private final HoroscopeFragment.OnListFragmentInteractionListener mListener;




    public ModelAllPhotosRecyclerViewAdapter(List<ModelProfile> items, Context context, OnPhotoClickListener listener) {
        modelProfileList = items;
        this.listener = listener;
        this.context = context;

    }
    

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_photos_item, parent, false);
        Fresco.initialize(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = modelProfileList.get(position);
        String currentPhoto = holder.mItem.getImage();
        AppLog.showLog(TAG, "currentPhoto :: "+currentPhoto);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(currentPhoto))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.imageView.getController())
                .build();
        holder.imageView.setController(controller);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.showLog(TAG, "position :: "+position);
                listener.onItemClick(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelProfileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView imageView;
        public ModelProfile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewAllPhotosItem);
        }
    }


}
