package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterDevice;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;

/**
 *
 * Created by 44489 on 2018/1/8.
 */

public class MyOnCtrlModelChangedListener implements Device.OnCtrlModelChangedListener {
    @Override
    public void onCtrlModelChanged(Device device, CtrlModel ctrlModel) {
        refreshUi(device);
        DeviceDao.get(HamaApp.HAMA_CONTEXT).update(device);
    }

    private void refreshUi(Device device){
        if(null != RecyclerAdapterDevice.handler){
            RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.CTRL_MODEL, device).sendToTarget();
        }
    }
}
