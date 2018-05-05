package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterCollect;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnSignalSourceChangedListener implements CollectProperty.OnSignalSourceChangedListener {
    @Override
    public void onSignalSourceChanged(DevCollect devCollect) {
        if (null != RecyclerAdapterCollect.handler) {
            RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.SRC_NAME, devCollect).sendToTarget();
        }
    }
}
