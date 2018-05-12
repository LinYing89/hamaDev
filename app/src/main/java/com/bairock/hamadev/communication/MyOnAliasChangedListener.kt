package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.RecyclerAdapterChildDevice
import com.bairock.hamadev.adapter.RecyclerAdapterDevice
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.DeviceDao
import com.bairock.iot.intelDev.device.Device

class MyOnAliasChangedListener : Device.OnAliasChangedListener{
    override fun onAliasChanged(p0: Device, p1: String?) {
        refreshUi(p0)
        updateDeviceDao(p0)
    }

    private fun refreshUi(device: Device) {
//        if (device is IStateDev) {
//            if (null != RecyclerAdapterElectrical.handler) {
//                //RecyclerAdapterElectrical.handler.obtainMessage(RecyclerAdapterElectrical.NAME, device).sendToTarget()
//            }
//        }else if(device is DevCollect){
//            if (null != RecyclerAdapterCollect.handler) {
//                //RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.NAME, device).sendToTarget()
//            }
//        }
        if(null != RecyclerAdapterDevice.handler){
            RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.ALIAS, device).sendToTarget()
        }

        if(null != RecyclerAdapterChildDevice.handler){
            RecyclerAdapterChildDevice.handler.obtainMessage(RecyclerAdapterChildDevice.ALIAS, device).sendToTarget()
        }
    }

    private fun updateDeviceDao(device: Device) {
        val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
        deviceDao.update(device)
    }
}