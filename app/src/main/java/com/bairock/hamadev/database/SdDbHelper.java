package com.bairock.hamadev.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;

import java.util.List;

import static com.bairock.hamadev.database.DbSb.*;

/**
 *
 * Created by Administrator on 2017/8/8.
 */

public class SdDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 3;
    private static final String DATABASE_NAME = "sd_db.db";

    public SdDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建user表
        db.execSQL("create table " + TabUser.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TabUser.Cols.EMAIL + ", " +
                TabUser.Cols.NAME + ", " +
                TabUser.Cols.PET_NAME + ", " +
                TabUser.Cols.PSD + ", " +
                TabUser.Cols.REGISTER_TIME + ", " +
                TabUser.Cols.TEL +
                ")"
        );
        //创建devGroup表
        db.execSQL("create table " + TabDevGroup.NAME + "(" +
                TabDevGroup.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevGroup.Cols.NAME + ", " +
                TabDevGroup.Cols.PET_NAME + ", " +
                TabDevGroup.Cols.PSD + ", " +
                TabDevGroup.Cols.USER_ID +
                ")"
        );
        //创建device表
        db.execSQL("create table " + TabDevice.NAME + "(" +
                TabDevice.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevice.Cols.DEVICE_TYPE + ", " +
                TabDevice.Cols.ALIAS + ", " +
                TabDevice.Cols.CTRL_MODEL + ", " +
                TabDevice.Cols.VISIBILITY + ", " +
                TabDevice.Cols.DELETED + ", " +
                TabDevice.Cols.DEV_CATEGORY + ", " +
                TabDevice.Cols.DEV_STATE_ID + ", " +
                TabDevice.Cols.GEAR + ", " +
                TabDevice.Cols.MAIN_CODE_ID + ", " +
                TabDevice.Cols.NAME + ", " +
                TabDevice.Cols.PLACE + ", " +
                TabDevice.Cols.SN + ", " +
                TabDevice.Cols.SORT_INDEX + ", " +
                TabDevice.Cols.SUB_CODE + ", " +
                TabDevice.Cols.PANID + ", " +
                TabDevice.Cols.DEV_GROUP_ID + ", " +
                TabDevice.Cols.PARENT_ID +
                ")"
        );
        //创建collect property表
        db.execSQL("create table " + TabCollectProperty.NAME + "(" +
                TabCollectProperty.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabCollectProperty.Cols.CREST_VALUE + ", " +
                TabCollectProperty.Cols.CREST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.CURRENT_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.PERCENT + ", " +
                TabCollectProperty.Cols.SIGNAL_SRC + ", " +
                TabCollectProperty.Cols.UNIT_SYMBOL + ", " +
                TabCollectProperty.Cols.CALIBRATION_VALUE + ", " +
                TabCollectProperty.Cols.FORMULA + ", " +
                TabCollectProperty.Cols.DEV_COLLECT_ID +
                ")"
        );
        //创建linkage holder表
        db.execSQL("create table " + TabLinkageHolder.NAME + "(" +
                TabLinkageHolder.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageHolder.Cols.DEVGROUP_ID + ", " +
                TabLinkageHolder.Cols.LINKAGE_TYPE + ", " +
                TabLinkageHolder.Cols.ENABLE +
                ")"
        );
        //创建linkage 子连锁数据表
        db.execSQL("create table " + TabLinkage.NAME + "(" +
                TabLinkage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkage.Cols.LINKAGE_TYPE + ", " +
                TabLinkage.Cols.DELETED + ", " +
                TabLinkage.Cols.ENABLE + ", " +
                TabLinkage.Cols.NAME + ", " +
                TabLinkage.Cols.TRIGGERED + ", " +
                TabLinkage.Cols.LOOP_COUNT + ", " +
                TabLinkage.Cols.LINKAGE_HOLDER_ID +
                ")"
        );
        //创建linkage condition 连锁条件数据表
        db.execSQL("create table " + TabLinkageCondition.NAME + "(" +
                TabLinkageCondition.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageCondition.Cols.COMPARE_SYMBOL + ", " +
                TabLinkageCondition.Cols.COMPARE_VALUE + ", " +
                TabLinkageCondition.Cols.DELETED + ", " +
                TabLinkageCondition.Cols.LOGIC + ", " +
                TabLinkageCondition.Cols.TRIGGER_STYLE + ", " +
                TabLinkageCondition.Cols.DEV_ID + ", " +
                TabLinkageCondition.Cols.SUBCHAIN_ID +
                ")"
        );
        //创建effect 连锁影响数据表
        db.execSQL("create table " + TabEffect.NAME + "(" +
                TabEffect.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabEffect.Cols.DELETED + ", " +
                TabEffect.Cols.DS_ID + ", " +
                TabEffect.Cols.EFFECT_CONTENT + ", " +
                TabEffect.Cols.EFFECT_COUNT + ", " +
                TabEffect.Cols.DEV_ID + ", " +
                TabEffect.Cols.LINKAGE_ID +
                ")"
        );
        //创建my time 时分秒数据表
        db.execSQL("create table " + TabMyTime.NAME + "(" +
                TabMyTime.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabMyTime.Cols.HOUR + ", " +
                TabMyTime.Cols.MINUTE + ", " +
                TabMyTime.Cols.TYPE + ", " +
                TabMyTime.Cols.TIMER_ID + ", " +
                TabMyTime.Cols.SECOND +
                ")"
        );
        //创建week helper 星期助手数据表
        db.execSQL("create table " + TabWeekHelper.NAME + "(" +
                TabWeekHelper.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabWeekHelper.Cols.ZTIMER_ID + ", " +
                TabWeekHelper.Cols.SUN + ", " +
                TabWeekHelper.Cols.MON + ", " +
                TabWeekHelper.Cols.TUES + ", " +
                TabWeekHelper.Cols.WED + ", " +
                TabWeekHelper.Cols.THUR + ", " +
                TabWeekHelper.Cols.FRI + ", " +
                TabWeekHelper.Cols.SAT +
                ")"
        );
        //创建ztimer 子定时数据表
        db.execSQL("create table " + TabZTimer.NAME + "(" +
                TabZTimer.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabZTimer.Cols.DELETED + ", " +
                TabZTimer.Cols.ENABLE + ", " +
                TabZTimer.Cols.TIMING_ID +
                ")"
        );
        //创建loop duration 循环区间，开区间，关区间数据表
        db.execSQL("create table " + TabLoopDuration.NAME + "(" +
                TabLoopDuration.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLoopDuration.Cols.DELETED + ", " +
                TabLoopDuration.Cols.ZLOOP_ID +
                ")"
        );

