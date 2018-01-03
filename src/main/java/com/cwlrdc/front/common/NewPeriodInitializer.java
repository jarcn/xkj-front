package com.cwlrdc.front.common;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlow;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.rt.entity.LttoCancelwinStatDataRT;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRT;
import com.cwlrdc.commondb.rt.entity.LttoWinstatDataRT;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.rt.service.LttoCancelwinStatDataRTService;
import com.cwlrdc.front.rt.service.LttoProvinceSalesDataRTService;
import com.cwlrdc.front.rt.service.LttoWinstatDataRTService;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * Created by yangqiju on 2017/8/31.
 */
@Slf4j
@Component
public class NewPeriodInitializer {

    @Resource
    private LttoRunFlowService runFlowService;
    @Resource
    private LttoLotteryAnnouncementService announcementService;
    @Resource
    private ParaGamePeriodInfoService periodNumService;
    @Resource
    private LttoProvinceSalesDataService lttoProvinceSalesDataService;
    @Resource
    private LttoWinstatDataService lottWinstatDataService;
    @Resource
    private LttoCancelWinStatDataService lttoCancelWinStatDataService;
    @Resource
    private LttoProvinceFileStatusService lttoProvinceFileStatusService;
    @Resource
    private GameInfoCache gameInfoCache;
    @Resource
    private ProvinceInfoCache provinceInfoCache;
    @Resource
    private LttoProvinceSalesDataRTService lttoProvinceSalesDataRTService;
    @Resource
    private LttoWinstatDataRTService lttoWinstatDataRTService;
    @Resource
    private LttoCancelwinStatDataRTService lttoCancelwinStatDataRTService;
    @Resource
    private PromotionManager promotionManager;

    /**
     * 初始化开奖流程记录
     *
     * @param gameCode
     * @param newPeriodNum
     */
    public void initRunFlow(String gameCode, String newPeriodNum) {
        log.debug("开始初始化新期开奖流程...");
        if (StringUtils.isBlank(newPeriodNum) || StringUtils.isBlank(gameCode)) {
            log.warn("开始初始化新期开奖流程,数据异常,期号[{}]游戏[{}]", newPeriodNum, gameCode);
            return;
        }
        List<LttoRunFlow> list = new ArrayList<>();
        for (FlowType ft : FlowType.values()) {
            LttoRunFlow lttoRunFlow = new LttoRunFlow();
            lttoRunFlow.setPeriodNum(newPeriodNum);
            lttoRunFlow.setGameCode(gameCode);
            lttoRunFlow.setMarks(ft.getTypeName());
            lttoRunFlow.setFlowType(ft.getTypeNum());
            lttoRunFlow.setFlowStatus(Constant.Status.TASK_LTTOERY_FLOW_0);
            list.add(lttoRunFlow);
        }
        try {
            runFlowService.batchInsert(list);
        } catch (Exception e) {
            log.warn("初始化开奖流程主键冲突", e);
            runFlowService.batchUpdate(list);
        }
        log.debug("完成初始化新期开奖流程");
    }

    /**
     * 获得当前游戏和当前期<br>
     * 将当前期变为已开期<br>
     * 将下一期变成开奖期<br>
     * 将下下期变成发参数期
     *
     * @param gameCode
     * @param currentPeriodNum
     */
    public void initPeriodParam(String gameCode, String currentPeriodNum) {
        log.info("初始化新期参数开始");
        List<ParaGamePeriodInfo> paraGamePeriodInfos = periodNumService.select2current();
        for (ParaGamePeriodInfo period : paraGamePeriodInfos) {
            String currGameCode = period.getGameCode();
            if (!currGameCode.equals(gameCode)) {
                period.setFlowNode(Status.FlowNode.OPEN);
                periodNumService.updateByPrimaryKeySelective(period);
            }
        }
        List<ParaGamePeriodInfo> periodInfos = new ArrayList<>();

        ParaGamePeriodInfo currentPeriodInfo = periodNumService.queryWinNum(gameCode, currentPeriodNum);

        if (currentPeriodInfo == null) {
            throw new IllegalArgumentException("数据没有当前期[" + currentPeriodNum + "]");
        }

        ParaGamePeriodInfo newPeriodInfo = periodNumService.nextPeriodInfo(gameCode, currentPeriodNum);

        if (newPeriodInfo == null) {
            throw new IllegalArgumentException("期号错误,当前期[" + currentPeriodNum + "]没有下一期");
        }

        ParaGamePeriodInfo paramPeriodInfo = periodNumService.nextPeriodInfo(gameCode, newPeriodInfo.getPeriodNum());

        if (paramPeriodInfo == null) {
            throw new IllegalArgumentException("期号错误,期号[" + newPeriodInfo.getPeriodNum() + "]没有下一期");
        }

        currentPeriodInfo.setStatus(Status.Period.PASSED);
        periodInfos.add(currentPeriodInfo);

        newPeriodInfo.setStatus(Status.Period.CURRENT);
        if (promotionManager.isPromotionNext(gameCode, currentPeriodNum)){
            newPeriodInfo.setPromotionStatus(Constant.PromotionCode.PROMOTION_CODE);
        }
        periodInfos.add(newPeriodInfo);

        paramPeriodInfo.setStatus(Status.Period.PARAM);
        periodInfos.add(paramPeriodInfo);

        try {
            periodNumService.batchUpdate(periodInfos);
        } catch (Exception e) {
            log.warn("初始化新期参数异常", e);
            throw e;
        }
        log.info("初始化新期参数完成");

    }

