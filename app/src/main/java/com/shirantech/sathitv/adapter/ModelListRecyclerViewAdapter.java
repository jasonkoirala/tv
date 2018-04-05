package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.OnReadMoreListener;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ChatActivity;
import com.shirantech.sathitv.fragment.ModelProfileFragment;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.util.List;


public class ModelListRecyclerViewAdapter extends RecyclerView.Adapter<ModelListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ModelListRecyclerViewAdapter.class.getName();
    private final List<ModelProfile> modelList;
    private Context context;
    private final boolean type = true;
    private OnReadMoreListener listener;
    ModelProfileFragment modelProfileFragment;

    public ModelListRecyclerViewAdapter(List<ModelProfile> items, OnReadMoreListener listener, Context context) {
        modelList = items;
        this.context = context;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_list_item, parent, false);
        Fresco.initialize(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = modelList.get(position);
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

        holder.textViewName.setText(holder.mItem.getName());
        holder.textViewStatus.setText(holder.mItem.getLoginStatus());
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(holder.mItem.getRating())));
        onClickReadMore(holder, position);
        String loginStatus = holder.mItem.getLoginStatus();
        if (loginStatus.equalsIgnoreCase("offline")) {
            holder.textViewStatus.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_offline_status_textview));
        } else {
            holder.textViewStatus.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_online_status_textview));
        }

      /*  holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.showLog(TAG, "position :: " + position);
//                modelProfileFragment = ModelProfileFragment.newInstance(modelList.get(position));
//                FragmentManager manager = ((Activity) context).getFragmentManager();
//                manager.beginTransaction().replace(R.id.containerModel,
//                        modelProfileFragment).commit();
            }
        });*/
        holder.buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                AppLog.showLog(TAG, "user id :: " + holder.mItem.getUserId());
                intent.putExtra(ChatActivity.EXTRA_ID, holder.mItem.getUserId());
                intent.putExtra(ChatActivity.EXTRA_NAME, holder.mItem.getName());
                intent.putExtra(ChatActivity.EXTRA_IMAGE, holder.mItem.getImage());
                v.getContext().startActivity(intent);
            }
        });
    }

    private void onClickReadMore(ViewHolder holder, final int position) {
        String text = holder.mItem.getSummary() + " Read More";
        SpannableString ss = new SpannableString(text);
        int start = text.lastIndexOf(" ") - 4;
        int end = text.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                listener.callback(position);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.textViewDescription.setText(ss);
        holder.textViewDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_size));
        holder.textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout linearLayout;
        public final SimpleDraweeView imageView;
        public final CustomFontTextView textViewName, textViewDescription, textViewStatus;
        public final RatingBar ratingBar;
        private final Button buttonChat;
        public ModelProfile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewModel);
            textViewName = (CustomFontTextView) view.findViewById(R.id.textViewModelName);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutModelList);
            textViewDescription = (CustomFontTextView) view.findViewById(R.id.textViewModelDescription);
            textViewStatus = (CustomFontTextView) view.findViewById(R.id.textViewStatus);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBarModel);
            buttonChat = (Button) view.findViewById(R.id.buttonModelChat);
        }
    }


}
