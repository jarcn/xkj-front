package com.cwlrdc.front.common;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.calc.bean.PrintAnnceExcelParaBean;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import java.math.BigDecimal;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 判断新期是否为继续促销，根据2017年双色球派奖规则
 * 一等奖促销，六等奖促销状态需要分别进行判断
 * 一等奖派奖前提条件：
 * 条件一：上期一等奖促销奖池大于0。
 * 条件二：上期一等奖奖池等于0，新期参数中促销状态为促销。
 * 条件一，条件二只要有一个成立则继续促销。
 * 说明：因为双色球一等奖促销期每期都会注入2千万，所以促销期范围内，派奖奖池不会出现小于0的情况。
 * 六等奖派奖前提条件:
 * 条件一:上期派奖奖池余额大于0。
 * 条件一成立则继续派奖。
 * 说明：六等奖派奖奖池在促销开始时一次性注入5亿，直到奖池被掏空派奖截止，跟新期参数中促销状态无关。
 */
@Component
public class PromotionManager {

  @Resource
  LttoLotteryAnnouncementService announcementService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private LttoLotteryAnnouncementService lotteryAnnouncementService;
  @Resource
  private LttoWinstatDataService winstatDataService;
  @Resource
  private PromotionManager promotionManager;
  @Resource
  private ParaGamePeriodInfoService periodInfoService;

  private static final long POOL_TOTAL_ZORE = 0L;

  /**
   * 判断当前期是否促销
   */
  public boolean isPromotionCurr(String promotionCode, String currPeriodNum) {
    LttoLotteryAnnouncement announcement = announcementService
        .selectByKey(promotionCode, currPeriodNum);
    if (announcement == null) {
      return false;
    }
    BigDecimal poolBgn = announcement.getPoolBgn();
    if (poolBgn == null) {
      return false;
    }
    if (poolBgn.longValue() > POOL_TOTAL_ZORE) {
      return true;
    }
    if (poolBgn.longValue() == POOL_TOTAL_ZORE) {
      if (Constant.PromotionCode.PRIZE6_GAME_CODE.equals(promotionCode)) {
        return false;
      } else {
        ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(Constant.GameCode.GAME_CODE_SLTO, currPeriodNum);
        String promotionStatus = periodInfo.getPromotionStatus();
        Integer periodNum = Integer.valueOf(currPeriodNum);
        Integer promotionPeriodSta = Integer.valueOf(Constant.PromotionCode.PROMOTION_PERIOD_START);
        Integer promotionPeriodEnd = Integer.valueOf(Constant.PromotionCode.PROMOTION_PERIOD_END);
        if (!Constant.Status.WIN_PROMOTION_STATUS_NO.equals(promotionStatus)) {
          if (periodNum >= promotionPeriodSta && periodNum < promotionPeriodEnd) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * 判断下期是否促销
   */
  public boolean isPromotionNext(String promotionCode, String currPeriodNum) {
    LttoLotteryAnnouncement announcement = announcementService.selectByKey(promotionCode, currPeriodNum);
    if (announcement == null) {
      return false;
    }
    BigDecimal poolTotal = announcement.getPoolTotal();

    if (null == poolTotal) {
      return false;
    }
    if (poolTotal.longValue() > POOL_TOTAL_ZORE) {
      return true;
    }
    if (poolTotal.longValue() == POOL_TOTAL_ZORE) {
      if (Constant.PromotionCode.PRIZE6_GAME_CODE.equals(promotionCode)) {
        return false;
      } else {
        ParaGamePeriodInfo nextPeriodInfo = periodInfoService.nextPeriodInfo(Constant.GameCode.GAME_CODE_SLTO, currPeriodNum);
        String nextPeriodNum = nextPeriodInfo.getPeriodNum();
        String promotionStatus = nextPeriodInfo.getPromotionStatus();
        Integer periodNum = Integer.valueOf(nextPeriodNum);
        Integer promotionPeriodSta = Integer.valueOf(Constant.PromotionCode.PROMOTION_PERIOD_START);
        Integer promotionPeriodEnd = Integer.valueOf(Constant.PromotionCode.PROMOTION_PERIOD_END);
        if (!Constant.Status.WIN_PROMOTION_STATUS_NO.equals(promotionStatus)) {
          if (periodNum >= promotionPeriodSta && periodNum < promotionPeriodEnd) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * 创建促销开奖公告打印数据
   */
  public PrintAnnceExcelParaBean createPrintPara(ParaGamePeriodInfo periodInfo) {
    PrintAnnceExcelParaBean paraBean = new PrintAnnceExcelParaBean();
    String gameCode = periodInfo.getGameCode();
    String periodNum = periodInfo.getPeriodNum();
    paraBean.setPeriodNum(periodNum);
    paraBean.setGameName(gameInfoCache.getGameName(gameCode));
    paraBean.setWinNum(periodInfo.getWinNum());
    paraBean.setAllPrize1Detail(this.getAllPrize1Count(gameCode, periodNum));
    paraBean.setFirstPromotion(
        promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum));
    paraBean.setSixPromotion(
        promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum));
    LttoLotteryAnnouncement announcement = lotteryAnnouncementService
        .selectByKey(gameCode, periodNum);
    LttoLotteryAnnouncement announcement1 = lotteryAnnouncementService
        .selectByKey(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum);
    LttoLotteryAnnouncement announcement6 = lotteryAnnouncementService
        .selectByKey(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
    if (null != announcement1) {
      announcement.setPrize7Money(announcement1.getPrize7Money());
    }
    if (null != announcement6) {
      announcement.setPrize8Money(announcement6.getPrize8Money());
    }
    paraBean.setAnnouncement(announcement);
    //仅适用于2017年双色球派奖规则
    int promotions = Integer.parseInt(Constant.PromotionCode.PROMOTION_PERIOD_START);
    int currperiod = Integer.parseInt(periodInfo.getPeriodNum());
    if (promotions - currperiod == 1) {
      paraBean.setCxPrize1PoolMoney("20000000");
      paraBean.setCxPrize6PoolMoney("500000000");
    } else {
      paraBean.setCxPrize1PoolMoney(
          this.promotionPoolTotal(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum));
      paraBean.setCxPrize6PoolMoney(
          this.promotionPoolTotal(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum));
    }
    return paraBean;
  }

  public String getAllPrize1Count(String gameCode, String periodNum) {
    return winstatDataService.getAllPrize1Count(gameCode, periodNum);
  }

  //等奖促销奖池余额
  private String promotionPoolTotal(String gameCode, String periodNum) {
    LttoLotteryAnnouncement prizePool = lotteryAnnouncementService.selectByKey(gameCode, periodNum);
    if (null == prizePool) {
      return "";
    } else {
      return String.valueOf(prizePool.getPoolTotal().longValue());
    }
  }

  public static void main(String[] args) {
       BigDecimal b = new BigDecimal(0);
       System.out.println(b.longValue());
  }

}
