package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.database.ZTimerDao;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.util.List;

/**
 * Created by 44489 on 2017/10/18.
 */

public class AdapterTimer extends BaseAdapter {

    private Context context;
    private List<ZTimer> listTimer;

    public AdapterTimer(Context context, List<ZTimer> listTimer){
        this.context = context;
        this.listTimer = listTimer;
    }

    public int getCount() {
        return listTimer.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = vi.inflate(R.layout.adapter_list_timer, parent, false);
        TextView textOnTime  = (TextView) convertView.findViewById(R.id.text_on_time);
        TextView textOffTime  = (TextView) convertView.findViewById(R.id.text_off_time);
        TextView textWeek  = (TextView) convertView.findViewById(R.id.text_week);
        Switch switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);

        final ZTimer timer = listTimer.get(position);
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
        switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timer.setEnable(isChecked);
                ZTimerDao zTimerDao = ZTimerDao.get(context);
                zTimerDao.update(timer, null);
            }
        });
        return convertView;
    }
}
