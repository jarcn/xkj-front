package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.front.calc.bean.FundOpetBeanInput;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * 调节基金维护
 * Created by chenjia on 2017/9/11.
 */
@Slf4j
@Controller
public class FundAssertOpetCtrl {
    @Resource
    private LttoLotteryAnnouncementService announcementService;
    @Resource
    private ParaGamePeriodInfoService periodInfoService;
    @Resource
    private GameInfoCache gameInfoCache;


    @RequestMapping(value = "/fundAssertOpetCtrl/createNotice", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo createNotice( HttpServletRequest req) {
        return new ReturnInfo("ok",true);
    }

    @ResponseBody
    @RequestMapping(value = "/fundAssertOpetCtrl/createNotice", method = RequestMethod.POST)
    public ReturnInfo createNoticeUpdate(@RequestBody FundOpetBeanInput fundOpetBean, HttpServletRequest req) {
        if(fundOpetBean == null){
            return new ReturnInfo("参数错误",false);
        }
        String gameCode = fundOpetBean.getGameCode();
        String currPeriodNum = fundOpetBean.getPeriodNum();
        BigDecimal amont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        if(StringUtils.isBlank(gameCode) || StringUtils.isBlank(currPeriodNum) || StringUtils.isBlank(String.valueOf(amont))){
            return new ReturnInfo("参数错误",false);
        }
        String nextPeriodNum = periodInfoService.nextPeriodNum(fundOpetBean.getGameCode(), fundOpetBean.getPeriodNum());
        if(StringUtils.isBlank(nextPeriodNum)){
            return new ReturnInfo("无下期期号,请联系管理人员",false);
        }
        String notice = this.createNotice(fundOpetBean, nextPeriodNum);
        return new ReturnInfo(notice,true);
    }

    @ResponseBody
    @RequestMapping(value = "/fundAssertOpetCtrl/executNotice",method = RequestMethod.POST)
    public ReturnInfo execute(@RequestBody FundOpetBeanInput fundOpetBean, HttpServletRequest req){
        if(fundOpetBean == null){
            return new ReturnInfo("参数错误",false);
        }
        String gameCode = fundOpetBean.getGameCode();
        String currPeriodNum = fundOpetBean.getPeriodNum();
        BigDecimal amont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        if(StringUtils.isBlank(gameCode) || StringUtils.isBlank(currPeriodNum) || StringUtils.isBlank(String.valueOf(amont))){
            return new ReturnInfo("参数错误",false);
        }
        String nextPeriodNum = periodInfoService.nextPeriodNum(fundOpetBean.getGameCode(), fundOpetBean.getPeriodNum());
        if(StringUtils.isBlank(nextPeriodNum)){
            return new ReturnInfo("无下期期号,请联系管理人员",false);
        }
        LttoLotteryAnnouncement announcement = announcementService.selectByKey(gameCode, currPeriodNum);
        if(announcement == null){
            return new ReturnInfo("无期开奖公告记录，请联系管理员",false);
        }
        if(!Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())){
            return new ReturnInfo("开奖公告计算未完成",false);
        }
        String notice = "";
        switch (fundOpetBean.getType()){
            case "1":
                notice = this.calFundTotal2PoolTotal(fundOpetBean,announcement,nextPeriodNum); break;
            case "2":notice = "调节基金转出支付特别奖维护,暂时不做";break;
            case "3":
                notice = this.calFundTotalAndFundTempOut(fundOpetBean,announcement);break;
            case "4":
                notice = this.calFundTotal(fundOpetBean,announcement);break;
            case "5":
                notice = this.calPoolTotalAndPoolTempIn(fundOpetBean,announcement,nextPeriodNum);break;
            case "6":
                notice = this.calPoolTotalAndPoolTempOut(fundOpetBean,announcement);break;
            default:
                notice = "无效金额维护";break;
        }
        return new ReturnInfo(notice,true);
    }

