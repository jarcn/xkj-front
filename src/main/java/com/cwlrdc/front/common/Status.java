package com.cwlrdc.front.common;

/**
 * Created by yangqiju on 2017/8/31.
 */
public class Status {

    public static class Period{
        /* 0为未开奖期 */
        public static final Integer NOT_STARTED = 0;
        /* 1为开奖期 */
        public static final Integer CURRENT = 1;
        /* 2为已开奖期 */
        public static final Integer PASSED = 2;
        /* 3为发参数期 */
        public static final Integer PARAM = 3;
    }

    public static class UploadStatus{
        public static final Integer NOT_UPLOADED = 0; //文件未上传
        public static final Integer UPLOADED_SUCCESS = 1; //文件已上传
        public static final Integer PERIOD_ERROR = 2; //期号错误
        public static final Integer PROVINCE_ERROR = 3; //省码错误
        public static final Integer GAMECODE_ERROR = 4; //游戏编码错误
        public static final Integer UNKOWN_ERROR = 5; //未知错误
        public static final Integer PRIZELEVEL_ERROR = 6; //奖级错误
        public static final Integer DOWNLOADING = 7;
    }


    public static class OvduDataStatus{
        public static final Integer NOT_UPLOADED = 0; //文件未上传
        public static final Integer UPLOADED_SUCCESS = 1; //文件已上传
        public static final Integer PERIOD_ERROR = 2; //期号错误
        public static final Integer PROVINCE_ERROR = 3; //省码错误
        public static final Integer GAMECODE_ERROR = 4; //游戏编码错误
        public static final Integer UNKOWN_ERROR = 5; //未知错误
        public static final Integer PRIZELEVEL_ERROR = 6; //奖级错误
        public static final Integer DOWNLOADING = 7;
        public static final Integer DETAIL_DIFF_SUM = 8; //明细与汇总金额不匹配
        public static final Integer NOFENQIOVDU = 9; //无分期弃奖
        public static final Integer  GRADENUM_ERROR= 10; //奖级个数错误
        public static final Integer  OVDUMONEY_ERROR= 11; //弃奖总金额为空
        public static final Integer  GRADENUMDTETIAL_DIFF_SUM= 12; //明细弃奖奖等于汇总不一致
    }


    public static class FlowNode{
        public static final String CLOSE = "0";
        public static final String OPEN = "1";
    }

    public static class TicketType{
        public static final Integer EFFECT = 1; //有效票
        public static final Integer CANCEL = 2; //注销票
        public static final Integer DAMAGE = 3; //不完整打印票
    }
}
