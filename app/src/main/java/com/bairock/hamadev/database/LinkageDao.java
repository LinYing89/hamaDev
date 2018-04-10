package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
import com.bairock.iot.intelDev.user.DevGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LinkageDao {
    private static LinkageDao linkageDevValueDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static LinkageDao get(Context context) {
        if(null == linkageDevValueDao){
            linkageDevValueDao = new LinkageDao(context);
        }
        return linkageDevValueDao;
    }

    private LinkageDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Linkage linkage, String linkageHolderId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabLinkage.Cols.ID, linkage.getId());
        if(null != linkageHolderId) {
            values.put(DbSb.TabLinkage.Cols.LINKAGE_HOLDER_ID, linkageHolderId);
        }
        values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, linkage.getClass().getSimpleName());
        values.put(DbSb.TabLinkage.Cols.NAME, linkage.getName());
        values.put(DbSb.TabLinkage.Cols.ENABLE, linkage.isEnable());
        values.put(DbSb.TabLinkage.Cols.DELETED, linkage.isDeleted());
        if(linkage instanceof SubChain){
            values.put(DbSb.TabLinkage.Cols.TRIGGERED, ((SubChain)linkage).isTriggered());
            if(linkage instanceof ZLoop){
                values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, ZLoop.class.getSimpleName());
                values.put(DbSb.TabLinkage.Cols.LOOP_COUNT, ((ZLoop)linkage).getLoopCount());
            }else {
                values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, SubChain.class.getSimpleName());
            }
        }else if(linkage instanceof Timing){
            values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, Timing.class.getSimpleName());
        }
        return values;
    }

    private static ContentValues getUpdateIdContentValues(String newId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabLinkage.Cols.ID, newId);
        return values;
    }

    public void add(Linkage linkage, String linkageHolderId) {
        List<Linkage> list = find(DbSb.TabLinkage.Cols.ID + " = ?"
                , new String[]{linkage.getId()});
        if(list.size() > 0){
            update(linkage, linkageHolderId);
        }else{
            ContentValues values1 = getContentValues(linkage, linkageHolderId);
            mDatabase.insert(DbSb.TabLinkage.NAME, null, values1);
        }
    }

    public void delete(Linkage linkage){
        if(linkage instanceof SubChain){
            LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(mContext);
            for(LinkageCondition linkageCondition : ((SubChain)linkage).getListCondition()){
                linkageConditionDao.delete(linkageCondition);
            }
            if(linkage instanceof ZLoop){
                LoopDurationDao loopDurationDao = LoopDurationDao.get(mContext);
                for(LoopDuration loopDuration : ((ZLoop)linkage).getListLoopDuration()){
                    loopDurationDao.delete(loopDuration);
                }
            }
        }else if(linkage instanceof Timing){
            ZTimerDao zTimerDao = ZTimerDao.get(mContext);
            for(ZTimer zTimer : ((Timing)linkage).getListZTimer()){
                zTimerDao.delete(zTimer);
            }
        }

        EffectDao effectDao = EffectDao.get(mContext);
        for(Effect effect : linkage.getListEffect()){
            effectDao.delete(effect);
        }

        mDatabase.delete(DbSb.TabLinkage.NAME, DbSb.TabLinkage.Cols.ID + "=?", new String[]{linkage.getId()});
    }

    public void update(Linkage linkage, String linkageHolderId) {
        ContentValues values = getContentValues(linkage, linkageHolderId);
        mDatabase.update(DbSb.TabLinkage.NAME, values,
                "id = ?",
                new String[] { linkage.getId() });
    }

    public void updateId(String oldId, String newId) {
        ContentValues values = getUpdateIdContentValues(newId);
        mDatabase.update(DbSb.TabLinkage.NAME, values,
                "id = ?",
                new String[] { oldId });
    }

    public List<Linkage> find(String whereClause, String[] whereArgs) {
        LinkageWrapper cursor = query(whereClause, whereArgs);
        return createLinkageDevValue(cursor, true);
    }

    public List<Linkage> findChainByLinkageHolderId(String linkageHolderId) {
        String whereClause = DbSb.TabLinkage.Cols.LINKAGE_HOLDER_ID + " = ?";
        String[] whereArgs = new String[]{linkageHolderId};
        LinkageWrapper cursor = query(whereClause, whereArgs);
        return createLinkageDevValue(cursor, false);
    }

    private LinkageWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabLinkage.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LinkageWrapper(cursor);
    }

    private List<Linkage> createLinkageDevValue(LinkageWrapper cursor, boolean lazy){
        List<Linkage> linkages = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Linkage linkage = cursor.getLinkage();
                linkages.add(linkage);
                if(!lazy) {
                    initLinkage(linkage);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return linkages;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabLinkage.NAME);
    }

    private void initLinkage(Linkage linkage){
        if(linkage instanceof SubChain){
            LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(mContext);
            List<LinkageCondition> linkageConditions = linkageConditionDao.findByLinkageId(linkage.getId());
            ((SubChain) linkage).setListCondition(linkageConditions);
            if(linkage instanceof ZLoop){
                LoopDurationDao loopDurationDao = LoopDurationDao.get(mContext);
                List<LoopDuration> loopDurations = loopDurationDao.findById(linkage.getId());
                ((ZLoop) linkage).setListLoopDuration(loopDurations);
            }
        }else if(linkage instanceof Timing){
            ZTimerDao zTimerDao = ZTimerDao.get(mContext);
            List<ZTimer> list = zTimerDao.findByLinkageId(linkage.getId());
            ((Timing) linkage).setListZTimer(list);
        }
        EffectDao effectDao = EffectDao.get(mContext);
        linkage.setListEffect(effectDao.findByLinkageId(linkage.getId()));
    }
}
