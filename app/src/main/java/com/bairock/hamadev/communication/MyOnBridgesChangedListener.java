package com.bairock.hamadev.communication;

import com.bairock.hamadev.settings.BridgesStateActivity;
import com.bairock.hamadev.settings.TcpLogActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;

/**
 * tcp连接建立关闭监听
 * Created by 44489 on 2018/3/7.
 */

public class MyOnBridgesChangedListener implements DevChannelBridgeHelper.OnBridgesChangedListener {

    @Override
    public void onAdd(DevChannelBridge devChannelBridge) {
        TcpLogActivity.addRec("新连接建立:id:" + devChannelBridge.getChannelId());
        BridgesStateActivity.addBridge(devChannelBridge.getChannelId());
    }

    @Override
    public void onRemove(DevChannelBridge devChannelBridge) {
        TcpLogActivity.addRec("连接关闭:id:" + devChannelBridge.getChannelId());
        BridgesStateActivity.removeBridge(devChannelBridge.getChannelId());
    }
}
