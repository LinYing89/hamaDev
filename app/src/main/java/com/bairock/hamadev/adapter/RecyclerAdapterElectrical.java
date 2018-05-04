package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterElectrical extends RecyclerView.Adapter<RecyclerAdapterElectrical.ViewHolder> {

    public static final int AUTO = 0;
    static final int CTRL_MODEL = 1;
    static final int STATE = 2;
    public static final int NAME = 3;
    public static final int ALIAS = 4;

    public static MyHandler handler;

    private LayoutInflater mInflater;
    private List<Device> listDevice;
    private List<RecyclerAdapterElectrical.ViewHolder> listViewHolder;

    public RecyclerAdapterElectrical(Context context, List<Device> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }


    @NonNull
    @Override
    public RecyclerAdapterElectrical.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterElectrical.ViewHolder vh = new RecyclerAdapterElectrical.ViewHolder(mInflater.inflate(R.layout.adapter_electrical, parent, false));
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterElectrical.ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Device device;
        private TextView textAlias;
        private TextView textName;
        private TextView textCtrlModel;
        private Button btnOn;
        private Button btnAuto;
        private Button btnOff;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textAlias = itemView.findViewById(R.id.text_gate);
            textName = itemView.findViewById(R.id.text_name);
            textCtrlModel = itemView.findViewById(R.id.text_ctrl_model);
            btnOn = itemView.findViewById(R.id.btn_on);
            btnAuto = itemView.findViewById(R.id.btn_auto);
            btnOff = itemView.findViewById(R.id.btn_off);
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init(){
            IStateDev iStateDev = (IStateDev)device;
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());

            btnOn.setOnClickListener(v -> {
                device.setGear(Gear.KAI);
                HamaApp.sendOrder(device, iStateDev.getTurnOnOrder(), true);
            });
            btnAuto.setOnClickListener(v -> device.setGear(Gear.ZIDONG));

            btnOff.setOnClickListener(v -> {
                device.setGear(Gear.GUAN);
                HamaApp.sendOrder(device, iStateDev.getTurnOffOrder(), true);
            });

            refreshAutoState();
            refreshState();
            refreshDeviceModel();
        }

        private void refreshAutoState(){
            switch (device.getGear()){
                case KAI:
                    btnOn.setTextColor(Color.BLUE);
                    btnAuto.setTextColor(Color.BLACK);
                    btnOff.setTextColor(Color.BLACK);
                    break;
                case GUAN:
                    btnOn.setTextColor(Color.BLACK);
                    btnAuto.setTextColor(Color.BLACK);
                    btnOff.setTextColor(Color.BLUE);
                    break;
                case UNKNOW:
                case ZIDONG:
                    btnOn.setTextColor(Color.BLACK);
                    btnAuto.setTextColor(Color.BLUE);
                    btnOff.setTextColor(Color.BLACK);
                    break;
            }
            DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
            deviceDao.update(device);
        }

        private void refreshState(){
            if (device.isNormal()) {
                if (device.isKaiState()) {
                    rootView.setBackgroundColor(HamaApp.stateKaiColorId);
                    //rootView.setBackgroundColor(Color.parseColor("#B4EEB4"));
                } else {
                    rootView.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                rootView.setBackgroundColor(HamaApp.abnormalColorId);
                //rootView.setBackgroundColor(Color.parseColor("#FFE9967A"));
            }
        }

        private void refreshDeviceModel(){
            if (device.getCtrlModel() == CtrlModel.REMOTE) {
                if (!textCtrlModel.getText().equals("远程")) {
                    textCtrlModel.setText("远程");
                }
            } else if (!textCtrlModel.getText().equals("本地")) {
                textCtrlModel.setText("本地");
            }
        }

        private void refreshName(){
            textName.setText(device.getName());
        }

        private void refreshAlias(){
            textAlias.setText(device.getAlias());
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterElectrical> mActivity;

        MyHandler(RecyclerAdapterElectrical activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterElectrical theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for(RecyclerAdapterElectrical.ViewHolder vh : theActivity.listViewHolder){
                if(vh.device == dev){
                    switch (msg.what) {
                        case AUTO:
                            vh.refreshAutoState();
                            break;
                        case CTRL_MODEL :
                            vh.refreshDeviceModel();
                            break;
                        case STATE:
                            Log.e("AdapterElectrical", "vh " + vh.device.getCoding());
                            vh.refreshState();
                            break;
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