    /**
     * 初始化新期的开奖公告记录
     */
    public void initNewPeriodAnnonce(String gameCode, String currentPeriodNum, String newPeriodNum) {
        try {
            LttoLotteryAnnouncement oldAnnoce = announcementService.selectByKey(gameCode, currentPeriodNum);
            if (oldAnnoce == null) {
                log.warn("初始化新期开奖公告错误,请联系运维人员.游戏[{}]期号[{}]的开奖公告信息为空", gameCode, currentPeriodNum);
                return;
            }
            LttoLotteryAnnouncement announcement = new LttoLotteryAnnouncement();
            announcement.setPeriodNum(newPeriodNum); //下一期的期号
            announcement.setGameCode(gameCode);
            announcement.setFundBgn(oldAnnoce.getFundTotal());
            announcement.setPoolBgn(oldAnnoce.getPoolTotal());
            announcement.setFund2Prize(oldAnnoce.getFund2Prize());
            announcement.setFund2PoolAutoAll(oldAnnoce.getFund2PoolAutoAll());
            announcement.setFund2PoolHand(oldAnnoce.getFund2PoolHand());
            announcement.setPoolTempOut(oldAnnoce.getPoolTempOut());
            announcement.setWinGroupCount(1); //默认奖组为1,促销时修改
            announcement.setGradeCount(10); //默认奖级个数为10,促销时改变
            if (Constant.PromotionCode.PRIZE1_GAME_CODE.equals(gameCode)){
                announcement.setPoolTempIn(BigDecimal.valueOf(20000000.00));
            }
            if (Constant.PromotionCode.PRIZE1_GAME_CODE.equals(gameCode)
                || Constant.PromotionCode.PRIZE6_GAME_CODE.equals(gameCode)){
                announcement.setFlag(1); //开奖公告促销有效标志
            } else {
                announcement.setPoolTempIn(oldAnnoce.getPoolTempIn());
            }
            log.info("开始初始化新期开奖公告数据,初始化参数:[{}]", JsonUtil.bean2JsonString(announcement));
            try {
                announcementService.insert(announcement);
            } catch (DuplicateKeyException e) {
                log.trace("初始化新期开奖公告数据异常,主键冲突", e);
                announcementService.updateByPrimaryKey(announcement);
            }
            log.info("完成初始化新期开奖公告数据");
        } catch (Exception e) {
            log.info("完成初始化新期开奖公告数据异常", e);
        }
    }

    /**
     * 初始化销售数据记录
     * @param gameCode
     * @param newPeriodNum
     */
    public void initSaleRptRecords(String gameCode, String newPeriodNum) {
        log.info("开始初始化销售文件记录.");
        Map<String, ParaProvinceInfo> provinces = provinceInfoCache.getProvincesNotHasCWL();
        for (Map.Entry<String, ParaProvinceInfo> entry : provinces.entrySet()) {
            ParaProvinceInfo provinceInfo = entry.getValue();
            if (provinceInfo.getGameSupport().contains(gameInfoCache.getGameName(gameCode))) {
                LttoProvinceSalesData data = new LttoProvinceSalesData();
                data.setGameCode(gameCode);
                data.setProvinceId(provinceInfo.getProvinceId());
                data.setPeriodNum(newPeriodNum);
                data.setDataStatus(Status.UploadStatus.NOT_UPLOADED);
                try {
                    lttoProvinceSalesDataService.insert(data);
                } catch (DuplicateKeyException e) {
                    log.debug("初始化ftpSaleData记录异常,记录已经存在", e);
                }

                LttoProvinceSalesDataRT dataRT = new LttoProvinceSalesDataRT();
                BeanCopyUtils.copyProperties(data, dataRT);
                try {
                    lttoProvinceSalesDataRTService.insert(dataRT);
                } catch (DuplicateKeyException e) {
                    log.debug("初始化rtSaleData记录异常,记录已经存在", e);
                }
            }
        }
        log.info("初始化销售文件记录完成.");
    }

