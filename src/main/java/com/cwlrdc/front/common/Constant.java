package com.cwlrdc.front.common;

public class Constant {

    public static class Model {
        //汇总文件
        public static final Integer RPT_FILE_RT = 0; //实时接口
        public static final Integer RPT_FILE_FTP = 1;
        //销量文件
        public static final Integer ZIP_FILE_RT = 0; //实时接口
        public static final Integer ZIP_FILE_FTP = 1;

        public static final Integer COLLECT_FILE_LOCAL = 0; //收集文件 本地模式
        public static final Integer COLLECT_FILE_FTP = 1;   //收集文件 FTP模式
    }

    public static class File {
        //文件上传状态
        public static final Integer FILE_UPLOAD_STATUS_FAILED_0 = 0; //文件未上传
        public static final Integer FILE_UPLOAD_STATUS_SUCCESS_1 = 1; //文件已上传
        public static final Integer FILE_DETAIL_STATUS_FAILED_2 = 2; //期号错误
        public static final Integer FILE_DETAIL_STATUS_FAILED_3 = 3; //省码错误
        public static final Integer FILE_DETAIL_STATUS_FAILED_4 = 4; //游戏编码错误
        public static final Integer FILE_DETAIL_STATUS_FAILED_5 = 5; //未知错误
        public static final Integer FILE_DETAIL_STATUS_FAILED_6 = 6; //奖级错误

        //ftp文件上传路径
        public static final String FILE_FTP_PATH = "ftp.file.path";
        public static final String FILE_LOCAL_PATH = "local.file.path";
        //系统导出文件存放目录
        public static final String SYSTEM_EXPORT_FILE_PATH = "sys.export.path";

        //ftp与实时接口开关
        public static final String FILE_TYPE_KEY = "cwlrdc.file.type";

        public static final Integer WIN_RETR_TYPE_EFFECT = 1; //有效票
        public static final Integer WIN_RETR_TYPE_CANCEL = 2; //注销票
        public static final Integer WIN_RETR_TYPE_INCOMP = 3; //不完整打印票

    }

    public static class Key {
        public static final String PROVINCEID_OF_CWL = "00"; //中彩中心省编码
        public static final int CASHEND_DEADLINE = 60;       //兑奖期长
    }

    public static class Status {
        //文件上传状态
        public static final Integer CHCEK_STATUS_RIGHT = 0;
        public static final Integer CHCEK_STATUS_WRONG = 1;

        public static final String FLOW_RUN_COMPLETE = "1";

        //数美接口处理状态
        public static final Integer TASK_RUN_COMPLETE_2 = 2;
        public static final Integer TASK_RUN_RUNNING_1 = 1;

        //开奖流程节点执行状态
        public static final Integer TASK_LTTOERY_FLOW_0 = 0; //未执行
        public static final Integer TASK_LTTOERY_FLOW_1 = 1; //执行完成
        public static final Integer TASK_LTTOERY_FLOW_2 = 2; //执行失败
        public static final Integer TASK_LTTOERY_FLOW_3 = 3; //执行中

        /* 0为未开奖期 */
        public static final Integer PERIOD_INFO_STATUS_0 = 0;
        /* 1为开奖期 */
        public static final Integer PERIOD_INFO_STATUS_1 = 1;
        /* 2为已开奖期 */
        public static final Integer PERIOD_INFO_STATUS_2 = 2;
        /* 3为发参数期 */
        public static final Integer PERIOD_INFO_STATUS_3 = 3;

        //非促销
        public static final String WIN_PROMOTION_STATUS_NO = "00";
        public static final String CWL_CODE_00 = "00";

        //资金
        public static final Integer FUNDS_TURN_UP = 1; //上缴
        public static final Integer FUNDS_TURN_DOWN = 0; //下划

    }

    public static class GameCode {
        //数美接口处理状态
        public static final String GAME_CODE_SLTO = "10001";
        public static final String GAME_CODE_LOTO = "10003";
        public static final String GAME_NAME_LOTO = "七乐彩";
        public static final String GAME_NAME_SLTO = "双色球";
    }


    public static class OPERATIORS {
        public static final String OPERATIORS = "operators";
    }

    public static class PromotionCode {
        public static final String PROMOTION_CODE = "35";
        public static final String PRIZE1_GAME_CODE = "10033";
        public static final String PRIZE6_GAME_CODE = "10034";
        public static final String NO_PROMOTION = "00";
        public static final String PRIZE1_PROMOTION = "10";
        public static final String PRIZE6_PROMOTION = "01";
        public static final String BOTH_PROMOTION = "11";
        public static final String PROMOTION_PERIOD_START = "2017129";
        public static final String PROMOTION_PERIOD_END = "2017148";
        public static final int PROMOTION_AWARD_ZERO = 0;
    }
    public static class Template {
        public static final String TEMPLATE_PATH_TKEY = "local.file.templatepath";
        public static final String TEMPLATE_NAME_TKEY = "local.file.templatename";
    }

}
