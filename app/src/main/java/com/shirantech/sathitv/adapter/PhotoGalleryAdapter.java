package com.shirantech.sathitv.adapter;

import android.graphics.PointF;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.PhotoGalleryFragment;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.PhotoAlbum;

import java.util.List;

/**
 * Adapter for showing model photos in grid in {@link PhotoGalleryFragment}
 */
public class PhotoGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = Integer.MIN_VALUE;

    private final List<Object> mDataList;
    private final OnClickListener mClickListener;

    public PhotoGalleryAdapter(List<Object> dataList, OnClickListener clickListener) {
        this.mDataList = dataList;
        mClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gallery_header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item, parent, false);
            return new PhotoAlbumViewHolder(view, mClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TYPE_HEADER == holder.getItemViewType()) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerView.setText((Integer) getDataAt(position));
        } else {
            final PhotoAlbumViewHolder photoAlbumViewHolder = (PhotoAlbumViewHolder) holder;
            final PhotoAlbum currentPhotoAlbum = (PhotoAlbum) getDataAt(position);
            photoAlbumViewHolder.photoView.setTag(position);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(getFirstPhoto(currentPhotoAlbum).getImagePath()))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(photoAlbumViewHolder.photoView.getController())
                    .build();
            photoAlbumViewHolder.photoView.setController(controller);
            photoAlbumViewHolder.infoButton.setVisibility(currentPhotoAlbum.getPhotoAlbumStatus() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataAt(position) instanceof Integer) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    private Object getDataAt(int position) {
        return mDataList.get(position);
    }

    /**
     * Get the first photo(at index 0) for the given post
     *
     * @param photoAlbum Post to get photo from
     * @return the first photo
     */
    private Photo getFirstPhoto(PhotoAlbum photoAlbum) {
        return photoAlbum.getPhotoList().get(0);
    }

    /**
     * ViewHolder to hold photo album
     */
    public static class PhotoAlbumViewHolder extends RecyclerView.ViewHolder {

        public final SimpleDraweeView photoView;
        public final ImageButton infoButton;

        // TODO : remove this focus
        private final PointF imageFocusPoint = new PointF(0.5f, 0);

        public PhotoAlbumViewHolder(View itemView, OnClickListener mClickListener) {
            super(itemView);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.photo_view);
            photoView.getHierarchy().setActualImageFocusPoint(imageFocusPoint);
            photoView.setOnClickListener(mClickListener);
            infoButton = (ImageButton) itemView.findViewById(R.id.info_button);
            infoButton.setOnClickListener(mClickListener);
        }
    }

    /**
     * ViewHolder to hold the header title
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView headerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = (TextView) itemView;
        }
    }
}
