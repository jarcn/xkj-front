package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.opet.entity.OpetReportParameter;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterKey;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocation;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationWeek;
import com.cwlrdc.commondb.stat.entity.StatFundDeduct;
import com.cwlrdc.commondb.stat.entity.StatFundsAllocation;
import com.cwlrdc.commondb.stat.entity.StatWinAllocation;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.GameCode;
import com.cwlrdc.front.common.Constant.PromotionCode;
import com.cwlrdc.front.common.PromotionManager;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.opet.service.OpetReportParameterService;
import com.cwlrdc.front.stat.service.StatBonusAllocationService;
import com.cwlrdc.front.stat.service.StatBonusAllocationWeekService;
import com.cwlrdc.front.stat.service.StatFundDeductService;
import com.cwlrdc.front.stat.service.StatFundsAllocationService;
import com.cwlrdc.front.stat.service.StatWinAllocationService;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * 根据省销售汇总数据计算报表显示数据
 * Created by chenjia on 2017/5/26.
 */
@Slf4j
@Component
public class CalculationReportUtil {

  @Resource
  private LttoProvinceSalesDataService provinceSalesDataService;
  @Resource
  private OpetReportParameterService reportParameterService;
  @Resource
  private StatBonusAllocationService bonusAllocationService;
  @Resource
  private StatFundsAllocationService fundsAllocationService;
  @Resource
  private LttoLotteryAnnouncementService announcementService;
  @Resource
  private LttoWinstatDataService winstatDataService;
  @Resource
  private StatFundDeductService fundDeductService;
  @Resource
  private StatWinAllocationService winAllocationService;
  @Resource
  private PromotionManager promotionManager;


  public Double adjustFund = 0.00;//调节基金比
  private Double moneyReturn = 0.00;//返奖奖金(百分比)
  private Double bonusSubTotal = 0.00;//奖金小计比例
  private Double lottery = 0.00;//中彩 发行费比例
  private Double province = 0.00;// 省中心发行比例Ò
  private Double bettShop = 0.00;//投注站发行费比例
  private Double offer = 0.00;//发行费小计比例
  private Double saleExt = 0.00;//销售提取比例（公益金提取比例）


  //奖金调配表
  public void calBonusAllocation(String gameCode, String periodNum) {
    List<StatBonusAllocation> resultList = new ArrayList<>();
    //各省总销售量
    List<LttoProvinceSalesData> salesDatas = provinceSalesDataService.getProvinceSaleData(gameCode, periodNum);
    //各省中奖数据
    List<LttoWinstatData> winstatDatas = winstatDataService.select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
    HashMap<String, Double> winMoneyMap = claNoPromotionWinMoney(winstatDatas, gameCode, periodNum); //各省中奖总金额
    HashMap<String, Double> deployMoneyMap = this.calDeployMoney(salesDatas, winMoneyMap); //调配奖金
    if (!CommonUtils.isEmpty(salesDatas)) {
      for (LttoProvinceSalesData data : salesDatas) {
        StatBonusAllocation bean = new StatBonusAllocation();
        bean.setPeriodNum(periodNum);
        bean.setGameCode(gameCode);
        bean.setProvinceId(data.getProvinceId());
        BigDecimal totalSaleMoney = data.getAmount();//销售总额
        bean.setSaleMoney(totalSaleMoney); //销售总额
        Double rewardMoney = totalSaleMoney.doubleValue() * moneyReturn;
        bean.setRewardBonusMoney(new BigDecimal(rewardMoney)); //返奖奖金
        Double regFundMoney = totalSaleMoney.doubleValue() * adjustFund;
        bean.setRegulationFundMoney(new BigDecimal(regFundMoney)); //省调节基金总额
        Double totalMoney = totalSaleMoney.doubleValue() * bonusSubTotal;
        bean.setTotalMoney(new BigDecimal(totalMoney)); //省返奖和调节基金总额
        bean.setWinBonusMoney(new BigDecimal(winMoneyMap.get(data.getProvinceId()))); //省中奖奖金总额
        //① 调配奖金 = 返奖奖金 - 中奖奖金
        bean.setAllocationBonusMoney(
            new BigDecimal(deployMoneyMap.get(data.getProvinceId()))); //省调配奖金总额
        //② 实际上缴 = 调配奖金 + 调节基金
        bean.setRealityPayCash(
            new BigDecimal(deployMoneyMap.get(data.getProvinceId()) + regFundMoney)); //省奖金实际上缴总额
        resultList.add(bean);
      }
    } else {
      log.warn("期号:[{}],游戏:[{}] 销售数据未传", periodNum, gameCode);
      return;
    }
    log.debug("期号:[{}],游戏:[{}]资金分配表数据入库", periodNum, gameCode);
    try {
      bonusAllocationService.batchInsert(resultList);
    } catch (DuplicateKeyException e) {
      log.warn("数据库中存在统计数据", e);
      bonusAllocationService.batchUpdate(resultList);
    }
    log.debug("资金分配表数据入库完成");

  }

