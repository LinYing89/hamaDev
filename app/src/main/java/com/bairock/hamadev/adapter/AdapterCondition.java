package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.linkage.LinkageCondition;

import java.util.List;

/**
 *
 * Created by Administrator on 2017/9/6.
 */

public class AdapterCondition extends BaseAdapter {

    private List<LinkageCondition> listCondition;
    private LayoutInflater vi;

    public AdapterCondition(Context context, List<LinkageCondition> listCondition){
        this.listCondition = listCondition;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listCondition.size();
    }

    @Override
    public Object getItem(int position) {
        return listCondition.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = vi.inflate(R.layout.adapter_list_condition, parent, false);
            mViewHolder.txtLogic  = (TextView) convertView.findViewById(R.id.txtLogic);
            mViewHolder.txtDevice  = (TextView) convertView.findViewById(R.id.txtDevice);
            mViewHolder.txtSymbol  = (TextView) convertView.findViewById(R.id.txtSymbol);
            mViewHolder.txtValue  = (TextView) convertView.findViewById(R.id.txtValue);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.condition = listCondition.get(position);
        mViewHolder.init();
        return convertView;

    }

    private static class ViewHolder {
        private LinkageCondition condition;
        private TextView txtLogic;
        private TextView txtDevice;
        private TextView txtSymbol;
        private TextView txtValue;

        private void init(){
            if(condition.getDevice().isDeleted()){
                txtDevice.setTextColor(Color.RED);
            }else{
                txtDevice.setTextColor(Color.BLACK);
            }
            txtLogic.setText(condition.getLogic().toString());
            txtDevice.setText(condition.getDevice().getName());
            switch (condition.getCompareSymbol()){
                case GREAT:
                    txtSymbol.setText(">");
                    break;
                case EQUAL:
                    txtSymbol.setText("=");
                    break;
                case LESS:
                    txtSymbol.setText("<");
                    break;
            }
            if (condition.getDevice() instanceof IStateDev){
                if(condition.getCompareValue() == 0){
                    txtValue.setText("关");
                }else {
                    txtValue.setText("开");
                }
            }else {
                txtValue.setText(String.valueOf(condition.getCompareValue()));
            }
        }
    }
}
