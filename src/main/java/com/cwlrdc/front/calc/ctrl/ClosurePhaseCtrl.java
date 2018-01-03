package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.calc.util.CalculationReportUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.PromotionCode;
import com.cwlrdc.front.common.NewPeriodInitializer;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.PromotionManager;
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.task.LttoProvinceFileDownloadContainer;
import com.joyveb.lbos.restful.common.ReturnInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 封期操作
 * 1:把当期设置为已开奖期
 * 2:把下期设为开奖期
 * 3:把下期+1设为待开奖期
 * 4:把下期+2设为开奖期参数发放期
 * 5:新建参数发放期目录
 * 7:开奖公告初始化下一期记录
 * 8:初始化下一期开奖流程
 * 9:根据开奖公告生成数据，更新资金动态表中的数据（奖池余额，调节基金余额）
 * 6:封期完成
 */
@Slf4j
@Controller
public class ClosurePhaseCtrl {

  @Resource
  private ParaGamePeriodInfoService periodNumService;
  @Resource
  private CalculationReportUtil calculationReportUtil;
  @Resource
  private NewPeriodInitializer newPeriodInitializer;
  @Resource
  private PromotionManager promotionManager;
  @Resource
  private LttoLotteryAnnouncementService lotteryAnnouncementService;
  @Resource
  private LttoProvinceFileDownloadContainer fileDownloadContainer;
  @Resource
  private OperatorsLogManager operatorsLogManager;


