package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.GenSalesRankExcelUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出开奖数据
 * 各省当期销售排名
 * Created by chenjia on 2017/5/23.
 */
@Controller
@Slf4j
public class ExportLotteryDataCtrl {

  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoLotteryAnnouncementService announcementService;
  @Resource
  private LttoProvinceSalesDataService salesDataService;
  @Resource
  private LttoWinstatDataService winstatDataService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  @ResponseBody
  @RequestMapping(value = "/query/lotteryData/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo queryLotteryData(@PathVariable String gameCode,
      @PathVariable String periodNum) {
    List<LttoProvinceSalesData> salelist = this.getProvinceSaleData(gameCode, periodNum);
    if (!CommonUtils.isEmpty(salelist)) {
      return ReturnInfo.Success;
    } else {
      return ReturnInfo.Faild;
    }
  }


  @ResponseBody
  @RequestMapping(value = "/export/lotteryData/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo exportLotteryData(@PathVariable String gameCode, @PathVariable String periodNum
      , HttpServletRequest request, HttpServletResponse response) {
    long start = System.currentTimeMillis();
    ReturnInfo returnInfo = new ReturnInfo();
    log.debug("[新开奖系统] 开始导出开奖数据");
    if (Constant.GameCode.GAME_CODE_LOTO.equals(gameCode)) { //七乐彩不导出销售排名表
      return new ReturnInfo("七乐彩无销售排名表", false);
    }
    if (StringUtils.isBlank(gameCode) || StringUtils.isBlank(periodNum) || periodNum.length() < 4) {
      return new ReturnInfo("游戏或者期号错误", false);
    }
    List<LttoProvinceSalesData> salelist = this.getProvinceSaleData(gameCode, periodNum);

    String year = periodNum.substring(0, 4);

    HashMap<String, String> resultMap = this.getwinSataByYear(gameCode, year);
    HashMap<String, LttoWinstatData> winMap = this.getProvincdWinData(gameCode, periodNum);
    LttoLotteryAnnouncement announcement = this.getAnnocementData(gameCode, periodNum);
    if (resultMap.size() == 0) {
      return new ReturnInfo("中奖数据未收集", false);
    }
    if (winMap.size() == 0) {
      return new ReturnInfo("中奖数据未收集", false);
    }
    if (!CommonUtils.isEmpty(salelist) && announcement != null) {
      try {
        Collections.sort(salelist);
        HSSFWorkbook wb = GenSalesRankExcelUtil
            .createExcel(salelist, resultMap, winMap, announcement, provinceInfoCache);
        String agents = request.getHeader("user-agent");
        String filename = gameInfoCache.getGameName(gameCode) + periodNum + "排名表";
        if (agents.contains("Firefox")) {
          filename = Base64Encode.base64EncodeFileName(filename + ".xls");
        } else {
          filename = URLEncoder.encode(filename + ".xls", "utf-8");
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

        try (OutputStream outputStream = response.getOutputStream();) {
          wb.write(outputStream);
        }
        log.debug("[新开奖系统] 完成导出开奖数据");
        returnInfo.setSuccess(true);
      } catch (Exception e) {
        log.error("[新开奖稽核系统]导出[弃奖报告]异常", e);
        return ReturnInfo.Faild;
      }
    } else {
      returnInfo.setSuccess(false);
      log.error("开奖流程操作有误");
    }
    log.info(operatorsLogManager.getLogInfo("页面通用", "开奖数据下发（查看销售排名）", start));
    return returnInfo;
  }

  //奖池信息
  private LttoLotteryAnnouncement getAnnocementData(String gameCode, String periodNum) {
    return announcementService.getAnnocementData(gameCode, periodNum);
  }

  //中奖数据信息
  private HashMap<String, LttoWinstatData> getProvincdWinData(String gameCode, String periodNum) {
    return winstatDataService.getProvincdWinData(gameCode, periodNum);
  }

  //获取一二等奖年累计注数
  private HashMap<String, String> getwinSataByYear(String gameCode, String year) {
    return winstatDataService.getwinSataByYear(gameCode, year);
  }

  //省销售数据当期
  private List<LttoProvinceSalesData> getProvinceSaleData(String gameCode, String periodNum) {
    return salesDataService.getProvinceSaleData(gameCode, periodNum);
  }

  private boolean mapIsEmpty(Map<String, Object> map) {
    if (map == null) {
      return true;
    } else if (map.size() == 0) {
      return true;
    } else {
      return false;
    }
  }

}
