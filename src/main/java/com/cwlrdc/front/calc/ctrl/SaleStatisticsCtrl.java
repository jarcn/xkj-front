package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditLoto;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditSlto;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditLotoRT;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditSltoRT;
import com.cwlrdc.front.calc.CalculateManager;
import com.cwlrdc.front.calc.CalculateManagerRT;
import com.cwlrdc.front.calc.bean.SalesStatReqBean;
import com.cwlrdc.front.calc.bean.SalesStatRespBean;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.GameCode;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.ltto.service.LttoSalesAuditLotoService;
import com.cwlrdc.front.ltto.service.LttoSalesAuditSltoService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.cwlrdc.front.rt.service.LttoSalesAuditLotoRTService;
import com.cwlrdc.front.rt.service.LttoSalesAuditSltoRTService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 销量统计(销量稽核按钮调用)
 * Created by chenjia on 2017/4/19.
 */
@Slf4j
@Controller
public class SaleStatisticsCtrl {

    @Resource
    private LttoProvinceFileStatusService lttoProvinceFileStatusService;
    @Resource
    private LttoSalesAuditLotoService salesAuditLotoService;
    @Resource
    private LttoSalesAuditSltoService salesAuditSltoService;
    @Resource
    private ParaProvinceInfoService provinceInfoService;
    @Resource
    private LttoSalesAuditLotoRTService rtsalesAuditLotoService;
    @Resource
    private LttoSalesAuditSltoRTService rtsalesAuditSltoService;
    @Resource
    private ProvinceInfoCache provinceInfoCache;
    @Resource
    private OperatorsLogManager operatorsLogManager;

