package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.linkage.LinkageCondition;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LinkageConditionDao {

    private static LinkageConditionDao linkageConditionDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static LinkageConditionDao get(Context context) {
        if(null == linkageConditionDao){
            linkageConditionDao = new LinkageConditionDao(context);
        }
        return linkageConditionDao;
    }

    private LinkageConditionDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(LinkageCondition linkageCondition, String linkageId) {
        ContentValues values = new ContentValues();
        if(null != linkageId) {
            values.put(DbSb.TabLinkageCondition.Cols.SUBCHAIN_ID, linkageId);
        }
        values.put(DbSb.TabLinkageCondition.Cols.ID, linkageCondition.getId());
        values.put(DbSb.TabLinkageCondition.Cols.COMPARE_SYMBOL, linkageCondition.getCompareSymbol().toString());
        values.put(DbSb.TabLinkageCondition.Cols.COMPARE_VALUE, linkageCondition.getCompareValue());
        values.put(DbSb.TabLinkageCondition.Cols.LOGIC, linkageCondition.getLogic().toString());
        values.put(DbSb.TabLinkageCondition.Cols.TRIGGER_STYLE, linkageCondition.getTriggerStyle().toString());
        values.put(DbSb.TabLinkageCondition.Cols.DEV_ID, linkageCondition.getDevice().getId());
        values.put(DbSb.TabLinkageCondition.Cols.DELETED, linkageCondition.isDeleted());
        return values;
    }

    public void add(LinkageCondition linkageCondition, String linkageId) {
        List<LinkageCondition> list = find(DbSb.TabLinkageCondition.Cols.ID + " = ?"
                , new String[]{linkageCondition.getId()});
        if(list.size() > 0){
            update(linkageCondition, linkageId);
        }else{
            ContentValues values1 = getContentValues(linkageCondition, linkageId);
            mDatabase.insert(DbSb.TabLinkageCondition.NAME, null, values1);
        }
    }

    public void delete(LinkageCondition linkageCondition){
        deleteById(linkageCondition.getId());
        //mDatabase.delete(DbSb.TabLinkageCondition.NAME, DbSb.TabLinkageCondition.Cols.ID + "=?", new String[]{linkageCondition.getId()});
    }

    public void deleteById(String id){
        mDatabase.delete(DbSb.TabLinkageCondition.NAME, DbSb.TabLinkageCondition.Cols.ID + "=?", new String[]{id});
    }

    public void update(LinkageCondition linkageCondition, String linkageId) {
        ContentValues values = getContentValues(linkageCondition, linkageId);
        mDatabase.update(DbSb.TabLinkageCondition.NAME, values,
                "id = ?",
                new String[] { linkageCondition.getId() });
    }

    private List<LinkageCondition> find(String whereClause, String[] whereArgs) {
        LinkageConditionWrapper cursor = query(whereClause, whereArgs);
        return createLinkageCondition(cursor);
    }

    public List<LinkageCondition> findByLinkageId(String linkageId) {
        String whereClause = DbSb.TabLinkageCondition.Cols.SUBCHAIN_ID + " = ?";
        String[] whereArgs = new String[]{linkageId};
        LinkageConditionWrapper cursor = query(whereClause, whereArgs);
        return createLinkageCondition(cursor);
    }

    private LinkageConditionWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabLinkageCondition.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LinkageConditionWrapper(cursor);
    }

    private List<LinkageCondition> createLinkageCondition(LinkageConditionWrapper cursor){
        List<LinkageCondition> listLinkageCondition = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                LinkageCondition linkageCondition = cursor.getLinkageCondition();
                if(null != linkageCondition) {
                    listLinkageCondition.add(linkageCondition);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listLinkageCondition;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabLinkageCondition.NAME);
    }
}
