package com.bairock.hamadev.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.List;

public class RecylerAdapterEffectGuagua extends RecyclerView.Adapter<RecylerAdapterEffectGuagua.ViewHolder>{

    private LayoutInflater mInflater;
    private List<Effect> listEffect;

    public RecylerAdapterEffectGuagua(Context context, List<Effect> listEffect) {
        this.mInflater = LayoutInflater.from(context);
        this.listEffect = listEffect;
    }

    @Override
    public int getItemCount() {
        return listEffect == null ? 0 : listEffect.size();
    }

    @NonNull
    @Override
    public RecylerAdapterEffectGuagua.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecylerAdapterEffectGuagua.ViewHolder(mInflater.inflate(R.layout.adapter_list_effect_guagua, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerAdapterEffectGuagua.ViewHolder holder, int position) {
        holder.setData(listEffect.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Effect effect;
        private TextView txtDevice;
        private TextView txtGuaguaConnect;
        private TextView txtGuaguaCount;

        ViewHolder(View itemView){
            super(itemView);
            txtDevice = itemView.findViewById(R.id.txtDeviceName);
            txtGuaguaConnect = itemView.findViewById(R.id.txtGuaguaContent);
            txtGuaguaCount = itemView.findViewById(R.id.txtGuaguaCount);
        }

        public void setData(Effect effect) {
            this.effect = effect;
            init();
        }

        private void init(){
            txtDevice.setText(effect.getDevice().getName());
            txtGuaguaConnect.setText(effect.getEffectContent());
            txtGuaguaCount.setText(String.valueOf(effect.getEffectCount()));
        }
    }
}
