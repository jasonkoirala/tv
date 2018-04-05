package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.response.InterviewsReplyResponse;

import java.util.List;

/**
 * Adapter for showing list of replies in {@link com.shirantech.sathitv.activity.InterviewActivity}
 */
public class InterviewListAdapter extends RecyclerView.Adapter<InterviewListAdapter.InterviewViewHolder> {
    private final List<InterviewsReplyResponse.InterviewItem> interviewItemList;
    private View.OnClickListener mReplyClickListener;

    public InterviewListAdapter(List<InterviewsReplyResponse.InterviewItem> interviewItemList, View.OnClickListener replyClickListener) {
        this.interviewItemList = interviewItemList;
        this.mReplyClickListener = replyClickListener;
    }

    @Override
    public InterviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interview_list_item, parent, false);
        return new InterviewViewHolder(view, mReplyClickListener);
    }

    @Override
    public void onBindViewHolder(InterviewViewHolder holder, int position) {
        final InterviewsReplyResponse.InterviewItem currentInterviewItem = getInterviewItemAt(position);
        holder.titleTextView.setText(currentInterviewItem.getTitle());
        holder.descriptionTextView.setText(currentInterviewItem.getDescription());
        holder.itemView.setTag(position);
        String currentPhoto = currentInterviewItem.getImage();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(currentPhoto))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.photoView.getController())
                .build();
        holder.photoView.setController(controller);
//        photoViewHolder.photoView.setAspectRatio(currentPhoto.getAspectRatio());
     /*   String currentPhoto = currentInterviewItem.getImage();
        // but for brevity, use the ImageView specific builder...
        Ion.with(holder.videoBg)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .animateLoad(spinAnimation)
                .animateIn(fadeInAnimation)
                .load("http://example.com/image.png");*/
    }

    @Override
    public int getItemCount() {
        return interviewItemList.size();
    }

    private InterviewsReplyResponse.InterviewItem getInterviewItemAt(int position) {
        return interviewItemList.get(position);
    }

    /**
     * ViewHolder to hold Janam Kundali reply
     */
    public static class InterviewViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTextView, descriptionTextView;
//        public final ImageView playIcon;
//        public final FrameLayout videoBg;
        public final SimpleDraweeView photoView;
        public InterviewViewHolder(View itemView, View.OnClickListener mReplyClickListener) {
            super(itemView);
            itemView.setOnClickListener(mReplyClickListener);
            titleTextView = (TextView) itemView.findViewById(R.id.textViewHorscopeSubject);
            descriptionTextView = (TextView) itemView.findViewById(R.id.textviewHoroscopeDescription);
//            playIcon = (ImageView) itemView.findViewById(R.id.play_icon);
//            videoBg = (FrameLayout) itemView.findViewById(R.id.frame_video_bg);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.photo_view);
        }

      /*  private void getVideoThumbnail(String videoUrl){
            Uri mUri =Uri.parse(videoUrl);
                    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(mUri);
            mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
            mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
            byte [] artwork = mmr.getEmbeddedPicture();

            mmr.release();
        }*/
    }
}
