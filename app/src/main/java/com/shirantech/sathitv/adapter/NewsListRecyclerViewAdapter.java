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
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.OnReadMoreListener;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.NewsWebViewActivity;
import com.shirantech.sathitv.model.News;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomFontBoldTextView;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NewsListRecyclerViewAdapter extends RecyclerView.Adapter<NewsListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = NewsListRecyclerViewAdapter.class.getName();
    private final List<News> newsList;
    private Context context;

    public NewsListRecyclerViewAdapter(List<News> items, Context context) {
        newsList = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false);
        Fresco.initialize(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = newsList.get(position);
        String currentPhoto = holder.mItem.getImage();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(currentPhoto))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.imageView.getController())
                .build();
        AppLog.showLog(TAG, "controller " + controller);
        holder.imageView.setController(controller);
        holder.textViewTitle.setText(holder.mItem.getTitle());
        holder.textViewSummary.setText(holder.mItem.getSummary());
        holder.textViewSource.setText(holder.mItem.getNews_source());
        convertDateFormat(holder);
        AppLog.showLog(TAG, " date :: " + holder.mItem.getCreated());
        onClickReadMore(holder, position);
       /* holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsWebViewActivity.class);
                intent.putExtra(NewsWebViewActivity.EXTRA_URL, holder.mItem.getNews_url());
                context.startActivity(intent);
          }
        });*/

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

    private void onClickReadMore(ViewHolder holder, final int position) {
        String text = holder.mItem.getSummary() + " Read More";
        final String newsUrl = holder.mItem.getNews_url();
        SpannableString ss = new SpannableString(text);
        int start = text.lastIndexOf(" ") - 4;
        int end = text.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(context, NewsWebViewActivity.class);
                intent.putExtra(NewsWebViewActivity.EXTRA_URL, newsUrl);
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.textViewSummary.setText(ss);
        holder.textViewSummary.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_size_news));
        holder.textViewSummary.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void addMoreVideos(List<News> newsDtoList) {
        newsList.addAll(newsDtoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView imageView;
        public final LinearLayout linearLayout;
        public final CustomFontTextView textViewSummary, textViewSource, textViewDate;
        public final CustomFontBoldTextView textViewTitle;
        public News mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewNews);
            textViewTitle = (CustomFontBoldTextView) view.findViewById(R.id.textViewNewsTitle);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutNews);
            textViewSummary = (CustomFontTextView) view.findViewById(R.id.textViewNewsSummary);
            textViewSource = (CustomFontTextView) view.findViewById(R.id.textViewNewsSource);
            textViewDate = (CustomFontTextView) view.findViewById(R.id.textViewNewsDate);
        }
    }


}
