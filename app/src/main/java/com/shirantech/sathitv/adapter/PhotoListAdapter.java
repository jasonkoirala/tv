package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.PhotoAlbumActivity;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.PhotoAlbum;

/**
 * Adapter for showing photo album details in {@link PhotoAlbumActivity}
 */
public class PhotoListAdapter extends RecyclerView.Adapter<ViewHolder> {
    @VisibleForTesting
    protected static final int TYPE_HEADER = Integer.MIN_VALUE;

    private final PhotoAlbum mCurrentPhotoAlbum;
    private final boolean mOwnPhoto;
    private final View.OnClickListener mPhotoClickListener;

    /**
     * Initialize the adapter with the current post
     *
     * @param photoAlbum               the current post
     * @param ownPhoto           whether this post is owned by the current user or not
     * @param photoClickListener click listener for the photo
     */
    public PhotoListAdapter(final PhotoAlbum photoAlbum, boolean ownPhoto, final View.OnClickListener photoClickListener) {
        this.mCurrentPhotoAlbum = photoAlbum;
        this.mOwnPhoto = ownPhoto;
        mPhotoClickListener = photoClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_album_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_album_item, parent, false);
            return new PhotoViewHolder(view, mOwnPhoto, mPhotoClickListener);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.photoAlbumDescriptionView.setText(getPhotoAlbumDescription());
            if (TextUtils.isEmpty(getPhotoAlbumDescription())) {
                headerViewHolder.photoAlbumDescriptionView.setVisibility(View.GONE);
            }
        } else {
            final Photo currentPhoto = getPhotoAt(getActualPosition(position));
            PhotoViewHolder photoViewHolder = ((PhotoViewHolder) holder);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(currentPhoto.getImagePath()))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(photoViewHolder.photoView.getController())
                    .build();
            photoViewHolder.photoView.setController(controller);
            photoViewHolder.photoView.setAspectRatio(currentPhoto.getAspectRatio());

            photoViewHolder.captionView.setText(currentPhoto.getPhotoCaption());
            photoViewHolder.captionView.setVisibility(
                    TextUtils.isEmpty(currentPhoto.getPhotoCaption()) ? View.GONE : View.VISIBLE);

            photoViewHolder.photoRatingView.setText(String.valueOf(currentPhoto.getPhotoRating()));
            photoViewHolder.photoView.setTag(getActualPosition(position));
            photoViewHolder.commentButton.setTag(currentPhoto);
            photoViewHolder.submitRatingButton.setTag(currentPhoto);
            photoViewHolder.editButton.setTag(currentPhoto);
            photoViewHolder.photoRatingBar.setRating(getPhotoAt(getActualPosition(position)).getMyRating());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mCurrentPhotoAlbum.getPhotoList().size() + 1;
    }

    @VisibleForTesting
    protected Photo getPhotoAt(int position) {
        return mCurrentPhotoAlbum.getPhotoList().get(position);
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

    @VisibleForTesting
    protected String getPhotoAlbumDescription() {
        return mCurrentPhotoAlbum.getDescription();
    }

    /**
     * {@link ViewHolder} for showing the Header
     */
    public static class HeaderViewHolder extends ViewHolder {

        public final TextView photoAlbumDescriptionView;
        public final ImageButton commentButton;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            photoAlbumDescriptionView = (TextView) headerView.findViewById(R.id.photo_album_description_view);
            commentButton = (ImageButton) headerView.findViewById(R.id.comment_button);
        }
    }

    /**
     * {@link ViewHolder} for showing photo list
     */
    public static class PhotoViewHolder extends ViewHolder implements View.OnClickListener {
        public final SimpleDraweeView photoView;
        public final TextView captionView;
        public final TextView photoRatingView;
        public final ImageButton commentButton, ratingButton, editButton;
        private final FrameLayout actionLayout;
        private final LinearLayout rateLayout;
        public final RatingBar photoRatingBar;
        public final Button submitRatingButton;

        private final View.OnClickListener mClickListener;

        public PhotoViewHolder(final View itemView, boolean mOwnPhoto, final View.OnClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.photo_view);
            photoView.setOnClickListener(clickListener);
            captionView = (TextView) itemView.findViewById(R.id.caption_view);
            photoRatingView = (TextView) itemView.findViewById(R.id.rate_view);
            commentButton = (ImageButton) itemView.findViewById(R.id.comment_button);
            commentButton.setOnClickListener(this);
            ratingButton = (ImageButton) itemView.findViewById(R.id.rate_button);
            ratingButton.setOnClickListener(this);
            ratingButton.setVisibility(mOwnPhoto ? View.GONE : View.VISIBLE);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(this);
            editButton.setVisibility(mOwnPhoto ? View.VISIBLE : View.GONE);
            actionLayout = (FrameLayout) itemView.findViewById(R.id.action_layout);
            rateLayout = (LinearLayout) itemView.findViewById(R.id.rate_layout);
            photoRatingBar = (RatingBar) itemView.findViewById(R.id.photo_ratingbar);
            submitRatingButton = (Button) itemView.findViewById(R.id.submit_rate_button);
            submitRatingButton.setOnClickListener(this);
        }

        /**
         * Show/hide the rating layout(layout for rating)
         *
         * @param showRate <code>true</code> to show <code>false</code> otherwise
         */
        private void showRatingView(boolean showRate) {
            if (showRate) {
                final Animation animationOut = AnimationUtils.loadAnimation(
                        itemView.getContext(), R.anim.slide_out_left);
                final Animation animationIn = AnimationUtils.loadAnimation(
                        itemView.getContext(), R.anim.slide_in_right);

                actionLayout.startAnimation(animationOut);
                actionLayout.setVisibility(View.GONE);
                rateLayout.setVisibility(View.VISIBLE);
                rateLayout.startAnimation(animationIn);
            } else {
                final Animation animationOut = AnimationUtils.loadAnimation(
                        itemView.getContext(), R.anim.slide_out_right);
                final Animation animationIn = AnimationUtils.loadAnimation(
                        itemView.getContext(), R.anim.slide_in_left);

                rateLayout.startAnimation(animationOut);
                rateLayout.setVisibility(View.GONE);
                actionLayout.setVisibility(View.VISIBLE);
                actionLayout.startAnimation(animationIn);
            }
        }

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.comment_button:
                case R.id.edit_button: {
                    mClickListener.onClick(clickedView);
                    break;
                }
                case R.id.rate_button: {
                    showRatingView(true);
                    break;
                }
                case R.id.submit_rate_button: {
                    final Photo currentPhoto = (Photo) clickedView.getTag();
                    currentPhoto.setMyRating(photoRatingBar.getRating());

                    // when submitting rating, send click event to the Activity and then to the server.
                    // send data by setting tag in the view
                    clickedView.setTag(currentPhoto);
                    mClickListener.onClick(clickedView);
                    showRatingView(false);
                    break;
                }
            }
        }
    }
}
