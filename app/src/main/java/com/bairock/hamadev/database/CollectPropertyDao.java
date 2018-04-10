package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

/**
 *
 * Created by 44489 on 2017/9/1.
 */

public class CollectPropertyDao {

    private static CollectPropertyDao collectPropertyDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CollectPropertyDao get(Context context) {
        if(null == collectPropertyDao){
            collectPropertyDao = new CollectPropertyDao(context);
        }
        return collectPropertyDao;
    }

    private CollectPropertyDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(CollectProperty collectProperty) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabCollectProperty.Cols.ID, collectProperty.getId());
        values.put(DbSb.TabCollectProperty.Cols.CREST_VALUE, collectProperty.getCrestValue());
        values.put(DbSb.TabCollectProperty.Cols.CREST_REFER_VALUE, collectProperty.getCrestReferValue());
        values.put(DbSb.TabCollectProperty.Cols.CURRENT_VALUE, collectProperty.getCurrentValue());
        values.put(DbSb.TabCollectProperty.Cols.LEAST_VALUE, collectProperty.getLeastValue());
        values.put(DbSb.TabCollectProperty.Cols.LEAST_REFER_VALUE, collectProperty.getLeastReferValue());
        values.put(DbSb.TabCollectProperty.Cols.PERCENT, collectProperty.getPercent());
        values.put(DbSb.TabCollectProperty.Cols.CALIBRATION_VALUE, collectProperty.getCalibrationValue());
        values.put(DbSb.TabCollectProperty.Cols.FORMULA, collectProperty.getFormula());
        if(null != collectProperty.getCollectSrc()) {
            values.put(DbSb.TabCollectProperty.Cols.SIGNAL_SRC, collectProperty.getCollectSrc().toString());
        }
        if(null != collectProperty.getUnitSymbol()) {
            values.put(DbSb.TabCollectProperty.Cols.UNIT_SYMBOL, collectProperty.getUnitSymbol());
        }
        values.put(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID, collectProperty.getDevCollect().getId());
        return values;
    }

    public void add(CollectProperty collectProperty){
        CollectProperty collectProperty1 = find(collectProperty.getDevCollect());
        if(null != collectProperty1){
            return;
        }
        ContentValues values = getContentValues(collectProperty);
        mDatabase.insert(DbSb.TabCollectProperty.NAME, null, values);
    }

    public void delete(CollectProperty collectProperty){
        mDatabase.delete(DbSb.TabCollectProperty.NAME, DbSb.TabCollectProperty.Cols.ID + "=?", new String[]{collectProperty.getId()});
    }

    public CollectProperty find(DevCollect devCollect) {
        return find(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID + " = ?", new String[]{devCollect.getId()});
    }

    public CollectProperty find(String whereClause, String[] whereArgs) {
        CollectProperty collectProperty = null;
        CollectPropertyCursorWrapper cursor = query(whereClause, whereArgs);
        try {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                collectProperty = cursor.getCollectProperty();
            }
        } finally {
            cursor.close();
        }
        return collectProperty;
    }

    public void update(CollectProperty collectProperty) {
        ContentValues values = getContentValues(collectProperty);
        mDatabase.update(DbSb.TabCollectProperty.NAME, values,
                "id = ?",
                new String[] { collectProperty.getId() });
    }

    private CollectPropertyCursorWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabCollectProperty.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CollectPropertyCursorWrapper(cursor);
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabCollectProperty.NAME);
    }
}
