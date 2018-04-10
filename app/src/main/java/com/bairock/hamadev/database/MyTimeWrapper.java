package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.timing.MyTime;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class MyTimeWrapper extends CursorWrapper {

    public MyTimeWrapper(Cursor cursor) {
        super(cursor);
    }

    public MyTime getMyTime() {
        String id = getString(getColumnIndex(DbSb.TabMyTime.Cols.ID));
        int hour = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.HOUR)));
        int minute = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.MINUTE)));
        int second = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.SECOND)));
        int type = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.TYPE)));
        MyTime zLoop = new MyTime();
        zLoop.setId(id);
        zLoop.setType(type);
        zLoop.setHour(hour);
        zLoop.setMinute(minute);
        zLoop.setSecond(second);
        return zLoop;
    }
}
