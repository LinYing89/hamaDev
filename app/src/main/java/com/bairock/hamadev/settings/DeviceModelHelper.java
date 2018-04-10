package com.bairock.hamadev.settings;

import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;

/**
 *
 * Created by 44489 on 2018/1/4.
 */

public class DeviceModelHelper {
    private Device devToSet;
    private CtrlModel ctrlModel;
    private String order;

    /**
     * 设置的设备
     * @return
     */
    public Device getDevToSet() {
        return devToSet;
    }

    /**
     * 设置的设备
     * @param devToSet
     */
    public void setDevToSet(Device devToSet) {
        this.devToSet = devToSet;
    }

    /**
     * 设置为的模式
     * @return
     */
    public CtrlModel getCtrlModel() {
        return ctrlModel;
    }

    /**
     * 设置为的模式
     * @param ctrlModel
     */
    public void setCtrlModel(CtrlModel ctrlModel) {
        this.ctrlModel = ctrlModel;
    }

    /**
     * 设置报文
     * @return
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置报文
     * @param order
     */
    public void setOrder(String order) {
        this.order = order;
    }
}