  @Resource
  private StatBonusAllocationWeekService bonusAllocationWeekService;

  //按周统计奖金调配统计表
  public void calStatBonusAllocation(String gameCode, String periodNum) {
    List<StatBonusAllocationWeek> resultList = new ArrayList<>();
    //各省总销售量
    List<LttoProvinceSalesData> salesDatas = provinceSalesDataService
        .getProvinceSaleData(gameCode, periodNum);
    //各省中奖数据
    List<LttoWinstatData> winstatDatas = winstatDataService
        .select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);

    HashMap<String, Double> winMoneyMap = claWinMoney(winstatDatas, gameCode, periodNum); //各省中奖总金额
    HashMap<String, Double> deployMoneyMap = this.calDeployMoney(salesDatas, winMoneyMap); //调配奖金

    if (!CommonUtils.isEmpty(salesDatas)) {
      for (LttoProvinceSalesData data : salesDatas) {
        StatBonusAllocationWeek bean = new StatBonusAllocationWeek();
        bean.setPeriodNum(periodNum);
        bean.setGameCode(gameCode);
        bean.setProvinceId(data.getProvinceId());
        BigDecimal totalSaleMoney = data.getAmount();//销售总额
        bean.setSaleMoney(totalSaleMoney); //销售总额
        Double rewardMoney = totalSaleMoney.doubleValue() * moneyReturn;
        bean.setRewardBonusMoney(new BigDecimal(rewardMoney)); //返奖奖金
        Double regFundMoney = totalSaleMoney.doubleValue() * adjustFund;
        bean.setRegulationFundMoney(new BigDecimal(regFundMoney)); //省调节基金总额
        Double totalMoney = totalSaleMoney.doubleValue() * bonusSubTotal;
        bean.setTotalMoney(new BigDecimal(totalMoney)); //省返奖和调节基金总额
        bean.setWinBonusMoney(new BigDecimal(winMoneyMap.get(data.getProvinceId()))); //省中奖奖金总额
        //① 调配奖金 = 返奖奖金 - 中奖奖金
        bean.setAllocationBonusMoney(
            new BigDecimal(deployMoneyMap.get(data.getProvinceId()))); //省调配奖金总额
        //② 实际上缴 = 调配奖金 + 调节基金
        bean.setRealityPayCash(
            new BigDecimal(deployMoneyMap.get(data.getProvinceId()) + regFundMoney)); //省奖金实际上缴总额
        resultList.add(bean);
      }
    } else {
      log.warn("期号:[{}],游戏:[{}] 销售数据未传", periodNum, gameCode);
      return;
    }
    log.debug("期号:[{}],游戏:[{}]资金分配统计表数据入库", periodNum, gameCode);
    try {
      bonusAllocationWeekService.batchInsert(resultList);
    } catch (DuplicateKeyException e) {
      log.warn("数据库中存在统计数据", e);
      bonusAllocationWeekService.batchUpdate(resultList);
    }
    log.debug("资金分配表数据入库完成");
  }


  //资金分配表T_STAT_FUNDS_ALLOCATION
  public void calStatFunsAllocation(String gameCode, String periodNum) {
    List<StatFundsAllocation> resultList = new ArrayList<>();
    //各省总销售量
    List<LttoProvinceSalesData> salesDatas = provinceSalesDataService
        .getProvinceSaleData(gameCode, periodNum);
    if (!CommonUtils.isEmpty(salesDatas)) {
      for (LttoProvinceSalesData data : salesDatas) {
        StatFundsAllocation bean = new StatFundsAllocation();
        BigDecimal totalSaleMoney = data.getAmount();
        bean.setPeriodNum(periodNum);
        bean.setGameCode(gameCode);
        bean.setProvinceId(data.getProvinceId());
        bean.setSaleMoney(totalSaleMoney);
        Double awardMoney = totalSaleMoney.doubleValue() * moneyReturn;
        bean.setBonusAwardMoney(new BigDecimal(awardMoney));
        Double regFundMoney = totalSaleMoney.doubleValue() * adjustFund;
        bean.setBonusRegulationfundMoney(new BigDecimal(regFundMoney));
        Double saleMoney = totalSaleMoney.doubleValue() * bonusSubTotal;
        bean.setBonusTotalMoney(new BigDecimal(saleMoney));
        Double centerIssueMoney = totalSaleMoney.doubleValue() * lottery;
        bean.setCenterLotteryIssueMoney(new BigDecimal(centerIssueMoney));
        Double provinceIssueMoney = totalSaleMoney.doubleValue() * province;
        bean.setProvinceIssueMoney(new BigDecimal(provinceIssueMoney));
        Double bttShopIssueMoney = totalSaleMoney.doubleValue() * bettShop;
        bean.setWagerStatoinIssueMoney(new BigDecimal(bttShopIssueMoney));
        Double issueTotalMoney = totalSaleMoney.doubleValue() * offer;
        bean.setIssueTotalMoney(new BigDecimal(issueTotalMoney));
        Double communityMoney = totalSaleMoney.doubleValue() * saleExt;
        bean.setCommunityChestMoney(new BigDecimal(communityMoney));
        resultList.add(bean);
      }
    } else {
      log.warn("期号:[{}],游戏:[{}] 销售数据未传", periodNum, gameCode);
      return;
    }
    log.debug("期号:[{}],游戏:[{}]资金分配表数据入库", periodNum, gameCode);
    try {
      fundsAllocationService.batchInsert(resultList);
    } catch (RuntimeException e) {
      log.warn("数据已经计算过", e);
      fundsAllocationService.batchUpdate(resultList);
    }
    log.debug("资金分配表数据入库完成");
  }

  //① 调配奖金 = 返奖奖金 - 中奖奖金
  //② 实际上缴 = 调配奖金 + 调节基金　　　　　　　　
  //prz_inout = sale_mny*0.49-winn_mny             '调配奖金
  //mny_inout = sale_mny*0.49-winn_mny + fund_mny   '实际上缴--彩票条例之弃奖纳入公益金
  //资金扣划通知书（上缴）T_STAT_FUND_DEDUCT
  public void calStatFundDeductUp(String gameCode, String periodNum) {
    List<StatFundDeduct> upList = new ArrayList<>();
    List<LttoProvinceSalesData> salesDatas = provinceSalesDataService
        .getProvinceSaleData(gameCode, periodNum);
    List<LttoWinstatData> winstatDatas = winstatDataService
        .select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);

    if (!CommonUtils.isEmpty(salesDatas) && !CommonUtils.isEmpty(winstatDatas)) {
      HashMap<String, Double> winMap = this.claWinMoney(winstatDatas, gameCode, periodNum); //中奖奖金
      HashMap<String, Double> deployMap = this.calDeployMoney(salesDatas, winMap); //调配奖金
      calDeployMoney(salesDatas, winMap);
      for (LttoProvinceSalesData bean : salesDatas) {
        Double reg = bean.getAmount().doubleValue() * adjustFund;
        double regulationFund = reg.doubleValue();//调节基金
        double deployMoney = deployMap.get(bean.getProvinceId()); //调配奖金
        StatFundDeduct statFundDeduct = new StatFundDeduct();
        statFundDeduct.setGameCode(gameCode);
        statFundDeduct.setPeriodNum(periodNum);
        statFundDeduct.setProvinceId(bean.getProvinceId());
        statFundDeduct.setRealityPayCash(BigDecimal.valueOf(deployMoney + regulationFund)); //实际上缴
        statFundDeduct.setType(com.cwlrdc.front.common.Constant.Status.FUNDS_TURN_UP);
        upList.add(statFundDeduct);
      }
      log.debug("资金扣划通知（上缴）数据开始入库 上缴数据");
      try {
        fundDeductService.batchInsert(upList);
      } catch (DuplicateKeyException e) {
        log.warn("数据已经计算过", e);
        fundDeductService.batchUpdate(upList);
      }
      log.debug("资金扣划通知（上缴）数据完成入库");
    } else {
      log.warn("各省销售数据或中奖数据未上传");
    }
  }

  //资金扣划通知书（下划）
  public void calStatFundDeductdDown(String gameCode, String periodNum) {
    List<StatFundDeduct> downList = new ArrayList<>();
    //各省销售总额
    List<LttoProvinceSalesData> salesDatas = provinceSalesDataService
        .getProvinceSaleData(gameCode, periodNum);
    //
    List<LttoWinstatData> winstatDatas = winstatDataService
        .select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);

    if (!CommonUtils.isEmpty(salesDatas) && !CommonUtils.isEmpty(winstatDatas)) {

      HashMap<String, Double> winMap = this.claWinMoney(winstatDatas, gameCode, periodNum); //中奖奖金
      HashMap<String, Double> deployMap = this.calDeployMoney(salesDatas, winMap); //调配奖金

      calDeployMoney(salesDatas, winMap);
      for (LttoProvinceSalesData bean : salesDatas) {
        Double reg = bean.getAmount().doubleValue() * adjustFund;//调节基金
        Double deployMoney = deployMap.get(bean.getProvinceId()); //调配奖金
        StatFundDeduct statFundDeduct = new StatFundDeduct();
        statFundDeduct.setGameCode(gameCode);
        statFundDeduct.setPeriodNum(periodNum);
        statFundDeduct.setProvinceId(bean.getProvinceId());
        statFundDeduct.setRealityPayCash(BigDecimal.valueOf(deployMoney + reg));
        statFundDeduct.setType(Constant.Status.FUNDS_TURN_DOWN);
        downList.add(statFundDeduct);
      }
      log.debug("资金扣划通知（下划）数据开始入库 下拨数据");
      try {
        fundDeductService.batchInsert(downList);
      } catch (DuplicateKeyException e) {
        log.warn("数据已经计算过", e);
        fundDeductService.batchUpdate(downList);
      }
      log.debug("资金扣划通知（下划）数据完成入库");
    } else {
      log.warn("各省销售数据或中奖数据未上传");
    }
  }


  //中奖明细表
  public void calWinDetails(String gameCode, String periodNum) {
    List<StatWinAllocation> statWinAllocations = new ArrayList<>();
    //各奖等单注中奖金额
    LttoLotteryAnnouncement announcement =
        announcementService.getAnnocementData(gameCode, periodNum);
    List<LttoWinstatData> winstatDatas =
        winstatDataService
            .select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
    if (!CommonUtils.isEmpty(winstatDatas)) {
      if (announcement != null
          && Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())) {
        long prize1M = announcement.getPrize1Money();
        long prize2M = announcement.getPrize2Money();
        long prize3M = announcement.getPrize3Money();
        long prize4M = announcement.getPrize4Money();
        long prize5M = announcement.getPrize5Money();
        long prize6M = announcement.getPrize6Money();
        long prize7M = announcement.getPrize7Money().longValue();
        long prize8M = announcement.getPrize8Money();
        long prize9M = announcement.getPrize9Money();
        long prize10M = announcement.getPrize10Money();
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
          LttoLotteryAnnouncement annoce10033 =
              announcementService.getAnnocementData(PromotionCode.PRIZE1_GAME_CODE, periodNum);
          LttoLotteryAnnouncement annoce10034 =
              announcementService.getAnnocementData(PromotionCode.PRIZE6_GAME_CODE, periodNum);
          if (annoce10033 != null
              && Constant.Status.TASK_RUN_COMPLETE_2.equals(annoce10033.getProcessStatus())) {
            prize7M = annoce10033.getPrize7Money().longValue();
          }
          if (annoce10034 != null
              && Constant.Status.TASK_RUN_COMPLETE_2.equals(annoce10034.getProcessStatus())) {
            prize8M = annoce10034.getPrize8Money().longValue();
          }
        }
        for (LttoWinstatData windata : winstatDatas) {
          StatWinAllocation statWinAllocation = new StatWinAllocation();
          //奖等对应中奖总额
          long win1Money = windata.getPrize1Count() * prize1M;
          long win2Money = windata.getPrize2Count() * prize2M;
          long win3Money = windata.getPrize3Count() * prize3M;
          long win4Money = windata.getPrize4Count() * prize4M;
          long win5Money = windata.getPrize5Count() * prize5M;
          long win6Money = windata.getPrize6Count() * prize6M;
          Long win7Money = windata.getPrize7Count() * prize7M;
          long win8Money = windata.getPrize8Count() * prize8M;
          long win9Money = windata.getPrize9Count() * prize9M;
          long win10Money = windata.getPrize10Count() * prize10M;
          if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
            boolean promotion1Curr = promotionManager
                .isPromotionCurr(PromotionCode.PRIZE1_GAME_CODE, periodNum);
            boolean promotion6Curr = promotionManager
                .isPromotionCurr(PromotionCode.PRIZE6_GAME_CODE, periodNum);
            statWinAllocation.setFloatingWinMoney(BigDecimal.valueOf(win1Money + win2Money));
            statWinAllocation.setFixedWinMoney(BigDecimal.valueOf(
                win3Money + win4Money + win5Money + win6Money + win7Money + win8Money + win9Money
                    + win10Money));
            if (promotion1Curr && promotion6Curr) {
              statWinAllocation
                  .setFloatingWinMoney(BigDecimal.valueOf(win1Money + win2Money + win8Money));
              statWinAllocation.setFixedWinMoney(BigDecimal
                  .valueOf(win3Money + win4Money + win5Money + win6Money + win9Money + win10Money));
            }
            if (promotion1Curr && !promotion6Curr) {
              statWinAllocation.setFloatingWinMoney(BigDecimal.valueOf(win1Money + win2Money));
              statWinAllocation.setFixedWinMoney(BigDecimal.valueOf(
                  win3Money + win4Money + win5Money + win6Money + win8Money + win9Money
                      + win10Money));
            }
            if (!promotion1Curr && promotion6Curr) {
              statWinAllocation
                  .setFloatingWinMoney(BigDecimal.valueOf(win1Money + win2Money + win8Money));
              statWinAllocation.setFixedWinMoney(BigDecimal
                  .valueOf(win3Money + win4Money + win5Money + win6Money + win9Money + win10Money));
            }
          } else {
            statWinAllocation
                .setFloatingWinMoney(BigDecimal.valueOf(win1Money + win2Money + win3Money));
            statWinAllocation.setFixedWinMoney(BigDecimal.valueOf(
                win4Money + win5Money + win6Money + win7Money + win8Money + win9Money
                    + win10Money));
          }
          statWinAllocation.setTotalMoney(
              statWinAllocation.getFloatingWinMoney().add(statWinAllocation.getFixedWinMoney()));
          //各奖等中奖注数与中奖总金额
          statWinAllocation.setOneWinCount(windata.getPrize1Count().intValue());
          statWinAllocation.setOneWinMoney(BigDecimal.valueOf(win1Money));
          statWinAllocation.setTwoWinCount(windata.getPrize2Count().intValue());
          statWinAllocation.setTwoWinMoney(BigDecimal.valueOf(win2Money));
          statWinAllocation.setThreeWinCount(windata.getPrize3Count().intValue());
          statWinAllocation.setThreeWinMoney(BigDecimal.valueOf(win3Money));
          statWinAllocation.setFourWinCount(windata.getPrize4Count().intValue());
          statWinAllocation.setFourWinMoney(BigDecimal.valueOf(win4Money));
          statWinAllocation.setFiveWinCount(windata.getPrize5Count().intValue());
          statWinAllocation.setFiveWinMoney(BigDecimal.valueOf(win5Money));
          statWinAllocation.setSixWinCount(windata.getPrize6Count().intValue());
          statWinAllocation.setSixWinMoney(BigDecimal.valueOf(win6Money));
          statWinAllocation.setSevenWinCount(windata.getPrize7Count().intValue());
          statWinAllocation.setSevenWinMoney(win7Money.intValue());
          statWinAllocation.setEightWinCount(windata.getPrize8Count().intValue());
          statWinAllocation.setEightWinMoney(BigDecimal.valueOf(win8Money));
          statWinAllocation.setProvinceId(windata.getProvinceId());
          statWinAllocation.setGameCode(windata.getGameCode());
          statWinAllocation.setPeriodNum(windata.getPeriodNum());
          statWinAllocations.add(statWinAllocation);
        }
        log.debug("开始中奖明细表数据入库");
        try {
          winAllocationService.batchInsert(statWinAllocations);
        } catch (DuplicateKeyException e) {
          log.warn("数据已经计算过", e);
          winAllocationService.batchUpdate(statWinAllocations);
        }
        log.debug("完成中奖明细数据入库");
      } else {
        log.warn("开奖公告生成操作未完成");
      }
    } else {
      log.warn("各省中奖明细数据未上报");
    }
  }


  //返奖奖金 = 销售总额*返奖比例
  //各省调配奖金 调配奖金 = 返奖奖金 - 中奖奖金
  private HashMap<String, Double> calDeployMoney(List<LttoProvinceSalesData> salesDatas,
      HashMap<String, Double> winMoneyMap) {
    HashMap<String, Double> deployMap = new HashMap<>();
    if (!CommonUtils.isEmpty(salesDatas)) {
      for (LttoProvinceSalesData salesData : salesDatas) {
        Double saleMoney = salesData.getAmount().doubleValue();
        String provinceId = salesData.getProvinceId();
        Double re = saleMoney * moneyReturn; //返奖奖金
        Double winMoney = winMoneyMap.get(provinceId); //中奖奖金
        Double deployMoney = re - winMoney; //调配奖金
        deployMap.put(provinceId, deployMoney);
      }
    }
    return deployMap;
  }

  //促销情况下各省中奖奖金（开奖公告计算完成后）
  private HashMap<String, Double> claWinMoney(List<LttoWinstatData> winList, String gameCode,
      String periodNum) {
    //促销奖池金额为0
    LttoLotteryAnnouncement announcement = announcementService
        .getAnnocementData(gameCode, periodNum);

    Double prize1M = 0.00, prize2M = 0.00, prize3M = 0.00, prize4M = 0.00, prize5M = 0.00, prize6M = 0.00, prize7M = 0.00, prize8M = 0.00, prize9M = 0.00, prize10M = 0.00;
    if (announcement != null && com.cwlrdc.front.common.Constant.Status.TASK_RUN_COMPLETE_2
        .equals(announcement.getProcessStatus())) {
      prize1M = announcement.getPrize1Money().doubleValue();
      prize2M = announcement.getPrize2Money().doubleValue();
      prize3M = announcement.getPrize3Money().doubleValue();
      prize4M = announcement.getPrize4Money().doubleValue();
      prize5M = announcement.getPrize5Money().doubleValue();
      prize6M = announcement.getPrize6Money().doubleValue();
      //涉及到促销是,对应奖等单注中奖金额处理
      if (GameCode.GAME_CODE_SLTO.equals(gameCode)) {
        LttoLotteryAnnouncement annocepro1 =
            announcementService.getAnnocementData(PromotionCode.PRIZE1_GAME_CODE, periodNum);
        LttoLotteryAnnouncement annocepro6 =
            announcementService.getAnnocementData(PromotionCode.PRIZE6_GAME_CODE, periodNum);
        if (annocepro1 != null && annocepro1.getPrize7Money() != null) {
          prize7M = annocepro1.getPrize7Money().doubleValue();
        } else {
          prize7M = announcement.getPrize7Money().doubleValue();
        }
        if (annocepro6 != null && annocepro6.getPrize8Money() != null) {
          prize8M = annocepro6.getPrize8Money().doubleValue();
        } else {
          prize8M = announcement.getPrize8Money().doubleValue();
        }
      }
      prize9M = announcement.getPrize9Money().doubleValue();
      prize10M = announcement.getPrize10Money().doubleValue();
    } else {
      log.warn("开奖公告未计算完成");
    }
    HashMap<String, Double> winResultMap = new HashMap<>();
    //浮动奖，固定奖，中奖注数*单注奖金金额 各奖等累加
    double totalWinMoney = 0.00;
    if (!CommonUtils.isEmpty(winList)) {
      for (LttoWinstatData winData : winList) {
        totalWinMoney = winData.getPrize1Count() * prize1M +
            winData.getPrize2Count() * prize2M +
            winData.getPrize3Count() * prize3M +
            winData.getPrize4Count() * prize4M +
            winData.getPrize5Count() * prize5M +
            winData.getPrize6Count() * prize6M +
            winData.getPrize7Count() * prize7M +
            winData.getPrize8Count() * prize8M +
            winData.getPrize9Count() * prize9M +
            winData.getPrize10Count() * prize10M;
        winResultMap.put(winData.getProvinceId(), totalWinMoney);
      }
    } else {
      log.warn("各省中奖数据尚未上传");
    }
    return winResultMap;
  }


  //非促销情况下全国中奖情(况开奖公告计算完成后)
  private HashMap<String, Double> claNoPromotionWinMoney(List<LttoWinstatData> winList,
      String gameCode, String periodNum) {
    //促销奖池金额为0
    LttoLotteryAnnouncement announcement = announcementService
        .getAnnocementData(gameCode, periodNum);
    Double prize1M = 0.00, prize2M = 0.00, prize3M = 0.00, prize4M = 0.00, prize5M = 0.00, prize6M = 0.00, prize7M = 0.00, prize8M = 0.00, prize9M = 0.00, prize10M = 0.00;
    if (announcement != null && com.cwlrdc.front.common.Constant.Status.TASK_RUN_COMPLETE_2
        .equals(announcement.getProcessStatus())) {
      prize1M = announcement.getPrize1Money().doubleValue();
      prize2M = announcement.getPrize2Money().doubleValue();
      prize3M = announcement.getPrize3Money().doubleValue();
      prize4M = announcement.getPrize4Money().doubleValue();
      prize5M = announcement.getPrize5Money().doubleValue();
      prize6M = announcement.getPrize6Money().doubleValue();
      //涉及到促销是,对应奖等单注中奖金额处理
      prize7M = announcement.getPrize7Money().doubleValue();
      prize8M = announcement.getPrize8Money().doubleValue();
      prize9M = announcement.getPrize9Money().doubleValue();
      prize10M = announcement.getPrize10Money().doubleValue();
    } else {
      log.warn("开奖公告未计算完成");
    }
    HashMap<String, Double> winResultMap = new HashMap<>();
    //浮动奖，固定奖，中奖注数*单注奖金金额 各奖等累加
    double totalWinMoney = 0.00;
    if (!CommonUtils.isEmpty(winList)) {
      for (LttoWinstatData winData : winList) {
        totalWinMoney = winData.getPrize1Count() * prize1M +
            winData.getPrize2Count() * prize2M +
            winData.getPrize3Count() * prize3M +
            winData.getPrize4Count() * prize4M +
            winData.getPrize5Count() * prize5M +
            winData.getPrize6Count() * prize6M +
            winData.getPrize7Count() * prize7M +
            winData.getPrize8Count() * prize8M +
            winData.getPrize9Count() * prize9M +
            winData.getPrize10Count() * prize10M;
        winResultMap.put(winData.getProvinceId(), totalWinMoney);
      }
    } else {
      log.warn("各省中奖数据尚未上传");
    }
    return winResultMap;
  }


  public void init(String gameCode) {
    //各省资金调配比例
    OpetReportParameterKey key = new OpetReportParameterKey();
    key.setGameCode(gameCode);
    OpetReportParameter parameter = reportParameterService.selectByPrimaryKey(key);
    if (parameter != null) {
      adjustFund = parameter.getAdjustFund().doubleValue() / 100.00;//调节基金比
      moneyReturn = parameter.getMoneyReturn().doubleValue() / 100.00;//返奖奖金(百分比)
      bonusSubTotal = parameter.getBonusSubtotal().doubleValue() / 100.00;//奖金小计比例
      lottery = parameter.getLottery().doubleValue() / 100.00;//中彩 发行费比例
      province = parameter.getProvince().doubleValue() / 100.00;// 省中心发行比例
      bettShop = parameter.getBettingShop().doubleValue() / 100.00;//投注站发行费比例
      offer = parameter.getOfferingSubtotal().doubleValue() / 100.00;//发行费小计比例
      saleExt = parameter.getSaleExtract().doubleValue() / 100.00;//销售提取比例（公益金提取比例）
    } else {
      log.warn("报表参数未初始化");
    }
  }

}
