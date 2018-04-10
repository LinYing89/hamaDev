package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;

import java.util.List;

/**
 * Created by 44489 on 2017/10/26.
 */

public class AdapterDurationList extends BaseAdapter {
    private Context context;
    private List<LoopDuration> listDuration;

    public AdapterDurationList(Context context, List<LoopDuration> listDuration){
        this.context = context;
        this.listDuration = listDuration;
    }

    public int getCount() {
        return listDuration.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if(convertView == null){
            mViewHolder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.adapter_list_duration, parent, false);
            mViewHolder.textOnTime  = (TextView) convertView.findViewById(R.id.text_on_time);
            mViewHolder.textOffTime  = (TextView) convertView.findViewById(R.id.text_off_time);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final LoopDuration duration = listDuration.get(position);
        mViewHolder.textOnTime.setText(duration.getOnKeepTime().toString());
        mViewHolder.textOffTime.setText(duration.getOffKeepTime().toString());
        return convertView;
    }

    static class ViewHolder {
        private TextView textOnTime;
        private TextView textOffTime;
    }
}
