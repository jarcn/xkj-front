package com.cwlrdc.front.calc.bean;

import lombok.Data;

/**
 * 全国开奖公告生成输入参数
 * Created by chenjia on 2017/4/12.
 */
@Data
public class LotteryAnnoceReqBean {
    private String periodNum;
    private String gameCode;
    private long prize1Count;       //全国1等奖中奖总注数
    private long prize2Count;       //全国2等奖中奖总注数
    private long prize3Count;       //全国3等奖中奖总注数
    private long prize4Count;       //全国4等奖中奖总注数
    private long prize5Count;       //全国5等奖中奖总注数
    private long prize6Count;       //全国6等奖中奖总注数
    private long prize7Count;       //全国7等奖中奖总注数
    private long prize8Count;       //全国8等奖中奖总注数
    private long prize9Count;       //全国9等奖中奖总注数
    private long prize10Count;      //全国10等奖中奖总注数
    private Double amountDetail;      //全国销售总额
    private Double allCanceledMoney;  //全国当期弃奖总额
    private Double allocationAddStartbalance; //加项 调节基金 期初余额
    private Double jaclpotAddStartbalance; //加项 奖池 期初余额
    private Double allocationSubPayspecialwin; //减项 调节基金 支付特别奖
    private Double allocationSubRollout; //减项 调节基金 转出弥补不足
    private Double allocationSubIntojackpot; //减项 调节基金 转入奖池
    private Double jaclpotAddChange; //加项 奖池 调整加项
    private Double jaclpotSubChan; //减项 奖池 调整减项
}
