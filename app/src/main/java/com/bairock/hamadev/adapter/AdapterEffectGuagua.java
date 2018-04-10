package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.List;

/**
 *
 * Created by 44489 on 2017/11/1.
 */

public class AdapterEffectGuagua  extends BaseAdapter {
    private Context context;
    private List<Effect> listEffect;
    private LayoutInflater vi;

    public AdapterEffectGuagua(Context context, List<Effect> listEffect){
        this.context = context;
        this.listEffect = listEffect;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listEffect.size();
    }

    @Override
    public Object getItem(int position) {
        return listEffect.get(position);
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
            convertView = vi.inflate(R.layout.adapter_list_effect_guagua, parent, false);
            mViewHolder.txtDevice = (TextView) convertView.findViewById(R.id.txtDeviceName);
            mViewHolder.txtGuaguaConnect  = (TextView) convertView.findViewById(R.id.txtGuaguaContent);
            mViewHolder.txtGuaguaCount  = (TextView) convertView.findViewById(R.id.txtGuaguaCount);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.effect = listEffect.get(position);
        mViewHolder.init();
        return convertView;
    }

    private static class ViewHolder {
        private Effect effect;
        private TextView txtDevice;
        private TextView txtGuaguaConnect;
        private TextView txtGuaguaCount;

        private void init(){
            txtDevice.setText(effect.getDevice().getName());
            txtGuaguaConnect.setText(effect.getEffectContent());
            txtGuaguaCount.setText(String.valueOf(effect.getEffectCount()));
        }
    }
}