     //(调节基金转出进入奖池)
    private String calFundTotal2PoolTotal(FundOpetBeanInput fundOpetBean,LttoLotteryAnnouncement announcement,String nextPeriodNum){
        log.debug("调节基金转出进入奖池修改前 inputAmont[{}],FUND_TOTAL[{}],POOL_TEMP_IN[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getFundTotal(),announcement.getPoolTempIn(),announcement.getPoolTotal());
        BigDecimal textAmont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        BigDecimal fundTotal = announcement.getFundTotal();
        fundTotal = fundTotal.subtract(textAmont);
        BigDecimal poolTempIn = announcement.getPoolTempIn();
        poolTempIn = poolTempIn.add(textAmont);
        BigDecimal poolTotal = announcement.getPoolTotal();
        poolTotal = poolTotal.add(textAmont);
        announcement.setFundTotal(fundTotal);
        announcement.setPoolTempIn(poolTempIn);
        announcement.setPoolTotal(poolTotal);
        String bullNote = this.formatStr(announcement.getBullNote());
        announcement.setBullNote(this.formatStr(bullNote)+this.createNotice(fundOpetBean,nextPeriodNum));
        int i = announcementService.updateByPrimaryKey(announcement);
        log.debug("调节基金转出进入奖池修改后 inputAmont[{}],FUND_TOTAL[{}],POOL_TEMP_IN[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getFundTotal(),announcement.getPoolTempIn(),announcement.getPoolTotal());
        if(i>0){
            return "调节基金转出进入奖池成功!";
        }else {
            return "调节基金转出进入奖池失败!";
        }

    }

    //调节基金临时转出
    private String calFundTotalAndFundTempOut(FundOpetBeanInput fundOpetBean,LttoLotteryAnnouncement announcement){
        log.debug("调节基金临时转出修改前 inputAmont[{}],FUND_TOTAL[{}],FUND_TEMP_OUT[{}]]",fundOpetBean.getAmont(),announcement.getFundTotal(),announcement.getFundTempOut());
        BigDecimal textAmont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        BigDecimal fundTotal = announcement.getFundTotal();
        fundTotal = fundTotal.subtract(textAmont);
        BigDecimal fundTempOut = announcement.getFundTempOut();
        fundTempOut = fundTempOut.add(textAmont);
        String fundNote = this.formatStr(announcement.getFundNote());
        announcement.setFundTotal(fundTotal);
        announcement.setFundTempOut(fundTempOut);
        announcement.setFundNote(this.formatStr(fundNote)+"本期调节基金拨出"+textAmont+"弥补当期派奖奖金不足!");
        int i =  announcementService.updateByPrimaryKey(announcement);
        log.debug("调节基金临时转出修改后 inputAmont[{}],FUND_TOTAL[{}],FUND_TEMP_OUT[{}]]",fundOpetBean.getAmont(),announcement.getFundTotal(),announcement.getFundTempOut());
        if(i>0){
            return "调节基金:临时转出成功!";
        }else {
            return "调节基金:临时转出失败!";
        }
    }

    //调节基金临时转入
    private String calFundTotal(FundOpetBeanInput fundOpetBean,LttoLotteryAnnouncement announcement){
        log.debug("调节基金临时转入修改前 inputAmont[{}],FUND_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getFundTotal());
        BigDecimal textAmont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        BigDecimal fundTotal = announcement.getFundTotal();
        fundTotal = fundTotal.add(textAmont);
        BigDecimal fundTempIn = announcement.getFundTempIn();
        fundTempIn =  fundTempIn.add(textAmont);
        announcement.setFundTempIn(fundTempIn);
        announcement.setFundTotal(fundTotal);
        String fundNote = this.formatStr(announcement.getFundNote());
        fundNote+="调节基金临时转入"+textAmont+"元";
        announcement.setFundNote(fundNote);
        int i = announcementService.updateByPrimaryKey(announcement);
        log.debug("调节基金临时转入修改后 inputAmont[{}],FUND_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getFundTotal());
        if(i>0){
            return "调节基金:临时转入成功!";
        }else {
            return "调节基金:临时转入失败!";
        }
    }

    //奖池临时注入（非调节基金注入）
    private String calPoolTotalAndPoolTempIn(FundOpetBeanInput fundOpetBean,LttoLotteryAnnouncement announcement,String nextPeriodNum){
        log.debug("奖池临时注入修改前 inputAmont[{}],POOL_TEMP_IN[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getPoolTempIn(),announcement.getPoolTotal());
        BigDecimal textAmont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        BigDecimal poolTempIn = announcement.getPoolTempIn();
        poolTempIn = poolTempIn.add(textAmont);
        BigDecimal poolTotal = announcement.getPoolTotal();
        poolTotal = poolTotal.add(textAmont);
        String bullNote = this.formatStr(announcement.getBullNote());
        bullNote+= this.createNotice(fundOpetBean,nextPeriodNum);
        announcement.setPoolTempIn(poolTempIn);
        announcement.setPoolTotal(poolTotal);
        announcement.setBullNote(bullNote);
        int i =  announcementService.updateByPrimaryKey(announcement);
        log.debug("奖池临时注入修改后 inputAmont[{}],POOL_TEMP_IN[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getPoolTempIn(),announcement.getPoolTotal());
        if(i>0){
            return "奖池:临时注入成功!";
        }else {
            return "奖池:临时注入失败!";
        }
    }

    //奖池临时转出
    private String calPoolTotalAndPoolTempOut(FundOpetBeanInput fundOpetBean,LttoLotteryAnnouncement announcement){
        log.debug("奖池临时转出修改前 inputAmont[{}],POOL_TEMP_OUT[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getPoolTempOut(),announcement.getPoolTotal());
        BigDecimal textAmont = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
        BigDecimal poolTotal = announcement.getPoolTotal();
        BigDecimal poolTempOut = announcement.getPoolTempOut();
        poolTotal = poolTotal.subtract(textAmont);
        poolTempOut = poolTempOut.add(textAmont);
        announcement.setPoolTotal(poolTotal);
        announcement.setPoolTempOut(poolTempOut);
        int i = announcementService.updateByPrimaryKey(announcement);
        log.debug("奖池临时转出修改后 inputAmont[{}],POOL_TEMP_OUT[{}],POOL_TOTAL[{}]",fundOpetBean.getAmont(),announcement.getPoolTempOut(),announcement.getPoolTotal());
        if(i>0){
            return "奖池:临时转出成功!";
        }else {
            return "奖池:临时转出失败!";
        }
    }


    private String createNotice(FundOpetBeanInput fundOpetBean,String nextPeriodNum){
        String gameCode = fundOpetBean.getGameCode();
        String currPeriodNum = fundOpetBean.getPeriodNum();
        StringBuilder sb = new StringBuilder();
        LttoLotteryAnnouncement currAnnouncement = announcementService.selectByKey(gameCode, currPeriodNum);
        switch (fundOpetBean.getType()){
            case "1":
                BigDecimal d11 = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
                BigDecimal d12 = null == currAnnouncement.getPoolTotal()?BigDecimal.ZERO:currAnnouncement.getPoolTotal();
                BigDecimal d13 = d11.add(d12);
                sb.append("中彩中心决定：从").append(gameInfoCache.getGameName(fundOpetBean.getGameCode())).append("调节基金拨出")
                        .append(d11.toString()).append("元，注入第").append(nextPeriodNum)
                        .append("期奖池，第").append(nextPeriodNum).append("期奖池资金原为:")
                        .append(d12.toString()).append("元,累计金额为:").append(d13.toString()).append("元。");
                break;
            case "5":
                BigDecimal d21 = BigDecimal.valueOf(Long.parseLong(fundOpetBean.getAmont()));
                BigDecimal d22 = null == currAnnouncement.getPoolTotal()?BigDecimal.ZERO:currAnnouncement.getPoolTotal();
                BigDecimal d23 = d21.add(d22);
                sb.append("注：中彩中心决定，第").append(nextPeriodNum).append("期奖池资金注入")
                        .append(d21.toString()).append("元，第").append(nextPeriodNum)
                        .append("期奖池资金原为").append(d22).append("元,累计金额为:")
                        .append(d23.toString()).append("元。");
                break;
            default:sb.append("参数错误");
                break;
        }
        return sb.toString();
    }


    private String formatStr(String val){
        if(StringUtils.isBlank(val)){
            return "";
        }else{
            return val;
        }
    }


}
