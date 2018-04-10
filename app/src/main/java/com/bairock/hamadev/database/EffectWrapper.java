package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.user.DevGroup;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class EffectWrapper extends CursorWrapper {

    public static DevGroup devGroup;

    public EffectWrapper(Cursor cursor) {
        super(cursor);
    }

    public Effect getEffect() {
        String devId = getString(getColumnIndex(DbSb.TabEffect.Cols.DEV_ID));
        Device device = devGroup.findDeviceByDevId(devId);
        if(null == device){
            return null;
        }

        String id = getString(getColumnIndex(DbSb.TabEffect.Cols.ID));
        //String linkageId = getString(getColumnIndex(DbSb.TabEffect.Cols.LINKAGE_ID));
        String dsId = getString(getColumnIndex(DbSb.TabEffect.Cols.DS_ID));
        boolean deleted = getString(getColumnIndex(DbSb.TabEffect.Cols.DELETED)).equals("1");

        String effectContent = getString(getColumnIndex(DbSb.TabEffect.Cols.EFFECT_CONTENT));
        String strEffectCount = getString(getColumnIndex(DbSb.TabEffect.Cols.EFFECT_COUNT));
        int effectCount = 0;
        try {
            effectCount = Integer.parseInt(strEffectCount);
        }catch (Exception e){
            e.printStackTrace();
        }
        Effect effect = new Effect();
        effect.setId(id);
        effect.setDsId(dsId);
        effect.setDeleted(deleted);
        effect.setDevice(device);
        effect.setEffectContent(effectContent);
        effect.setEffectCount(effectCount);
        return effect;
    }

}
