package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterElectrical2 extends RecyclerView.Adapter<RecyclerAdapterElectrical2.ViewHolder> {

    public static final int AUTO = 0;
    public static final int STATE = 2;
    public static final int NAME = 3;
    public static final int ALIAS = 4;
    private static int colorNoraml;
    private static int strokeOn;
    private static int strokeOff;
    private static int gearBackground;
    private static int gearPressedBackground;

    public static RecyclerAdapterElectrical2.MyHandler handler;

    private LayoutInflater mInflater;
    private List<Device> listDevice;
    private List<RecyclerAdapterElectrical2.ViewHolder> listViewHolder;

    public RecyclerAdapterElectrical2(Context context, List<Device> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new RecyclerAdapterElectrical2.MyHandler(this);
        colorNoraml = context.getResources().getColor(R.color.back_fort);
        strokeOn = context.getResources().getColor(R.color.yellow_switch_on_stroke);
        strokeOff = context.getResources().getColor(R.color.break_line);
        gearBackground = context.getResources().getColor(R.color.back_ground);
        gearPressedBackground = context.getResources().getColor(R.color.menu_blue);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }


    @NonNull
    @Override
    public RecyclerAdapterElectrical2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterElectrical2.ViewHolder vh = new RecyclerAdapterElectrical2.ViewHolder(mInflater.inflate(R.layout.adapter_electrical_cube, parent, false));
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterElectrical2.ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Device device;
        private TextView textAlias;
        private TextView textName;
        private Button btnOn;
        private Button btnAuto;
        private Button btnOff;
        private Button btnState;
        private Button btnCancel;
        private LinearLayout llBtnSwitch;

        public ViewHolder(View itemView) {
            super(itemView);
            textAlias = itemView.findViewById(R.id.textAlias);
            textName = itemView.findViewById(R.id.textName);
            btnOn = itemView.findViewById(R.id.btnOn);
            btnAuto = itemView.findViewById(R.id.btnAuto);
            btnOff = itemView.findViewById(R.id.btnOff);
            btnState = itemView.findViewById(R.id.btnState);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            llBtnSwitch = itemView.findViewById(R.id.llBtnSwitch);
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init() {
            IStateDev iStateDev = (IStateDev) device;
            refreshName();
            refreshAlias();
            btnState.setOnClickListener(view -> showGearLayout());

            btnOn.setOnClickListener(v -> {
                device.setGear(Gear.KAI);
                refreshAutoState();
                HamaApp.sendOrder(device, iStateDev.getTurnOnOrder(), true);
                updateDeviceDao();
                //refreshBntStateText(Gear.KAI);
            });

            btnAuto.setOnClickListener(v -> {
                device.setGear(Gear.ZIDONG);
                refreshAutoState();
                showBtnState();
                updateDeviceDao();
            });

            btnOff.setOnClickListener(v -> {
                device.setGear(Gear.GUAN);
                refreshAutoState();
                HamaApp.sendOrder(device, iStateDev.getTurnOffOrder(), true);
                updateDeviceDao();
                //refreshBntStateText(Gear.GUAN);
            });
            btnCancel.setOnClickListener(v -> showBtnState());

            refreshAutoState();
            refreshState();
            refreshBntStateText(device.getGear());
        }

        private void showBtnState() {
            if (llBtnSwitch.getVisibility() != View.GONE) {
                llBtnSwitch.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                refreshBtnState();
                refreshBntStateText(device.getGear());
            }
        }

        private void showGearLayout() {
            btnState.setVisibility(View.GONE);
            llBtnSwitch.setVisibility(View.VISIBLE);
            refreshAutoState();
            refreshGearState();
        }

        private void refreshAutoState() {
            switch (device.getGear()) {
                case KAI:
                    btnOn.setTextColor(Color.WHITE);
                    ((GradientDrawable) btnOn.getBackground()).setColor(gearPressedBackground);
                    btnAuto.setTextColor(colorNoraml);
                    ((GradientDrawable) btnAuto.getBackground()).setColor(gearBackground);
                    btnOff.setTextColor(colorNoraml);
                    ((GradientDrawable) btnOff.getBackground()).setColor(gearBackground);
                    break;
                case GUAN:
                    ((GradientDrawable) btnOn.getBackground()).setColor(gearBackground);
                    btnOn.setTextColor(colorNoraml);
                    ((GradientDrawable) btnAuto.getBackground()).setColor(gearBackground);
                    btnAuto.setTextColor(colorNoraml);
                    ((GradientDrawable) btnOff.getBackground()).setColor(gearPressedBackground);
                    btnOff.setTextColor(Color.WHITE);
                    break;
                case UNKNOW:
                case ZIDONG:
                    ((GradientDrawable) btnOn.getBackground()).setColor(gearBackground);
                    btnOn.setTextColor(colorNoraml);
                    ((GradientDrawable) btnAuto.getBackground()).setColor(gearPressedBackground);
                    btnAuto.setTextColor(Color.WHITE);
                    ((GradientDrawable) btnOff.getBackground()).setColor(gearBackground);
                    btnOff.setTextColor(colorNoraml);
                    break;
            }
        }

        private void updateDeviceDao() {
            DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
            deviceDao.update(device);
        }

        private void refreshState() {
            if (!device.isNormal()) {
                textName.setTextColor(HamaApp.abnormalColorId);
            } else {
                textName.setTextColor(colorNoraml);
                if(btnState.getVisibility() == View.VISIBLE) {
                    refreshBtnState();
                }else {
                    refreshGearState();
                }
            }
        }

        private void refreshBntStateText(Gear gear) {
            switch (gear) {
                case KAI:
                    btnState.setText("<");
                    break;
                case GUAN:
                    btnState.setText(">");
                    break;
                default:
                    btnState.setText("O");
                    break;
            }
        }

        private void refreshBtnState(){
            if (device.isKaiState()) {
                btnState.setBackgroundResource(R.drawable.sharp_btn_switch_on);
            } else {
                btnState.setBackgroundResource(R.drawable.sharp_btn_switch_off);
            }
        }

        private void refreshGearState() {
            if (device.isNormal()) {
                if (device.isKaiState()) {
                    ((GradientDrawable) btnOn.getBackground()).setStroke(3, strokeOn);
                    ((GradientDrawable) btnOff.getBackground()).setStroke(2, strokeOff);
                } else {
                    ((GradientDrawable) btnOn.getBackground()).setStroke(2, strokeOff);
                    ((GradientDrawable) btnOff.getBackground()).setStroke(3, strokeOn);
                }
            }
        }

        private void refreshName() {
            textName.setText(device.getName());
        }

        private void refreshAlias() {
            textAlias.setText(device.getAlias());
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterElectrical2> mActivity;

        MyHandler(RecyclerAdapterElectrical2 activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterElectrical2 theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (RecyclerAdapterElectrical2.ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case AUTO:
                            if(vh.btnState.getVisibility() == View.VISIBLE){
                                vh.refreshBntStateText(dev.getGear());
                            }else {
                                vh.refreshAutoState();
                            }
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
