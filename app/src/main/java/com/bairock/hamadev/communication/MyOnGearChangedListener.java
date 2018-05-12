package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.OrderHelper;

/**
 * 挡位改变事件
 * Created by 44489 on 2017/12/29.
 */

public class MyOnGearChangedListener implements Device.OnGearChangedListener{
    @Override
    public void onGearChanged(Device device, Gear gear) {
        PadClient.getIns().send(OrderHelper.getOrderMsg(OrderHelper.FEEDBACK_HEAD  + device.getCoding() + OrderHelper.SEPARATOR + "b" + device.getGear()));
        refreshUi(device);
        updateDeviceDao(device);
    }

    private void refreshUi(Device device){
        if (device instanceof IStateDev) {
            if (null != RecyclerAdapterElectrical3.handler) {
                RecyclerAdapterElectrical3.handler.obtainMessage(RecyclerAdapterElectrical3.AUTO, device).sendToTarget();
            }
        }
    }

    private void updateDeviceDao(Device device) {
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.update(device);
    }
}
