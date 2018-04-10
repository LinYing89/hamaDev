package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevCategory;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.DeviceAssistent;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.linkage.ChainHolder;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder;
import com.bairock.iot.intelDev.linkage.loop.LoopHolder;
import com.bairock.iot.intelDev.linkage.timing.TimingHolder;
import com.bairock.iot.intelDev.user.DevGroup;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class DeviceCursorWrapper extends CursorWrapper {

    public DeviceCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Device getDevice() {
        String id = getString(getColumnIndex(DbSb.TabDevice.Cols.ID));
        //String deviceType = getString(getColumnIndex(DbSb.TabDevice.Cols.DEVICE_TYPE));
        String alias = getString(getColumnIndex(DbSb.TabDevice.Cols.ALIAS));
        String ctrlModel = getString(getColumnIndex(DbSb.TabDevice.Cols.CTRL_MODEL));

        //设备是否可见
        boolean visibility = true;
        int iVisibility = getColumnIndex(DbSb.TabDevice.Cols.VISIBILITY);
        if(iVisibility != -1){
            String strVisibility = getString(iVisibility);
            if(null != strVisibility){
                visibility = getString(getColumnIndex(DbSb.TabDevice.Cols.VISIBILITY)).equals("1");
            }
        }

        boolean deleted = getString(getColumnIndex(DbSb.TabDevice.Cols.DELETED)).equals("1");
        String devCategory = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_CATEGORY));
        String stateId = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_STATE_ID));
        String gear = getString(getColumnIndex(DbSb.TabDevice.Cols.GEAR));
        String mainCodeId = getString(getColumnIndex(DbSb.TabDevice.Cols.MAIN_CODE_ID));
        String name = getString(getColumnIndex(DbSb.TabDevice.Cols.NAME));
        String place = getString(getColumnIndex(DbSb.TabDevice.Cols.PLACE));
        int sortIndex = Integer.parseInt(getString(getColumnIndex(DbSb.TabDevice.Cols.SORT_INDEX)));
        String subCode = getString(getColumnIndex(DbSb.TabDevice.Cols.SUB_CODE));
        String panid = getString(getColumnIndex(DbSb.TabDevice.Cols.PANID));
        //String devGroupId = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_GROUP_ID));
        //String parentId = getString(getColumnIndex(DbSb.TabDevice.Cols.PARENT_ID));

        Device device = DeviceAssistent.createDeviceByMcId(mainCodeId, subCode);
        device.setId(id);
        device.setAlias(alias);
        device.setCtrlModel(Enum.valueOf(CtrlModel.class, ctrlModel));
        device.setVisibility(visibility);
        device.setDeleted(deleted);
        device.setDevCategory(Enum.valueOf(DevCategory.class, devCategory));
        device.setDevStateId(stateId);
        device.setGear(Enum.valueOf(Gear.class, gear));
        device.setName(name);
        device.setPlace(place);
        device.setSortIndex(sortIndex);
        if(device instanceof Coordinator) {
            ((Coordinator)device).setPanid(panid);
        }
        return device;
    }

}
