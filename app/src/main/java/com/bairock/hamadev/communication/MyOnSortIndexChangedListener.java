package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

public class MyOnSortIndexChangedListener implements Device.OnSortIndexChangedListener {
    @Override
    public void onSortIndexChanged(Device device, int i) {
        DeviceDao.get(HamaApp.HAMA_CONTEXT).update(device);
    }
}