    /**
     * 触发所有省的销量统计
     *
     * @param gameCode
     * @param periodNum
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/CwlAuditApi/rdti/saleStat/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo salesStatistics(@PathVariable String gameCode, @PathVariable String periodNum) {
        try {
            long start = System.currentTimeMillis();
            log.debug("[开奖稽核系统]触发游戏:{}期号:{}销量汇总任务", gameCode, periodNum);
            //查询文件上传状态表 确保销售明细文件都已经上传
            List<LttoProvinceFileStatus> list = lttoProvinceFileStatusService.select2Infos(periodNum, gameCode);
            List<ParaProvinceInfo> provinceInfos = provinceInfoService.findAll();
            if (!CommonUtils.isEmpty(provinceInfos)) {
                for (ParaProvinceInfo pinfo : provinceInfos) {
                    if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(pinfo.getProvinceId())) {
                        if (Constant.Model.ZIP_FILE_FTP.equals(pinfo.getIsFtp())) {
                            this.ftpSaleStatistics(list, pinfo.getProvinceId());
                        } else {
                            this.rtSaleStatistics(list, pinfo.getProvinceId());
                        }
                    }
                }
            } else {
                log.warn("未配置省码信息");
                return ReturnInfo.Faild;
            }
            //查询数据计算结果
            log.info(operatorsLogManager.getLogInfo("销售稽核", "销量核对", start));
            return this.selectResult(gameCode, periodNum);
        } catch (Exception e) {
            log.info("销售稽核异常", e);
            return ReturnInfo.Faild;
        }
    }

    /**
     * 指定省份销售统计触发
     * @param reqBean
     * @param resp
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/CwlAuditApi/rdti/saleStat/byId", method = RequestMethod.POST)
    public ReturnInfo salesStatByProvId(@RequestBody SalesStatReqBean reqBean, HttpServletResponse resp) {
        ReturnInfo info = new ReturnInfo(true);
        String provinceId = reqBean.getProvinceId();
        String gameCode = reqBean.getGameCode();
        String periodNum = reqBean.getPeriodNum();
        if (StringUtils.isBlank(provinceId) || StringUtils.isBlank(gameCode) || StringUtils.isBlank(periodNum)) {
            return new ReturnInfo("参数错误", false);
        }
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(provinceId);
        if (Constant.Model.ZIP_FILE_FTP.equals(provinceInfo.getIsFtp())) {
            LttoProvinceFileStatus fileStatus = lttoProvinceFileStatusService.select2key(periodNum, gameCode, provinceId);
            if (null == fileStatus || !Status.UploadStatus.UPLOADED_SUCCESS.equals(fileStatus.getUploadStatus())) {
                return new ReturnInfo("文件未收集", false);
            }
            reqBean.setFilePath(fileStatus.getFilePath());
            log.debug("[新开奖]调用[数美销量汇总FTP接口] 请求参数:{}", JsonUtil.bean2JsonString(reqBean));
            SalesStatRespBean result = CalculateManager.salesStatistics(reqBean);
            log.debug("[新开奖]调用[数美销量汇总FTP接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
        } else {
            log.debug("[新开奖]调用[数美销量汇总RT接口] 请求参数:{}", JsonUtil.bean2JsonString(reqBean));
            SalesStatRespBean result = CalculateManagerRT.salesStatistics(reqBean);
            log.debug("[新开奖]调用[数美销量汇总RT接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
        }
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
            if (Constant.Model.ZIP_FILE_FTP.equals(provinceInfo.getIsFtp())) {
                LttoSalesAuditSlto auditSlto = salesAuditSltoService.selectByKey(provinceId, periodNum);
                info.setRetObj(auditSlto);
            } else {
                LttoSalesAuditSltoRT rtAuditSlto = this.rtSltoCalResult(provinceInfo, periodNum);
                info.setRetObj(rtAuditSlto);
            }
        } else {
            if (Constant.Model.ZIP_FILE_FTP.equals(provinceInfo.getIsFtp())) {
                LttoSalesAuditLoto auditLoto = salesAuditLotoService.selectByKey(provinceId, periodNum);
                info.setRetObj(auditLoto);
            } else {
                LttoSalesAuditLotoRT rtAuditLoto = this.rtLotoCalResult(provinceInfo, periodNum);
                info.setRetObj(rtAuditLoto);
            }
        }
        return info;
    }


    /**
     * 刷新统计状态
     * 直接查询数据库
     */
    @ResponseBody
    @RequestMapping(value = "/refreshsalestat/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo refreshSaleStatis(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request) {
        if (StringUtils.isBlank(gameCode) || StringUtils.isBlank(periodNum)) {
            return new ReturnInfo("参数错误", false);
        }
        return selectResult(gameCode, periodNum);
    }

