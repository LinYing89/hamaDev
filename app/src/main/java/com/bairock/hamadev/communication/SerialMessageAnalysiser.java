package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.WelcomeActivity;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.communication.MessageAnalysiser;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.LinkType;
import com.bairock.iot.intelDev.linkage.LinkageTab;
import com.bairock.iot.intelDev.user.DevGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2018/3/15.
 */

public class SerialMessageAnalysiser extends MessageAnalysiser {
    @Override
    public void deviceFeedback(Device device, String s) {
        device.setLinkType(LinkType.SERIAL);
    }

    @Override
    public void unKnowDev(Device device, String s) {
        device.setDevGroup(HamaApp.DEV_GROUP);
        //先添加到数据库，后添加到用户组，因为添加到数据库后，如果数据库中已有设备的数据信息
        //则会读取数据信息进行赋值
        DeviceDao.get(HamaApp.HAMA_CONTEXT).add(device);

        HamaApp.DEV_GROUP.addDevice(device);
        //设置设备状态改变监听器
        WelcomeActivity.setDeviceListener(device, new MyOnStateChangedListener(),
                new MyOnGearChangedListener(), new MyOnCtrlModelChangedListener());
        device.setDevStateId(DevStateHelper.DS_ZHENG_CHANG);

        //添加到连锁内存表
        List<Device> listIStateDev = new ArrayList<>();
        DevGroup.findListIStateDev(listIStateDev, device, true);
        for (Device device1 : listIStateDev) {
            LinkageTab.getIns().addTabRow(device1);
        }
    }

    @Override
    public void unKnowMsg(String s) {

    }

    @Override
    public void allMessageEnd() {

    }

    @Override
    public void singleMessageEnd(Device device, String s) {

    }

    @Override
    public void configDevice(Device device, String s) {

    }

    @Override
    public void configDeviceCtrlModel(Device device, String s) {

    }
}
