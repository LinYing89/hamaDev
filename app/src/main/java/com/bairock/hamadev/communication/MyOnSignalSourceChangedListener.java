package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.AdapterCollect;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnSignalSourceChangedListener implements CollectProperty.OnSignalSourceChangedListener {
    @Override
    public void onSignalSourceChanged(DevCollect devCollect) {
        if (null != AdapterCollect.handler) {
            AdapterCollect.handler.obtainMessage(AdapterCollect.SRC_NAME, devCollect).sendToTarget();
        }
    }
}
