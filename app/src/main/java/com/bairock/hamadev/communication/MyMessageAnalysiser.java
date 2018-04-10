package com.bairock.hamadev.communication;

import android.util.Log;

import com.bairock.hamadev.settings.EsptouchActivity;
import com.bairock.hamadev.settings.SearchActivity;
import com.bairock.hamadev.settings.TcpLogActivity;
import com.bairock.iot.intelDev.communication.MessageAnalysiser;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.LinkType;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class MyMessageAnalysiser extends MessageAnalysiser {

    @Override
    public void receivedMsg(String msg) {
        //TcpLogActivity.addRec(msg);
    }

    @Override
    public void deviceFeedback(Device device, String msg) {
        device.setLinkType(LinkType.NET);
		PadClient.getIns().sendIfSync("$" + msg);
        updateDevice(device);
    }

    public void updateDevice(Device device){
        if(device.getCtrlModel() != CtrlModel.LOCAL){
            device.setCtrlModel(CtrlModel.LOCAL);
        }
        if(null != SearchActivity.deviceModelHelper && device == SearchActivity.deviceModelHelper.getDevToSet()
                && SearchActivity.deviceModelHelper.getCtrlModel() == CtrlModel.LOCAL){
            if(null != SearchActivity.handler){
                Log.e("MyMessageAnalysiser", "handler");
                SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 3).sendToTarget();
            }
        }
    }

    @Override
    public void unKnowDev(Device device, String s) {

    }

    @Override
    public void noTheDev(String msg) {

    }

    @Override
    public void allMessageEnd() {

    }

    @Override
    public void singleMessageEnd(Device device, String msg) {
    }

    @Override
    public void configDevice(Device device, String s) {
        if(null != SearchActivity.handler){
            SearchActivity.handler.obtainMessage(SearchActivity.handler.DEV_ADD_CHILD, device).sendToTarget();
        }
    }

    @Override
    public void configDeviceCtrlModel(Device device, String s) {
        if(null != SearchActivity.deviceModelHelper){
            if(device == SearchActivity.deviceModelHelper.getDevToSet()){
                if(null != SearchActivity.handler){
                    Log.e("PadClientHandler", "handler 2");
                    SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 2).sendToTarget();
                }
            }
        }
    }
}
