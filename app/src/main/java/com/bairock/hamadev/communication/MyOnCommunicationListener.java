package com.bairock.hamadev.communication;

import com.bairock.hamadev.settings.BridgesStateActivity;
import com.bairock.hamadev.settings.TcpLogActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.device.Device;

/**
 * tcp数据收发监听
 * Created by 44489 on 2018/3/7.
 */

public class MyOnCommunicationListener implements DevChannelBridge.OnCommunicationListener {
    @Override
    public void onSend(DevChannelBridge devChannelBridge, String s) {
        TcpLogActivity.addSend("id:" + devChannelBridge.getChannelId() + " - " + s);
        devChannelBridge.sendCountAnd1();
        devChannelBridge.setLastSendMsg(s);
        BridgesStateActivity.sendCountAnd(devChannelBridge.getChannelId(), devChannelBridge.getSendCount(), s);
    }

    @Override
    public void onReceived(DevChannelBridge devChannelBridge, String s) {
        TcpLogActivity.addRec("id:" + devChannelBridge.getChannelId() + " - " + s);
        devChannelBridge.receivedCountAnd1();
        devChannelBridge.setLastReceivedMsg(s);
        String devCoding = null;
        Device device = devChannelBridge.getDevice();
        if(null != device){
            devCoding = device.getCoding();
        }
        BridgesStateActivity.recCountAnd(devCoding, devChannelBridge.getChannelId(), devChannelBridge.getReceivedCount(), s);
    }
}
