package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class EffectDao {

    private static EffectDao effectDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static EffectDao get(Context context) {
        if(null == effectDao){
            effectDao = new EffectDao(context);
        }
        return effectDao;
    }

    private EffectDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Effect effect, String linkageId) {
        ContentValues values = new ContentValues();
        if(null != linkageId) {
            values.put(DbSb.TabEffect.Cols.LINKAGE_ID, linkageId);
        }
        values.put(DbSb.TabEffect.Cols.ID, effect.getId());
        values.put(DbSb.TabEffect.Cols.DS_ID, effect.getDsId());
        values.put(DbSb.TabEffect.Cols.DEV_ID, effect.getDevice().getId());
        values.put(DbSb.TabEffect.Cols.DELETED, effect.isDeleted());
        values.put(DbSb.TabEffect.Cols.EFFECT_CONTENT, effect.getEffectContent());
        values.put(DbSb.TabEffect.Cols.EFFECT_COUNT, effect.getEffectCount());
        return values;
    }

    public void add(Effect effect, String linkageId) {
        List<Effect> list = find(DbSb.TabEffect.Cols.ID + " = ?"
                , new String[]{effect.getId()});
        if(list.size() > 0){
            update(effect, linkageId);
        }else{
            ContentValues values1 = getContentValues(effect, linkageId);
            mDatabase.insert(DbSb.TabEffect.NAME, null, values1);
        }
    }

    public void delete(Effect effect){
        deleteById(effect.getId());
    }

    public void deleteById(String id){
        mDatabase.delete(DbSb.TabEffect.NAME, DbSb.TabEffect.Cols.ID + "=?", new String[]{id});
    }

    public void update(Effect effect, String linkageId) {
        ContentValues values = getContentValues(effect, linkageId);
        mDatabase.update(DbSb.TabEffect.NAME, values,
                "id = ?",
                new String[] { effect.getId() });
    }

    private List<Effect> find(String whereClause, String[] whereArgs) {
        EffectWrapper cursor = query(whereClause, whereArgs);
        return createEffect(cursor);
    }

    public List<Effect> findByLinkageId(String linkageId) {
        String whereClause = DbSb.TabEffect.Cols.LINKAGE_ID + " = ?";
        String[] whereArgs = new String[]{linkageId};
        EffectWrapper cursor = query(whereClause, whereArgs);
        return createEffect(cursor);
    }

    private EffectWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabEffect.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new EffectWrapper(cursor);
    }

    private List<Effect> createEffect(EffectWrapper cursor){
        List<Effect> listEffect = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Effect effect = cursor.getEffect();
                if(null != effect) {
                    listEffect.add(effect);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listEffect;
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabEffect.NAME);
    }
}
