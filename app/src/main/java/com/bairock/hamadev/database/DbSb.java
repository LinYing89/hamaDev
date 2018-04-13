package com.bairock.hamadev.database;

/**
 * 数据库
 * Created by Administrator on 2017/8/8.
 */

public class DbSb {

    /**
     * user 数据表
     */
    public static final class TabUser {
        /**
         * 数据表表名
         */
        public static final String NAME = "user";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 邮箱
             */
            public static final String ID = "_id";
            /**
             * 邮箱
             */
            public static final String EMAIL = "email";
            /**
             * 用户名
             */
            public static final String NAME = "name";
            /**
             * 用户昵称
             */
            public static final String PET_NAME = "petName";
            /**
             * 用户密码
             */
            public static final String PSD = "psd";
            /**
             * 注册时间
             */
            public static final String REGISTER_TIME = "registerTime";
            /**
             * 电话
             */
            public static final String TEL = "tel";
        }
    }

    /**
     * devGroup 数据表
     */
    public static final class TabDevGroup {
        /**
         * 数据表表名
         */
        public static final String NAME = "devgroup";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，UUID
             */
            public static final String ID = "id";
            /**
             * 组名
             */
            public static final String NAME = "name";
            /**
             * 组昵称
             */
            public static final String PET_NAME = "petName";
            /**
             * 组密码
             */
            public static final String PSD = "psd";
            /**
             * 用户表外键
             */
            public static final String USER_ID = "user_id";
        }
    }

    /**
     * device 数据表
     */
    public static final class TabDevice {
        /**
         * 数据表表名
         */
        public static final String NAME = "device";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，UUID
             */
            public static final String ID = "id";
            /**
             * 设备类型，开关、液位计
             */
            public static final String DEVICE_TYPE = "device_type";
            /**
             * 别名
             */
            public static final String ALIAS = "alias";
            /**
             * 控制模式，本地、远程
             */
            public static final String CTRL_MODEL = "ctrlModel";
            /**
             * 是否可见
             */
            public static final String VISIBILITY = "visibility";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 设备分类
             */
            public static final String DEV_CATEGORY =  "devCategory";
            /**
             * 设备状态id
             */
            public static final String DEV_STATE_ID = "devStateId";
            /**
             * 设备档位
             */
            public static final String GEAR = "gear";
            /**
             * 主编吗id
             */
            public static final String MAIN_CODE_ID = "mainCodeId";
            /**
             * 设备名称
             */
            public static final String NAME = "name";
            /**
             * 设备位置
             */
            public static final String PLACE = "place";
            /**
             * 设备序列号
             */
            public static final String SN = "sn";
            /**
             * 排序索引
             */
            public static final String SORT_INDEX = "sortIndex";
            /**
             * 设备子编码
             */
            public static final String SUB_CODE = "subCode";
            /**
             * 协调器panid
             */
            public static final String PANID=  "panid";
            /**
             * 设备组外键
             */
            public static final String DEV_GROUP_ID = "devGroup_id";
            /**
             * 父设备外键
             */
            public static final String PARENT_ID = "parent_id";
        }
    }

    /**
     * collect property 采集设备属性数据表
     */
    public static final class TabCollectProperty {
        /**
         * 数据表表名
         */
        public static final String NAME = "collectproperty";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 最大值采集值
             */
            public static final String CREST_VALUE = "crestValue";
            /**
             * 最大值采集值对应的最大使用值
             */
            public static final String CREST_REFER_VALUE = "crestReferValue";
            /**
             * 当前值
             */
            public static final String CURRENT_VALUE = "currentValue";
            /**
             * 最小值采集值
             */
            public static final String LEAST_VALUE = "leastValue";
            /**
             * 最小值采集值对应的最小使用值
             */
            public static final String LEAST_REFER_VALUE = "leastReferValue";
            /**
             * 百分比
             */
            public static final String PERCENT = "percent";
            /**
             * 信号源
             */
            public static final String SIGNAL_SRC = "signalSrc";
            /**
             * 单位符号
             */
            public static final String UNIT_SYMBOL = "unitSymbol";
            /**
             * 标定值
             */
            public static final String CALIBRATION_VALUE = "calibrationValue";
            /**
             * 公式
             */
            public static final String FORMULA = "formula";
            /**
             * 采集设备外键
             */
            public static final String DEV_COLLECT_ID = "devCollect_id";
        }
    }

    /**
     * linkage holder 根连锁数据表
     */
    public static final class TabLinkageHolder {
        /**
         * 数据表表名
         */
        public static final String NAME = "linkageholder";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 连锁类型，连锁、循环、定时、呱呱
             */
            public static final String LINKAGE_TYPE = "linkage_type";
            /**
             * 是否使能
             */
            public static final String ENABLE = "enable";
            /**
             * 组id
             */
            public static final String DEVGROUP_ID = "devGroup_id";
        }
    }

    /**
     * linkage device value 普通设备值连锁数据表
     */
    public static final class TabLinkage {
        /**
         * 数据表表名
         */
        public static final String NAME = "linkage";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 子连锁类型，连锁、定时、循环
             */
            public static final String LINKAGE_TYPE = "linkage_type";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 是否使能
             */
            public static final String ENABLE = "enable";
            /**
             * 连锁名称
             */
            public static final String NAME = "name";
            /**
             * 是否触发了
             */
            public static final String TRIGGERED = "triggered";
            /**
             * 循环次数，循环可用
             */
            public static final String LOOP_COUNT = "loopCount";
            /**
             * 根连锁id，关联linkageHolder表的id
             */
            public static final String LINKAGE_HOLDER_ID = "linkageHolder_id";
        }
    }

    /**
     * linkage condition 连锁条件数据表
     */
    public static final class TabLinkageCondition {
        /**
         * 数据表表名
         */
        public static final String NAME = "linkagecondition";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 比较符号，and、or
             */
            public static final String COMPARE_SYMBOL = "compareSymbol";
            /**
             * 比较值
             */
            public static final String COMPARE_VALUE = "compareValue";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 比较逻辑
             */
            public static final String LOGIC = "logic";
            /**
             * 触发值类型
             */
            public static final String TRIGGER_STYLE = "triggerStyle";
            /**
             * 设备id，设备表外键
             */
            public static final String DEV_ID = "dev_id";
            /**
             * 连锁id，子连锁表外键
             */
            public static final String SUBCHAIN_ID = "subChain_id";
        }
    }

    /**
     * effect 连锁影响数据表
     */
    public static final class TabEffect {
        /**
         * 数据表表名
         */
        public static final String NAME = "effect";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 影响设备到某状态的设备状态id
             */
            public static final String DS_ID = "dsId";
            /**
             * 影响内容
             */
            public static final String EFFECT_CONTENT = "effectContent";
            /**
             * 影响次数
             */
            public static final String EFFECT_COUNT = "effectCount";
            /**
             * 设备id，设备表外键
             */
            public static final String DEV_ID = "dev_id";
            /**
             * 连锁id，子连锁表外键
             */
            public static final String LINKAGE_ID = "linkage_id";
        }
    }

    /**
     * my time 时分秒数据表
     */
    public static final class TabMyTime {
        /**
         * 数据表表名
         */
        public static final String NAME = "mytime";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 时
             */
            public static final String HOUR = "hour";
            /**
             * 分
             */
            public static final String MINUTE = "minute";
            /**
             * 秒
             */
            public static final String SECOND = "second";
            /**
             * 类型0为关时间，1为开时间
             */
            public static final String TYPE = "type";
            /**
             * 持有者id
             */
            public static final String TIMER_ID = "timerId";
        }
    }

    /**
     * week helper 星期助手数据表
     */
    public static final class TabWeekHelper {
        /**
         * 数据表表名
         */
        public static final String NAME = "weekhelper";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 周日
             */
            public static final String SUN = "sun";
            /**
             * 周一
             */
            public static final String MON = "mon";
            /**
             * 周二
             */
            public static final String TUES = "tues";
            /**
             * 周三
             */
            public static final String WED = "wed";
            /**
             * 周四
             */
            public static final String THUR = "thur";
            /**
             * 周五
             */
            public static final String FRI = "fri";
            /**
             * 周六
             */
            public static final String SAT = "sat";
            /**
             * 持有者id
             */
            public static final String ZTIMER_ID = "zTimer_id";
        }
    }

    /**
     * ztimer 子定时条件数据表
     */
    public static final class TabZTimer {
        /**
         * 数据表表名
         */
        public static final String NAME = "ztimer";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 是否使能
             */
            public static final String ENABLE = "enable";
            /**
             * 定时id，定时表外键
             */
            public static final String TIMING_ID = "timing_id";
        }
    }

    /**
     * loop duration 循环区间，开区间，关区间数据表
     */
    public static final class TabLoopDuration {
        /**
         * 数据表表名
         */
        public static final String NAME = "loopduration";

        /**
         * 数据表列
         */
        public static final class Cols {
            /**
             * 主键，uuid
             */
            public static final String ID = "id";
            /**
             * 是否已删除
             */
            public static final String DELETED = "deleted";
            /**
             * 连锁id，连锁（循环）表外键
             */
            public static final String ZLOOP_ID = "zLoop_id";
        }
    }
}
