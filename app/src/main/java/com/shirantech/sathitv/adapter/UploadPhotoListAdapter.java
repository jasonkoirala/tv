package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.PhotoUploadActivity;
import com.shirantech.sathitv.model.PhotoToUpload;

import java.util.List;

/**
 * Adapter for showing photos to upload in {@link PhotoUploadActivity}
 */
public class UploadPhotoListAdapter extends RecyclerView.Adapter<ViewHolder> {
    @VisibleForTesting
    protected static final int TYPE_HEADER = Integer.MIN_VALUE;

    private final List<PhotoToUpload> mUploadPhotoList;
    private final View.OnClickListener mClickListener;

    private String mPhotoAlbumDescription;

    /**
     * Initialize the adapter with the list of photos
     *
     * @param mUploadPhotoList list of photos to upload
     */
    public UploadPhotoListAdapter(List<PhotoToUpload> mUploadPhotoList, View.OnClickListener clickListener) {
        this.mUploadPhotoList = mUploadPhotoList;
        this.mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_upload_list_header, parent, false);
            return new HeaderViewHolder(view, new InputTextWatcher());
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_upload_list_item, parent, false);
            return new PhotoViewHolder(view, new InputTextWatcher(), mClickListener);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 && holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.inputTextWatcher.updatePosition(position);
            headerViewHolder.photoAlbumDescriptionEditText.setText(mPhotoAlbumDescription);
        } else {
            PhotoViewHolder photoViewHolder = ((PhotoViewHolder) holder);
            if (getPhotoAt(getActualPosition(position)).isPhotoValid()) {
                Uri photoUri = Uri.parse(
                        String.format("file://%s", getPhotoAt(getActualPosition(position)).getPhotoUri().getPath()));
                photoViewHolder.photoView.setImageURI(photoUri);
                photoViewHolder.photoView.setAspectRatio(getPhotoAt(getActualPosition(position)).getPhotoAspectRatio());
                photoViewHolder.inputTextWatcher.updatePosition(position);
                photoViewHolder.removePhotoButton.setTag(getActualPosition(position));
                photoViewHolder.photoCaptionView.setText(getPhotoAt(getActualPosition(position)).getPhotoCaption());
            } else {
                final Uri errorImageUri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.drawable.image_loading_error))
                        .build();
                photoViewHolder.photoView.setImageURI(errorImageUri);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mUploadPhotoList.size() + 1;
    }

    @VisibleForTesting
    protected PhotoToUpload getPhotoAt(int position) {
        return mUploadPhotoList.get(position);
    }

    /**
     * Get the actual position by subtracting the the header count from the given position.
     *
     * @param position current position
     * @return the actual position
     */
    private int getActualPosition(int position) {
        return position - 1;
    }

    /**
     * Get the description for the photo album.
     *
     * @return the photo album description
     */
    public String getPhotoAlbumDescription() {
        return mPhotoAlbumDescription;
    }

    /**
     * {@link TextWatcher} with position
     */
    private class InputTextWatcher implements TextWatcher {

        private int position = -1;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (position != -1) {
                if (position == 0) {
                    mPhotoAlbumDescription = s.toString();
                } else {
                    mUploadPhotoList.get(position - 1).setPhotoCaption(s.toString());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // no op
        }
    }

    /**
     * {@link RecyclerView.ViewHolder} for showing the Header
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public final EditText photoAlbumDescriptionEditText;
        public final InputTextWatcher inputTextWatcher;

        public HeaderViewHolder(View headerView, InputTextWatcher inputTextWatcher) {
            super(headerView);
            photoAlbumDescriptionEditText = (EditText) headerView.findViewById(R.id.photo_album_description_edittext);
            photoAlbumDescriptionEditText.addTextChangedListener(inputTextWatcher);
            this.inputTextWatcher = inputTextWatcher;
        }
    }

    /**
     * {@link RecyclerView.ViewHolder} for showing photo list
     */
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final SimpleDraweeView photoView;
        public final EditText photoCaptionView;
        public final ImageButton removePhotoButton;
        public final InputTextWatcher inputTextWatcher;

        public PhotoViewHolder(View itemView, InputTextWatcher inputTextWatcher, View.OnClickListener clickListener) {
            super(itemView);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.photo_view);
            photoCaptionView = (EditText) itemView.findViewById(R.id.photo_caption_view);
            photoCaptionView.addTextChangedListener(inputTextWatcher);
            removePhotoButton = (ImageButton) itemView.findViewById(R.id.remove_photo_button);
            removePhotoButton.setOnClickListener(clickListener);
            this.inputTextWatcher = inputTextWatcher;
        }
    }
}
