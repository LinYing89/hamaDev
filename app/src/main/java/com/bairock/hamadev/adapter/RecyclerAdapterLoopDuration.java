package com.bairock.hamadev.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;

import java.util.List;

public class RecyclerAdapterLoopDuration extends RecyclerView.Adapter<RecyclerAdapterLoopDuration.ViewHolder> {

    private LayoutInflater mInflater;
    private List<LoopDuration> listDuration;

    public RecyclerAdapterLoopDuration(Context context, List<LoopDuration> listDuration) {
        this.mInflater = LayoutInflater.from(context);
        this.listDuration = listDuration;
    }

    @Override
    public int getItemCount() {
        return listDuration == null ? 0 : listDuration.size();
    }

    @NonNull
    @Override
    public RecyclerAdapterLoopDuration.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapterLoopDuration.ViewHolder(mInflater.inflate(R.layout.adapter_list_duration, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterLoopDuration.ViewHolder holder, int position) {
        holder.setData(listDuration.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LoopDuration duration;
        private TextView textOnTime;
        private TextView textOffTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textOnTime = itemView.findViewById(R.id.text_on_time);
            textOffTime = itemView.findViewById(R.id.text_off_time);
        }

        public void setData(LoopDuration duration) {
            this.duration = duration;
            init();
        }

        private void init() {
            textOnTime.setText(duration.getOnKeepTime().toString());
            textOffTime.setText(duration.getOffKeepTime().toString());
        }
    }
}
