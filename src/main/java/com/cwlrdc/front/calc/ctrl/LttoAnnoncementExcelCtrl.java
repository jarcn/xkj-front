package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.calc.bean.PrintAnnceExcelParaBean;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.GenLottAnnExcelUtil;
import com.cwlrdc.front.calc.util.GenSltoAnnExcelUtil;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Created by chenjia on 2017/8/23.
 */
@Controller
@Slf4j
public class LttoAnnoncementExcelCtrl {

  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoLotteryAnnouncementService lotteryAnnouncementService;
  @Resource
  private LttoWinstatDataService winstatDataService;
  @Resource
  private ParaGamePeriodInfoService gamePeriodInfoService;
  @Resource
  private PromotionManager promotionManager;
  @Resource
  private OperatorsLogManager operatorsLogManager;


  /**
   * 导出开奖公告文件
   */
  @ResponseBody
  @RequestMapping(value = "/lttoannoncementexport/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo exportLttoannoncement(@PathVariable String gameCode,
      @PathVariable String periodNum, HttpServletRequest request, HttpServletResponse response) {
    HSSFWorkbook wb;
    long start = System.currentTimeMillis();
    ReturnInfo info = new ReturnInfo();
    LttoLotteryAnnouncement announcement = lotteryAnnouncementService
        .selectByKey(gameCode, periodNum);
    if (announcement != null) {
      if (Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())) {
        String gameName = gameInfoCache.getGameName(gameCode);
        ParaGamePeriodInfo periodInfo = gamePeriodInfoService.selectbyKey(gameCode, periodNum);
        if (null == periodInfo) {
          return new ReturnInfo("期号错误", false);
        }
        if (StringUtils.isBlank(periodInfo.getWinNum())) {
          return new ReturnInfo("无开奖号码", false);
        }
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
          PrintAnnceExcelParaBean printPara = promotionManager.createPrintPara(periodInfo);
          wb = GenSltoAnnExcelUtil.createSltoAnnExcel(printPara);
        } else {
          String allPeize1Detail = promotionManager.getAllPrize1Count(gameCode, periodNum);
          String winNum = periodInfo.getWinNum();
          wb = GenLottAnnExcelUtil
              .bean2Excel(periodInfo, announcement, gameName, winNum, allPeize1Detail);
        }
        String agents = request.getHeader("user-agent");
        String filename = gameName + periodNum + "开奖公告";
        try {
          if (agents.contains("Firefox")) {
            filename = Base64Encode.base64EncodeFileName(filename + ".xls");
          } else {
            filename = URLEncoder.encode(filename + ".xls", "utf-8");
          }
          response.setCharacterEncoding("utf-8");
          response.setContentType("multipart/form-data");
          response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
          try (OutputStream outputStream = response.getOutputStream()) {
            wb.write(outputStream);
            log.info(operatorsLogManager.getLogInfo("页面通用", "打印开奖公告", start));
          }
        } catch (Exception e) {
          log.error("[新开奖稽核系统]导出[生成全国开奖公告]异常", e);
          info.setSuccess(false);
        }
      } else {
        info.setSuccess(false);
        info.setRetcode(announcement.getProcessStatus());
      }
      return info;
    } else {
      info.setSuccess(false);
      info.setDescription("开奖公告未生成");
      return info;
    }
  }
}

