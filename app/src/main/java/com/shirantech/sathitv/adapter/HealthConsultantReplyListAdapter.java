package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.CheenaPreviewActivity;
import com.shirantech.sathitv.model.HealthConsultant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Adapter for showing list of replies in {@link com.shirantech.sathitv.fragment.HealthConsultantReplyFragment}
 */
public class HealthConsultantReplyListAdapter extends RecyclerView.Adapter<HealthConsultantReplyListAdapter.HealthConsultantViewHolder> {
    private final List<HealthConsultant> mHealthConsultantList;
    private View.OnClickListener mReplyClickListener;
    private Context context;

    public HealthConsultantReplyListAdapter(List<HealthConsultant> healthConsultantList, View.OnClickListener replyClickListener, Context context) {
        this.mHealthConsultantList = healthConsultantList;
        this.mReplyClickListener = replyClickListener;
        this.context = context;
    }

    @Override
    public HealthConsultantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.health_consultant_reply_list_item, parent, false);
        return new HealthConsultantViewHolder(view, mReplyClickListener);
    }

    @Override
    public void onBindViewHolder(HealthConsultantViewHolder holder, int position) {
        final HealthConsultant currentHealthConsultant = getHealthConsultantAt(position);
        holder.textViewSubject.setText(currentHealthConsultant.getSubject());
        holder.textViewReply.setText(currentHealthConsultant.getReply());
        holder.textViewMessage.setText(currentHealthConsultant.getMessage());
        holder.textViewSymptoms.setText(currentHealthConsultant.getSymptoms());
        holder.requestedDateTextView.setText(currentHealthConsultant.getRequestedDate().getDate());
        holder.repliedDateTextView.setText(currentHealthConsultant.getRequestedDate().getDate());
        convertDateFormat(currentHealthConsultant, holder);
        holder.itemView.setTag(position);


        if (TextUtils.isEmpty(currentHealthConsultant.getUploadImage())) {
            holder.buttonUploadImage.setVisibility(View.GONE);
        } else {
            holder.buttonUploadImage.setVisibility(View.VISIBLE);
            holder.buttonUploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CheenaPreviewActivity.class);
                    intent.putExtra(CheenaPreviewActivity.EXTRA_IMAGE, currentHealthConsultant.getUploadImage());
                    context.startActivity(intent);
                }
            });
        }

        if (TextUtils.isEmpty(currentHealthConsultant.getReplyImage())) {
            holder.buttonReplyImage.setVisibility(View.GONE);
        } else {
            holder.buttonReplyImage.setVisibility(View.VISIBLE);
            holder.buttonReplyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CheenaPreviewActivity.class);
                    intent.putExtra(CheenaPreviewActivity.EXTRA_IMAGE, currentHealthConsultant.getReplyImage());
                    context.startActivity(intent);
                }
            });
        }
    }

    private void convertDateFormat(HealthConsultant currentHealthConsultant, HealthConsultantViewHolder holder) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
        Date requestedDate, repliedDate = null;
        String formattedRequestedDate, formattedRepliedDate = null;

        try {
            /*
            * requested query date format changed.
            * */
            requestedDate = inputFormat.parse(currentHealthConsultant.getRequestedDate().getDate());
            formattedRequestedDate = outputFormat.format(requestedDate);
            holder.requestedDateTextView.setText(formattedRequestedDate);

            /*
            * replied query date format changed.
            * */
            repliedDate = inputFormat.parse(currentHealthConsultant.getRepliedDate().getDate());
            formattedRepliedDate = outputFormat.format(repliedDate);
            holder.repliedDateTextView.setText(formattedRepliedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mHealthConsultantList.size();
    }

    private HealthConsultant getHealthConsultantAt(int position) {
        return mHealthConsultantList.get(position);
    }

    /**
     * ViewHolder to hold Health Consultant Reply
     */
    public static class HealthConsultantViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewSubject, textViewReply, textViewSymptoms, textViewMessage,
                requestedDateTextView, repliedDateTextView;
        public final Button buttonUploadImage, buttonReplyImage;

        public HealthConsultantViewHolder(View itemView, View.OnClickListener mReplyClickListener) {
            super(itemView);
            itemView.setOnClickListener(mReplyClickListener);
            textViewSubject = (TextView) itemView.findViewById(R.id.textViewHealthReplySubject);
            textViewSymptoms = (TextView) itemView.findViewById(R.id.textViewHealthReplySymptoms);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewHealthReplyMessage);
            textViewReply = (TextView) itemView.findViewById(R.id.textViewHealthReply);
            buttonUploadImage = (Button) itemView.findViewById(R.id.buttonViewUploadImage);
            buttonReplyImage = (Button) itemView.findViewById(R.id.buttonViewReplyImage);
            requestedDateTextView = (TextView) itemView.findViewById(R.id.textViewHealthRequestedDate);
            repliedDateTextView = (TextView) itemView.findViewById(R.id.textViewHealthReplyDate);

        }
    }
}
