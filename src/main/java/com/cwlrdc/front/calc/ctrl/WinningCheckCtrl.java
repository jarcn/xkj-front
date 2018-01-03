package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrieval;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRT;
import com.cwlrdc.front.calc.bean.UnCompTicketBean;
import com.cwlrdc.front.calc.bean.WinDataCheckBean;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoWinningRetrievalService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.rt.service.LttoWinningRetrievalRTService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.CommonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 中奖结果核对
 * Created by chenjia on 2017/5/5.
 */
@Controller
public class WinningCheckCtrl {

    @Resource
    private LttoWinstatDataService lottWinstatDataService; //省中心上传文件
    @Resource
    private LttoWinningRetrievalService winningRetrievalService;
    @Resource
    private LttoWinstatDataService winDataService;
    @Resource
    private LttoWinningRetrievalRTService winningRetrievalRTService;
    @Resource
    private ProvinceInfoCache provinceInfoCache;

    /**
     * 中奖结果核对
     *
     * @param gameCode
     * @param periodNum
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/winnigcheck/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo winningCheck(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request) {
        // 查询省中心上报中奖结果
        List<LttoWinstatData> proWinDatas = lottWinstatDataService.select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        //查询数美计算结果
        List<LttoWinningRetrieval> smRetrievlas = this.getSmRetrievlasResult(gameCode, periodNum);
        if (!CommonUtils.isEmpty(proWinDatas) && !CommonUtils.isEmpty(smRetrievlas)) {
            // 核对中奖数据
            List<WinDataCheckBean> resultList = this.compareWinData(proWinDatas, smRetrievlas);
            Collections.sort(resultList);
            return new ReturnInfo("中奖核对结果",0,resultList,true);
        } else {
            return new ReturnInfo("省中心中奖结果文件未汇总或中奖检索动作未执行",false);
        }
    }

    /**
     * 不完整打印票核对
     * @param gameCode
     * @param periodNum
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uncompticket/{gameCode}/{periodNum}", method = RequestMethod.GET)
    public ReturnInfo uncompTicket(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest request) {
        //不完整打印票
        List<LttoWinningRetrieval> nocompls = this.getSmResults(gameCode, periodNum, Constant.File.WIN_RETR_TYPE_INCOMP);
        //有效票
        List<LttoWinningRetrieval> winningEffect = this.getSmResults(gameCode, periodNum, Constant.File.WIN_RETR_TYPE_EFFECT);
        //中奖数据
        List<LttoWinstatData> winDataList = winDataService.select2datas(gameCode, periodNum, Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        if (!CommonUtils.isEmpty(nocompls) && !CommonUtils.isEmpty(winningEffect) && !CommonUtils.isEmpty(winDataList)) {
            List<UnCompTicketBean> ticketBeenList = this.conver2TicketBean(winningEffect, nocompls, winDataList);
            Collections.sort(ticketBeenList);
            return new ReturnInfo("不完整打印票",0,ticketBeenList,true);
        } else {
            return new ReturnInfo("不规范流程操作",false);
        }
    }


    //查询数美计算结果
    private List<LttoWinningRetrieval> getSmRetrievlasResult(String gameCode, String periodNum) {
        List<LttoWinningRetrieval> smResult = new ArrayList<>();
        List<LttoWinningRetrieval> smFtpRetrievlas = winningRetrievalService.selec2winDatas(gameCode, periodNum);
        smResult.addAll(smFtpRetrievlas);
        List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
        if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
            List<LttoWinningRetrievalRT> lttoWinningRetrievalRTS = winningRetrievalRTService.selec2datas(gameCode, periodNum, realTimeTypeProvinces);
            for(LttoWinningRetrievalRT rt:lttoWinningRetrievalRTS){
                LttoWinningRetrieval ftpWinRetr = new LttoWinningRetrieval();
                BeanCopyUtils.copyProperties(rt,ftpWinRetr);
                smResult.add(ftpWinRetr);
            }
        }
        return smResult;
    }

    //数美中奖检索计算结果
    private List<LttoWinningRetrieval> getSmResults(String gameCode,String periodNum,int type){
        List<LttoWinningRetrieval> nocompls = new ArrayList<>();
        List<LttoWinningRetrieval> ftpNoCompls = winningRetrievalService.selec2SmRetrivalRsuts(gameCode, periodNum,type);
        nocompls.addAll(ftpNoCompls);
        List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
        if(!CommonUtils.isEmpty(realTimeTypeProvinces)){
            List<LttoWinningRetrievalRT> lttoWinningRetrievalRTS = winningRetrievalRTService.selec2Results(gameCode, periodNum, realTimeTypeProvinces,type);
            for(LttoWinningRetrievalRT rt:lttoWinningRetrievalRTS){
                LttoWinningRetrieval ftpWinRetr = new LttoWinningRetrieval();
                BeanCopyUtils.copyProperties(rt,ftpWinRetr);
                nocompls.add(ftpWinRetr);
            }
        }
        return nocompls;
    }

    //省上报中奖结果和数美统计中奖结果进行核对
    private List<WinDataCheckBean> compareWinData(List<LttoWinstatData> proWinDatas, List<LttoWinningRetrieval> smWinDatas) {
        List<WinDataCheckBean> beanList = new ArrayList<>();
        WinDataCheckBean checkBean = null;
        for (LttoWinstatData proBean : proWinDatas) { //省上报的中奖结果
            checkBean = new WinDataCheckBean();
            for (LttoWinningRetrieval smBean : smWinDatas) { //数美计算中奖结果
                if (proBean.getProvinceId().equals(smBean.getProvinceId()) && proBean.getGameCode().equals(smBean.getGameCode()) && proBean.getPeriodNum().equals(smBean.getPeriodNum())) {
                    if (Constant.File.WIN_RETR_TYPE_EFFECT.equals(smBean.getTicketType())) { //有效票

                        Integer pro1 = proBean.getPrize1Count().intValue();
                        Integer pro2 = proBean.getPrize2Count().intValue();
                        Integer pro3 = proBean.getPrize3Count().intValue();
                        Integer pro4 = proBean.getPrize4Count().intValue();
                        Integer pro5 = proBean.getPrize5Count().intValue();
                        Integer pro6 = proBean.getPrize6Count().intValue();
                        Integer pro7 = proBean.getPrize7Count().intValue();
                        Integer pro8 = proBean.getPrize8Count().intValue();
                        Integer pro9 = proBean.getPrize9Count().intValue();
                        Integer pro10 = proBean.getPrize10Count().intValue();

                        Integer sm1 = smBean.getPrize1Count();
                        Integer sm2 = smBean.getPrize2Count();
                        Integer sm3 = smBean.getPrize3Count();
                        Integer sm4 = smBean.getPrize4Count();
                        Integer sm5 = smBean.getPrize5Count();
                        Integer sm6 = smBean.getPrize6Count();
                        Integer sm7 = smBean.getPrize7Count();
                        Integer sm8 = smBean.getPrize8Count();
                        Integer sm9 = smBean.getPrize9Count();
                        Integer sm10 = smBean.getPrize10Count();

                        checkBean.setProvinceWin1(pro1);
                        checkBean.setProvinceWin2(pro2);
                        checkBean.setProvinceWin3(pro3);
                        checkBean.setProvinceWin4(pro4);
                        checkBean.setProvinceWin5(pro5);
                        checkBean.setProvinceWin6(pro6);
                        checkBean.setProvinceWin7(pro7);
                        checkBean.setProvinceWin8(pro8);
                        checkBean.setProvinceWin9(pro9);
                        checkBean.setProvinceWin10(pro10);

                        checkBean.setSmWin1(sm1);
                        checkBean.setSmWin2(sm2);
                        checkBean.setSmWin3(sm3);
                        checkBean.setSmWin4(sm4);
                        checkBean.setSmWin5(sm5);
                        checkBean.setSmWin6(sm6);
                        checkBean.setSmWin7(sm7);
                        checkBean.setSmWin8(sm8);
                        checkBean.setSmWin9(sm9);
                        checkBean.setSmWin10(sm10);
                        if (pro1.equals(sm1) && pro2.equals(sm2) && pro3.equals(sm3) && pro4.equals(sm4) && pro5.equals(sm5) && pro6.equals(sm6)
                                && pro7.equals(sm7) && pro8.equals(sm8) && pro9.equals(sm9) && pro10.equals(sm10)) {
                            checkBean.setStatus(Constant.Status.CHCEK_STATUS_RIGHT);
                        } else {
                            checkBean.setStatus(Constant.Status.CHCEK_STATUS_WRONG);
                        }
                        checkBean.setGameCode(proBean.getGameCode());
                        checkBean.setPeriodNum(proBean.getPeriodNum());
                        checkBean.setProvinceId(proBean.getProvinceId());
                        beanList.add(checkBean);
                    }
                }
            }
        }

        List<String> upDataProvinces = this.upDataProvinces(proWinDatas);
        List<String> smCalProvinces = this.smCalProvinces(smWinDatas);
        upDataProvinces.removeAll(smCalProvinces);
        for (LttoWinstatData bean : proWinDatas) {
            for (String str : upDataProvinces) {
                if (str.equalsIgnoreCase(bean.getProvinceId())) {
                    checkBean = new WinDataCheckBean();
                    checkBean.setGameCode(bean.getGameCode());
                    checkBean.setProvinceId(bean.getProvinceId());
                    checkBean.setPeriodNum(bean.getPeriodNum());
                    checkBean.setProvinceWin1(bean.getPrize1Count().intValue());
                    checkBean.setProvinceWin2(bean.getPrize2Count().intValue());
                    checkBean.setProvinceWin3(bean.getPrize3Count().intValue());
                    checkBean.setProvinceWin4(bean.getPrize4Count().intValue());
                    checkBean.setProvinceWin5(bean.getPrize5Count().intValue());
                    checkBean.setProvinceWin6(bean.getPrize6Count().intValue());
                    checkBean.setProvinceWin7(bean.getPrize7Count().intValue());
                    checkBean.setProvinceWin8(bean.getPrize8Count().intValue());
                    checkBean.setProvinceWin9(bean.getPrize9Count().intValue());
                    checkBean.setProvinceWin10(bean.getPrize10Count().intValue());
                    checkBean.setStatus(Constant.Status.CHCEK_STATUS_WRONG);
                    beanList.add(checkBean);
                }
            }
        }

        return beanList;
    }

    //不完整打印票
    //计算公式：中奖检索中的销售票中奖结果+中奖检索中的不完整打印票中奖结果-中奖结果数据集中的中奖结果=0
    private List<UnCompTicketBean> conver2TicketBean(List<LttoWinningRetrieval> effectDatas,
                                                     List<LttoWinningRetrieval> nocomDatas, List<LttoWinstatData> winDataList) {
        List<UnCompTicketBean> list = new ArrayList<>();
        //未完成票核对
        //有效票+不完整-省上报 = 0；
        for (LttoWinningRetrieval effect : effectDatas) {
            for (LttoWinningRetrieval nocomp : nocomDatas) {
                String effid = effect.getProvinceId();
                String effgame = effect.getGameCode();
                String effperiod = effect.getPeriodNum();
                String noid = nocomp.getProvinceId();
                String nogame = nocomp.getGameCode();
                String noeriod = nocomp.getPeriodNum();
                if (effid.equals(noid) && effgame.equals(nogame) && effperiod.equals(noeriod)) {
                    effect.setPrize1Count(effect.getPrize1Count() + nocomp.getPrize1Count());
                    effect.setPrize2Count(effect.getPrize2Count() + nocomp.getPrize2Count());
                    effect.setPrize3Count(effect.getPrize3Count() + nocomp.getPrize3Count());
                    effect.setPrize4Count(effect.getPrize4Count() + nocomp.getPrize4Count());
                    effect.setPrize5Count(effect.getPrize5Count() + nocomp.getPrize5Count());
                    effect.setPrize6Count(effect.getPrize6Count() + nocomp.getPrize6Count());
                    effect.setPrize7Count(effect.getPrize7Count() + nocomp.getPrize7Count());
                    effect.setPrize8Count(effect.getPrize8Count() + nocomp.getPrize8Count());
                    effect.setPrize9Count(effect.getPrize9Count() + nocomp.getPrize9Count());
                    effect.setPrize10Count(effect.getPrize10Count() + nocomp.getPrize10Count());
                    break;
                }
            }
        }

        UnCompTicketBean ticketBean = null;
        for (LttoWinningRetrieval effect : effectDatas) {
            for (LttoWinstatData windata : winDataList) {
                String effid = effect.getProvinceId();
                String effgame = effect.getGameCode();
                String effperiod = effect.getPeriodNum();
                String winid = windata.getProvinceId();
                String wingame = windata.getGameCode();
                String wineriod = windata.getPeriodNum();
                if (effid.equals(winid) && effgame.equals(wingame) && effperiod.equals(wineriod)) {
                    ticketBean = new UnCompTicketBean();
                    ticketBean.setPeriodNum(wineriod);
                    ticketBean.setGameCode(wingame);
                    ticketBean.setProvinceId(winid);
                    ticketBean.setUncomp1Count(effect.getPrize1Count() - (windata.getPrize1Count().intValue()));
                    ticketBean.setUncomp2Count(effect.getPrize2Count() - (windata.getPrize2Count().intValue()));
                    ticketBean.setUncomp3Count(effect.getPrize3Count() - (windata.getPrize3Count().intValue()));
                    ticketBean.setUncomp4Count(effect.getPrize4Count() - (windata.getPrize4Count().intValue()));
                    ticketBean.setUncomp5Count(effect.getPrize5Count() - (windata.getPrize5Count().intValue()));
                    ticketBean.setUncomp6Count(effect.getPrize6Count() - (windata.getPrize6Count().intValue()));
                    ticketBean.setUncomp7Count(effect.getPrize7Count() - (windata.getPrize7Count().intValue()));
                    ticketBean.setUncomp8Count(effect.getPrize8Count() - (windata.getPrize8Count().intValue()));
                    ticketBean.setUncomp9Count(effect.getPrize9Count() - (windata.getPrize9Count().intValue()));
                    ticketBean.setUncomp10Count(effect.getPrize10Count() - (windata.getPrize10Count().intValue()));
                    list.add(ticketBean);
                    break;
                }
            }
        }

        List<String> upDataProvinces = this.upDataProvinces(winDataList);
        List<String> smCalProvinces = this.smCalProvinces(effectDatas);
        upDataProvinces.removeAll(smCalProvinces);
        for (LttoWinstatData bean : winDataList) {
            for (String str : upDataProvinces) {
                if (str.equalsIgnoreCase(bean.getProvinceId())) {
                    ticketBean = new UnCompTicketBean();
                    ticketBean.setGameCode(bean.getGameCode());
                    ticketBean.setProvinceId(bean.getProvinceId());
                    ticketBean.setPeriodNum(bean.getPeriodNum());
                    list.add(ticketBean);
                }
            }
        }
        return list;
    }

    //上报数据汇总数据的省中新
    private List<String> upDataProvinces(List<LttoWinstatData> proWinDatas) {
        List<String> list = new ArrayList<>();
        if (!CommonUtils.isEmpty(proWinDatas)) {
            for (LttoWinstatData data : proWinDatas) {
                list.add(data.getProvinceId());
            }
        }
        return list;
    }

    //数美根据上报的明细数据计算的所有省
    private List<String> smCalProvinces(List<LttoWinningRetrieval> nocomDatas) {
        List<String> list = new ArrayList<>();
        if (!CommonUtils.isEmpty(nocomDatas)) {
            for (LttoWinningRetrieval data : nocomDatas) {
                list.add(data.getProvinceId());
            }
        }
        return list;
    }

}
