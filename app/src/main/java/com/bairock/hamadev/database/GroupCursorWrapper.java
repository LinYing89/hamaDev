package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.user.DevGroup;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

class GroupCursorWrapper extends CursorWrapper {

    GroupCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    DevGroup getDevGroup() {
        String id = getString(getColumnIndex(DbSb.TabDevGroup.Cols.ID));
        String name = getString(getColumnIndex(DbSb.TabDevGroup.Cols.NAME));
        String petName = getString(getColumnIndex(DbSb.TabDevGroup.Cols.PET_NAME));
        String psd = getString(getColumnIndex(DbSb.TabDevGroup.Cols.PSD));
        //String userId = getString(getColumnIndex(DbSb.TabDevGroup.Cols.USER_ID));
        DevGroup group = new DevGroup();
        group.setId(id);
        group.setName(name);
        group.setPetName(petName);
        group.setPsd(psd);

        return group;
    }
}
