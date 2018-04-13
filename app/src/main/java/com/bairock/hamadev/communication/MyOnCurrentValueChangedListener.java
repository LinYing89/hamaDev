package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.AdapterCollect;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnCurrentValueChangedListener implements CollectProperty.OnCurrentValueChangedListener {
    @Override
    public void onCurrentValueChanged(DevCollect devCollect, Float aFloat) {
        if (null != AdapterCollect.handler) {
            AdapterCollect.handler.obtainMessage(AdapterCollect.VALUE, devCollect).sendToTarget();
        }
    }
}
