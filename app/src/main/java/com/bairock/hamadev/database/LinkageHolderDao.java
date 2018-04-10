package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.ChainHolder;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder;
import com.bairock.iot.intelDev.linkage.loop.LoopHolder;
import com.bairock.iot.intelDev.linkage.timing.TimingHolder;

import java.util.ArrayList;
import java.util.List;

/**
 *  连锁控制器数据库访问
 * Created by Administrator on 2017/9/11.
 */

public class LinkageHolderDao {
    private static LinkageHolderDao linkageHolderDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static LinkageHolderDao get(Context context) {
        if(null == linkageHolderDao){
            linkageHolderDao = new LinkageHolderDao(context);
        }
        return linkageHolderDao;
    }

    private LinkageHolderDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(LinkageHolder linkageHolder) {
        ContentValues values = new ContentValues();
        if(null != linkageHolder.getDevGroup()){
            values.put(DbSb.TabLinkageHolder.Cols.DEVGROUP_ID, linkageHolder.getDevGroup().getId());
        }
        values.put(DbSb.TabLinkageHolder.Cols.ID, linkageHolder.getId());
        values.put(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE, linkageHolder.getClass().getSimpleName());
        values.put(DbSb.TabLinkageHolder.Cols.ENABLE, linkageHolder.isEnable());
        return values;
    }

    private static ContentValues getUpdateIdContentValues(String newId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabLinkageHolder.Cols.ID, newId);
        return values;
    }

    public void add(LinkageHolder linkageHolder) {
        List<LinkageHolder> list = find(DbSb.TabLinkageHolder.Cols.ID + " = ?"
                , new String[]{linkageHolder.getId()});
        if(list.size() > 0){
            update(linkageHolder);
        }else{
            ContentValues values1 = getContentValues(linkageHolder);
            mDatabase.insert(DbSb.TabLinkageHolder.NAME, null, values1);
        }
    }

    public void update(LinkageHolder linkageHolder) {
        ContentValues values = getContentValues(linkageHolder);
        mDatabase.update(DbSb.TabLinkageHolder.NAME, values,
                "id = ?",
                new String[] { linkageHolder.getId() });
    }

    public void updateId(String oldId, String newId) {
        ContentValues values = getUpdateIdContentValues(newId);
        mDatabase.update(DbSb.TabLinkageHolder.NAME, values,
                "id = ?",
                new String[] { oldId });
    }

    public List<LinkageHolder> find() {
        LinkageHolderWrapper cursor = query();
        return createLinkageHolder(cursor);
    }

    public List<LinkageHolder> find(String whereClause, String[] whereArgs) {
        LinkageHolderWrapper cursor = query(whereClause, whereArgs);
        return createLinkageHolder(cursor);
    }

    public List<LinkageHolder> findByDevGroupId(String devGroupId) {
        LinkageHolderWrapper cursor = query(DbSb.TabLinkageHolder.Cols.DEVGROUP_ID + " =?", new String[]{devGroupId});
        return createLinkageHolder(cursor);
    }

    public ChainHolder findChainHolder() {
        LinkageHolderWrapper cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", new String[]{ChainHolder.class.getSimpleName()});
        List<LinkageHolder> list = createLinkageHolder(cursor);
        if(list.size() > 0){
            return (ChainHolder)(list.get(0));
        }
        return null;
    }
    public LoopHolder findLoopHolder() {
        LinkageHolderWrapper cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", new String[]{LoopHolder.class.getSimpleName()});
        List<LinkageHolder> list = createLinkageHolder(cursor);
        if(list.size() > 0){
            return (LoopHolder)(list.get(0));
        }
        return null;
    }
    public TimingHolder findTimingHolder() {
        LinkageHolderWrapper cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", new String[]{TimingHolder.class.getSimpleName()});
        List<LinkageHolder> list = createLinkageHolder(cursor);
        if(list.size() > 0){
            return (TimingHolder)(list.get(0));
        }
        return null;
    }
    public GuaguaHolder findGuaguaHolder() {
        LinkageHolderWrapper cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", new String[]{GuaguaHolder.class.getSimpleName()});
        List<LinkageHolder> list = createLinkageHolder(cursor);
        if(list.size() > 0){
            return (GuaguaHolder)(list.get(0));
        }
        return null;
    }

    private LinkageHolderWrapper query() {
        Cursor cursor = mDatabase.query(
                DbSb.TabLinkageHolder.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LinkageHolderWrapper(cursor);
    }

    private LinkageHolderWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabLinkageHolder.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LinkageHolderWrapper(cursor);
    }

    private List<LinkageHolder> createLinkageHolder(LinkageHolderWrapper cursor){
        List<LinkageHolder> listLinkageHolder = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                LinkageHolder linkageHolder = cursor.getLinkageHolder();
                listLinkageHolder.add(linkageHolder);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listLinkageHolder;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabLinkageHolder.NAME);
    }
}
