package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.List;

/**
 *
 * Created by Administrator on 2017/9/7.
 */

public class AdapterEffect extends BaseAdapter {

    private Context context;
    private List<Effect> listEffect;
    private LayoutInflater vi;
    private boolean showSwitch;

    public AdapterEffect(Context context, List<Effect> listEffect, boolean showSwitch){
        this.context = context;
        this.listEffect = listEffect;
        this.showSwitch = showSwitch;
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
            mViewHolder = new ViewHolder(context, showSwitch);
            convertView = vi.inflate(R.layout.adapter_list_effect, parent, false);
            mViewHolder.txtDevice = (TextView) convertView.findViewById(R.id.txtDevice);
            mViewHolder.switchState  = (Switch) convertView.findViewById(R.id.switchState);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.effect = listEffect.get(position);
        mViewHolder.init();
        return convertView;
    }

    private static class ViewHolder {
        private Context context;
        private Effect effect;
        private TextView txtDevice;
        private Switch switchState;
        private boolean showSwitch;

        ViewHolder(Context context, boolean showSwitch){
            this.context = context;
            this.showSwitch = showSwitch;
        }

        private void init(){
            if(showSwitch){
                switchState.setVisibility(View.VISIBLE);
            }else{
                switchState.setVisibility(View.GONE);
            }
            if(effect.getDevice().isDeleted()){
                txtDevice.setTextColor(Color.RED);
            }else{
                txtDevice.setTextColor(Color.BLACK);
            }
            txtDevice.setText(effect.getDevice().getName());
            if(switchState.getVisibility() == View.VISIBLE) {
                if (effect.getDsId().equals(DevStateHelper.DS_KAI)) {
                    switchState.setChecked(true);
                } else {
                    switchState.setChecked(false);
                }
            }
            switchState.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    effect.setDsId(DevStateHelper.DS_KAI);
                }else{
                    effect.setDsId(DevStateHelper.DS_GUAN);
                }
                EffectDao effectDao = EffectDao.get(context);
                effectDao.update(effect, null);
            }
        };
    }
}
