package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.BridgeState;

import java.util.List;

/**
 *
 * Created by 44489 on 2018/3/7.
 */

public class AdapterBridgeState extends BaseAdapter {
    private Context context;
    private List<BridgeState> listDevChannelBridge;

    public AdapterBridgeState(Context context, List<BridgeState> listDevChannelBridge) {
        super();
        this.context = context;
        this.listDevChannelBridge = listDevChannelBridge;
    }

    @Override
    public int getCount() {
        return listDevChannelBridge.size();
    }

    @Override
    public Object getItem(int position) {
        return listDevChannelBridge.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.layout_bridge_state, null);
        TextView tvDevice = (TextView)convertView.findViewById(R.id.tvDevice);
        TextView tvChannelId = (TextView)convertView.findViewById(R.id.tvChannelId);
        TextView tvSendCount = (TextView)convertView.findViewById(R.id.tvSendCount);
        TextView tvReceivedCount = (TextView)convertView.findViewById(R.id.tvReceivedCount);
        BridgeState bridge = listDevChannelBridge.get(position);
        if(null != bridge.getDevCoding()) {
            tvDevice.setText(bridge.getDevCoding());
        }else{
            tvDevice.setText("未知");
        }
        tvChannelId.setText(bridge.getChannelId());
        tvSendCount.setText(String.valueOf(bridge.getSendCount()));
        tvReceivedCount.setText(String.valueOf(bridge.getRecCount()));
        return convertView;
    }
}
