package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.AdapterElectrical;
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
    }

    private void refreshUi(Device device){
        if (device instanceof IStateDev) {
            if (null != AdapterElectrical.handler) {
                AdapterElectrical.handler.obtainMessage(AdapterElectrical.AUTO, device).sendToTarget();
            }
        }
    }
}
