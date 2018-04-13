package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.CompareSymbol;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.TriggerStyle;
import com.bairock.iot.intelDev.linkage.ZLogic;
import com.bairock.iot.intelDev.user.DevGroup;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LinkageConditionWrapper extends CursorWrapper {

    public static DevGroup devGroup;

    public LinkageConditionWrapper(Cursor cursor) {
        super(cursor);
    }

    public LinkageCondition getLinkageCondition() {
        String devId = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.DEV_ID));
        Device device = devGroup.findDeviceByDevId(devId);
        if(null == device){
            return null;
        }

        String id = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.ID));
        //String linkageId = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.LINKAGE_ID));
        CompareSymbol compareSymbol = Enum.valueOf(CompareSymbol.class, getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.COMPARE_SYMBOL)));
        float compareValue = Float.valueOf(getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.COMPARE_VALUE)));
        ZLogic logic = Enum.valueOf(ZLogic.class, getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.LOGIC)));
        TriggerStyle triggerStyle = Enum.valueOf(TriggerStyle.class, getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.TRIGGER_STYLE)));

        LinkageCondition linkageCondition = new LinkageCondition();
        linkageCondition.setId(id);
        linkageCondition.setCompareSymbol(compareSymbol);
        linkageCondition.setCompareValue(compareValue);
        linkageCondition.setLogic(logic);
        linkageCondition.setTriggerStyle(triggerStyle);
        linkageCondition.setDevice(device);
        return linkageCondition;
    }
}
