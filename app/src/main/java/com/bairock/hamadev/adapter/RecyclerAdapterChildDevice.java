package com.bairock.hamadev.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterChildDevice extends RecyclerView.Adapter<RecyclerAdapterChildDevice.ViewHolder> {

    public static final int NAME = 0;
    public static final int ALIAS = 1;

    private LayoutInflater mInflater;
    private List<Device> listElectrical;

    public static RecyclerAdapterChildDevice.MyHandler handler;

    private List<RecyclerAdapterChildDevice.ViewHolder> listViewHolder;

    public RecyclerAdapterChildDevice(Context context, List<Device> listElectrical) {
        this.mInflater = LayoutInflater.from(context);
        listViewHolder = new ArrayList<>();
        handler = new RecyclerAdapterChildDevice.MyHandler(this);
        this.listElectrical = listElectrical;
    }

    @Override
    public int getItemCount() {
        return listElectrical == null ? 0 : listElectrical.size();
    }

    @NonNull
    @Override
    public RecyclerAdapterChildDevice.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterChildDevice.ViewHolder vh = new RecyclerAdapterChildDevice.ViewHolder(mInflater.inflate(R.layout.adapter_child_electrical, parent, false));
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterChildDevice.ViewHolder holder, int position) {
        holder.setData(listElectrical.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;
        private TextView textAlias;
        private TextView textState;
        private CheckBox cbVisibility;
        private Device device;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            textAlias = itemView.findViewById(R.id.textAlias);
            textState = itemView.findViewById(R.id.text_num);
            cbVisibility = itemView.findViewById(R.id.cbVisibility);
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init(){
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());
            textState.setText(device.getSubCode());
            cbVisibility.setChecked(device.isVisibility());
            cbVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                device.setVisibility(isChecked);
                DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
                deviceDao.update(device);
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
                }
                if(null != ClimateFragment.handler){
                    ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
                }
            });
        }

        private void refreshName(){
            textName.setText(device.getName());
        }

        private void refreshAlias(){
            textAlias.setText(device.getAlias());
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterChildDevice> mActivity;

        MyHandler(RecyclerAdapterChildDevice activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterChildDevice theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case NAME:
                            vh.refreshName();
                            break;
                        case ALIAS:
                            vh.refreshAlias();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
