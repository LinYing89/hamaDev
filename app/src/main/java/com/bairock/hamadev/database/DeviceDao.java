package com.bairock.hamadev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.user.DevGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class DeviceDao {

    private static DeviceDao deviceDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DeviceDao get(Context context) {
        if(null == deviceDao){
            deviceDao = new DeviceDao(context);
        }
        return deviceDao;
    }

    private DeviceDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Device device) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabDevice.Cols.ID, device.getId());
        values.put(DbSb.TabDevice.Cols.DEVICE_TYPE, device.getClass().getSimpleName());
        values.put(DbSb.TabDevice.Cols.ALIAS, device.getAlias());
        values.put(DbSb.TabDevice.Cols.CTRL_MODEL, device.findSuperParent().getCtrlModel().toString());
        values.put(DbSb.TabDevice.Cols.VISIBILITY, device.isVisibility());
        values.put(DbSb.TabDevice.Cols.DELETED, device.isDeleted());
        values.put(DbSb.TabDevice.Cols.DEV_CATEGORY, device.getDevCategory().toString());
        values.put(DbSb.TabDevice.Cols.DEV_STATE_ID, device.getDevStateId());
        values.put(DbSb.TabDevice.Cols.GEAR, device.getGear().toString());
        values.put(DbSb.TabDevice.Cols.MAIN_CODE_ID, device.getMainCodeId());
        values.put(DbSb.TabDevice.Cols.NAME, device.getName());
        values.put(DbSb.TabDevice.Cols.PLACE, device.getPlace());
        values.put(DbSb.TabDevice.Cols.SORT_INDEX, device.getSortIndex());
        values.put(DbSb.TabDevice.Cols.SUB_CODE, device.getSubCode());
        if(device instanceof Coordinator) {
            values.put(DbSb.TabDevice.Cols.PANID, ((Coordinator)device).getPanid());
        }
        values.put(DbSb.TabDevice.Cols.DEV_GROUP_ID, device.findSuperParent().getDevGroup().getId());
        if(device.getParent() != null) {
            values.put(DbSb.TabDevice.Cols.PARENT_ID, device.getParent().getId());
        }
        return values;
    }

    private static ContentValues getUpdateIdContentValues(String newId) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabDevice.Cols.ID, newId);
        return values;
    }

    public void add(Device device) {
        List<Device> list = find(DbSb.TabDevice.Cols.MAIN_CODE_ID + " = ? and " + DbSb.TabDevice.Cols.SUB_CODE + " = ?"
            , new String[]{device.getMainCodeId(), device.getSubCode()});
        if(list.size() > 0){
            Device devDb = list.get(0);
            device.setId(devDb.getId());
            device.setName(devDb.getName());
            device.setDeleted(false);
            device.setAlias(devDb.getAlias());
            device.setCtrlModel(devDb.getCtrlModel());
            device.setDevCategory(devDb.getDevCategory());
            device.setGear(devDb.getGear());
            device.setPlace(devDb.getPlace());
            device.setSn(devDb.getSn());
            device.setSortIndex(devDb.getSortIndex());
            update(device);
            if(device instanceof DevHaveChild){
                ((DevHaveChild) device).setListDev(findChildDevice(device));
            }
            return;
        }
        addDevice(device);
//        ContentValues values = getContentValues(device);
//        mDatabase.insert(DbSb.TabDevice.NAME, null, values);
//        if(device instanceof DevHaveChild){
//            for(Device dev : ((DevHaveChild)device).getListDev()){
//                ContentValues values1 = getContentValues(dev);
//                mDatabase.insert(DbSb.TabDevice.NAME, null, values1);
//            }
//        }
//        if(device instanceof DevCollect){
//            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(mContext);
//            collectPropertyDao.add(((DevCollect)device).getCollectProperty());
//        }
    }

    private void addDevice(Device device){
        ContentValues values1 = getContentValues(device);
        mDatabase.insert(DbSb.TabDevice.NAME, null, values1);

        if(device instanceof DevHaveChild){
            for(Device dev : ((DevHaveChild)device).getListDev()){
                addDevice(dev);
            }
        }else if(device instanceof DevCollect){
            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(mContext);
            collectPropertyDao.add(((DevCollect)device).getCollectProperty());
        }
    }

    public void delete(Device device){
        if(device instanceof DevHaveChild){
            for(Device dev : ((DevHaveChild)device).getListDev()){
                delete(dev);
            }
        }else if(device instanceof DevCollect){
            CollectPropertyDao.get(mContext).delete(((DevCollect)device).getCollectProperty());
        }
        mDatabase.delete(DbSb.TabDevice.NAME, DbSb.TabDevice.Cols.ID + "=?", new String[]{device.getId()});
    }

    public void clean(){
        mDatabase.execSQL("delete from " + DbSb.TabDevice.NAME);
    }

    public void update(Device device) {
        ContentValues values = getContentValues(device);
        mDatabase.update(DbSb.TabDevice.NAME, values,
                "id = ?",
                new String[] { device.getId() });
    }
    public void updateId(String oldId, String newId) {
        ContentValues values = getUpdateIdContentValues(newId);
        mDatabase.update(DbSb.TabDevice.NAME, values,
                DbSb.TabDevice.Cols.ID + " = ?",
                new String[] { oldId });
    }

    public List<Device> find() {
//        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is ? and " + DbSb.TabDevice.Cols.DELETED + " = ?",new String[]{"null", "0"});
        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is null and deleted = 0", null);
        return createDevices(cursor);
    }

    public List<Device> findIncludeDeleted() {
//        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is ? and " + DbSb.TabDevice.Cols.DELETED + " = ?",new String[]{"null", "0"});
        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is null", null);
        return createDevices(cursor);
    }

    public List<Device> find(String whereClause, String[] whereArgs) {
        DeviceCursorWrapper cursor = query(whereClause, whereArgs);
        return createDevices(cursor);
    }

    private List<Device> findChildDevice(Device parent){
        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " = ? and deleted = 0", new String[]{parent.getId()});
        return createDevices(cursor);
    }

    private List<Device> findChildDeviceIncludeDeleted(Device parent){
        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " = ?", new String[]{parent.getId()});
        return createDevices(cursor);
    }

    private List<Device> createDevices(DeviceCursorWrapper cursor){
        List<Device> listDevice = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Device device = cursor.getDevice();
                initDevice(device);
                listDevice.add(device);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listDevice;
    }

    private void initDevice(Device device){
        if(device instanceof DevHaveChild){
            DevHaveChild devHaveChild = (DevHaveChild)device;
            List<Device> listChildDevice = findChildDevice(devHaveChild);
            devHaveChild.getListDev().clear();
            for(Device device1 : listChildDevice){
                initDevice(device1);
                initDevCollect(device1);
                devHaveChild.addChildDev(device1);
            }
        }
        initDevCollect(device);
    }

    private void initDevCollect(Device device){
        if(device instanceof DevCollect){
            DevCollect devCollect = (DevCollect)device;
            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(mContext);
            devCollect.setCollectProperty(collectPropertyDao.find(devCollect));
        }
    }

    private DeviceCursorWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabDevice.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new DeviceCursorWrapper(cursor);
    }

    private DeviceCursorWrapper query() {
        Cursor cursor = mDatabase.query(
                DbSb.TabDevice.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new DeviceCursorWrapper(cursor);
    }
}
