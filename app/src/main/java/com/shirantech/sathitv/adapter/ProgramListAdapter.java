package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.TvChannelActivity;
import com.shirantech.sathitv.model.ChannelProgram;
import com.shirantech.sathitv.utils.AppLog;

import java.util.List;

/**
 * Adapter for showing list of programs in {@link TvChannelActivity}
 */
public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ProgramViewHolder> {
    private final String TAG = ProgramListAdapter.class.getName();
    private final List<ChannelProgram> mChannelProgramList;
    private Context context;

    public ProgramListAdapter(List<ChannelProgram> channelProgramList,Context context) {
        this.mChannelProgramList = channelProgramList;
        this.context = context;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_list_item, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {

        final ChannelProgram currentProgram = getProgramAt(position);
        holder.programTimeView.setText(currentProgram.getTime().getTime());
        holder.programNameView.setText(currentProgram.getName());
        AppLog.showLog(TAG, "program name :: "+ currentProgram.getName());
        if (position % 2 == 0) {
            /*holder.programNameView.setBackgroundColor(
                    ContextCompat.getColor(holder.programNameView.getContext(), R.color.primary_light));*/
            holder.programNameView.setBackgroundColor(context.getResources().getColor(R.color.primary_light));
        } else {
            holder.programNameView.setBackgroundColor(context.getResources().getColor(R.color.primary_text_inverse));
        }
    }

    @Override
    public int getItemCount() {
        return mChannelProgramList.size();
    }

    private ChannelProgram getProgramAt(int position) {
        return mChannelProgramList.get(position);
    }

    /**
     * ViewHolder to hold channel logos
     */
    public static class ProgramViewHolder extends RecyclerView.ViewHolder {

        public final TextView programTimeView, programNameView;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            programTimeView = (TextView) itemView.findViewById(R.id.program_time_view);
            programNameView = (TextView) itemView.findViewById(R.id.program_name_view);
        }
    }
}
