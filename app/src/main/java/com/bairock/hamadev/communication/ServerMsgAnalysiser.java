package com.bairock.hamadev.communication;

import com.bairock.hamadev.settings.SearchActivity;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.LinkType;

/**
 *
 * Created by Administrator on 2018-02-14.
 */

public class ServerMsgAnalysiser extends MyMessageAnalysiser {
    @Override
    public void deviceFeedback(Device device, String msg) {
        //PadClient.getIns().sendIfSync("$" + msg);
        device.setLinkType(LinkType.NET);
        updateDevice(device);
    }

    @Override
    public void updateDevice(Device device){
        if(device.getCtrlModel() != CtrlModel.REMOTE){
            device.setCtrlModel(CtrlModel.REMOTE);
        }
        if(null != SearchActivity.deviceModelHelper && device == SearchActivity.deviceModelHelper.getDevToSet()
                && SearchActivity.deviceModelHelper.getCtrlModel() == CtrlModel.REMOTE){
                if(null != SearchActivity.handler){
                    //Log.e("PadClientHandler", "handler 2");
                    SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 3).sendToTarget();
                }
        }
    }

    @Override
    public void configDeviceCtrlModel(Device device, String s) {
    }
}
