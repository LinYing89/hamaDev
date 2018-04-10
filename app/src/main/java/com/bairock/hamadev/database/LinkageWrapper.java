package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.Timing;

/**
 *
 * Created by 44489 on 2017/9/25.
 */

public class LinkageWrapper extends CursorWrapper {
    public LinkageWrapper(Cursor cursor) {
        super(cursor);
    }

    public Linkage getLinkage() {
        String linkageType = getString(getColumnIndex(DbSb.TabLinkage.Cols.LINKAGE_TYPE));
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabLinkageDevValue.Cols.LINKAGE_HOLDER_ID));
        String id = getString(getColumnIndex(DbSb.TabLinkage.Cols.ID));
        String name = getString(getColumnIndex(DbSb.TabLinkage.Cols.NAME));
        boolean enable = getString(getColumnIndex(DbSb.TabLinkage.Cols.ENABLE)).equals("1");

        String strLoopCount = getString(getColumnIndex(DbSb.TabLinkage.Cols.LOOP_COUNT));
        int iLoopCount = 0;
        try {
            iLoopCount = Integer.parseInt(strLoopCount);
        }catch (Exception e){
            e.printStackTrace();
        }
        Linkage linkage = null;
        switch (linkageType){
            case "SubChain":
                linkage = new SubChain();
                break;
            case "Timing":
                linkage = new Timing();
                break;
            case "ZLoop":
                linkage = new ZLoop();
                ((ZLoop)linkage).setLoopCount(iLoopCount);
                break;
        }

        linkage.setId(id);
        linkage.setName(name);
        linkage.setEnable(enable);
        return linkage;
    }
}
