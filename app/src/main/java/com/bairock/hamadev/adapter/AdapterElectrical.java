package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

/**
 *
 * Created by 44489 on 2017/8/31.
 */

public class AdapterElectrical extends BaseAdapter {
    public static final int AUTO = 0;
    public static final int CTRL_MODEL = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;
    public static final int ALIAS = 4;

    public static MyHandler handler;

    private LayoutInflater mInflater;
    private Context context;
    private List<Device> listDevice;
    private List<ViewHolder> listViewHolder;

    public AdapterElectrical(Context context, List<Device> listDevice){
        this.context = context;
        this.listDevice = listDevice;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
    }
    @Override
    public int getCount() {
        return listDevice.size();
    }

    @Override
    public Object getItem(int position) {
        return listDevice.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myViews;
        final Device device = listDevice.get(position);
        if (convertView == null){
            myViews = new ViewHolder();
            listViewHolder.add(myViews);
            convertView = mInflater.inflate(R.layout.adapter_electrical, parent, false);
            myViews.textAlias = (TextView) convertView.findViewById(R.id.text_gate);
            myViews.textName = (TextView) convertView.findViewById(R.id.text_name);
            myViews.textCtrlModel = (TextView) convertView.findViewById(R.id.text_ctrl_model);
            myViews.btnOn = (Button) convertView.findViewById(R.id.btn_on);
            myViews.btnAuto = (Button) convertView.findViewById(R.id.btn_auto);
            myViews.btnOff = (Button) convertView.findViewById(R.id.btn_off);
            convertView.setTag(myViews);
            myViews.rootView = convertView;
        }
        else {
            myViews = (ViewHolder ) convertView.getTag();
        }

        myViews.device = device;

        myViews.init();
        myViews.refreshState();
        myViews.refreshDeviceModel();
        myViews.refreshAutoState();

        return convertView;
    }

    class ViewHolder {
        private View rootView;
        private Device device;
        private TextView textAlias;
        private TextView textName;
        private TextView textCtrlModel;
        private Button btnOn;
        private Button btnAuto;
        private Button btnOff;

        private void init() {
            IStateDev iStateDev = (IStateDev)device;
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());

            btnOn.setOnClickListener(v -> {
                device.setGear(Gear.KAI);
                HamaApp.sendOrder(device, iStateDev.getTurnOnOrder());
            });
            btnAuto.setOnClickListener(v -> device.setGear(Gear.ZIDONG));

            btnOff.setOnClickListener(v -> {
                device.setGear(Gear.GUAN);
                HamaApp.sendOrder(device, iStateDev.getTurnOffOrder());
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
            DeviceDao deviceDao = DeviceDao.get(context);
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
        WeakReference<AdapterElectrical> mActivity;

        MyHandler(AdapterElectrical activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AdapterElectrical theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for(ViewHolder vh : theActivity.listViewHolder){
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
