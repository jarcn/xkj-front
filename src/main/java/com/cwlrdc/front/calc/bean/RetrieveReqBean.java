package com.cwlrdc.front.calc.bean;

import lombok.Data;

/**
 * 中奖检索输入参数
 * Created by chenjia on 2017/4/12.
 */
@Data
public class RetrieveReqBean {

    private String provinceId;
    private String periodNum;
    private String gameCode;
    private String winNum;
    private String filePath;
    /*
       “00”：代表复式一等奖和复式六等奖都不计算；
       “10”：代表复式一等奖计算和复式六等奖不计算；
       “01”：代表复式一等奖不计算和复式六等奖计算；
       “11”：代表复式一等奖和复式六等奖都要计算。
     */
    private String promotionCode;
}
