package com.bairock.hamadev.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.WeekHelper;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

/**
 * Created by 44489 on 2017/9/26.
 */

public class WeekHelperWrapper extends CursorWrapper {

    public WeekHelperWrapper(Cursor cursor) {
        super(cursor);
    }

    public WeekHelper getWeekHelper() {
        String id = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.ID));
        boolean sun = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.SUN)).equals("1");
        boolean mon = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.MON)).equals("1");
        boolean tues = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.TUES)).equals("1");
        boolean wed = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.WED)).equals("1");
        boolean thur = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.THUR)).equals("1");
        boolean fri = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.FRI)).equals("1");
        boolean sat = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.SAT)).equals("1");
        WeekHelper weekHelper = new WeekHelper();
        weekHelper.setId(id);
        weekHelper.setSun(sun);
        weekHelper.setMon(mon);
        weekHelper.setTues(tues);
        weekHelper.setWed(wed);
        weekHelper.setThur(thur);
        weekHelper.setFri(fri);
        weekHelper.setSat(sat);
        return weekHelper;
    }

}