  /**
   * 打印开奖公告前判断
   * 下期一等奖是否继续促销
   */
  @ResponseBody
  @RequestMapping(value = "/showPromotion1Msg/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo showPromotion1Msg(@PathVariable String gameCode,
      @PathVariable String periodNum) {
    if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
      int currPeriod = Integer.parseInt(periodNum);
      int promotionStart = Integer.parseInt(PromotionCode.PROMOTION_PERIOD_START);
      int promotionEnd = Integer.parseInt(PromotionCode.PROMOTION_PERIOD_END);
      if (currPeriod < promotionStart || currPeriod > promotionEnd) {
        return ReturnInfo.Faild;
      }
      boolean promotionNext1 = promotionManager
          .isPromotionNext(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum);
      if (!promotionNext1) {
        return new ReturnInfo("****下一期一等奖派奖结束,请务必通知系统管理员!!!****", true);
      }
    }
    return ReturnInfo.Faild;
  }

  /**
   * 打印开奖公告前判断
   * 下期一等奖是否继续促销
   * 由于六等奖派奖金额超出了2222.00元,故从双色球调节基金拨出2222.00元，
   * 注入第2016125期六等奖复式派奖奖池（rpt_bull表中game_id的值为10034），
   * 用于补足派奖金额不足。
   */
  @ResponseBody
  @RequestMapping(value = "/showPromotion6Msg/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo showPromotion6Msg(@PathVariable String gameCode,
      @PathVariable String periodNum) {
    if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
      int currPeriod = Integer.parseInt(periodNum);
      int promotionStart = Integer.parseInt(PromotionCode.PROMOTION_PERIOD_START);
      int promotionEnd = Integer.parseInt(PromotionCode.PROMOTION_PERIOD_END);
      if (currPeriod < promotionStart || currPeriod > promotionEnd) {
        return ReturnInfo.Faild;
      }
      //判断当期六等奖派奖金额是否大于奖池余额
      LttoLotteryAnnouncement promotionAnnouncement =
          lotteryAnnouncementService.selectByKey(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
      if (promotionAnnouncement == null) {
        return new ReturnInfo("无促销奖池", false);
      }
      Integer prize8Count = promotionAnnouncement.getPrize8Count();
      if (prize8Count == null) {
        return new ReturnInfo("促销奖池信息错误", false);
      }
      if (promotionAnnouncement.getPoolTotal().doubleValue() <= 0) {
        long subVal = prize8Count * 5L - promotionAnnouncement.getPoolBgn().longValue();
        String bullNote = promotionAnnouncement.getFundNote();
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(bullNote)){
          sb.append(bullNote);
          sb.append("\r\n****下一期六等等奖派奖结束,请务必通知系统管理员!!!****");
        }
        return new ReturnInfo(sb.toString(), true);
      }

      boolean promotionNext6 = promotionManager
          .isPromotionNext(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
      if (!promotionNext6) {
        //派奖奖池
        return new ReturnInfo("****下一期六等等奖派奖结束,请务必通知系统管理员!!!****", true);
      }
      return new ReturnInfo();
    }
    return ReturnInfo.Faild;
  }

  /**
   * 封期操作入口
   *
   * @param gameCode 游戏编码
   * @param currentPeriodNum 当前期号
   * @param req 请求参数
   */
  @ResponseBody
  @RequestMapping(value = "/close/phase/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo closePhase(@PathVariable String gameCode,
      @PathVariable("periodNum") String currentPeriodNum, HttpServletRequest req) {
    long start = System.currentTimeMillis();
    try {
      if (StringUtils.isBlank(currentPeriodNum) || StringUtils.isBlank(gameCode)) {
        log.warn("开始初始化新期开奖流程,数据异常,期号[{}]游戏[{}]", currentPeriodNum, gameCode);
        ReturnInfo result = new ReturnInfo("期号或参数错误", false);
        return result;
      }
      ParaGamePeriodInfo currentPeriodInfo =
          periodNumService.queryWinNum(gameCode, currentPeriodNum);
      if (currentPeriodInfo == null
          || !Status.Period.CURRENT.equals(currentPeriodInfo.getStatus())) {
        return new ReturnInfo("错误,请求不是当前期", false);
      }
      String newPeriodNum = periodNumService.nextPeriodNum(gameCode, currentPeriodNum);
      newPeriodInitializer.initPeriodParam(gameCode, currentPeriodNum);
      newPeriodInitializer.initRunFlow(gameCode, newPeriodNum);
      newPeriodInitializer.initSaleRptRecords(gameCode, newPeriodNum);
      newPeriodInitializer.initOverdueRptRecords(gameCode, newPeriodNum);
      newPeriodInitializer.initWinRptRecords(gameCode, newPeriodNum);
      newPeriodInitializer.initSaleZipRecords(gameCode, newPeriodNum);
      newPeriodInitializer.initNewPeriodAnnonce(gameCode, currentPeriodNum, newPeriodNum);
      //促销情况
      if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
        boolean promotion1Next = promotionManager.isPromotionNext(PromotionCode.PRIZE1_GAME_CODE
            , currentPeriodNum);
        if (promotion1Next) {
          newPeriodInitializer.initNewPeriodAnnonce(Constant.PromotionCode.PRIZE1_GAME_CODE
              , currentPeriodNum, newPeriodNum);
        }
        boolean promotion6Next = promotionManager.isPromotionNext(PromotionCode.PRIZE6_GAME_CODE
            , currentPeriodNum);
        if (promotion6Next) {
          newPeriodInitializer.initNewPeriodAnnonce(Constant.PromotionCode.PRIZE6_GAME_CODE
              , currentPeriodNum, newPeriodNum);
        }
      }
    } catch (Exception e) {
      log.debug("[开奖稽核系统]执行封期操作异常", e);
      return ReturnInfo.Faild;
    }
    log.info(operatorsLogManager.getLogInfo("生成全国开奖公告", "封期", start));
    //重新加载文件下载线程
    fileDownloadContainer.reload();
    log.debug("封期完成,当前游戏[{}],重新加载ZIP文件下载线程", gameCode);
    return ReturnInfo.Success;
  }

  /**
   * 报表计算入口
   */
  @ResponseBody
  @RequestMapping(value = "/calculation/report/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo calculationReport(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req) {
    try {
      log.debug("[开奖稽核系统] 开始 计算 游戏[{}]期号[{}]报表数据", gameCode, periodNum);
      initReportData(gameCode, periodNum); //计算当期报表统计数据
    } catch (Exception e) {
      log.error("[开奖稽核系统]计算当期报表异常", e);
      return ReturnInfo.Faild;
    }
    log.debug("[开奖稽核系统] 完成 计算 游戏[{}]期号[{}]报表数据", gameCode, periodNum);
    return ReturnInfo.Success;
  }

  /**
   * 报表数据生成
   *
   * @param gameCode 游戏编码
   * @param periodNum 期号
   */
  private void initReportData(String gameCode, String periodNum) {
    calculationReportUtil.init(gameCode); //初始化报表计算参数
    //期报表
    calculationReportUtil.calBonusAllocation(gameCode, periodNum); //奖金调配表
    calculationReportUtil.calStatFunsAllocation(gameCode, periodNum); //资金分配表
    calculationReportUtil.calWinDetails(gameCode, periodNum);  //中奖明细表
    //周报表
    calculationReportUtil.calStatBonusAllocation(gameCode, periodNum); //奖金调配表
    calculationReportUtil.calStatFundDeductUp(gameCode, periodNum); //资金扣划通知书（上缴）
    calculationReportUtil.calStatFundDeductdDown(gameCode, periodNum); //资金扣划通知书（下划)
  }

}
