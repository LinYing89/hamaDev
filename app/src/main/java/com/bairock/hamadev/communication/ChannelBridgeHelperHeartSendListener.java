package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.OrderHelper;
import com.bairock.iot.intelDev.device.devswitch.SubDev;

/**
 * 网络通道发送心跳时，串口通道一起发送
 * 即使没有设备进行网络连接，发送心跳的线程也会运行的
 * Created by 44489 on 2018/3/16.
 */

public class ChannelBridgeHelperHeartSendListener implements DevChannelBridgeHelper.OnHeartSendListener {
    @Override
    public void onHeartSend() {
        noResponsePlus();
        SerialPortHelper.getIns().send(OrderHelper.getOrderMsg("h2"));
    }

    private void noResponsePlus(){
        if(null != HamaApp.DEV_GROUP) {
            for (Device device : HamaApp.DEV_GROUP.getListDevice()) {
                noResponsePlus(device);
            }
        }
    }

    private void noResponsePlus(Device device){
        device.noResponsePlus();
        if(device instanceof DevHaveChild){
            for(Device dev : ((DevHaveChild) device).getListDev()){
                if(!(dev instanceof SubDev)) {
                    noResponsePlus(dev);
                }
            }
        }
    }
}
