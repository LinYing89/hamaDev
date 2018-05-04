package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.linkage.LinkageCondition;

import java.util.List;

public class RecyclerAdapterCondition extends RecyclerView.Adapter<RecyclerAdapterCondition.ViewHolder> {

    private LayoutInflater mInflater;
    private List<LinkageCondition> listCondition;

    public RecyclerAdapterCondition(Context context, List<LinkageCondition> listCondition) {
        this.mInflater = LayoutInflater.from(context);
        this.listCondition = listCondition;
    }

    @Override
    public int getItemCount() {
        return listCondition == null ? 0 : listCondition.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_list_condition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(listCondition.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinkageCondition condition;
        private TextView txtLogic;
        private TextView txtDevice;
        private TextView txtSymbol;
        private TextView txtValue;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLogic = itemView.findViewById(R.id.txtLogic);
            txtDevice = itemView.findViewById(R.id.txtDevice);
            txtSymbol = itemView.findViewById(R.id.txtSymbol);
            txtValue = itemView.findViewById(R.id.txtValue);
        }

        public void setData(LinkageCondition condition) {
            this.condition = condition;
            init();
        }

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
