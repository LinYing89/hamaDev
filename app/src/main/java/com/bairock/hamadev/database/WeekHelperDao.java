package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.WeekHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/26.
 */

public class WeekHelperDao {
    private static WeekHelperDao weekHelperDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static WeekHelperDao get(Context context) {
        if(null == weekHelperDao){
            weekHelperDao = new WeekHelperDao(context);
        }
        return weekHelperDao;
    }

    private WeekHelperDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(WeekHelper weekHelper) {
        ContentValues values = new ContentValues();
        if(weekHelper.getzTimer() != null){
            values.put(DbSb.TabWeekHelper.Cols.ZTIMER_ID, weekHelper.getzTimer().getId());
        }
        values.put(DbSb.TabWeekHelper.Cols.ID, weekHelper.getId());
        values.put(DbSb.TabWeekHelper.Cols.SUN, weekHelper.isSun());
        values.put(DbSb.TabWeekHelper.Cols.MON, weekHelper.isMon());
        values.put(DbSb.TabWeekHelper.Cols.TUES, weekHelper.isTues());
        values.put(DbSb.TabWeekHelper.Cols.WED, weekHelper.isWed());
        values.put(DbSb.TabWeekHelper.Cols.THUR, weekHelper.isThur());
        values.put(DbSb.TabWeekHelper.Cols.FRI, weekHelper.isFri());
        values.put(DbSb.TabWeekHelper.Cols.SAT, weekHelper.isSat());
        return values;
    }

    public void add(WeekHelper weekHelper) {
        List<WeekHelper> list = find(DbSb.TabWeekHelper.Cols.ID + " = ?"
                , new String[]{weekHelper.getId()});
        if(list.size() > 0){
            update(weekHelper);
        }else{
            ContentValues values1 = getContentValues(weekHelper);
            mDatabase.insert(DbSb.TabWeekHelper.NAME, null, values1);
        }
    }

    public void delete(WeekHelper weekHelper){
        mDatabase.delete(DbSb.TabWeekHelper.NAME, DbSb.TabWeekHelper.Cols.ID + "=?", new String[]{weekHelper.getId()});
    }

    public void update(WeekHelper weekHelper) {
        ContentValues values = getContentValues(weekHelper);
        mDatabase.update(DbSb.TabWeekHelper.NAME, values,
                "id = ?",
                new String[] { weekHelper.getId() });
    }

    private List<WeekHelper> find(String whereClause, String[] whereArgs) {
        WeekHelperWrapper cursor = query(whereClause, whereArgs);
        return createWeekHelper(cursor);
    }

    public WeekHelper findById(String id) {
        String whereClause = DbSb.TabWeekHelper.Cols.ID + " = ?";
        String[] whereArgs = new String[]{id};
        WeekHelperWrapper cursor = query(whereClause, whereArgs);
        List<WeekHelper> list = createWeekHelper(cursor);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }
    public WeekHelper findByZTimerId(String zTimerId) {
        String whereClause = DbSb.TabWeekHelper.Cols.ZTIMER_ID + " = ?";
        String[] whereArgs = new String[]{zTimerId};
        WeekHelperWrapper cursor = query(whereClause, whereArgs);
        List<WeekHelper> list = createWeekHelper(cursor);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    private WeekHelperWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabWeekHelper.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new WeekHelperWrapper(cursor);
    }

    private List<WeekHelper> createWeekHelper(WeekHelperWrapper cursor){
        List<WeekHelper> weekHelpers = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                WeekHelper weekHelper = cursor.getWeekHelper();
                weekHelpers.add(weekHelper);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return weekHelpers;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabWeekHelper.NAME);
    }
}
