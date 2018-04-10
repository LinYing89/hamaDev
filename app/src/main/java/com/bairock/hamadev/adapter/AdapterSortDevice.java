package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.device.Device;

import java.util.List;

/**
 * Created by 44489 on 2017/11/2.
 */

public class AdapterSortDevice extends BaseAdapter {
    public Device selectedDevice;
    private Context context;
    private List<Device> listDevice;

    public AdapterSortDevice(Context context, List<Device> listDevice){
        this.context = context;
        this.listDevice = listDevice;
    }

    public int getCount() {
        return listDevice.size();
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

            convertView = vi.inflate(R.layout.list_text, parent, false);
            mViewHolder.textName  = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Device device = listDevice.get(position);
        mViewHolder.textName.setText(device.getName());
        if(null != selectedDevice && selectedDevice == device) {
            convertView.setBackgroundColor(Color.BLUE);
            mViewHolder.textName.setTextColor(Color.WHITE);
        }else{
            convertView.setBackgroundColor(Color.TRANSPARENT);
            mViewHolder.textName.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    static class ViewHolder {
        private TextView textName;
    }
}
