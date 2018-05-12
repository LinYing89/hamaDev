package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.RecyclerAdapterCollect
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.DevCollect

class MyOnUnitSymbolChangedListener : CollectProperty.OnUnitSymbolChangedListener{

    override fun onUnitSymbolChanged(p0: DevCollect?, p1: String?) {
        if(null != RecyclerAdapterCollect.handler){
            RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.SYMBOL, p0).sendToTarget()
        }
    }
}