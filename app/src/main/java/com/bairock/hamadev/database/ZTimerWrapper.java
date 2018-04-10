package com.bairock.hamadev.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.timing.ZTimer;

/**
 *
 * Created by 44489 on 2017/9/26.
 */

public class ZTimerWrapper extends CursorWrapper {

    public ZTimerWrapper(Cursor cursor) {
        super(cursor);
    }

    public ZTimer getZTimer(Context context) {
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabZLoop.Cols.LINKAGE_HOLDER_ID));
        String id = getString(getColumnIndex(DbSb.TabZTimer.Cols.ID));
        boolean enable = getString(getColumnIndex(DbSb.TabZTimer.Cols.ENABLE)).equals("1");
        boolean deleted = getString(getColumnIndex(DbSb.TabZTimer.Cols.DELETED)).equals("1");
        ZTimer zTimer = new ZTimer();
        zTimer.setId(id);
        zTimer.setEnable(enable);
        zTimer.setDeleted(deleted);
        return zTimer;
    }
}
