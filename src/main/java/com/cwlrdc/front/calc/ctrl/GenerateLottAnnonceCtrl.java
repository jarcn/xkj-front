package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.*;
import com.cwlrdc.front.calc.CalculateManager;
import com.cwlrdc.front.calc.bean.LotteryAnnoceReqBean;
import com.cwlrdc.front.calc.bean.LotteryAnnoceRespBean;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开奖公告生成操作
 * Created by chenjia on 2017/4/19.
 */

@Slf4j
@Controller
public class GenerateLottAnnonceCtrl {

    @Resource
    private LttoLotteryAnnouncementService lotteryAnnouncementService;
    @Resource
    private LttoProvinceSalesDataService provinceSalesDataService;
    @Resource
    private LttoCancelWinStatDataService cancelWinStatDataService;
    @Resource
    private LttoWinstatDataService winstatDataService;
    @Resource
    private ParaGamePeriodInfoService gamePeriodInfoService;
    @Autowired
    private ParaSysparameCache paraSysparameCache;
    @Resource
    private PromotionManager promotionManager;
    @Resource
    private OperatorsLogManager operatorsLogManager;

    @ResponseBody
    @RequestMapping(value = "/CwlAuditApi/rdti/generateAnnounce/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo generateLottAnnonce(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request, HttpServletResponse response) {
        try {
            long start = System.currentTimeMillis();
            ReturnInfo info = new ReturnInfo();
            //判断当前的环境,如果是生产,判断是否收集完成
            ENV env = paraSysparameCache.getEnv();
            if (env != null && ENV.pro == env) {
                int saleDataFaildCount = provinceSalesDataService.selectUploadFaildCount(gameCode, periodNum);
                int winstatFaildCount = winstatDataService.selectUploadFaildCount(gameCode, periodNum);
                if (saleDataFaildCount > 0) {
                    return new ReturnInfo("当前销售数据上传不完整", false);
                }
                if (winstatFaildCount > 0) {
                    return new ReturnInfo("当前中奖数据上传不完整", false);
                }
            }
            //非派奖拼接开奖公告请求参数
            LotteryAnnoceReqBean reqBean = this.createRequestBean(gameCode, periodNum);
            log.debug("[开奖稽核系统] 调用 [数美生成开奖公告接口] 请求参数:[{}]", JsonUtil.bean2Json(reqBean));
            LotteryAnnoceRespBean respBean = CalculateManager.generateLotteryAnnounce(reqBean);
            log.debug("[开奖稽核系统] 调用 [数美生成开奖公告]接口 响应参数:[{}]", JsonUtil.bean2Json(respBean));
            //判断本期一等奖是否派奖
            if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
                if (promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum)) {
                    LotteryAnnoceReqBean promotion1 = this.createPromotionRequestBean(Constant.PromotionCode.PRIZE1_GAME_CODE, reqBean);
                    log.debug("[开奖稽核系统] 调用 [数美一等奖派奖开奖公告] 请求参数:[{}]", JsonUtil.bean2Json(promotion1));
                    LotteryAnnoceRespBean respBean1 = CalculateManager.generateLotteryAnnounce(promotion1);
                    log.debug("[开奖稽核系统] 调用 [数美一等奖派奖开奖公告] 响应参数:[{}]", JsonUtil.bean2Json(respBean1));
                }
                //判断本期六等奖是否派奖
                if (promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum)) {
                    LotteryAnnoceReqBean promotion6 = this.createPromotionRequestBean(Constant.PromotionCode.PRIZE6_GAME_CODE, reqBean);
                    log.debug("[开奖稽核系统] 调用 [数美六等奖派奖开奖公告] 请求参数:[{}]", JsonUtil.bean2Json(promotion6));
                    LotteryAnnoceRespBean respBean6 = CalculateManager.generateLotteryAnnounce(promotion6);
                    log.debug("[开奖稽核系统] 调用 [数美六等奖派奖开奖公告] 响应参数:[{}]", JsonUtil.bean2Json(respBean6));
                }
            }
            //TODO 开奖公告计算完成，更新中奖情况汇总中中奖金额字段
            if (ResponseResult.操作成功.getId().equals(respBean.getErrorCode())) {
                LttoLotteryAnnouncement announcement = lotteryAnnouncementService.getAnnocementData(gameCode,periodNum);
                if (Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())) {
                    info.setDescription("开奖公告生成完成");
                    if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
                        info.setDescription(this.showMessage(announcement));
                    }
                    info.setRetcode(announcement.getProcessStatus());
                    info.setSuccess(true);
                    info.setRetObj(announcement);
                    log.info(operatorsLogManager.getLogInfo("生成全国开奖公告", "生成开奖公告", start));
                } else if (Constant.Status.TASK_RUN_RUNNING_1.equals(announcement.getProcessStatus())) {
                    info.setDescription("开奖公告生成中...");
                    info.setRetcode(announcement.getProcessStatus());
                    info.setSuccess(true);
                    log.info(operatorsLogManager.getLogInfo("生成全国开奖公告", "生成开奖公告", start));
                } else {
                    info.setRetcode(announcement.getProcessStatus());
                    info.setDescription("开奖公告生成异常");
                    info.setSuccess(false);
                }
                return info;
            } else {
                info.setSuccess(false);
                info.setRetcode(Integer.parseInt(respBean.getErrorCode()));
                info.setDescription(respBean.getErrorMsg());
                log.error("[开奖稽核系统]调用[开奖公告生成]接口异常 [{}]", JsonUtil.bean2Json(info));
                return info;
            }
        } catch (Exception e) {
            log.warn("处理错误", e);
            return new ReturnInfo("程序错误", false);
        }
    }

    //是否生成开奖公告
    @ResponseBody
    @RequestMapping(value = "/hasgenerated/annocement/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo hasGeneratedAnnace(@PathVariable String gameCode, @PathVariable String periodNum) {
        LttoLotteryAnnouncementKey announcementKey = new LttoLotteryAnnouncementKey();
        announcementKey.setGameCode(gameCode);
        announcementKey.setPeriodNum(periodNum);
        LttoLotteryAnnouncement announcement = lotteryAnnouncementService.selectByPrimaryKey(announcementKey);
        if (announcement != null && Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())) {
            return ReturnInfo.Success;
        } else {
            return ReturnInfo.Faild;
        }
    }


    private String showMessage(LttoLotteryAnnouncement announcement) {
        StringBuilder sb = new StringBuilder();
        sb.append("开奖公告生成完成");
        //开奖公告计算完成后需要给市场二部提示的话
        double fund2PoolAutoAll = announcement.getFund2PoolAutoAll().doubleValue();
        double poolTotal = announcement.getPoolTotal().doubleValue();
        if (fund2PoolAutoAll > 0) {
            String note = "中彩中心决定:从双色球调节基金拨出" + fund2PoolAutoAll + "元,弥补当期派奖奖金不足。";
            announcement.setBullNote(note);
            lotteryAnnouncementService.updateByPrimaryKey(announcement);
            sb.append(note);
        }
        //修改公告提示调节基金(都是在原有基础上加)
        if (poolTotal > 90000000 && poolTotal < 100000000) {
            sb.append("奖池低于1亿高于9000万，请打印公告与二部商议加奖池事宜！！！原则上补足至1亿元,补足金额精确到百万元。在'数据维护——调节基金奖池维护'下加奖池后，再打印公告下发！");
        }
        if (poolTotal < 30000000) {
            sb.append("奖池低于3000万，请打印公告与二部商议加奖池事宜！！！原则上低于3000万补足至3000万，补足金额精确到百万元。在'数据维护——调节基金奖池维护'下加奖池后，再打印公告下发！");
        }
        return sb.toString();
    }


    private LotteryAnnoceReqBean createPromotionRequestBean(String gameCode, LotteryAnnoceReqBean reqBean) {
        LotteryAnnoceReqBean reqProBean = new LotteryAnnoceReqBean();
        BeanCopyUtils.copyProperties(reqBean,reqProBean);
        reqProBean.setGameCode(gameCode);
        return reqProBean;
    }

    private LotteryAnnoceReqBean createRequestBean(String gameCode, String periodNum) {
        LotteryAnnoceReqBean reqBean = new LotteryAnnoceReqBean();
        reqBean.setGameCode(gameCode);
        reqBean.setPeriodNum(periodNum);
        List<LttoProvinceSalesData> provinceSalesDataList = provinceSalesDataService.getProvinceSaleData(gameCode, periodNum);
        reqBean.setAmountDetail(this.getAllSaleMoney(provinceSalesDataList)); //明细文件销售额 全国
        List<LttoCancelWinStatData> cancelWinStatDataList = cancelWinStatDataService.selectCancelWinDatas(gameCode, periodNum);
        reqBean.setAllCanceledMoney(this.getAllCancelMoney(cancelWinStatDataList)); //本期弃奖总额 全国
        List<LttoWinstatData> winstatDataList = winstatDataService.select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        Map<String, Long> levelMap = this.getAllLevel(winstatDataList);
        //中奖文件集中表各奖等中奖个数 全国
        reqBean.setPrize1Count(levelMap.get("allPrize1Count"));
        reqBean.setPrize2Count(levelMap.get("allPrize2Count"));
        reqBean.setPrize3Count(levelMap.get("allPrize3Count"));
        reqBean.setPrize4Count(levelMap.get("allPrize4Count"));
        reqBean.setPrize5Count(levelMap.get("allPrize5Count"));
        reqBean.setPrize6Count(levelMap.get("allPrize6Count"));
        reqBean.setPrize7Count(levelMap.get("allPrize7Count"));
        reqBean.setPrize8Count(levelMap.get("allPrize8Count"));
        reqBean.setPrize9Count(levelMap.get("allPrize9Count"));
        reqBean.setPrize10Count(levelMap.get("allPrize10Count"));
        if (StringUtils.isNotBlank(periodNum)) {
            //查询本期的开奖公告 //封期时初始化开奖公告记录
            LttoLotteryAnnouncement announcement = lotteryAnnouncementService.selectByKey(gameCode, periodNum);
            if (announcement == null) {
                throw new IllegalArgumentException("查询开奖公告错误,游戏[" + gameCode + "]期号[" + periodNum + "]");
            }
            reqBean.setJaclpotAddStartbalance(BigDecimalUtils.toDouble(announcement.getPoolBgn())); //本期奖池期初余额（对应上一期的期末余额）
            reqBean.setAllocationAddStartbalance(BigDecimalUtils.toDouble(announcement.getFundBgn()));//本期调节基金期初余额（对应上一期的期末余额）
            reqBean.setAllocationSubIntojackpot(BigDecimalUtils.toDouble(announcement.getFund2PoolHand()));
            reqBean.setAllocationSubPayspecialwin(BigDecimalUtils.toDouble(announcement.getFund2Prize()));
            reqBean.setAllocationSubRollout(BigDecimalUtils.toDouble(announcement.getFund2PoolAutoAll()));
            reqBean.setJaclpotAddChange(BigDecimalUtils.toDouble(announcement.getPoolTempIn()));
            reqBean.setJaclpotSubChan(BigDecimalUtils.toDouble(announcement.getPoolTempOut()));
        }
        return reqBean;
    }

    //全国总销量
    private Double getAllSaleMoney(List<LttoProvinceSalesData> provinceSalesDataList) {
        Double allSaleMoney = 0.00;
        for (LttoProvinceSalesData bean : provinceSalesDataList) {
            allSaleMoney += BigDecimalUtils.toDouble(bean.getAmount());
        }
        return allSaleMoney;
    }

    //全国总弃奖额
    private Double getAllCancelMoney(List<LttoCancelWinStatData> cancelWinStatDataList) {
        Double allCancelMoney = 0.00;
        for (LttoCancelWinStatData bean : cancelWinStatDataList) {
            allCancelMoney += BigDecimalUtils.toDouble(bean.getAllCanceledMoney());
        }
        return allCancelMoney;
    }

    //全国各奖等中奖注数
    private HashMap<String, Long> getAllLevel(List<LttoWinstatData> winstatDataList) {
        HashMap<String, Long> winLevelCount = new HashMap<String, Long>();

        Long allPrize1Count = 0L, allPrize2Count = 0L, allPrize3Count = 0L, allPrize4Count = 0L,
                allPrize5Count = 0L, allPrize6Count = 0L, allPrize7Count = 0L, allPrize8Count = 0L,
                allPrize9Count = 0L, allPrize10Count = 0L;

        //各省各奖等中奖注数总和
        for (LttoWinstatData bean : winstatDataList) {
            allPrize1Count += bean.getPrize1Count();
            allPrize2Count += bean.getPrize2Count();
            allPrize3Count += bean.getPrize3Count();
            allPrize4Count += bean.getPrize4Count();
            allPrize5Count += bean.getPrize5Count();
            allPrize6Count += bean.getPrize6Count();
            allPrize7Count += bean.getPrize7Count();
            allPrize8Count += bean.getPrize8Count();
            allPrize9Count += bean.getPrize9Count();
            allPrize10Count += bean.getPrize10Count();
        }
        winLevelCount.put("allPrize1Count", allPrize1Count);
        winLevelCount.put("allPrize2Count", allPrize2Count);
        winLevelCount.put("allPrize3Count", allPrize3Count);
        winLevelCount.put("allPrize4Count", allPrize4Count);
        winLevelCount.put("allPrize5Count", allPrize5Count);
        winLevelCount.put("allPrize6Count", allPrize6Count);
        winLevelCount.put("allPrize7Count", allPrize7Count);
        winLevelCount.put("allPrize8Count", allPrize8Count);
        winLevelCount.put("allPrize9Count", allPrize9Count);
        winLevelCount.put("allPrize10Count", allPrize10Count);
        return winLevelCount;
    }

}
