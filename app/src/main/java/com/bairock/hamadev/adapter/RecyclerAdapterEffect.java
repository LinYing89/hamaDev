package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.List;

public class RecyclerAdapterEffect extends RecyclerView.Adapter<RecyclerAdapterEffect.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Effect> listEffect;
    private boolean showSwitch;

    public RecyclerAdapterEffect(Context context, List<Effect> listEffect, boolean showSwitch) {
        this.mInflater = LayoutInflater.from(context);
        this.listEffect = listEffect;
        this.showSwitch = showSwitch;
    }

    @Override
    public int getItemCount() {
        return listEffect == null ? 0 : listEffect.size();
    }

    @NonNull
    @Override
    public RecyclerAdapterEffect.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapterEffect.ViewHolder(mInflater.inflate(R.layout.adapter_list_effect, parent, false), showSwitch);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterEffect.ViewHolder holder, int position) {
        holder.setData(listEffect.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Effect effect;
        private TextView txtDevice;
        private Switch switchState;
        private boolean showSwitch;

        ViewHolder(View itemView, boolean showSwitch){
            super(itemView);
            this.showSwitch = showSwitch;
            txtDevice = itemView.findViewById(R.id.txtDevice);
            switchState  = itemView.findViewById(R.id.switchState);
        }

        public void setData(Effect effect) {
            this.effect = effect;
            init();
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
                EffectDao effectDao = EffectDao.get(HamaApp.HAMA_CONTEXT);
                effectDao.update(effect, null);
            }
        };
    }
}
