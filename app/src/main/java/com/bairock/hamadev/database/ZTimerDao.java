package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/26.
 */

public class ZTimerDao {

    private static ZTimerDao zTimerDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ZTimerDao get(Context context) {
        if(null == zTimerDao){
            zTimerDao = new ZTimerDao(context);
        }
        return zTimerDao;
    }

    private ZTimerDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(ZTimer zTimer, String linkageHolderId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabZTimer.Cols.ID, zTimer.getId());
        if(null != linkageHolderId) {
            values.put(DbSb.TabZTimer.Cols.TIMING_ID, linkageHolderId);
        }
        values.put(DbSb.TabZTimer.Cols.ENABLE, zTimer.isEnable());
        values.put(DbSb.TabZTimer.Cols.DELETED, zTimer.isDeleted());
        return values;
    }

    public void add(ZTimer zTimer, String linkageHolderId) {
        List<ZTimer> list = find(DbSb.TabZTimer.Cols.ID + " = ?"
                , new String[]{zTimer.getId()});
        if(list.size() > 0){
            update(zTimer, linkageHolderId);
        }else{
            ContentValues values1 = getContentValues(zTimer, linkageHolderId);
            mDatabase.insert(DbSb.TabZTimer.NAME, null, values1);
        }
    }

    public void delete(ZTimer zTimer){
        MyTimeDao myTimeDao = MyTimeDao.get(mContext);
        for (MyTime myTime : zTimer.getListTimes()){
            myTimeDao.delete(myTime);
        }
        WeekHelperDao.get(mContext).delete(zTimer.getWeekHelper());

        mDatabase.delete(DbSb.TabZTimer.NAME, DbSb.TabZTimer.Cols.ID + "=?", new String[]{zTimer.getId()});
    }

    public void update(ZTimer zTimer, String linkageHolderId) {
        ContentValues values = getContentValues(zTimer, linkageHolderId);
        mDatabase.update(DbSb.TabZTimer.NAME, values,
                "id = ?",
                new String[] { zTimer.getId() });
    }

    private List<ZTimer> find(String whereClause, String[] whereArgs) {
        ZTimerWrapper cursor = query(whereClause, whereArgs);
        return createZTimer(cursor);
    }

    public List<ZTimer> findByLinkageId(String linkageId) {
        String whereClause = DbSb.TabZTimer.Cols.TIMING_ID + " = ?";
        String[] whereArgs = new String[]{linkageId};
        ZTimerWrapper cursor = query(whereClause, whereArgs);
        return createZTimer(cursor);
    }

    private ZTimerWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabZTimer.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ZTimerWrapper(cursor);
    }

    private List<ZTimer> createZTimer(ZTimerWrapper cursor){
        List<ZTimer> zTimers = new ArrayList<>();
        try {
            cursor.moveToFirst();
            MyTimeDao myTimeDao = MyTimeDao.get(mContext);
            WeekHelperDao weekHelperDao = WeekHelperDao.get(mContext);
            while (!cursor.isAfterLast()){
                ZTimer zTimer = cursor.getZTimer(mContext);
                zTimers.add(zTimer);
                zTimer.setListTimes(myTimeDao.findByTimerId(zTimer.getId()));
                zTimer.setWeekHelper(weekHelperDao.findByZTimerId(zTimer.getId()));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return zTimers;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabZTimer.NAME);
    }
}
