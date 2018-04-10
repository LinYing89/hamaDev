package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.AdapterElectrical;
import com.bairock.hamadev.adapter.AdapterSearchDev;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;

/**
 *
 * Created by 44489 on 2018/1/8.
 */

public class MyOnCtrlModelChangedListener implements Device.OnCtrlModelChangedListener {
    @Override
    public void onCtrlModelChanged(Device device, CtrlModel ctrlModel) {
        refreshUi(device);
    }

    private void refreshUi(Device device){
        if (device instanceof IStateDev) {
            if (null != AdapterElectrical.handler) {
                AdapterElectrical.handler.obtainMessage(AdapterElectrical.CTRL_MODEL, device).sendToTarget();
            }
        }else if(null != AdapterSearchDev.handler){
            AdapterSearchDev.handler.obtainMessage(AdapterSearchDev.CTRL_MODEL, device).sendToTarget();
        }
    }
}