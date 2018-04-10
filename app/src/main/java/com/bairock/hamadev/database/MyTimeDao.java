package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class MyTimeDao {

    private static MyTimeDao myTimeDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static MyTimeDao get(Context context) {
        if(null == myTimeDao){
            myTimeDao = new MyTimeDao(context);
        }
        return myTimeDao;
    }

    private MyTimeDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(MyTime myTime, String timerId) {
        ContentValues values = new ContentValues();

        if(null != timerId){
            values.put(DbSb.TabMyTime.Cols.TIMER_ID, timerId);
        }
        values.put(DbSb.TabMyTime.Cols.TYPE, myTime.getType());
        values.put(DbSb.TabMyTime.Cols.ID, myTime.getId());
        values.put(DbSb.TabMyTime.Cols.HOUR, myTime.getHour());
        values.put(DbSb.TabMyTime.Cols.MINUTE, myTime.getMinute());
        values.put(DbSb.TabMyTime.Cols.SECOND, myTime.getSecond());
        return values;
    }

    public void add(MyTime myTime, String timerId) {
        List<MyTime> list = find(DbSb.TabMyTime.Cols.ID + " = ?"
                , new String[]{myTime.getId()});
        if(list.size() > 0){
            update(myTime, timerId);
        }else{
            ContentValues values1 = getContentValues(myTime, timerId);
            mDatabase.insert(DbSb.TabMyTime.NAME, null, values1);
        }
    }

    public void delete(MyTime myTime){
        mDatabase.delete(DbSb.TabMyTime.NAME, DbSb.TabMyTime.Cols.ID + "=?", new String[]{myTime.getId()});
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabMyTime.NAME);
    }

    public void update(MyTime myTime, String timerId) {
        ContentValues values = getContentValues(myTime, timerId);
        mDatabase.update(DbSb.TabMyTime.NAME, values,
                "id = ?",
                new String[] { myTime.getId() });
    }

    private List<MyTime> find(String whereClause, String[] whereArgs) {
        MyTimeWrapper cursor = query(whereClause, whereArgs);
        return createMyTime(cursor);
    }

    public MyTime findById(String myTimeId) {
        String whereClause = DbSb.TabMyTime.Cols.ID + " = ?";
        String[] whereArgs = new String[]{myTimeId};
        MyTimeWrapper cursor = query(whereClause, whereArgs);
        List<MyTime> list = createMyTime(cursor);
        if(list.size() > 0){
            return list.get(0);
        }else{
            return new MyTime();
        }
    }

    public List<MyTime> findByTimerId(String timerId) {
        String whereClause = DbSb.TabMyTime.Cols.TIMER_ID + " = ?";
        String[] whereArgs = new String[]{timerId};
        MyTimeWrapper cursor = query(whereClause, whereArgs);
        return createMyTime(cursor);
    }

    private MyTimeWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabMyTime.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new MyTimeWrapper(cursor);
    }

    private List<MyTime> createMyTime(MyTimeWrapper cursor){
        List<MyTime> zLoops = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                MyTime myTime = cursor.getMyTime();
                zLoops.add(myTime);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return zLoops;
    }
}