    /**
     * 销售统计FTP接口
     * @param list
     * @param provinceId
     */
    private void ftpSaleStatistics(List<LttoProvinceFileStatus> list, String provinceId) {
        if (!CommonUtils.isEmpty(list)) {
            //销售明细文件收集完成  数美接口使用异步调用
            for (LttoProvinceFileStatus bean : list) {
                if (provinceId.equalsIgnoreCase(bean.getProvinceId())) {
                    SalesStatReqBean requestBean = new SalesStatReqBean();
                    BeanCopyUtils.copyProperties(bean, requestBean);
                    if (StringUtils.isBlank(requestBean.getFilePath())) {
                        log.warn("[{}]省销售明细文件[{}]错误", provinceId, bean.getFilePath());
                        continue;
                    }
                    if (new File(bean.getFilePath()).exists()) {
                        if (!isCalacted(provinceId, bean.getGameCode(), bean.getPeriodNum())) {
                            log.debug("[新开奖]调用[数美销量汇总FTP接口] 请求参数:{}", JsonUtil.bean2JsonString(requestBean));
                            SalesStatRespBean result = CalculateManager.salesStatistics(requestBean);
                            log.debug("[新开奖]调用[数美销量汇总FTP接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
                        }
                    } else {
                        log.warn("销售明细文件[{}]不存在", requestBean.getFilePath());
                    }
                }
            }
        }
    }


    /**
     * 销量统计实时接口
     * @param list
     * @param provinceId
     */
    private void rtSaleStatistics(List<LttoProvinceFileStatus> list, String provinceId){
        try {
            if (!CommonUtils.isEmpty(list)) {
                //销售明细文件收集完成  数美接口使用异步调用
                for (LttoProvinceFileStatus bean : list) {
                    if (provinceId.equalsIgnoreCase(bean.getProvinceId())) {
                        //TODO 查询数美处理状态,只调计算未处理的份
                        if (!this.hasCaled(bean.getGameCode(), bean.getPeriodNum(),provinceId)){
                            SalesStatReqBean requestBean = new SalesStatReqBean();
                            BeanCopyUtils.copyProperties(bean, requestBean);
                            log.debug("[新开奖]调用[数美销量汇总RT接口] 请求参数:{}", JsonUtil.bean2JsonString(requestBean));
                            SalesStatRespBean result = CalculateManagerRT.salesStatistics(requestBean);
                            log.debug("[新开奖]调用[数美销量汇总RT接口] 响应参数:{}", JsonUtil.bean2JsonString(result));
                        }else {
                            log.debug("省[]实时接口销量汇总已计算",provinceId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("销量汇总实时接口调用异常",e);
        }
    }

    /**
     * 判断数美是否已经计算过
     * @param gameCode
     * @param periodNum
     * @param provinceId
     * @return
     */
    private boolean hasCaled(String gameCode, String periodNum, String provinceId){
        if(GameCode.GAME_CODE_SLTO.equals(gameCode)){
            LttoSalesAuditSltoRT lttoSalesAuditSltoRT =
                rtsalesAuditSltoService.selectByKey(provinceId, periodNum);
            if (null != lttoSalesAuditSltoRT){
                Integer processStatus = lttoSalesAuditSltoRT.getProcessStatus();
                if (Constant.Status.TASK_RUN_COMPLETE_2.equals(processStatus)){
                    return true;
                }
            }
        }else {
            LttoSalesAuditLotoRT lttoSalesAuditLoto =
                rtsalesAuditLotoService.selectByKey(provinceId, periodNum);
            if (null != lttoSalesAuditLoto){
                Integer processStatus = lttoSalesAuditLoto.getProcessStatus();
                if (Constant.Status.TASK_RUN_COMPLETE_2.equals(processStatus)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 查询数美销量统计结果
     *
     * @param gameCode
     * @param periodNum
     * @return
     */
    private ReturnInfo selectResult(String gameCode, String periodNum) {
        ReturnInfo info = new ReturnInfo();
        List<LttoSalesAuditSlto> sltoResults = new ArrayList<>();
        List<LttoSalesAuditLoto> lotoResults = new ArrayList<>();
        List<ParaProvinceInfo> provinceInfos = provinceInfoService.findAll();
        //查询数据计算结果
        for (ParaProvinceInfo pinfo : provinceInfos) {
            if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(pinfo.getProvinceId())) {
                if (Constant.Model.ZIP_FILE_FTP.equals(pinfo.getIsFtp())) {
                    //查询并返回数美计算结果
                    if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                        LttoSalesAuditSlto result = this.ftpSltoCalResult(pinfo, periodNum);
                        sltoResults.add(result);
                    } else {
                        LttoSalesAuditLoto result = this.ftpLotoCalResult(pinfo, periodNum);
                        lotoResults.add(result);
                    }
                } else {
                    if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                        LttoSalesAuditSltoRT rt = this.rtSltoCalResult(pinfo, periodNum);
                        LttoSalesAuditSlto result = new LttoSalesAuditSlto();
                        if (rt != null && Constant.Status.TASK_RUN_COMPLETE_2.equals(rt.getProcessStatus())){
                            BeanCopyUtils.copyProperties(rt, result);
                            log.debug("游戏[{}]期[{}]省[{}]实时统计结果计算已完成",gameCode,periodNum,pinfo.getProvinceId());
                        }else{
                            log.debug("游戏[{}]期[{}]省[{}]实时统计结果计算未完成",gameCode,periodNum,pinfo.getProvinceId());
                        }
                        sltoResults.add(result);
                    } else {
                        LttoSalesAuditLotoRT rt = this.rtLotoCalResult(pinfo, periodNum);
                        LttoSalesAuditLoto result = new LttoSalesAuditLoto();
                        if (rt != null && Constant.Status.TASK_RUN_COMPLETE_2.equals(rt.getProcessStatus())){
                            BeanCopyUtils.copyProperties(rt, result);
                            log.debug("游戏[{}]期[{}]省[{}]实时统计结果计算已完成",gameCode,periodNum,pinfo.getProvinceId());
                        }else{
                            log.debug("游戏[{}]期[{}]省[{}]实时统计结果计算未完成",gameCode,periodNum,pinfo.getProvinceId());
                        }
                        lotoResults.add(result);
                    }
                }
            }
            if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                if (sltoResults.size() > 0) {
                    info.setSuccess(true);
                } else {
                    info.setSuccess(false);
                }
                info.setRetObj(sltoResults);
            } else {
                if (lotoResults.size() > 0) {
                    info.setSuccess(true);
                } else {
                    info.setSuccess(false);
                }
                info.setRetObj(lotoResults);
            }
        }

        return info;
    }

    //判断省销量汇总是否已经计算过
    private boolean isCalacted(String provinceId, String gameCode, String periodNum) {
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(provinceId);
        if(Constant.Model.ZIP_FILE_FTP.equals(provinceInfo.getIsFtp())){
            if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                LttoSalesAuditSlto auditSlto = salesAuditSltoService.selectByKey(provinceId, periodNum);
                if (null != auditSlto && Constant.Status.TASK_RUN_COMPLETE_2.equals(auditSlto.getProcessStatus())) {
                    return true;
                }
            } else {
                LttoSalesAuditLoto auditLoto = salesAuditLotoService.selectByKey(provinceId, periodNum);
                if (null != auditLoto && Constant.Status.TASK_RUN_COMPLETE_2.equals(auditLoto.getProcessStatus())) {
                    return true;
                }
            }
        }else{
            if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                LttoSalesAuditSltoRT auditSlto = rtsalesAuditSltoService.selectByKey(provinceId, periodNum);
                if (null != auditSlto && Constant.Status.TASK_RUN_COMPLETE_2.equals(auditSlto.getProcessStatus())) {
                    return true;
                }
            } else {
                LttoSalesAuditLotoRT auditLoto = rtsalesAuditLotoService.selectByKey(provinceId, periodNum);
                if (null != auditLoto && Constant.Status.TASK_RUN_COMPLETE_2.equals(auditLoto.getProcessStatus())) {
                    return true;
                }
            }
        }
        return false;
    }


    private LttoSalesAuditSlto ftpSltoCalResult(ParaProvinceInfo pinfo, String periodNum) {
        return salesAuditSltoService.selectByKey(pinfo.getProvinceId(), periodNum);
    }

    private LttoSalesAuditLoto ftpLotoCalResult(ParaProvinceInfo pinfo, String periodNum) {
        return salesAuditLotoService.selectByKey(pinfo.getProvinceId(), periodNum);
    }

    private LttoSalesAuditSltoRT rtSltoCalResult(ParaProvinceInfo pinfo, String periodNum) {
        return rtsalesAuditSltoService.selectByKey(pinfo.getProvinceId(), periodNum);
    }

    private LttoSalesAuditLotoRT rtLotoCalResult(ParaProvinceInfo pinfo, String periodNum) {
        return rtsalesAuditLotoService.selectByKey(pinfo.getProvinceId(), periodNum);
    }

}
