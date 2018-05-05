package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterCollect;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnCurrentValueChangedListener implements CollectProperty.OnCurrentValueChangedListener {
    @Override
    public void onCurrentValueChanged(DevCollect devCollect, Float aFloat) {
        if (null != RecyclerAdapterCollect.handler) {
            RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.VALUE, devCollect).sendToTarget();
        }
    }
}
