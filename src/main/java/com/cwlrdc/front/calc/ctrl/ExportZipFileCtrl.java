package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.calc.bean.PrintAnnceExcelParaBean;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.CompressedFileUtil;
import com.cwlrdc.front.calc.util.GenLottAnnExcelUtil;
import com.cwlrdc.front.calc.util.GenOverDueDataExcelUtil;
import com.cwlrdc.front.calc.util.GenPeriodSaleDetailTxtUtil;
import com.cwlrdc.front.calc.util.GenPrintNoticeExcelUtil;
import com.cwlrdc.front.calc.util.GenSalesRankExcelUtil;
import com.cwlrdc.front.calc.util.GenSltoAnnExcelUtil;
import com.cwlrdc.front.calc.util.GenWinDataExcelUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PromotionManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 导出压缩文件
 * Created by chenjia on 2017/8/26.
 */
@Slf4j
@Controller
public class ExportZipFileCtrl {

  private String destDir;
  @Resource
  private ParaSysparameCache sysparameCache;
  @Resource
  private LttoProvinceSalesDataService lttoProvinceSalesDataService;
  @Resource
  private LttoWinstatDataService lttoWinstatDataService;
  @Resource
  private LttoCancelWinStatDataService lttoCancelWinStatDataService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoLotteryAnnouncementService announcementService;
  @Resource
  private ParaGamePeriodInfoService gamePeriodInfoService;
  @Resource
  private PromotionManager promotionManager;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  private boolean createDir(String destDirName) {
    destDir = destDirName;
    File dir = new File(destDir);
    if (dir.exists()) {
      return false;
    }
    if (!destDir.endsWith(File.separator)) {
      destDir = destDirName + File.separator;
    }
    //创建目录
    if (dir.mkdirs()) {
      return true;
    } else {
      return false;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/exportzipfile/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo exportZipFile(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request, HttpServletResponse response) {
    long start = System.currentTimeMillis();
    ParaGamePeriodInfo periodInfo = gamePeriodInfoService.selectbyKey(gameCode, periodNum);
    Integer status = periodInfo.getStatus();
    if (Status.Period.CURRENT.equals(status) || Status.Period.PASSED.equals(status)) {
      if (exportExcel(gameCode, periodNum)) {
        String zipDir = this.getOutDir(gameCode, periodNum);
        String filename = gameCode + "_" + periodNum + "开奖数据";
        String compressedFile = "";
        try {
          compressedFile = CompressedFileUtil
              .compressedFile(zipDir, zipDir, filename); //压缩文件 生成zip包
          if (new File(compressedFile).exists()) {
            String agents = request.getHeader("user-agent");
            if (agents.contains("Firefox")) {
              filename = Base64Encode.base64EncodeFileName(filename + ".zip");
            } else {
              filename = URLEncoder.encode(filename + ".zip", "utf-8");
            }
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
          }
        } catch (Exception exception) {
          log.info("开奖数据导出异常", exception);
          return new ReturnInfo("开奖数据导出异常", false);
        }
        try (ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(new File(compressedFile));
        ) {
          byte[] b = new byte[1024];
          int count;
          while ((count = fileInputStream.read(b)) > 0) {
            outputStream.write(b, 0, count);
          }
          outputStream.flush();
        } catch (Exception e) {
          log.info("开奖数据导出异常", e);
          return new ReturnInfo("开奖数据导出异常", false);
        }
      }
    } else {
      return new ReturnInfo("开奖数据导出失败", false);
    }
    log.info(operatorsLogManager.getLogInfo("导出文件", "导出文件", start));
    return ReturnInfo.Success;
  }

  /**
   * 导出监控数据（zip文件）
   */
  @ResponseBody
  @RequestMapping(value = "/monitordata/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo exportMonitorData(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request, HttpServletResponse response) {
    try {
      long start = System.currentTimeMillis();
      log.debug("[新开奖系统] 开始导出[{}]期,游戏[{}]监控数据", periodNum, gameCode);
      String xlsDir = this.getOutDir(gameCode, periodNum);
      boolean success = this.exportExcel(gameCode, periodNum);
      if (!success) {
        return new ReturnInfo("监控数据导出失败", false);
      }
      String txtFilePath = this.exportSaleDetailTxt(gameCode, periodNum);
      if (StringUtils.isBlank(txtFilePath)) {
        return new ReturnInfo("监控数据导出失败", false);
      }
      String saleDataName = gameCode + "_" + periodNum + "销售数据";
      String saleZipFilePath = CompressedFileUtil
          .compressedFile(xlsDir, sysparameCache.getFtpLocalPath() + File.separator, saleDataName);
      String monitorName = gameCode + "_" + periodNum + "监控数据";
      File zipFile = new File(saleZipFilePath);
      File txtFile = new File(txtFilePath);
      if (!zipFile.exists() || !txtFile.exists()) {
        return new ReturnInfo("监控数据导出失败", false);
      }
      String appendZipFile = CompressedFileUtil
          .zipAppendZipFile(zipFile, txtFile, sysparameCache.getFtpLocalPath() + File.separator,
              monitorName);
      log.debug("[新开奖系统] 完成导出[{}]期,游戏[{}]监控数据[{}]", periodNum, gameCode, appendZipFile);
      log.info(operatorsLogManager.getLogInfo("导出监控数据", "导出监控数据", start));
      return new ReturnInfo("监控数据导出成功", true);
    } catch (Exception e) {
      log.warn("监控数据导出异常", e);
      return new ReturnInfo("监控数据导出失败", false);
    }
  }


  private boolean exportExcel(String gameCode, String periodNum) {
    boolean rankExcel = false;
    boolean saleExcel = false;
    boolean winExcel = false;
    boolean mentExcel = false;
    boolean detailTxt = true;
    if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
      rankExcel = this.exportRankExcel(gameCode, periodNum);
      saleExcel = this.exportSaleExcel(gameCode, periodNum);
      mentExcel = this.exportAnnoceMentExcel(gameCode, periodNum);
      winExcel = this.exportWinExcel(gameCode, periodNum);
    } else {
      rankExcel = true; //七乐彩无销售排名表
      saleExcel = this.exportSaleExcel(gameCode, periodNum);
      mentExcel = this.exportAnnoceMentExcel(gameCode, periodNum);
      winExcel = this.exportWinExcel(gameCode, periodNum);
    }

    if (exportAllTrue(rankExcel, saleExcel, winExcel, mentExcel, detailTxt)) {
      return true;
    } else {
      return false;
    }
  }

  //销售情况
  private boolean exportSaleExcel(String gameCode, String periodNum) {
    FileOutputStream out = null;
    String outDir = null;
    String fileName = null;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameInfoCache.getGameName(gameCode) + periodNum + "销售额.xls";
      File dir = new File(outDir);
      if (!dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      out = new FileOutputStream(saleFile);
      List<LttoProvinceSalesData> list = lttoProvinceSalesDataService
          .selectByPeriodNumAndGameCode(periodNum, gameCode);
      HSSFWorkbook workbook = GenPrintNoticeExcelUtil
          .list2Excel(list, gameCode, periodNum, gameInfoCache, provinceInfoCache);
      workbook.write(out);
      out.flush();
    } catch (Exception e) {
      log.error("导出销售额文件异常", e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        log.error("导出销售额文件异常", e);
      }
    }
    if (new File(outDir + fileName).exists()) {
      return true;
    } else {
      return false;
    }
  }

  //中奖情况
  public boolean
  exportWinExcel(String gameCode, String periodNum) {
    FileOutputStream out = null;
    String outDir = null;
    String fileName = null;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameInfoCache.getGameName(gameCode) + periodNum + "中奖情况.xls";
      File dir = new File(outDir);
      if (dir.isDirectory() && !dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      out = new FileOutputStream(saleFile);
      List<LttoWinstatData> list =
          lttoWinstatDataService.select2datas(gameCode, periodNum
              , Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
      HSSFWorkbook workbook = GenWinDataExcelUtil.list2Excel(list, gameCode, periodNum, gameInfoCache, provinceInfoCache);
      workbook.write(out);
      out.flush();
    } catch (Exception e) {
      log.error("导出中奖情况文件异常", e);
    } finally {
      try {
        if (null != out) {
          out.close();
        }
      } catch (IOException e) {
        log.error("导出中奖情况文件异常", e);
      }
    }
    if (new File(outDir + fileName).exists()) {
      return true;
    } else {
      return false;
    }
  }

  //弃奖情况
  public boolean exportOverdueExcel(String gameCode, String periodNum) {
    FileOutputStream out = null;
    String outDir = null;
    String fileName = null;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameInfoCache.getGameName(gameCode) + periodNum + "弃奖情况.xls";
      File dir = new File(outDir);
      if (!dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      out = new FileOutputStream(saleFile);
      List<LttoCancelWinStatData> list = lttoCancelWinStatDataService
          .selectCancelWinDatas(gameCode, periodNum);
      HSSFWorkbook workbook = GenOverDueDataExcelUtil
          .list2Excel(list, gameCode, periodNum, provinceInfoCache, gameInfoCache);
      workbook.write(out);
      out.flush();
    } catch (Exception e) {
      log.error("导出弃奖情况文件异常", e);
    } finally {
      try {
        if (null != out) {
          out.close();
        }
      } catch (IOException e) {
        log.error("导出弃奖情况文件异常", e);
      }
    }
    if (new File(outDir + fileName).exists()) {
      return true;
    } else {
      return false;
    }
  }

  //销售排名表
  public boolean exportRankExcel(String gameCode, String periodNum) {
    FileOutputStream out = null;
    String outDir = null;
    String fileName = null;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameInfoCache.getGameName(gameCode) + periodNum + "销售排名.xls";
      File dir = new File(outDir);
      if (!dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      out = new FileOutputStream(saleFile);
      List<LttoProvinceSalesData> provinceSaleData = lttoProvinceSalesDataService
          .getProvinceSaleData(gameCode, periodNum);
      HashMap<String, String> getwinSataByYear = lttoWinstatDataService
          .getwinSataByYear(gameCode, periodNum);
      HashMap<String, LttoWinstatData> winData = lttoWinstatDataService
          .getProvincdWinData(gameCode, periodNum);
      LttoLotteryAnnouncement annocementData = announcementService
          .getAnnocementData(gameCode, periodNum);
      HSSFWorkbook workbook = GenSalesRankExcelUtil
          .createExcel(provinceSaleData, getwinSataByYear, winData, annocementData,
              provinceInfoCache);
      workbook.write(out);
      out.flush();
    } catch (Exception e) {
      log.error("导出销售排名文件异常", e);
    } finally {
      try {
        if (null != out) {
          out.close();
        }
      } catch (IOException e) {
        log.error("导出销售排名文件异常", e);
      }
    }
    if (new File(outDir + fileName).exists()) {
      return true;
    } else {
      return false;
    }
  }

  //开奖公告excel文件导出
  private boolean exportAnnoceMentExcel(String gameCode, String periodNum) {
    String outDir = "";
    String fileName = "";
    HSSFWorkbook workbook;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameInfoCache.getGameName(gameCode) + periodNum + "开奖公告.xls";
      File dir = new File(outDir);
      if (!dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      try (FileOutputStream out = new FileOutputStream(saleFile)) {
        LttoLotteryAnnouncement announcement = announcementService.selectByKey(gameCode, periodNum);
        String gameName = gameInfoCache.getGameName(gameCode);
        ParaGamePeriodInfo winNum = gamePeriodInfoService.queryWinNum(gameCode, periodNum);
        String allPrize1Count = lttoWinstatDataService.getAllPrize1Count(gameCode, periodNum);
        List<ParaGamePeriodInfo> paraGamePeriodInfos =
            gamePeriodInfoService.selectCurrentGame(gameCode);
        if (!CommonUtils.isEmpty(paraGamePeriodInfos) && paraGamePeriodInfos.size() > 1) {
          return false;
        }
        ParaGamePeriodInfo periodInfo = paraGamePeriodInfos.get(0);
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
          PrintAnnceExcelParaBean printPara = promotionManager.createPrintPara(periodInfo);
          workbook = GenSltoAnnExcelUtil.createSltoAnnExcel(printPara);
        } else {
          workbook =
              GenLottAnnExcelUtil.bean2Excel(paraGamePeriodInfos.get(0), announcement
                  , gameName, winNum.getWinNum(),allPrize1Count);
        }
        workbook.write(out);
        out.flush();
      }
    } catch (Exception e) {
      log.error("导出开奖公告文件异常", e);
    }
    if (new File(outDir + fileName).exists()) {
      return true;
    } else {
      return false;
    }
  }

  //本期销售详情
  public String exportSaleDetailTxt(String gameCode, String periodNum) {
    String outDir = null;
    String fileName = null;
    try {
      outDir = this.getOutDir(gameCode, periodNum);
      fileName = gameCode + "_" + periodNum + ".txt";
      File dir = new File(outDir);
      if (!dir.exists()) {
        this.createDir(outDir);
      }
      File saleFile = new File(outDir + fileName);
      try (FileOutputStream out = new FileOutputStream(saleFile);) {
        String querySql = GenPeriodSaleDetailTxtUtil.getQuerySql(gameCode, periodNum);
        List<HashMap<String, Object>> mapList = lttoCancelWinStatDataService.dosql(querySql);
        String txtFile = GenPeriodSaleDetailTxtUtil.creatTxtFile(mapList);
        out.write(txtFile.getBytes(Charset.forName("UTF-8")));
        out.flush();
      }

    } catch (Exception e) {
      log.warn("期号[{}]游戏[{}]销售详情导出异常", periodNum, gameCode, e);
      return "";
    }
    File txtFile = new File(outDir + fileName);
    if (txtFile.exists()) {
      return txtFile.getAbsolutePath();
    } else {
      return "";
    }
  }


  private String getOutDir(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(sysparameCache.getFtpLocalPath());
    sb.append(File.separator);
    sb.append(gameCode);
    sb.append(File.separator);
    sb.append(periodNum);
    sb.append(File.separator);
    return sb.toString();
  }

  private boolean exportAllTrue(boolean rankExcel, boolean saleExcel, boolean winExcel,
      boolean mentExcel, boolean saleTxt) {
    if (rankExcel && saleExcel && winExcel && mentExcel && saleTxt) {
      return true;
    } else {
      return false;
    }
  }

}
