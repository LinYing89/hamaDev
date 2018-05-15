package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterCollect;
import com.bairock.hamadev.adapter.RecyclerAdapterDevice;
import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.communication.RefreshCollectorValueHelper;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.LinkType;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignalContainer;
import com.bairock.iot.intelDev.device.devcollect.Pressure;
import com.bairock.iot.intelDev.device.devswitch.SubDev;

/**
 * 设备状态改变监听器
 * Created by 44489 on 2017/8/30.
 */

public class MyOnStateChangedListener implements Device.OnStateChangedListener {

    private final static Object syno = new Object();
//    private static final String TAG = MyOnStateChangedListener.class.getSimpleName();
    @Override
    public void onStateChanged(Device device, String s) {
        refreshUi(device);
    }

    @Override
    public void onNormalToAbnormal(Device device) {
        //Log.e(TAG, "onNormalToAbnormal " + device.getCoding());
        refreshSearchUi(device);
        HamaApp.addOfflineDevCoding(device);
        if(!(device instanceof SubDev)) {
            PadClient.getIns().send(device.createAbnormalOrder());
        }
        if(device instanceof DevCollect || device instanceof DevCollectSignalContainer){
            if(!(device instanceof Pressure)) {
                RefreshCollectorValueHelper.getIns().endRefresh(device);
            }
        }
    }

    @Override
    public void onAbnormalToNormal(Device device) {
        //Log.e(TAG, "onAbnormalToNormal " + device.getCoding());
        refreshSearchUi(device);
        HamaApp.removeOfflineDevCoding(device);

        boolean canAdd = false;
        if(device instanceof DevCollectSignalContainer){
            canAdd = true;
        }else if(device instanceof DevCollect){
            if(device.getParent() == null){
                canAdd = true;
            }else if(!(device.getParent() instanceof DevCollectSignalContainer)){
                canAdd = true;
            }
        }
        if(canAdd){
            if(!(device instanceof Pressure)) {
                RefreshCollectorValueHelper.getIns().RefreshDev(device);
            }
        }
    }

    private void refreshUi(Device device){
        synchronized (syno) {
            if (device instanceof IStateDev) {
                if (null != RecyclerAdapterElectrical3.handler) {
                    RecyclerAdapterElectrical3.handler.obtainMessage(RecyclerAdapterElectrical3.STATE, device).sendToTarget();
                }
            }else if(device instanceof DevCollect){
                if (null != RecyclerAdapterCollect.handler) {
                    RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.STATE, device).sendToTarget();
                }
            }
        }
    }

    private void refreshSearchUi(Device device){
        if(!(device instanceof SubDev)){
            if(null != RecyclerAdapterDevice.handler){
                RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.STATE, device).sendToTarget();
            }
        }
    }

    @Override
    public void onNoResponse(Device device) {
        if(device.getLinkType() == LinkType.SERIAL){
            device.setDevStateId(DevStateHelper.DS_YI_CHANG);
        }
    }
}