    /**
     * 初始化弃奖数据记录
     *
     * @param gameCode
     * @param newPeriodNum
     */

    public void initOverdueRptRecords(String gameCode, String newPeriodNum) {
        Map<String, ParaProvinceInfo> provinces = provinceInfoCache.getProvincesNotHasCWL();
        for (Map.Entry<String, ParaProvinceInfo> entry : provinces.entrySet()) {
            ParaProvinceInfo provinceInfo = entry.getValue();
            if (provinceInfo.getGameSupport().contains(gameInfoCache.getGameName(gameCode))) {
                LttoCancelWinStatData data = new LttoCancelWinStatData();
                data.setGameCode(gameCode);
                data.setProvinceId(provinceInfo.getProvinceId());
                data.setPeriodNum(newPeriodNum);
                data.setDataStatus(Status.UploadStatus.NOT_UPLOADED);
                try {
                    lttoCancelWinStatDataService.insert(data);
                } catch (Exception e) {
                    log.debug("初始化OverdueRpt记录异常,记录已经存在", e);
                }
                LttoCancelwinStatDataRT dataRT = new LttoCancelwinStatDataRT();
                BeanCopyUtils.copyProperties(data, dataRT);
                try {
                    lttoCancelwinStatDataRTService.insert(dataRT);
                } catch (Exception e) {
                    log.debug("初始化OverdueRt记录异常,记录已经存在", e);
                }
            }
        }

    }

    /**
     * 初始化中奖数据记录
     *
     * @param gameCode
     * @param newPeriodNum
     */
    public void initWinRptRecords(String gameCode, String newPeriodNum) {
        Map<String, ParaProvinceInfo> provinces = provinceInfoCache.getProvincesNotHasCWL();
        for (Map.Entry<String, ParaProvinceInfo> entry : provinces.entrySet()) {
            ParaProvinceInfo provinceInfo = entry.getValue();
            if (provinceInfo.getGameSupport().contains(gameInfoCache.getGameName(gameCode))) {
                LttoWinstatData data = new LttoWinstatData();
                data.setGameCode(gameCode);
                data.setProvinceId(provinceInfo.getProvinceId());
                data.setPeriodNum(newPeriodNum);
                data.setDataStatus(Status.UploadStatus.NOT_UPLOADED);
                try {
                    lottWinstatDataService.insert(data);
                } catch (Exception e) {
                    log.debug("初始化WinRpt记录异常,记录已经存在", e);
                }
                LttoWinstatDataRT dataRT = new LttoWinstatDataRT();
                BeanCopyUtils.copyProperties(data, dataRT);
                try {
                    lttoWinstatDataRTService.insert(dataRT);
                } catch (Exception e) {
                    log.debug("初始化WinRt记录异常,记录已经存在", e);
                }
            }
        }

    }

    /**
     * 初始化销售明细数据记录
     *
     * @param gameCode
     * @param newPeriodNum
     */
    public void initSaleZipRecords(String gameCode, String newPeriodNum) {
        Map<String, ParaProvinceInfo> provinces = provinceInfoCache.getProvincesNotHasCWL();
        for (Map.Entry<String, ParaProvinceInfo> entry : provinces.entrySet()) {
            ParaProvinceInfo provinceInfo = entry.getValue();
            if (provinceInfo.getGameSupport().contains(gameInfoCache.getGameName(gameCode))) {
                LttoProvinceFileStatus data = new LttoProvinceFileStatus();
                data.setGameCode(gameCode);
                data.setProvinceId(provinceInfo.getProvinceId());
                data.setPeriodNum(newPeriodNum);
                data.setUploadStatus(Status.UploadStatus.NOT_UPLOADED);
                try {
                    lttoProvinceFileStatusService.insert(data);
                } catch (Exception e) {
                    log.debug("初始化SaleZip记录异常,记录已经存在", e);
                }
            }
        }

    }

}
