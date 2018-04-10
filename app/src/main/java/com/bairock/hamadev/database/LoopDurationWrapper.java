package com.bairock.hamadev.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.timing.MyTime;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LoopDurationWrapper extends CursorWrapper {
    public LoopDurationWrapper(Cursor cursor) {
        super(cursor);
    }

    public LoopDuration getLoopDuration(Context context) {
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabZLoop.Cols.LINKAGE_HOLDER_ID));
        String id = getString(getColumnIndex(DbSb.TabLoopDuration.Cols.ID));
        boolean deleted = getString(getColumnIndex(DbSb.TabLoopDuration.Cols.DELETED)).equals("1");
        LoopDuration loopDuration = new LoopDuration();
        loopDuration.setId(id);
        loopDuration.setDeleted(deleted);
        return loopDuration;
    }
}
