package com.cwlrdc.front.common;

import lombok.Getter;

/**
 * Created by 馬鳳閣 on 2017/5/9.
 */
public enum FlowType {
    //点击数据汇总
    SALES_DATA_SUM("销售数据汇总", 1),
    //点击数据汇总
    CANCEL_DATA_SUM("弃奖数据汇总", 2),
    //页面初始化
    DETAIL_FILE_UPLOAD("明细文件报送情况", 3),
    //点击人工核对
    LOOK_CANCEL_FAX("查看销售弃奖传真核对", 4),
    //点击销量稽核按钮
    ALL_SALES_CHECK("销售明细数据核对", 5),
    //点击调用刻录
    SALES_RECORD_CD("销售明细数据刻录光盘", 6),
    //中彩，摇奖现场 点击保存按钮
    INPUT_WIN_NUM("录入中奖号码", 7),
    //中彩点下发全国
    DEPLOY_WIN_NUM("发布中奖号码", 16),
    //中彩，摇奖现场 发布中奖号码传真
    ISSUE_WINNUM_FAX("发布中奖号码传真", 8),
    //中彩"查看中奖结果传真"点击"人工核对完成"
    LOOK_WINRESULT_FAX_CHECK("中奖结果传真核对", 9),
    //"中奖结果核对" 点击不完整打印票
    WIN_CHECK("中奖稽核结果核对", 10),
    //点击 生成开奖公告
    CREATE_DRAW_NOTICE("生成全国开奖公告", 11),
    // 发布全国开奖公告 点击发布
    ISSUE_DRAW_FAX("发布全国开奖公告传真", 12),
    //点击 "开奖数据下发"
    ISSUE_PARA("发布参数", 13),
    //报表打印任一打印按钮
    REPORT_PRINT("报表打印", 14),
    //"查看销售/弃奖传真" 点击发布摇奖通知单
    SALES_CANCEL_CHECK("发布摇奖通知单", 15),
    //导出开奖公告excel
    EXPORT_LOTTERY_REPORT("导出开奖公告", 17);


    private @Getter
    String typeName;
    private @Getter
    int typeNum;

    FlowType(String typeName, int typeNum) {
        this.typeName = typeName;
        this.typeNum = typeNum;
    }

    public static String getTypeName(int typeNum) {
        for (FlowType f : FlowType.values()) {
            if (f.getTypeNum() == typeNum) {
                return f.getTypeName();
            }
        }
        return null;
    }
}
