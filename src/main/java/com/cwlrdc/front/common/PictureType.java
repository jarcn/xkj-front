package com.cwlrdc.front.common;

import lombok.Getter;

/**
 * Created by 马凤阁 on 2017/6/1.
 */
public enum  PictureType {
    //省上报图片类型
    SALES_CANCLE_FAX("sale", "销售/弃奖传真",1),
    //省上报图片类型
    WIN_RESULt_FAX("win", "中奖结果传真",2),
    //省上报图片类型
    DRAW_NOTICE_FAX("notice", "开奖公告传真",3),
    //省上报图片类型
    DRAW_NUM_FAX("num", "开奖号码传真",4);

    private @Getter String picName;
    private @Getter int picNum;
    private @Getter String typeName;


    PictureType(String typeName, String picName, int picNum){
        this.picName = picName;
        this.picNum = picNum;
        this.typeName = typeName;
    }

    public static String getPicName(int picNum){
        for (PictureType p : PictureType.values()){
            if (p.picNum == picNum){
                return p.picName;
            }
        }
        return null;
    }

    public static String getTypeName(int picNum){
        for (PictureType p : PictureType.values()){
            if (p.picNum == picNum){
                return p.typeName;
            }
        }
        return null;
    }
}
