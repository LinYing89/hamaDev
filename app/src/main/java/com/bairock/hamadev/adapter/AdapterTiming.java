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
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.iot.intelDev.linkage.timing.Timing;

/**
 * Created by 44489 on 2017/10/18.
 */

public class AdapterTiming extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;

    public AdapterTiming(Context context){
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return HamaApp.DEV_GROUP.getTimingHolder().getListLinkage().size();
    }

    @Override
    public Object getItem(int position) {
        return HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterTiming.ViewHolder myViews;
        final Timing timing = (Timing) HamaApp.DEV_GROUP.getTimingHolder().getListLinkage().get(position);
        if (convertView == null){
            myViews = new AdapterTiming.ViewHolder(context);
            convertView = mInflater.inflate(R.layout.adapter_linkage_holder, null);
            myViews.txtLinkageName = (TextView) convertView.findViewById(R.id.txtLinkageName);
            myViews.switchEnable = (Switch) convertView.findViewById(R.id.switchEnable);
            convertView.setTag(myViews);
        }
        else {
            myViews = (AdapterTiming.ViewHolder) convertView.getTag();
        }

        myViews.timing = timing;
        myViews.init();
        return convertView;
    }

    private static class ViewHolder {
        private Timing timing;
        private TextView txtLinkageName;
        private Switch switchEnable;
        private Context context;

        ViewHolder(Context context){
            this.context = context;
        }

        void init(){
            txtLinkageName.setText(timing.getName());
            switchEnable.setChecked(timing.isEnable());
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timing.setEnable(isChecked);
                LinkageDao.get(context).update(timing, null);
            }
        };
    }
}
