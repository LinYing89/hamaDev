package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevCategory;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.DeviceAssistent;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;

/**
 *
 * Created by 44489 on 2017/9/1.
 */

public class CollectPropertyCursorWrapper extends CursorWrapper {

    public CollectPropertyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public CollectProperty getCollectProperty() {
        String id = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.ID));
        Float crestValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CREST_VALUE));
        Float crestReferValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CREST_REFER_VALUE));
        Float currentValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CURRENT_VALUE));
        Float leastValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.LEAST_VALUE));
        Float leastReferValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.LEAST_REFER_VALUE));
        Float percent = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.PERCENT));
        String formula = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.FORMULA));
        Float calibration = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CALIBRATION_VALUE));

        CollectSignalSource collectSignalSource = CollectSignalSource.DIGIT;
        int iSignalSrc = getColumnIndex(DbSb.TabCollectProperty.Cols.SIGNAL_SRC);
        if(iSignalSrc != -1){
            String signalSrc = getString(iSignalSrc);
            if(null != signalSrc){
                try {
                    collectSignalSource = Enum.valueOf(CollectSignalSource.class, signalSrc);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        String unitSymbol = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.UNIT_SYMBOL));
        //String devCollectId = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID));

        CollectProperty collectProperty = new CollectProperty();
        collectProperty.setId(id);
        collectProperty.setCrestValue(crestValue);
        collectProperty.setCrestReferValue(crestReferValue);
        collectProperty.setCurrentValue(currentValue);
        collectProperty.setLeastValue(leastValue);
        collectProperty.setLeastReferValue(leastReferValue);
        collectProperty.setPercent(percent);
        collectProperty.setCollectSrc(collectSignalSource);
        collectProperty.setUnitSymbol(unitSymbol);
        collectProperty.setFormula(formula);
        collectProperty.setCalibrationValue(calibration);
        return collectProperty;
    }
}
