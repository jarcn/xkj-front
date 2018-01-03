package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PeriodManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.para.service.ParaFtpInfoService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;

import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 发布新期参数 根据各省实际时间发布新期参数
 * 00_10001_2013125_SALE.DWN  期参数文件名
 * Created by chenjia on 2017/5/24.
 */
@Slf4j
@Controller
public class PublishNewPeriodCtrl {

  private final String FILE_NAME_SUFFIX = "_SALE.DWN";
  @Resource
  private ParaGamePeriodInfoService periodInfoService;
  @Resource
  private ParaSysparameCache sysparameCache;
  @Resource
  private ParaFtpInfoService ftpInfoService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private PeriodManager periodManager;

  /**
   * 新期参数下发
   */
  @ResponseBody
  @RequestMapping(value = "/publish/newperiod/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo publishNewPeriod(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req) {
    List<ParaFtpInfo> ftpInfos = ftpInfoService.findAll();
    log.debug("[开奖稽核系统] 开始执行下发新期参数...");
    try {
      for (ParaFtpInfo info : ftpInfos) {
        String provinceId = info.getProvinceId();
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(provinceId);
        if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
          if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId())) {
            //使用ftp文件传输模式
            if (Constant.Model.RPT_FILE_FTP.equals(info.getFlag())) {
              String host = info.getFtpIp();
              String username = info.getFtpUsername();
              String password = info.getFtpPassword();
              Integer port = Integer.valueOf(info.getFtpPort());
              String fileFtplDir = info.getFtpPath();//ftp文件路径
              String fileLocalDir = sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH);//本地文件路径
              String nextPeriodNum = periodInfoService.nextPeriodNum(gameCode, periodNum);
              String newPeriodInfo = periodInfoService.nextPeriodNum(gameCode, nextPeriodNum);
              String fileName = info.getProvinceId() + "_" + gameCode + "_" + newPeriodInfo + FILE_NAME_SUFFIX;
              String fileAbsName = fileLocalDir + File.separator + fileName;//本地文件绝地路径
              File periodInfoFile = new File(fileLocalDir);
              if (!periodInfoFile.exists()) {
                boolean mkdirs = periodInfoFile.mkdirs();
              }
              String[] sltoTime = info.getSltoTime().split("[,;]");
              String[] lotoTime = info.getLotoTime().split("[,;]");
              File periodLocalFile = new File(fileAbsName);
              try (FileOutputStream out = new FileOutputStream(periodLocalFile)) {
                String fileContent = getNewPeriodInfo(gameCode, periodNum, sltoTime, lotoTime);
                out.write(fileContent.getBytes(Charset.forName("UTF-8")));
              }
              try (FtpService ftpClient = new FtpService();) {
                if (periodLocalFile.exists()) {
                  ftpClient.getConnect(host, port, username, password);
                  ftpClient.upload(fileFtplDir, fileAbsName);
                  log.debug("新期参数文件{}下发路径{}", fileName, fileFtplDir);
                  boolean deleteSuccess = periodLocalFile.delete();
                  if (!deleteSuccess) {
                    log.warn("文件出导出失败[{}]", periodLocalFile.getAbsolutePath());
                  }
                }
              } catch (Exception e) {
                log.error("新期参数下发异常", e);
              }
            } else {
              String fileLocalDir = sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH);
              String fileName =
                  info.getProvinceId() + "_" + gameCode + "_" + periodNum + FILE_NAME_SUFFIX;
              String filePath = fileLocalDir + File.separator + fileName;
              File periodDir = new File(fileLocalDir);
              if (!periodDir.exists()) {
                boolean mkdirs = periodDir.mkdirs();
              }
              String[] sltoTime = null;
              String[] lotoTime = null;
              if (info != null) {
                if (StringUtils.isNotBlank(info.getSltoTime())
                    && StringUtils.isNotBlank(info.getLotoTime())) {
                  sltoTime = info.getSltoTime().split("[,;]");
                  lotoTime = info.getLotoTime().split("[,;]");
                }
                File periodFile = new File(filePath);
                try (FileOutputStream out = new FileOutputStream(periodFile)) {
                  String fileContent = getNewPeriodInfo(gameCode, periodNum, sltoTime, lotoTime);
                  out.write(fileContent.getBytes(Charset.forName("UTF-8")));
                } catch (IOException e) {
                  log.error("文件导出失败", e);
                  return ReturnInfo.Faild;
                }
              } else {
                log.warn("省码[{}]未配置ftp信息", info.getProvinceId());
              }
              log.debug("[开奖稽核系统] 完成执行下发新期参数,下发文件路径{}", fileLocalDir + File.separator + fileName);
            }
          }
        } else {
          log.info("省[{}]为实时接口模式", info.getProvinceId());
        }
      }
    } catch (IOException e) {
      log.error("新期参数下发异常{}", e);
      return ReturnInfo.Faild;
    }
    return ReturnInfo.Success;
  }


  /**
   * 开奖系统期参数下发
   * 参数维护页面，历史期参数下发维护
   */
  @ResponseBody
  @RequestMapping(value = "/publish/currperiod/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo publishCurrPeriod(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req) {
    ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
    if (null == periodInfo) {
      return new ReturnInfo("期号或游戏编码错误", false);
    }
    if (Constant.Status.PERIOD_INFO_STATUS_2.equals(periodInfo.getStatus())) {
      return new ReturnInfo("已开奖期", false);
    }
    List<ParaFtpInfo> ftpInfos = ftpInfoService.findAll();
    boolean done = false;
    log.debug("[开奖稽核系统] 开始执行下发新期参数...");
    for (ParaFtpInfo info : ftpInfos) {
      ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(info.getProvinceId());
      if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
        if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId()) && isSupportGame(gameCode,
            info.getProvinceId())) {
          if (Constant.Model.RPT_FILE_FTP.equals(info.getFlag())) { //   使用ftp
            done = uploadFtpFile(info, gameCode, periodNum);
          } else { //使用本地文件
            done = exportLocal(info, gameCode, periodNum);
          }
        }
      } else {
        log.info("省[{}]为实时接口模式", info.getProvinceId());
      }
    }
    log.debug("[开奖稽核系统] 完成新期参数下发");
    return new ReturnInfo(done);
  }


  private boolean uploadFtpFile(ParaFtpInfo info, String gameCode, String periodNum) {
    String host = info.getFtpIp();
    String username = info.getFtpUsername();
    String password = info.getFtpPassword();
    Integer port = Integer.valueOf(info.getFtpPort());
    String fileFtplDir = info.getFtpPath();//ftp文件路径
    String fileLocalDir = sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH);//本地文件路径
    String fileName = info.getProvinceId() + "_" + gameCode + "_" + periodNum + FILE_NAME_SUFFIX;
    String fileAbsName = fileLocalDir + File.separator + fileName;//本地文件绝地路径
    File periodInfoFile = new File(fileLocalDir);
    if (!periodInfoFile.exists()) {
      boolean mkdirs = periodInfoFile.mkdirs();
    }
    String[] sltoTime = info.getSltoTime().split("[,;]");
    String[] lotoTime = info.getLotoTime().split("[,;]");
    File periodLocalFile = new File(fileAbsName);
    try (FileOutputStream out = new FileOutputStream(periodLocalFile)) {
      String fileContent = getCurrPeriodInfo(gameCode, periodNum, sltoTime, lotoTime);
      out.write(fileContent.getBytes(Charset.forName("UTF-8")));
      out.flush();
    } catch (Exception e) {
      log.warn("文件导出失败", e);
      return false;
    }
    FtpService ftpClient = new FtpService();
    if (periodLocalFile.exists()) {
      try {
        ftpClient.getConnect(host, port, username, password);
      } catch (IOException e) {
        log.error("新期参数下发异常", e);
        return false;
      }
      ftpClient.upload(fileFtplDir, fileAbsName);
      log.debug("新期参数文件下发路径{},文件名称{}", fileFtplDir, periodLocalFile.getName());
      boolean deleteSuccess = periodLocalFile.delete();
      if (!deleteSuccess) {
        log.warn("文件导出出失败[{}]", periodLocalFile.getAbsolutePath());
      }
    }
    return true;
  }

  private boolean exportLocal(ParaFtpInfo info, String gameCode, String periodNum) {
    String fileLocalDir = sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH);
    String fileName = info.getProvinceId() + "_" + gameCode + "_" + periodNum + FILE_NAME_SUFFIX;
    String filePath = fileLocalDir + File.separator + fileName;
    File periodDir = new File(fileLocalDir);
    if (!periodDir.exists()) {
      boolean mkdirs = periodDir.mkdirs();
    }
    String[] sltoTime = info.getSltoTime().split("[,;]");
    String[] lotoTime = info.getLotoTime().split("[,;]");
    File periodFile = new File(filePath);
    try (FileOutputStream out = new FileOutputStream(periodFile)) {
      String fileContent = getNewPeriodInfo(gameCode, periodNum, sltoTime, lotoTime);
      out.write(fileContent.getBytes(Charset.forName("UTF-8")));
      out.flush();
    } catch (IOException e) {
      log.error("文件导出失败", e);
      return false;
    }
    return true;
  }


  //新期参数文件内容
  private String getNewPeriodInfo(String gameCode, String periodNum, String[] sltoTime,
      String[] lotoTime) {
    String fileContent = null;
    switch (gameCode) {
      case Constant.GameCode.GAME_CODE_SLTO:
        fileContent = this.createNewPeriodFile(gameCode, periodNum, sltoTime[0], sltoTime[1]);
        break;
      case Constant.GameCode.GAME_CODE_LOTO:
        fileContent = this.createNewPeriodFile(gameCode, periodNum, lotoTime[0], lotoTime[1]);
        break;
      default:
        break;
    }
    return fileContent;
  }

  //指定期号期参数文件内容
  private String getCurrPeriodInfo(String gameCode, String periodNum, String[] sltoTime,
      String[] lotoTime) {
    String fileContent = null;
    switch (gameCode) {
      case Constant.GameCode.GAME_CODE_SLTO:
        fileContent = this.createHisPeriodFile(gameCode, periodNum, sltoTime[0], sltoTime[1]);
        break;
      case Constant.GameCode.GAME_CODE_LOTO:
        fileContent = this.createHisPeriodFile(gameCode, periodNum, lotoTime[0], lotoTime[1]);
        break;
      default:
        break;
    }
    return fileContent;
  }

  /*`CASH_TERM` int(2) DEFAULT NULL COMMENT '兑奖期长',
  `CASH_END_TIME` bigint(20) DEFAULT NULL COMMENT '兑奖截止时间',*/
  // 10001,2014012,00,00,2014-01-26 20:00:00,2014-01-28 19:50:00,60,2014-03-28 23:59:59,00012013140, 期参数内容示例
  private String createNewPeriodFile(String gameCode, String periodNum, String sTime,
      String eTime) {
    StringBuilder perBuff = new StringBuilder();
    String nextPeriodNum = periodInfoService.nextPeriodNum(gameCode, periodNum);
    String newPeriodInfo = periodInfoService.nextPeriodNum(gameCode, nextPeriodNum);
    ParaGamePeriodInfo nextPeriodInfo = periodInfoService.selectbyKey(gameCode, newPeriodInfo);
    if (nextPeriodInfo != null) {
      String newPeriodNum = nextPeriodInfo.getPeriodNum();
      String newGameCode = nextPeriodInfo.getGameCode();
      String status = nextPeriodInfo.getPromotionStatus();
      String periodstartDate = nextPeriodInfo.getPeriodBeginTime() + " " + sTime;
      String periodEndDate = nextPeriodInfo.getPeriodEndTime() + " " + eTime;
      String newCashEndDate = nextPeriodInfo.getCashEndTime() + " 23:59:59";
      Integer cashTerm = nextPeriodInfo.getCashTerm();

      perBuff.append(newGameCode + FileUtils.createSpace(5 - newGameCode.length()) + ",")
          .append(newPeriodNum + FileUtils.createSpace(12 - newPeriodNum.length()) + ",")
          .append("00,")
          .append(String.format("%02d", Integer.valueOf(status)) + ",")
          .append(periodstartDate + FileUtils.createSpace(19 - periodstartDate.length()) + ",")
          .append(periodEndDate + FileUtils.createSpace(19 - periodEndDate.length()) + ",")
          .append(cashTerm + ",")
          .append(newCashEndDate + ",")
          .append(this.getOverduePeriod(nextPeriodInfo.getOverduePeriod()));//弃奖详情 // 生成期号时提前计算
    }
    return perBuff.toString();
  }

  private String getOverduePeriod(String overdueperiod) {
    if (StringUtils.isBlank(overdueperiod)) {
      return "0" + FileUtils.createSpace(16 - "0".length()) + ",";
    } else {
      return overdueperiod + FileUtils.createSpace(16 - overdueperiod.length()) + ",";
    }
  }

  private boolean isSupportGame(String gameCode, String provinceId) {
    ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(provinceId);
    String gameName = gameInfoCache.getGameName(gameCode);
    String gameSupport = provinceInfo.getGameSupport();
    if (gameSupport.contains(gameName)) {
      return true;
    } else {
      return false;
    }
  }

  //下发历史期参数
  private String createHisPeriodFile(String gameCode, String periodNum, String sTime,
      String eTime) {
    StringBuilder perBuff = new StringBuilder();
    ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
    if (periodInfo != null) {
      String newPeriodNum = periodInfo.getPeriodNum();
      String newGameCode = periodInfo.getGameCode();
      String status = periodInfo.getPromotionStatus();
      String periodstartDate = periodInfo.getPeriodBeginTime() + " " + sTime;
      String periodEndDate = periodInfo.getPeriodEndTime() + " " + eTime;
      Integer cashTerm = periodInfo.getCashTerm();
      String oldCashEndDate = periodInfo.getCashEndTime() + " 23:59:59";
      perBuff.append(newGameCode + FileUtils.createSpace(5 - newGameCode.length()) + ",")
          .append(newPeriodNum + FileUtils.createSpace(12 - newPeriodNum.length()) + ",")
          .append(periodInfo.getProvinceId() + ",")
          .append(status + ",")
          .append(periodstartDate + FileUtils.createSpace(19 - periodstartDate.length()) + ",")
          .append(periodEndDate + FileUtils.createSpace(19 - periodEndDate.length()) + ",")
          .append(cashTerm + ",")
          .append(oldCashEndDate + ",")
          .append(this.getOverduePeriod(periodInfo.getOverduePeriod())); //弃奖详情 //TODO 需要计算
    }
    return perBuff.toString();
  }
}
