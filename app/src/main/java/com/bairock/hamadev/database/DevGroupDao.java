package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.user.DevGroup;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class DevGroupDao {

    private static DevGroupDao devGroupDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DevGroupDao get(Context context) {
        if(null == devGroupDao){
            devGroupDao = new DevGroupDao(context);
        }
        return devGroupDao;
    }

    private DevGroupDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(DevGroup group) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabDevGroup.Cols.ID, group.getId());
        values.put(DbSb.TabDevGroup.Cols.NAME, group.getName());
        values.put(DbSb.TabDevGroup.Cols.PET_NAME, group.getPetName());
        values.put(DbSb.TabDevGroup.Cols.PSD, group.getPsd());
        values.put(DbSb.TabDevGroup.Cols.USER_ID, group.getUser().getId());
        return values;
    }

    public void add(DevGroup group) {
        ContentValues values = getContentValues(group);
        mDatabase.insert(DbSb.TabDevGroup.NAME, null, values);
    }

    public void update(DevGroup group) {
        ContentValues values = getContentValues(group);
        mDatabase.update(DbSb.TabDevGroup.NAME, values,
                "id = ?",
                new String[] { group.getId() });
    }

    public DevGroup find() {
        DevGroup user = null;
        GroupCursorWrapper cursor = query();
        try {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                user = cursor.getDevGroup();
            }
        } finally {
            cursor.close();
        }
        return user;
    }

    private Cursor query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabDevGroup.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return cursor;
    }

    private GroupCursorWrapper query() {
        Cursor cursor = mDatabase.query(
                DbSb.TabDevGroup.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new GroupCursorWrapper(cursor);
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabDevGroup.NAME);
    }
}
