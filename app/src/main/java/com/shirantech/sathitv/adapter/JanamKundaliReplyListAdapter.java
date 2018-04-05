package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.content.Intent;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.CheenaPreviewActivity;
import com.shirantech.sathitv.fragment.JanamKundaliReplyFragment;
import com.shirantech.sathitv.model.JanamKundali;
import com.shirantech.sathitv.utils.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Adapter for showing list of replies in {@link JanamKundaliReplyFragment}
 */
public class JanamKundaliReplyListAdapter extends RecyclerView.Adapter<JanamKundaliReplyListAdapter.JanamKundaliViewHolder> {
    private static final String TAG = JanamKundaliReplyListAdapter.class.getName();
    private final List<JanamKundali> mJanamKundaliList;
    private final Context context;

    private View.OnClickListener mReplyClickListener;

    public JanamKundaliReplyListAdapter(List<JanamKundali> janamKundaliList, View.OnClickListener replyClickListener, Context context) {
        this.mJanamKundaliList = janamKundaliList;
        this.mReplyClickListener = replyClickListener;
        this.context = context;
    }

    @Override
    public JanamKundaliViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.janam_kundali_reply_list_item, parent, false);
        return new JanamKundaliViewHolder(view, mReplyClickListener);
    }

    @Override
    public void onBindViewHolder(JanamKundaliViewHolder holder, int position) {
        final JanamKundali currentJanamKundali = getJanamKundaliAt(position);
        AppLog.showLog("birth place :: "+currentJanamKundali.getBirthPlace());
        holder.nameTextView.setText(currentJanamKundali.getName());
        holder.genderTextView.setText(currentJanamKundali.getGender());
//        holder.dobTextView.setText(currentJanamKundali.getBirthDate());
        holder.timeTextView.setText(currentJanamKundali.getBirthTime());
        holder.placeTextView.setText(currentJanamKundali.getBirthPlace());
        holder.queryTextView.setText(currentJanamKundali.getQuery());
        holder.replyTextView.setText(currentJanamKundali.getReply());
        holder.playIcon.setVisibility(currentJanamKundali.getStatus() ? View.VISIBLE : View.GONE);
        holder.videoTextView.setVisibility(currentJanamKundali.getStatus() ? View.VISIBLE : View.GONE);
        holder.itemView.setTag(position);
        convertDateFormat(currentJanamKundali, holder);
        convertBirthDateFormat(currentJanamKundali,holder);
        if(TextUtils.isEmpty(currentJanamKundali.getImage())){
            holder.buttonView.setVisibility(View.GONE);
        }else{
            holder.buttonView.setVisibility(View.VISIBLE);
            holder.buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CheenaPreviewActivity.class);
                    intent.putExtra(CheenaPreviewActivity.EXTRA_IMAGE, currentJanamKundali.getImage());
                    context.startActivity(intent);
                }
            });
        }

    }

    private void convertBirthDateFormat(JanamKundali currentJanamKundali, JanamKundaliViewHolder holder) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy ");
        Date birthDate = null;
        String formattedBirthDate = null;

        try {
          /*
            * replied query date format changed.
            * */
        birthDate = inputFormat.parse(currentJanamKundali.getBirthDate());
        formattedBirthDate = outputFormat.format(birthDate);
        holder.dobTextView.setText(formattedBirthDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void convertDateFormat(JanamKundali currentJanamKundali, JanamKundaliViewHolder holder) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
        Date requestedDate, repliedDate=null;
        String formattedRequestedDate, formattedRepliedDate = null;

        try {
            /*
            * requested query date format changed.
            * */
            requestedDate = inputFormat.parse(currentJanamKundali.getRequestedDate().getDate());
            formattedRequestedDate = outputFormat.format(requestedDate);
            holder.requestedDateTextView.setText(formattedRequestedDate);

            /*
            * replied query date format changed.
            * */
            repliedDate = inputFormat.parse(currentJanamKundali.getRepliedDate().getDate());
            formattedRepliedDate = outputFormat.format(repliedDate);
            holder.repliedDateTextView.setText(formattedRepliedDate);



        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mJanamKundaliList.size();
    }

    private JanamKundali getJanamKundaliAt(int position) {
        return mJanamKundaliList.get(position);
    }

    /**
     * ViewHolder to hold Janam Kundali reply
     */
    public static class JanamKundaliViewHolder extends RecyclerView.ViewHolder {

        public final TextView nameTextView, genderTextView, dobTextView,
                timeTextView, placeTextView, queryTextView, replyTextView, requestedDateTextView,repliedDateTextView;
        public final Button buttonView;
        public final LinearLayout videoTextView;
        public final ImageView playIcon;

        public JanamKundaliViewHolder(View itemView, View.OnClickListener mReplyClickListener) {
            super(itemView);
            itemView.setOnClickListener(mReplyClickListener);
            nameTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyName);
            genderTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyGender);
            dobTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyDate);
            timeTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyTime);
            queryTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyQuery);
            replyTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReply);
            placeTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliReplyBirthPlace);
            requestedDateTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliRequestedDate);
            repliedDateTextView = (TextView) itemView.findViewById(R.id.textViewJanamKundaliRepliedDate);
            buttonView = (Button) itemView.findViewById(R.id.buttonViewCheena);
            playIcon = (ImageView) itemView.findViewById(R.id.play_icon);
            videoTextView = (LinearLayout) itemView.findViewById(R.id.linearLayoutPlay);

        }
    }
}
