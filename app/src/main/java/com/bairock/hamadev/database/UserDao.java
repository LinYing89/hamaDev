package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.user.User;

import static com.bairock.hamadev.database.DbSb.TabUser;

/**
 * user表数据库处理
 * Created by Administrator on 2017/8/8.
 */

public class UserDao {

    private static UserDao userDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static UserDao get(Context context) {
        if(null == userDao){
            userDao = new UserDao(context);
        }
        return userDao;
    }

    private UserDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        //values.put(TabUser.Cols.EMAIL, user.getEmail());
        values.put(TabUser.Cols.NAME, user.getName());
        values.put(TabUser.Cols.PET_NAME, user.getPetName());
        values.put(TabUser.Cols.PSD, user.getPsd());
        //values.put(TabUser.Cols.REGISTER_TIME, user.getRegisterTime().getTime());
        //values.put(TabUser.Cols.TEL, user.getTel());
        return values;
    }

    public void addUser(User c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(TabUser.NAME, null, values);
    }

    public void updateUser(User c) {
        ContentValues values = getContentValues(c);
        mDatabase.update(TabUser.NAME, values,
                "_id = ?",
                new String[] { c.getId().toString()});
    }

    public User getUser() {
        User user = null;
        UserCursorWrapper cursor = queryUser();
        try {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                user = cursor.getUser();
            }
        } finally {
            cursor.close();
        }
        return user;
    }

    private Cursor queryUser(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TabUser.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return cursor;
    }

    private UserCursorWrapper queryUser() {
        Cursor cursor = mDatabase.query(
                TabUser.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new UserCursorWrapper(cursor);
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabUser.NAME);
    }
}
