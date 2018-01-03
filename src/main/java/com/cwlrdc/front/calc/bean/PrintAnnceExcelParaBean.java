package com.cwlrdc.front.calc.bean;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 打印开奖公告参数
 * @author chenjia
 */

@Data
public class PrintAnnceExcelParaBean {
    private String winNum;
    private String periodNum;
    private String gameName;
    private boolean firstPromotion;
    private boolean sixPromotion;
    private String allPrize1Detail;
    private LttoLotteryAnnouncement announcement;
    private String cxPrize1PoolMoney;
    private String cxPrize6PoolMoney;
    private String printDate = DateFormatUtils.format(System.currentTimeMillis(),"yyyy年MM月dd日");
}
