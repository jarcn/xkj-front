package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrieval;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRT;
import com.cwlrdc.front.calc.CalculateManager;
import com.cwlrdc.front.calc.CalculateManagerRT;
import com.cwlrdc.front.calc.bean.LttoNewWinningRetrieval;
import com.cwlrdc.front.calc.bean.RetrieveReqBean;
import com.cwlrdc.front.calc.bean.RetrieveRespBean;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.ltto.service.LttoWinningRetrievalService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.rt.service.LttoWinningRetrievalRTService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;


/**
 * 中奖检索操作
 * Created by chenjia on 2017/4/19.
 */
@Slf4j
@Controller
public class WinRetrievalCtrl {

    @Resource
    private LttoProvinceFileStatusService lttoProvinceFileStatusService;
    @Resource
    private ParaGamePeriodInfoService paraGamePeriodInfoService;
    @Resource
    private LttoWinningRetrievalService winningRetrievalService;
    @Resource
    private LttoWinningRetrievalRTService winningRetrievalRTService;
    @Autowired
    private ProvinceInfoCache provinceInfoCache;
    @Resource
    private PromotionManager promotionManager;
    @Resource
    private OperatorsLogManager operatorsLogManager;

    /**
     * 检索所有省份销量明细数据
     *
     * @param gameCode
     * @param periodNum
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/CwlAuditApi/rdti/winRetrieval/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo winRetrieval(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request) {
        try {
            long start = System.currentTimeMillis();
            String winNum = this.getCurWinNum(gameCode, periodNum);
            if (StringUtils.isBlank(winNum)) {
                String desc = String.format("游戏[%s]期号[%s]没有开奖号码", gameCode, periodNum);
                log.warn(desc);
                return new ReturnInfo(desc, false);
            }
            log.debug("[开奖稽核系统]开始触发游戏:[{}]期号:[{}]开奖号码:[{}]中奖检索任务", gameCode, periodNum, winNum);
            List<LttoProvinceFileStatus> saleDataList = lttoProvinceFileStatusService.select2Infos(periodNum, gameCode);
            if (CommonUtils.isEmpty(saleDataList)) {
                String desc = String.format("游戏[%s]期号[%s]明细文件未下载", gameCode, periodNum);
                log.warn(desc);
                return new ReturnInfo(desc, false);
            }
            List<ParaProvinceInfo> provinceInfos = provinceInfoCache.getProvincesNotHasCWLList();
            if (CommonUtils.isEmpty(provinceInfos)) {
                String desc = "省份初始化信息错误";
                log.warn(desc);
                return new ReturnInfo(desc, false);
            }
            for (ParaProvinceInfo pinfo : provinceInfos) {
                if (Constant.Model.ZIP_FILE_FTP.equals(pinfo.getIsFtp())) {
                    this.ftpWinRetrieval(saleDataList, pinfo.getProvinceId(), winNum);
                } else {
                    this.rtWinRetrieval(saleDataList, pinfo.getProvinceId(), winNum);
                }
            }
            List<LttoNewWinningRetrieval> allTickets = this.getAllTickets(gameCode, periodNum);
            ReturnInfo returnInfo = new ReturnInfo(true);
            returnInfo.setRetObj(allTickets);
            log.info(operatorsLogManager.getLogInfo("中奖检索", "中奖检索", start));
            return returnInfo;
        } catch (Exception e) {
            log.warn("触发所有的明细文件进行中奖检索操作异常", e);
            return new ReturnInfo("处理错误,请联系运维人员", false);
        }
    }

    /**
     * 检索指定省码的销售明细文件
     *
     * @param retrieveReqBean
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/CwlAuditApi/rdti/retrievalbyid", method = RequestMethod.POST)
    public ReturnInfo winRetrievalById(@RequestBody RetrieveReqBean retrieveReqBean, HttpServletRequest request) {
        String gameCode = retrieveReqBean.getGameCode();
        String periodNum = retrieveReqBean.getPeriodNum();
        String provinceId = retrieveReqBean.getProvinceId();
        String winNum = this.getCurWinNum(gameCode, periodNum);
        if (StringUtils.isBlank(winNum)) {
            return new ReturnInfo("中奖号码不存在", false);
        }
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(provinceId);
        if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
            LttoProvinceFileStatus fileStatus = lttoProvinceFileStatusService.select2key(periodNum, gameCode, provinceId);
            if (StringUtils.isBlank(fileStatus.getFilePath())) {
                return new ReturnInfo("销量明细文件不存在", false);
            }
            RetrieveReqBean retrieveReqPara = this.createRetrieveReqPara(fileStatus, winNum);
            log.debug("[开奖稽核系统]开始触发游戏:[{}]期号:[{}]开奖号码:[{}]FTP模式中奖检索任务", gameCode, periodNum, winNum);
            RetrieveRespBean result = CalculateManager.winningRetrieval(retrieveReqPara);
            log.debug("[开奖稽核系统]完成触发游戏:[{}]期号:[{}]开奖号码:[{}]FTP模式中奖检索任务", gameCode, periodNum, winNum);
            return new ReturnInfo(result.getErrorMsg(), true);
        } else {
            log.debug("[开奖稽核系统]开始触发游戏:[{}]期号:[{}]开奖号码:[{}]RT模式中奖检索任务", gameCode, periodNum, winNum);
            LttoProvinceFileStatus fileStatus = lttoProvinceFileStatusService.select2key(periodNum, gameCode, provinceId);
            RetrieveReqBean retrieveReqPara = this.createRetrieveReqPara(fileStatus, winNum);
            RetrieveRespBean result = CalculateManagerRT.winningRetrieval(retrieveReqPara);
            log.debug("[开奖稽核系统]完成触发游戏:[{}]期号:[{}]开奖号码:[{}]RT模式中奖检索任务", gameCode, periodNum, winNum);
            return new ReturnInfo(result.getErrorMsg(), true);
        }
    }


    /**
     * FTP中奖检索接口
     *
     * @param saleDataList
     * @param provinceId
     * @param winNum
     */
    private void ftpWinRetrieval(List<LttoProvinceFileStatus> saleDataList, String provinceId, String winNum) {
        for (LttoProvinceFileStatus bean : saleDataList) {
            if (provinceId.equalsIgnoreCase(bean.getProvinceId())) {
                if (StringUtils.isNotBlank(bean.getFilePath())) {
                    RetrieveReqBean requestBean = this.createRetrieveReqPara(bean, winNum);
                    if (new File(requestBean.getFilePath()).exists()) {
                        if (!isRetrievaled(bean.getGameCode(), provinceId, bean.getPeriodNum())) {
                            log.debug("[新开奖]调用[数美FTP中奖检索接口] 请求参数:{}", JsonUtil.bean2JsonString(requestBean));
                            RetrieveRespBean result = CalculateManager.winningRetrieval(requestBean);
                            log.debug("[开奖稽核系统]调用[数美FTP中奖检索接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
                        }

                    }
                }
            }
        }
    }

    /**
     * 中奖检索实时接口
     *
     * @param saleDataList
     * @param provinceId
     * @param winNum
     */
    private void rtWinRetrieval(List<LttoProvinceFileStatus> saleDataList, String provinceId, String winNum) {
        try {
            if (!CommonUtils.isEmpty(saleDataList)) {
                for (LttoProvinceFileStatus bean : saleDataList) {
                    if (provinceId.equalsIgnoreCase(bean.getProvinceId())) {
                        RetrieveReqBean requestBean = this.createRetrieveReqPara(bean, winNum);
                        log.debug("[新开奖]调用[数美RT中奖检索接口] 请求参数:{}", JsonUtil.bean2JsonString(requestBean));
                        RetrieveRespBean result = CalculateManagerRT.winningRetrieval(requestBean);
                        log.debug("[开奖稽核系统]调用[数美RT中奖检索接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
                    }
                }
            }
        } catch (Exception e) {
            log.debug("中奖检索调用实时接口异常", e);
        }
    }

    /**
     * 创建中奖检索请求参数
     *
     * @param bean
     * @param winNum
     * @return
     */
    private RetrieveReqBean createRetrieveReqPara(LttoProvinceFileStatus bean, String winNum) {
        RetrieveReqBean requestBean = new RetrieveReqBean();
        String periodNum = bean.getPeriodNum();
        BeanCopyUtils.copyProperties(bean, requestBean);
        requestBean.setWinNum(winNum);
        requestBean.setFilePath(bean.getFilePath());
        if (Constant.GameCode.GAME_CODE_SLTO.equals(bean.getGameCode())) {
            boolean promotion1 = promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum);
            boolean promotion6 = promotionManager.isPromotionCurr(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
            if (promotion1 && promotion6) {
                requestBean.setPromotionCode(Constant.PromotionCode.BOTH_PROMOTION);
            }
            if (promotion1 && !promotion6) {
                requestBean.setPromotionCode(Constant.PromotionCode.PRIZE1_PROMOTION);
            }
            if (!promotion1 && promotion6) {
                requestBean.setPromotionCode(Constant.PromotionCode.PRIZE6_PROMOTION);
            }
            if (!promotion1 && !promotion6) {
                requestBean.setPromotionCode(Constant.PromotionCode.NO_PROMOTION);
            }
        } else {
            requestBean.setPromotionCode(Constant.PromotionCode.NO_PROMOTION);
        }

        return requestBean;
    }


    private List<LttoNewWinningRetrieval> getAllTickets(String gameCode, String periodNum) {
        List<LttoNewWinningRetrieval> results = new ArrayList<>();
        List<LttoWinningRetrieval> lttoWinningRetrievals = this.queryWinRetrievals(gameCode, periodNum);
        Map<String, LttoNewWinningRetrieval> winningResult = new HashMap<>();
        for (LttoWinningRetrieval info : lttoWinningRetrievals) {
            String key = this.genKey(info.getGameCode(), info.getPeriodNum(), info.getProvinceId());

            LttoNewWinningRetrieval lttoNewWinningRetrieval = winningResult.get(key);
            if (lttoNewWinningRetrieval == null) {
                lttoNewWinningRetrieval = new LttoNewWinningRetrieval();
                lttoNewWinningRetrieval.setProvinceId(info.getProvinceId());
                lttoNewWinningRetrieval.setPeriodNum(info.getPeriodNum());
                lttoNewWinningRetrieval.setGameCode(info.getGameCode());
            }

            if (info.getTicketType() != null && info.getTicketType().equals(Status.TicketType.EFFECT)) {
                lttoNewWinningRetrieval.setPrize1Count(info.getPrize1Count());
                lttoNewWinningRetrieval.setPrize2Count(info.getPrize2Count());
                lttoNewWinningRetrieval.setPrize3Count(info.getPrize3Count());
                lttoNewWinningRetrieval.setPrize4Count(info.getPrize4Count());
                lttoNewWinningRetrieval.setPrize5Count(info.getPrize5Count());
                lttoNewWinningRetrieval.setPrize6Count(info.getPrize6Count());
                lttoNewWinningRetrieval.setPrize7Count(info.getPrize7Count());
                lttoNewWinningRetrieval.setPrize8Count(info.getPrize8Count());
                lttoNewWinningRetrieval.setPrize9Count(info.getPrize9Count());
                lttoNewWinningRetrieval.setPrize10Count(info.getPrize10Count());
            }

            if (info.getTicketType() != null && info.getTicketType().equals(Status.TicketType.CANCEL)) {
                lttoNewWinningRetrieval.setPrize11Count(info.getPrize1Count());
                lttoNewWinningRetrieval.setPrize12Count(info.getPrize2Count());
                lttoNewWinningRetrieval.setPrize13Count(info.getPrize3Count());
                lttoNewWinningRetrieval.setPrize14Count(info.getPrize4Count());
                lttoNewWinningRetrieval.setPrize15Count(info.getPrize5Count());
                lttoNewWinningRetrieval.setPrize16Count(info.getPrize6Count());
                lttoNewWinningRetrieval.setPrize17Count(info.getPrize7Count());
                lttoNewWinningRetrieval.setPrize18Count(info.getPrize8Count());
                lttoNewWinningRetrieval.setPrize19Count(info.getPrize9Count());
                lttoNewWinningRetrieval.setPrize20Count(info.getPrize10Count());
            }

            if (info.getTicketType() != null && info.getTicketType().equals(Status.TicketType.DAMAGE)) {
                lttoNewWinningRetrieval.setPrize21Count(info.getPrize1Count());
                lttoNewWinningRetrieval.setPrize22Count(info.getPrize2Count());
                lttoNewWinningRetrieval.setPrize23Count(info.getPrize3Count());
                lttoNewWinningRetrieval.setPrize24Count(info.getPrize4Count());
                lttoNewWinningRetrieval.setPrize25Count(info.getPrize5Count());
                lttoNewWinningRetrieval.setPrize26Count(info.getPrize6Count());
                lttoNewWinningRetrieval.setPrize27Count(info.getPrize7Count());
                lttoNewWinningRetrieval.setPrize28Count(info.getPrize8Count());
                lttoNewWinningRetrieval.setPrize29Count(info.getPrize9Count());
                lttoNewWinningRetrieval.setPrize30Count(info.getPrize10Count());
            }

            winningResult.put(key, lttoNewWinningRetrieval);
        }
        results.addAll(winningResult.values());
        return results;
    }

    private List<LttoWinningRetrieval> queryWinRetrievals(String gameCode, String periodNum) {
        List<Integer> types = new ArrayList<>();
        types.add(Status.TicketType.EFFECT);
        types.add(Status.TicketType.CANCEL);
        types.add(Status.TicketType.DAMAGE);
        List<LttoWinningRetrieval> lttoWinningRetrievals = new ArrayList<>();
        List<String> ftpTypeProvinces = provinceInfoCache.getFtpTypeProvinces();
        if (!CommonUtils.isEmpty(ftpTypeProvinces)) {
            List<LttoWinningRetrieval> ftpWinningRetrievals = winningRetrievalService.selec2datas(gameCode, periodNum, types, ftpTypeProvinces);
            lttoWinningRetrievals.addAll(ftpWinningRetrievals);
        }
        List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
        if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
            List<LttoWinningRetrievalRT> rtWinningRetrievals = winningRetrievalRTService.selec2datas(gameCode, periodNum, types, realTimeTypeProvinces);
            for (LttoWinningRetrievalRT rtData : rtWinningRetrievals) {
                LttoWinningRetrieval ftpWinRetrieval = new LttoWinningRetrieval();
                BeanCopyUtils.copyProperties(rtData, ftpWinRetrieval);
                lttoWinningRetrievals.add(ftpWinRetrieval);
            }
        }
        return lttoWinningRetrievals;
    }

    private String genKey(String gameCode, String periodNum, String provinceId) {
        return gameCode + "_" + periodNum + "_" + provinceId;
    }

    //刷新中奖检索状态
    @ResponseBody
    @RequestMapping(value = "/refresh/retrieval/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo refreshAll(@PathVariable String gameCode, @PathVariable String periodNum) {
        try {
            ReturnInfo info = new ReturnInfo(false);
            List<LttoNewWinningRetrieval> allTickets = this.getAllTickets(gameCode, periodNum);
            if (CommonUtils.isNotEmpty(allTickets)) {
                Collections.sort(allTickets);
                info.setRetObj(allTickets);
                info.setSuccess(true);
            } else {
                info.setSuccess(false);
            }
            return info;
        } catch (Exception e) {
            log.warn("刷新中奖检索状态异常", e);
            return new ReturnInfo("刷新中奖检索状态错误", false);
        }
    }

    //查询中奖号码
    @RequestMapping(value = "/queryWinNum/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryWinNum(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request) {
        ReturnInfo returnInfo = new ReturnInfo();
        ParaGamePeriodInfo paraInfo = paraGamePeriodInfoService.queryWinNum(gameCode, periodNum);
        if (paraInfo != null) {
            returnInfo.setRetObj(paraInfo);
            returnInfo.setSuccess(true);
        } else {
            returnInfo.setSuccess(false);
            returnInfo.setDescription("未开奖");
        }
        return returnInfo;
    }

    //获取当期开奖号码
    private String getCurWinNum(String gameCode, String curPeriodNum) {
        try {
            ParaGamePeriodInfo winNumInfo = paraGamePeriodInfoService.queryWinNum(gameCode, curPeriodNum);
            if (null != winNumInfo && StringUtils.isNotBlank(winNumInfo.getWinNum())) {
                String winNumInfoWinNum = winNumInfo.getWinNum();
                return FileUtils.getCurrWinNum(gameCode, winNumInfoWinNum);
            } else {
                return "";
            }
        } catch (Exception e) {
            log.debug("获取当期开奖号码异常", e);
            return "";
        }
    }

    //判断是否已经计算过
    private boolean isRetrievaled(String gameCode, String provinceId, String periodNum) {
        boolean status = false;
        List<LttoWinningRetrieval> list = winningRetrievalService.selectbyKey(gameCode, provinceId, periodNum);
        if (!CommonUtils.isEmpty(list)) {
            for (LttoWinningRetrieval bean : list) {
                if (null != bean && Constant.Status.TASK_RUN_COMPLETE_2.equals(bean.getProcessStatus())
                        && !Constant.Status.TASK_RUN_RUNNING_1.equals(bean.getProcessStatus())) {
                    status = true;
                } else {
                    status = false;
                }
            }
        }
        return status;
    }

}
