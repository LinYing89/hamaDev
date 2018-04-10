package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.NetMsgType;

import java.util.List;

/**
 *
 * Created by 44489 on 2018/3/7.
 */

public class AdapterBridgeMsg extends BaseAdapter {
    private Context context;
    private List<? extends NetMsgType> listDevChannelBridge;

    public AdapterBridgeMsg(Context context, List<? extends NetMsgType> listDevChannelBridge) {
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
        convertView = mInflater.inflate(R.layout.layout_bridge_msg, null);
        TextView tvTime = (TextView)convertView.findViewById(R.id.tvTime);
        TextView tvRec = (TextView)convertView.findViewById(R.id.tvRec);
        TextView tvSend = (TextView)convertView.findViewById(R.id.tvSend);
        NetMsgType bridge = listDevChannelBridge.get(position);
        tvTime.setText(bridge.getTime());
        if(bridge.getType() == 0){
            tvRec.setText(bridge.getMsg());
            tvSend.setText("");
        }else{
            tvRec.setText("");
            tvSend.setText(bridge.getMsg());
        }
        return convertView;
    }
}
