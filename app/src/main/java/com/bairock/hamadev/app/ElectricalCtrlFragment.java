package com.bairock.hamadev.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterElectrical;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.user.DevGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElectricalCtrlFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    public static final int REFRESH_ELE_STATE = 1;
    public static final int REFRESH_ELE = 2;
    public static final int REFRESH_SORT= 3;
    public static final int SERVER_STATE= 4;
    /**
     * 设置设备模式时，服务器的响应
     */
    public static final int SET_MODEL_RESPONSE = 5;
    public static final int SHOW_ALERT_DIALOG= 6;

    public static MyHandler handler;

    private AdapterElectrical adapterElectrical;
    private ListView listViewElectrical;

    //显示连接服务器状态
    private TextView textServerState;

    private List<Device> listIStateDev = new ArrayList<>();

    public ElectricalCtrlFragment() {
        // Required empty public constructor
    }

    public static ElectricalCtrlFragment newInstance(int sectionNumber2) {
        ElectricalCtrlFragment fragment = new ElectricalCtrlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, sectionNumber2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electrical_ctrl, container, false);
        handler = new MyHandler(this);
        textServerState = (TextView)view.findViewById(R.id.text_server_state);
        listViewElectrical = (ListView)view.findViewById(R.id.listview_electrical);
        //listViewElectrical.setOnItemLongClickListener(onItemLongClickListener);
        setGridViewElectrical();
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        for(Device device : listIStateDev){
            removeDeviceListener(device);
        }
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        AdapterElectrical.handler = null;
    }

    public void setGridViewElectrical() {
        if(null != HamaApp.DEV_GROUP) {
            listIStateDev = HamaApp.DEV_GROUP.findListIStateDev(true);
            Collections.sort(listIStateDev);
            adapterElectrical = new AdapterElectrical(this.getContext(), listIStateDev);
            listViewElectrical.setAdapter(adapterElectrical);
            for(Device device : listIStateDev){
                setDeviceListener(device);
            }
        }
    }

    private void setDeviceListener(Device device){
        device.addOnNameChangedListener(onNameChangedListener);
        device.addOnAliasChangedListener(onAliasChangedListener);
    }

    private void removeDeviceListener(Device device){
        device.removeOnNameChangedListener(onNameChangedListener);
        device.removeOnAliasChangedListener(onAliasChangedListener);
    }

    private Device.OnNameChangedListener onNameChangedListener = new Device.OnNameChangedListener() {
        @Override
        public void onNameChanged(Device device, String s) {
            if(null != AdapterElectrical.handler){
                AdapterElectrical.handler.obtainMessage(AdapterElectrical.NAME, device).sendToTarget();
            }
        }
    };

    private Device.OnAliasChangedListener onAliasChangedListener = new Device.OnAliasChangedListener() {
        @Override
        public void onAliasChanged(Device device, String s) {
            if(null != AdapterElectrical.handler){
                AdapterElectrical.handler.obtainMessage(AdapterElectrical.ALIAS, device).sendToTarget();
            }
        }
    };

    private DevGroup.OnDeviceCollectionChangedListener onDeviceCollectionChangedListener = new DevGroup.OnDeviceCollectionChangedListener() {
        @Override
        public void onAdded(Device device) {
            addDev(device);
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            removeDev(device);
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }
    };

    private void addDev(Device device){
        if(device instanceof IStateDev && device.isVisibility()){
            listIStateDev.add(device);
            setDeviceListener(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                addDev(device1);
            }
        }
    }

    private void removeDev(Device device){
        if(device instanceof IStateDev){
            listIStateDev.remove(device);
            removeDeviceListener(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                removeDev(device1);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<ElectricalCtrlFragment> mActivity;

        MyHandler(ElectricalCtrlFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            ElectricalCtrlFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_ELE_STATE:
                    theActivity.adapterElectrical.notifyDataSetChanged();
                    break;
                case REFRESH_ELE :
                    theActivity.setGridViewElectrical();
                    break;
                case REFRESH_SORT :
                    break;
                case SERVER_STATE :
                    String str = (String) msg.obj;
                    if(str.equals("OK")){
                        theActivity.textServerState.setVisibility(View.INVISIBLE);
                    }else{
                        theActivity.textServerState.setVisibility(View.VISIBLE);
                        theActivity.textServerState.setText(str);
                    }
                    break;
                case SHOW_ALERT_DIALOG:
                    break;
            }
        }
    }
}
