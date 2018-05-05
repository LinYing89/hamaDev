package com.bairock.hamadev.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.ZTimerDao;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.util.List;

public class RecyclerAdapterTimer extends RecyclerView.Adapter<RecyclerAdapterTimer.ViewHolder> {

    private LayoutInflater mInflater;
    private List<ZTimer> listTimer;

    public RecyclerAdapterTimer(Context context, List<ZTimer> listTimer) {
        this.mInflater = LayoutInflater.from(context);
        this.listTimer = listTimer;
    }

    @Override
    public int getItemCount() {
        return listTimer == null ? 0 : listTimer.size();
    }

    @NonNull
    @Override
    public RecyclerAdapterTimer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapterTimer.ViewHolder(mInflater.inflate(R.layout.adapter_list_timer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterTimer.ViewHolder holder, int position) {
        holder.setData(listTimer.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ZTimer timer;
        private TextView textOnTime;
        private TextView textOffTime;
        private TextView textWeek;
        private Switch switchEnable;

        public ViewHolder(View itemView) {
            super(itemView);
            textOnTime  = itemView.findViewById(R.id.text_on_time);
            textOffTime  = itemView.findViewById(R.id.text_off_time);
            textWeek  = itemView.findViewById(R.id.text_week);
            switchEnable  = itemView.findViewById(R.id.switch_enable);
        }

        public void setData(ZTimer timer) {
            this.timer = timer;
            init();
        }

        private void init(){
            if(timer.getOnTime() != null) {
                textOnTime.setText(timer.getOnTime().toString());
            }
            if(timer.getOffTime() != null) {
                textOffTime.setText(timer.getOffTime().toString());
            }
            if(timer.getWeekHelper() != null) {
                textWeek.setText(timer.getWeekHelper().getWeeksName());
            }
            switchEnable.setChecked(timer.isEnable());
            switchEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
                timer.setEnable(isChecked);
                ZTimerDao zTimerDao = ZTimerDao.get(HamaApp.HAMA_CONTEXT);
                zTimerDao.update(timer, null);
            });
        }
    }
}
