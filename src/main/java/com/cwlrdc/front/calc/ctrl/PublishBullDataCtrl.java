package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.PromotionCode;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PromotionManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.para.service.ParaFtpInfoService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 00_10001_2017032_BULL.DWN
 * 发布开奖公告电子文件，
 * 查询开奖公告数据库，生成对应的文件下发
 * Created by chenjia on 2017/6/26.
 */
@Slf4j
@Controller
public class PublishBullDataCtrl {

  private static final String FILE_NAME_SUFFIX = "_BULL.DWN";
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ParaGamePeriodInfoService periodInfoService;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private ParaSysparameCache sysparameCache;
  @Resource
  private ParaFtpInfoService ftpInfoService;
  @Resource
  private LttoLotteryAnnouncementService announcementService;
  @Resource
  private PromotionManager promotionManager;


  /**
   * 历史开奖公告电子文件下发
   */
  @ResponseBody
  @RequestMapping(value = "/publish/hisbulldata/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo publishBullData(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req) {
    ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
    if (periodInfo == null) {
      return new ReturnInfo("无效期号", false);
    }
    if (!Constant.Status.PERIOD_INFO_STATUS_2.equals(periodInfo.getStatus())) {
      return new ReturnInfo("未开奖期不能下发开奖公告", false);
    }
    LttoLotteryAnnouncement announcement = announcementService.selectByKey(gameCode, periodNum);
    if (null == announcement) {
      return new ReturnInfo("无开奖公告数据", false);
    }
    List<ParaFtpInfo> ftpInfos = ftpInfoService.findAll();

    log.debug("[新开奖系统] 开始 下发开奖公告文件...");
    try {
      for (ParaFtpInfo info : ftpInfos) {
        if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId()) && isSupportGame(gameCode,
            info.getProvinceId())) {
          ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(info.getProvinceId());
          Integer isFtp = provinceInfo.getIsFtp();
          Integer saledetailType = provinceInfo.getIsFtp();
          if (Constant.Model.RPT_FILE_FTP.equals(isFtp) && Constant.Model.RPT_FILE_FTP
              .equals(saledetailType)) {
            if (Constant.Model.RPT_FILE_FTP.equals(info.getFlag())) {
              this.uploadFile(info, gameCode, periodNum);
            } else {
              this.uploadLocal(gameCode, periodNum);
            }
          } else {
            log.info("省[{}]为实时接口模式", provinceInfo.getProvinceId());
          }
        }
      }
    } catch (IOException e) {
      log.debug("开奖公告下发异常{}", e);
      return ReturnInfo.Faild;
    }
    return ReturnInfo.Success;
  }

  /**
   * 下发开奖公告电子文件
   */
  @ResponseBody
  @RequestMapping(value = "/publish/bulldata/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo publishHisBullData(@PathVariable String gameCode,
      @PathVariable String periodNum, HttpServletRequest req) {
    ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
    if (periodInfo == null) {
      return new ReturnInfo("无效期号", false);
    }
    LttoLotteryAnnouncement announcement = announcementService.selectByKey(gameCode, periodNum);
    if (null == announcement) {
      return new ReturnInfo("无开奖公告数据", false);
    }
    if (!Constant.Status.TASK_RUN_COMPLETE_2.equals(announcement.getProcessStatus())) {
      return new ReturnInfo("开奖公告未生成", false);
    }
    List<ParaFtpInfo> ftpInfos = ftpInfoService.findAll();
    log.debug("[新开奖系统] 开始 下发开奖公告文件...");
    try {
      for (ParaFtpInfo info : ftpInfos) {
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(info.getProvinceId());
        if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
          if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId()) && isSupportGame(
              gameCode, info.getProvinceId())) {
            if (Constant.Model.RPT_FILE_FTP.equals(info.getFlag())) { //使用ftp
              this.uploadFile(info, gameCode, periodNum);
            } else { //使用本地文件
              this.uploadLocal(gameCode, periodNum);
            }
          }
        } else {
          log.info("省[{}]为实时接口模式", info.getProvinceId());
        }
      }
    } catch (IOException e) {
      log.debug("开奖公告下发异常{}", e);
      return ReturnInfo.Faild;
    }
    return ReturnInfo.Success;
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

  private String uploadLocal(String gameCode, String periodNum) {
    String fileLocalDir = this.getLocalDir();
    String fileName = this.getFileName(gameCode, periodNum);
    String filePath = fileLocalDir + File.separator + fileName;
    File periodFile = new File(filePath);
    try (FileOutputStream out = new FileOutputStream(periodFile)) {
      File periodDir = new File(fileLocalDir);
      if (!periodDir.exists()) {
        boolean mkdirs = periodDir.mkdirs();
      }
      String fileContent = getBullData(gameCode, periodNum);
      out.write(fileContent.getBytes(Charset.forName("UTF-8")));
      return filePath;
    } catch (IOException e) {
      log.error("下发开奖公告文件异常", e);
      return "";
    }
  }

  private void uploadFile(ParaFtpInfo info, String gameCode, String periodNum) throws IOException {
    String local = this.uploadLocal(gameCode, periodNum);
    try (FtpService ftpClient = new FtpService();) {
      File localFile = new File(local);
      if (localFile.exists()) {
        ftpClient.getConnect(info.getFtpIp(), Integer.parseInt(info.getFtpPort()), info.getFtpUsername(),
                info.getFtpPassword());
        ftpClient.upload(info.getFtpPath(), local);
        log.debug("开奖公告文件{}下发路径{}", localFile.getName(), info.getFtpPath());
      }
    } catch (Exception e) {
      log.error("开奖公告文件下发异常", e);
    }
  }

  private String getFileName(String gameCode, String periodNum) {
    return Constant.Key.PROVINCEID_OF_CWL + "_" + gameCode + "_" + periodNum + FILE_NAME_SUFFIX;
  }

  private String getLocalDir() {
    StringBuilder sb = new StringBuilder();
    sb.append(sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH));
    return sb.toString();
  }

  private String getBullData(String gameCode, String periodNum) {
    ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
    LttoLotteryAnnouncement announcement =
        announcementService.getAnnocementData(gameCode, periodNum);
    if (periodInfo != null && announcement != null) {
      return this.createBullFile(periodInfo, announcement);
    } else {
      return "";
    }
  }

  private String createBullFile(ParaGamePeriodInfo periodInfo,
      LttoLotteryAnnouncement announcement) {
    StringBuilder perBuff = new StringBuilder();
    try {
      String winNum = FileUtils.getCurrWinNum(periodInfo.getGameCode(), periodInfo.getWinNum());
      DecimalFormat df = new DecimalFormat("0.00");
      String poolInfo = this.createPoolInfo(periodInfo, announcement);
      BigDecimal prize7Money = this.createPromotionLevelInfo(periodInfo, announcement, PromotionCode.PRIZE1_GAME_CODE);
      BigDecimal prize8Money = this.createPromotionLevelInfo(periodInfo, announcement, PromotionCode.PRIZE6_GAME_CODE);
      perBuff.append(
          periodInfo.getGameCode() + FileUtils.createSpace(5 - periodInfo.getGameCode().length())
              + ",") //游戏编码
          .append(periodInfo.getPeriodNum() + FileUtils
              .createSpace(12 - periodInfo.getPeriodNum().length()) + ",")//当期期号
          .append(Constant.Key.PROVINCEID_OF_CWL + FileUtils
              .createSpace(2 - Constant.Key.PROVINCEID_OF_CWL.length()) + ",")//省码
          .append(String.format("%02d", announcement.getWinGroupCount()) + ",")//中奖号码 组数
          .append(winNum + FileUtils.createSpace(100 - winNum.length()) + ",")//第一组中 奖号码
          .append(announcement.getSaleMoneyTotal() + FileUtils
              .createSpace(13 - String.valueOf(announcement.getSaleMoneyTotal()).length())
              + ",")//本期销售 总额
          .append(
              poolInfo + FileUtils.createSpace(100 - poolInfo.length()) + ",")//本期奖池 期末余额 带促销奖池信息
          .append(String.format("%02d", announcement.getGradeCount()) + ",")//奖级个数
          .append(announcement.getPrize1Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize1Count()).length()))
              + ",")//一等奖 中奖数量
          .append(df.format(announcement.getPrize1Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize1Money()).length())
              + ",")//第一等奖中奖金额 (单注)
          .append(announcement.getPrize2Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize2Count()).length()))
              + ",")//2等奖 中奖数量
          .append(df.format(announcement.getPrize2Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize2Money()).length())
              + ",")//第2等奖中奖金额 (单注)
          .append(announcement.getPrize3Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize3Count()).length()))
              + ",")//3等奖 中奖数量
          .append(df.format(announcement.getPrize3Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize3Money()).length())
              + ",")//第3等奖中奖金额 (单注)
          .append(announcement.getPrize4Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize4Count()).length()))
              + ",")//4等奖 中奖数量
          .append(df.format(announcement.getPrize4Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize4Money()).length())
              + ",")//第4等奖中奖金额 (单注)
          .append(announcement.getPrize5Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize5Count()).length()))
              + ",")//5等奖 中奖数量
          .append(df.format(announcement.getPrize5Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize5Money()).length())
              + ",")//第5等奖中奖金额 (单注)
          .append(announcement.getPrize6Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize6Count()).length()))
              + ",")//6等奖 中奖数量
          .append(df.format(announcement.getPrize6Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize6Money()).length())
              + ",")//第6等奖中奖金额 (单注)
          .append(announcement.getPrize7Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize7Count()).length()))
              + ",")//7等奖 中奖数量
          .append(
              df.format(prize7Money) + FileUtils.createSpace(13 - df.format(prize8Money).length())
                  + ",")//第7等奖中奖金额 (单注)
          .append(announcement.getPrize8Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize8Count()).length()))
              + ",")//8等奖 中奖数量
          .append(
              df.format(prize8Money) + FileUtils.createSpace(13 - df.format(prize8Money).length())
                  + ",")//第8等奖中奖金额 (单注)
          .append(announcement.getPrize9Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize9Count()).length()))
              + ",")//9等奖 中奖数量
          .append(df.format(announcement.getPrize9Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize9Money()).length())
              + ",")//第9等奖中奖金额 (单注)
          .append(announcement.getPrize10Count() + FileUtils
              .createSpace(8 - (String.valueOf(announcement.getPrize10Count()).length()))
              + ",")//10等奖 中奖数量
          .append(df.format(announcement.getPrize10Money()) + FileUtils
              .createSpace(13 - df.format(announcement.getPrize10Money()).length())
              + ","); //第10等奖中奖金额 (单注)
    } catch (Exception e) {
      log.error("生成开奖公告电子文件异常", e);
    }
    return perBuff.toString();
  }

  /**
   * 创建开奖公告弟子文件促销奖池信息
   *
   * @param periodInfo 期信息
   * @param announcement 开奖公告数据
   * @return 奖池信息
   */
  private String createPoolInfo(ParaGamePeriodInfo periodInfo,
      LttoLotteryAnnouncement announcement) {
    StringBuilder sb = new StringBuilder();
    String periodNum = periodInfo.getPeriodNum();
    if (Constant.GameCode.GAME_CODE_SLTO.equals(periodInfo.getGameCode())) {
      sb.append(announcement.getPoolTotal().toString()); //正常奖池
      //一等奖派奖奖池
      if (promotionManager.isPromotionNext(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum)) {
        LttoLotteryAnnouncement promotionPrize1Pool =
            announcementService.getAnnocementData(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum);
        sb.append("+" + promotionPrize1Pool.getPoolTotal().toString());
      } else {
        if (Integer.parseInt(periodNum) < Integer.parseInt(PromotionCode.PROMOTION_PERIOD_END)
            && Integer.parseInt(periodNum) > Integer.parseInt(PromotionCode.PROMOTION_PERIOD_START)){
          sb.append("+-0.01");
        }
      }
      if (promotionManager.isPromotionNext(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum)) {
        LttoLotteryAnnouncement promotionPrize6Pool =
            announcementService.getAnnocementData(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
        sb.append("+" + promotionPrize6Pool.getPoolTotal().toString());
      } else {
        if (Integer.parseInt(periodNum) < Integer.parseInt(PromotionCode.PROMOTION_PERIOD_END)
            && Integer.parseInt(periodNum) > Integer.parseInt(PromotionCode.PROMOTION_PERIOD_START)){
          sb.append("+-0.01");
        }
      }
    } else {
      sb.append(announcement.getPoolTotal().toString());
    }
    return sb.toString();
  }

  /**
   * @param periodInfo 期信息
   * @param announcement 奖池信息
   * @param promotionGameCode 促销奖等编码
   * @return 促销奖奖等单注中奖金额
   */
  private BigDecimal createPromotionLevelInfo(ParaGamePeriodInfo periodInfo,
      LttoLotteryAnnouncement announcement, String promotionGameCode) {
    String periodNum = periodInfo.getPeriodNum();
    String gameCode = periodInfo.getGameCode();
    if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
      if (promotionManager.isPromotionCurr(promotionGameCode, periodNum)) {
        if (Constant.PromotionCode.PRIZE1_GAME_CODE.equals(promotionGameCode)) {
          LttoLotteryAnnouncement promotionPrize1Pool = announcementService
              .getAnnocementData(Constant.PromotionCode.PRIZE1_GAME_CODE, periodNum);
          return promotionPrize1Pool.getPrize7Money();
        } else {
          LttoLotteryAnnouncement promotionPrize6Pool = announcementService
              .getAnnocementData(Constant.PromotionCode.PRIZE6_GAME_CODE, periodNum);
          return new BigDecimal(promotionPrize6Pool.getPrize8Money());
        }
      } else {
        return new BigDecimal(Constant.PromotionCode.PROMOTION_AWARD_ZERO);
      }
    } else {
      if (Constant.PromotionCode.PRIZE1_GAME_CODE.equals(promotionGameCode)) {
        return announcement.getPrize7Money();
      } else {
        return new BigDecimal(announcement.getPrize8Money());
      }
    }
  }

}
