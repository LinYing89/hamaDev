package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.timing.MyTime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LoopDurationDao {

    private static LoopDurationDao loopDurationDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static LoopDurationDao get(Context context) {
        if(null == loopDurationDao){
            loopDurationDao = new LoopDurationDao(context);
        }
        return loopDurationDao;
    }

    private LoopDurationDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(LoopDuration loopDuration, String zLoopId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabLoopDuration.Cols.ID, loopDuration.getId());
        if(null != zLoopId) {
            values.put(DbSb.TabLoopDuration.Cols.ZLOOP_ID, zLoopId);
        }
        values.put(DbSb.TabLoopDuration.Cols.DELETED, loopDuration.isDeleted());
        return values;
    }

    public void add(LoopDuration loopDuration, String zLoopId) {
        List<LoopDuration> list = find(DbSb.TabLoopDuration.Cols.ID + " = ?"
                , new String[]{loopDuration.getId()});
        if(list.size() > 0){
            update(loopDuration, zLoopId);
        }else{
            ContentValues values1 = getContentValues(loopDuration, zLoopId);
            mDatabase.insert(DbSb.TabLoopDuration.NAME, null, values1);
        }
    }

    public void delete(LoopDuration loopDuration){
        MyTimeDao myTimeDao = MyTimeDao.get(mContext);
        for(MyTime myTime : loopDuration.getListTimes()){
            myTimeDao.delete(myTime);
        }
        mDatabase.delete(DbSb.TabLoopDuration.NAME, DbSb.TabLoopDuration.Cols.ID + "=?", new String[]{loopDuration.getId()});
    }

    public void update(LoopDuration loopDuration, String zLoopId) {
        ContentValues values = getContentValues(loopDuration, zLoopId);
        mDatabase.update(DbSb.TabLoopDuration.NAME, values,
                "id = ?",
                new String[] { loopDuration.getId() });
    }

    private List<LoopDuration> find(String whereClause, String[] whereArgs) {
        LoopDurationWrapper cursor = query(whereClause, whereArgs);
        return createLoopDuration(cursor);
    }

    public List<LoopDuration> findById(String zLoopId) {
        String whereClause = DbSb.TabLoopDuration.Cols.ZLOOP_ID + " = ?";
        String[] whereArgs = new String[]{zLoopId};
        LoopDurationWrapper cursor = query(whereClause, whereArgs);
        return createLoopDuration(cursor);
    }

    private LoopDurationWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabLoopDuration.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LoopDurationWrapper(cursor);
    }

    private List<LoopDuration> createLoopDuration(LoopDurationWrapper cursor){
        List<LoopDuration> loopDurations = new ArrayList<>();
        try {
            cursor.moveToFirst();
            MyTimeDao myTimeDao = MyTimeDao.get(mContext);
            while (!cursor.isAfterLast()){
                LoopDuration loopDuration = cursor.getLoopDuration(mContext);
                loopDurations.add(loopDuration);
                loopDuration.setListTimes(myTimeDao.findByTimerId(loopDuration.getId()));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return loopDurations;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabLoopDuration.NAME);
    }
}
