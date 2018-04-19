package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.AdapterCollect;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnSimulatorChangedListener implements CollectProperty.OnSimulatorChangedListener {
    @Override
    public void onSimulatorChanged(DevCollect devCollect, Float aFloat) {
        if (null != AdapterCollect.handler) {
            AdapterCollect.handler.obtainMessage(AdapterCollect.SIMULATOR, devCollect).sendToTarget();
        }
    }
}