//        db.execSQL("PRAGMA foreign_keys=ON;");
//
//        db.execSQL("ALTER table " + TabCollectProperty.NAME +
//                " add constraint fk_dev_id foreign key (" +
//                TabCollectProperty.Cols.DEV_COLLECT_ID +
//                ") references " + TabDevice.NAME +
//                " (" + TabDevice.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabConditionHandler.NAME +
//                " add constraint fk_linkage_id foreign key (" +
//                TabConditionHandler.Cols.LINKAGE_ID +
//                ") references " + TabZLoop.NAME +
//                " (" + TabZLoop.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevGroup.NAME +
//                " add constraint fk_chainHolder_id foreign key (" +
//                TabDevGroup.Cols.CHAIN_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevGroup.NAME +
//                " add constraint fk_guaguaHolder_id foreign key (" +
//                TabDevGroup.Cols.GUAGUA_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevGroup.NAME +
//                " add constraint fk_loopHolder_id foreign key (" +
//                TabDevGroup.Cols.LOOP_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevGroup.NAME +
//                " add constraint fk_timingHolder_id foreign key (" +
//                TabDevGroup.Cols.TIMING_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevGroup.NAME +
//                " add constraint fk_user_id foreign key (" +
//                TabDevGroup.Cols.USER_ID +
//                ") references " + TabUser.NAME +
//                " (id)"
//        );
//        db.execSQL("ALTER table " + TabDevice.NAME +
//                " add constraint fk_devGroup_id foreign key (" +
//                TabDevice.Cols.DEV_GROUP_ID +
//                ") references " + TabDevGroup.NAME +
//                " (" + TabDevGroup.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabDevice.NAME +
//                " add constraint fk_devGroup_id foreign key (" +
//                TabDevice.Cols.PARENT_ID +
//                ") references " + TabDevice.NAME +
//                " (" + TabDevice.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabEffect.NAME +
//                " add constraint fk_dev_id foreign key (" +
//                TabEffect.Cols.DEV_ID +
//                ") references " + TabDevice.NAME +
//                " (" + TabDevice.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabEffect.NAME +
//                " add constraint fk_linkage_id foreign key (" +
//                TabEffect.Cols.LINKAGE_ID +
//                ") references " + TabZLoop.NAME +
//                " (" + TabZLoop.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabEffectGuagua.NAME +
//                " add constraint fk_linkage_id foreign key (" +
//                TabEffectGuagua.Cols.EFFECT_ID +
//                ") references " + TabEffect.NAME +
//                " (" + TabEffect.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLinkageCondition.NAME +
//                " add constraint fk_dev_id foreign key (" +
//                TabLinkageCondition.Cols.DEV_ID +
//                ") references " + TabDevice.NAME +
//                " (" + TabDevice.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLinkageCondition.NAME +
//                " add constraint fk_linkage_id foreign key (" +
//                TabLinkageCondition.Cols.LINKAGE_ID +
//                ") references " + TabConditionHandler.NAME +
//                " (" + TabConditionHandler.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLinkageDevValue.NAME +
//                " add constraint fk_linkage_holder_id foreign key (" +
//                TabLinkageDevValue.Cols.LINKAGE_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLoopDuration.NAME +
//                " add constraint fk_offKeepTime_id foreign key (" +
//                TabLoopDuration.Cols.OFF_KEEP_TIME_ID +
//                ") references " + TabMyTime.NAME +
//                " (" + TabMyTime.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLoopDuration.NAME +
//                " add constraint fk_onKeepTime_id foreign key (" +
//                TabLoopDuration.Cols.ON_KEEP_TIME_ID +
//                ") references " + TabMyTime.NAME +
//                " (" + TabMyTime.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabLoopDuration.NAME +
//                " add constraint fk_linkage_id foreign key (" +
//                TabLoopDuration.Cols.LINKAGE_ID +
//                ") references " + TabZLoop.NAME +
//                " (" + TabZLoop.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabTiming.NAME +
//                " add constraint fk_linkage_holder_id foreign key (" +
//                TabTiming.Cols.LINKAGE_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabZLoop.NAME +
//                " add constraint fk_linkage_holder_id foreign key (" +
//                TabZLoop.Cols.LINKAGE_HOLDER_ID +
//                ") references " + TabLinkageHolder.NAME +
//                " (" + TabLinkageHolder.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabZTimer.NAME +
//                " add constraint fk_offTime_id foreign key (" +
//                TabZTimer.Cols.OFF_TIME_ID +
//                ") references " + TabMyTime.NAME +
//                " (" + TabMyTime.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabZTimer.NAME +
//                " add constraint fk_onTime_id foreign key (" +
//                TabZTimer.Cols.ON_TIME_ID +
//                ") references " + TabMyTime.NAME +
//                " (" + TabMyTime.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabZTimer.NAME +
//                " add constraint fk_week_id foreign key (" +
//                TabZTimer.Cols.WEEK_ID +
//                ") references " + TabWeekHelper.NAME +
//                " (" + TabWeekHelper.Cols.EFFECT_ID + ") "
//        );
//        db.execSQL("ALTER table " + TabZTimer.NAME +
//                " add constraint fk_timing_id foreign key (" +
//                TabZTimer.Cols.TIMING_ID +
//                ") references " + TabTiming.NAME +
//                " (" + TabTiming.Cols.EFFECT_ID + ") "
//        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void cleanDb(){
        CollectPropertyDao.get(HamaApp.HAMA_CONTEXT).clean();
        DevGroupDao.get(HamaApp.HAMA_CONTEXT).clean();
        DeviceDao.get(HamaApp.HAMA_CONTEXT).clean();
        EffectDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageConditionDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).clean();
        LoopDurationDao.get(HamaApp.HAMA_CONTEXT).clean();
        MyTimeDao.get(HamaApp.HAMA_CONTEXT).clean();
        UserDao.get(HamaApp.HAMA_CONTEXT).clean();
        WeekHelperDao.get(HamaApp.HAMA_CONTEXT).clean();
        ZTimerDao.get(HamaApp.HAMA_CONTEXT).clean();
    }

    public static void replaceDbUser(User user){
        cleanDb();
        DevGroup devGroup = user.getListDevGroup().get(0);
        UserDao.get(HamaApp.HAMA_CONTEXT).addUser(user);
        DevGroupDao.get(HamaApp.HAMA_CONTEXT).add(devGroup);
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        for(Device device : devGroup.getListDevice()){
            deviceDao.add(device);
        }

        LinkageHolderDao linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT);
        for(LinkageHolder linkageHolder : devGroup.getListLinkageHolder()){
            linkageHolderDao.add(linkageHolder);
        }

        LinkageDao linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
        LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(HamaApp.HAMA_CONTEXT);
        EffectDao effectDao = EffectDao.get(HamaApp.HAMA_CONTEXT);

        for(LinkageHolder linkageHolder : devGroup.getListLinkageHolder()){
            for(Linkage linkage : linkageHolder.getListLinkage()){
                linkageDao.add(linkage, linkageHolder.getId());
                for(Effect effect : linkage.getListEffect()){
                    effectDao.add(effect, linkage.getId());
                }
                if(linkage instanceof SubChain){
                    SubChain subChain = (SubChain)linkage;
                    for(LinkageCondition linkageCondition : subChain.getListCondition()){
                        linkageConditionDao.add(linkageCondition, subChain.getId());
                    }
                    if(linkage instanceof ZLoop){
                        ZLoop loop = (ZLoop)linkage;
                        LoopDurationDao loopDurationDao = LoopDurationDao.get(HamaApp.HAMA_CONTEXT);
                        MyTimeDao myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT);
                        for(LoopDuration loopDuration : loop.getListLoopDuration()) {
                            loopDurationDao.add(loopDuration, loop.getId());
                            myTimeDao.add(loopDuration.getOnKeepTime(), loopDuration.getId());
                            myTimeDao.add(loopDuration.getOffKeepTime(), loopDuration.getId());
                        }
                    }
                }else if(linkage instanceof Timing){
                    Timing timing = (Timing)linkage;
                    ZTimerDao zTimerDao = ZTimerDao.get(HamaApp.HAMA_CONTEXT);
                    MyTimeDao myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT);
                    WeekHelperDao weekHelperDao = WeekHelperDao.get(HamaApp.HAMA_CONTEXT);
                    for(ZTimer zTimer : timing.getListZTimer()){
                        zTimerDao.add(zTimer, zTimer.getId());
                        myTimeDao.add(zTimer.getOnTime(), zTimer.getId());
                        myTimeDao.add(zTimer.getOffTime(), zTimer.getId());
                        weekHelperDao.add(zTimer.getWeekHelper());
                    }
                }
            }
        }
    }

    public static User getDbUser(){
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        User user =  userDao.getUser();
        if(null == user){
            return null;
        }

        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        DevGroup group = devGroupDao.find();
        if(null == group){
            return null;
        }
        user.addGroup(group);

        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        List<Device> listDevice = deviceDao.findIncludeDeleted();

        for (Device device : listDevice){
            group.addDevice(device);
        }

        LinkageConditionWrapper.devGroup = group;
        EffectWrapper.devGroup = group;
        //连锁
        LinkageHolderDao linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT);
        group.setListLinkageHolder(linkageHolderDao.findByDevGroupId(group.getId()));

        LinkageDao linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
        for(LinkageHolder linkageHolder : group.getListLinkageHolder()){
            List<Linkage> listLinkage = linkageDao.findChainByLinkageHolderId(linkageHolder.getId());
            linkageHolder.setListLinkage(listLinkage);
        }
        return user;
    }
}
