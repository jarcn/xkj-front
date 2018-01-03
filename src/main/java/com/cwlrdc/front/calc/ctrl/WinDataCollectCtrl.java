package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataExample;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataKey;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.rt.entity.LttoWinstatDataRT;
import com.cwlrdc.commondb.rt.entity.LttoWinstatDataRTExample;
import com.cwlrdc.front.calc.bean.SalesStatReqBean;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.cwlrdc.front.rt.service.LttoWinstatDataRTService;
import com.google.common.io.Files;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 中奖文件汇总数据集中
 * 解析ftp文件书库查询
 * 90_10001_2017001_WINN.RPT
 * 省码_10003_游戏期号_WINN.RPT
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
@Controller
public class WinDataCollectCtrl {

  private static final String WIN_STAT_FILE_SUFFIX = ".RPT";
  private static final String WIN_STAT_FILE_TYPE = "WINN";
  @Resource
  private LttoWinstatDataService lottWinstatDataService;
  @Resource
  private ParaSysparameCache systemParaCache;
  @Resource
  private LttoWinstatDataRTService rtWinDataService;
  @Resource
  private ParaProvinceInfoService provinceInfoService;
  @Resource
  private FtpInfoCache ftpInfoCache;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private MigratingDataManager migratingDataManager;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  /**
   * 获取所有省份中奖统计文件
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/winDataCollect", method = RequestMethod.GET)
  public ReturnInfo salesDataCollect(@RequestParam("periodNum") String periodNum,
      @RequestParam("gameCode") String gameCode) {
    ReturnInfo info = new ReturnInfo(null, true);
    long start = System.currentTimeMillis();
    try {
      log.debug("[新开奖系统] 开始 收集省中奖文件汇总数据...");
      this.migratDatas(gameCode, periodNum);
      this.processFiles(gameCode, periodNum);
      log.debug("[新开奖系统] 完成 收集省中奖汇总数据!");
      List<LttoWinstatData> lttoWinstatData = lottWinstatDataService.select2datas(gameCode, periodNum);
      info.setRetObj(lttoWinstatData);
    } catch (Exception e) {
      log.warn("获取所有省份中奖统计文件", e);
      return new ReturnInfo("程序处理异常", false);
    }
    log.info(operatorsLogManager.getLogInfo("中奖数据集中", "汇总数据", start));
    return info;
  }
  /**
   * 获取指定省份中奖统计数据
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/winDataCollectById", method = RequestMethod.POST)
  public ReturnInfo getWinFileById(@RequestBody SalesStatReqBean reqBean) {
    ReturnInfo info = null;
    try {
      info = new ReturnInfo();
      String provinceId = reqBean.getProvinceId();
      String gameCode = reqBean.getGameCode();
      String periodNum = reqBean.getPeriodNum();
      this.fileBackup(gameCode, periodNum, provinceId);
      ParaProvinceInfo provinceInfo = provinceInfoService.select2Key(provinceId);
      if (supportGame(provinceInfo.getGameSupport(), gameCode)) {
        log.debug("[中彩中心网站] 开始 获取省码:[{}],游戏:[{}],期号:[{}]中奖统计文件", provinceId, gameCode, periodNum);
        this.processFile(gameCode, periodNum, provinceId);
        log.debug("[新开奖系统] 完成 获取省码:[{}],游戏:[{}],期号:[{}]中奖统计文件", provinceId, gameCode, periodNum);
      }
      LttoWinstatData result = lottWinstatDataService.selectByKey(periodNum, gameCode, provinceId);
      info.setSuccess(true);
      info.setRetObj(result);
    } catch (Exception e) {
      log.warn("获取指定省份中奖统计数据", e);
      return new ReturnInfo("程序处理异常", false);
    }
    return info;
  }


  /**
   * 查询初始化信息
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/initProvinceWinInfo/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo initProvinceInfo(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo(true);
    try {
      List<LttoWinstatData> provinceWinData = lottWinstatDataService .select2datas(gameCode, periodNum);
      info.setRetObj(provinceWinData);
    } catch (Exception e) {
      log.warn("查询初始化信息", e);
      return new ReturnInfo("程序异常", false);
    }
    return info;
  }


  private void migratDatas(String gameCode, String periodNum) {
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    for (String provinceId : realTimeTypeProvinces) {
      if (!isUploaded(gameCode, periodNum, provinceId)) {
        migratingDataManager.migratWinDataRt2Ftp(gameCode, periodNum);
      } else {
        log.debug("[{}]省,[{}]期,[{}]游戏 销售汇总实时数据已收集", provinceId, periodNum, gameCode);
      }
    }
  }

  private void processFiles(String gameCode, String periodNum) {
    List<ParaProvinceInfo> paraProvinceInfos = provinceInfoService
        .findByFTP(Constant.Model.RPT_FILE_FTP);
    for (ParaProvinceInfo provinceInfo : paraProvinceInfos) {
      if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(provinceInfo.getProvinceId())) {
        if (supportGame(provinceInfo.getGameSupport(), gameCode) && !isUploaded(gameCode, periodNum,
            provinceInfo.getProvinceId())) {
          this.processFile(gameCode, periodNum, provinceInfo.getProvinceId());
        } else {
          log.debug("[{}]省,[{}]期,[{}]游戏 中奖数据文件已收集", provinceInfo.getProvinceId(), periodNum,
              gameCode);
        }
      }
    }
  }

  private boolean supportGame(String gameSupport, String gameCode) {
    return gameSupport.contains(gameInfoCache.getGameName(gameCode));
  }

  private void processFile(String gameCode, String periodNum, String provinceId) {
    try {
      String localFileName = this.download(gameCode, periodNum, provinceId);
      this.readFileAndDb(localFileName, gameCode, periodNum, provinceId);
    } catch (Exception e) {
      log.warn("文件处理失败", e);
    }
  }

  private void readFileAndDb(String filePath, String gameCode, String periodNum, String provinceId)
      throws IOException {
    LttoWinstatData winData = null;
    winData = new LttoWinstatData();
    winData.setGameCode(gameCode);
    winData.setPeriodNum(periodNum);
    winData.setProvinceId(provinceId);
    winData.setFilePath(filePath);
    winData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
    try {
      if (new File(filePath).exists()) {
        String strline = Files.readFirstLine(new File(filePath), Charset.forName("UTF-8"));
        String[] windatas = strline.split(",");
        if (windatas.length > 0) {
          //文件校验
          String game = windatas[0].trim();
          String period = windatas[1].trim();
          String province = windatas[2].trim();
          if (gameCode.equalsIgnoreCase(game) && periodNum.equalsIgnoreCase(period) && provinceId
              .equalsIgnoreCase(province)) {
            winData.setUploadTime(System.currentTimeMillis());
            winData.setWinDetail(windatas[3].trim());
            winData.setAllPrizeMoney(BigDecimal.valueOf(Double.valueOf(windatas[4].trim())));
            winData.setGradeCount(Long.parseLong(windatas[5].trim()));
            winData.setPrize1Count(Long.parseLong(windatas[6].trim()));
            winData.setPrize1Money(BigDecimal.valueOf(Double.valueOf(windatas[7].trim())));
            winData.setPrize2Count(Long.valueOf(windatas[8].trim()));
            winData.setPrize2Money(BigDecimal.valueOf(Double.valueOf(windatas[9].trim())));
            winData.setPrize3Count(Long.parseLong(windatas[10].trim()));
            winData.setPrize3Money(BigDecimal.valueOf(Double.valueOf(windatas[11].trim())));
            winData.setPrize4Count(Long.parseLong(windatas[12].trim()));
            winData.setPrize4Money(BigDecimal.valueOf(Double.valueOf(windatas[13].trim())));
            winData.setPrize5Count(Long.parseLong(windatas[14].trim()));
            winData.setPrize5Money(BigDecimal.valueOf(Double.valueOf(windatas[15].trim())));
            winData.setPrize6Count(Long.parseLong(windatas[16].trim()));
            winData.setPrize6Money(BigDecimal.valueOf(Double.valueOf(windatas[17].trim())));
            winData.setPrize7Count(Long.parseLong(windatas[18].trim()));
            winData.setPrize7Money(BigDecimal.valueOf(Double.valueOf(windatas[19].trim())));
            winData.setPrize8Count(Long.parseLong(windatas[20].trim()));
            winData.setPrize8Money(BigDecimal.valueOf(Double.valueOf(windatas[21].trim())));
            winData.setPrize9Count(Long.parseLong(windatas[22].trim()));
            winData.setPrize9Money(BigDecimal.valueOf(Double.valueOf(windatas[23].trim())));
            winData.setPrize10Count(Long.parseLong(windatas[24].trim()));
            winData.setPrize10Money(BigDecimal.valueOf(Double.valueOf(windatas[25].trim())));
            winData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
          }
          if (!gameCode.equalsIgnoreCase(game)) {
            winData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_4);
          }
          if (!periodNum.equalsIgnoreCase(period)) {
            winData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_2);
          }
          if (!provinceId.equalsIgnoreCase(province)) {
            winData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_3);
          }
        } else {
          winData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_5);
        }
      } else {
        winData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_FAILED_0);
      }
    } catch (Exception e) {
      log.error("文件解析异常", e);
      winData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_4);
    }
    log.debug("省[{}]中奖汇总数据开始入库,数据详情:[{}]", winData.getProvinceId(),
        JsonUtil.bean2JsonString(winData));
    lottWinstatDataService.updateByPrimaryKey(winData);
    log.debug("中奖数据入库完成");
  }

  private String download(String gameCode, String periodNum, String provinceId) {
    String localFileName = null;
    try {
      ParaFtpInfo bean = ftpInfoCache.getFtpInfo(provinceId);
      String localDirPath = this.getLocalPath(gameCode, periodNum);
      String ftpFileName = this.getFileName(provinceId, gameCode, periodNum);
      localFileName = localDirPath + ftpFileName;
      if (Constant.Model.COLLECT_FILE_FTP.equals(bean.getFlag())) {
        String host = bean.getFtpIp();
        String username = bean.getFtpUsername();
        String password = bean.getFtpPassword();
        Integer port = Integer.valueOf(bean.getFtpPort());
        String ftpDirPath = bean.getFtpPath();
        try (FtpService ftpClient = new FtpService();) {
          ftpClient.getConnect(host, port, username, password);
          ftpClient.download(ftpDirPath, ftpFileName, localDirPath);
        }
      }
    } catch (Exception e) {
      log.warn("文件处理失败", e);
    }
    return localFileName;
  }

  private String getFileName(String provinceId, String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(provinceId).append("_");
    sb.append(gameCode).append("_");
    sb.append(periodNum).append("_");
    sb.append(WIN_STAT_FILE_TYPE);
    sb.append(WIN_STAT_FILE_SUFFIX);
    return sb.toString();
  }

  private String getLocalPath(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(systemParaCache.getFtpLocalPath()).append(File.separator);
    sb.append(gameCode).append(File.separator);
    sb.append(gameCode).append("_").append(periodNum).append(File.separator);
    return sb.toString();
  }


  private void fileBackup(String gameCode, String periodNum, String provinceId) {
    String localPath = this.getLocalPath(gameCode, periodNum);
    String fileName = this.getFileName(provinceId, gameCode, periodNum);
    File localFile = new File(localPath + fileName);
    if (localFile.exists()) {
      log.debug("开始备份[{}]省,[{}]期,[{}]游戏中奖汇总文件...", provinceId, periodNum, gameCode);
      boolean b = localFile
          .renameTo(new File(localPath + fileName + "." + System.currentTimeMillis() + ".BAK"));
      if (b){
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏中奖汇总文件...", provinceId, periodNum, gameCode);
      }else {
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏中奖汇总文件失败", provinceId, periodNum, gameCode);
      }
    }
  }

  @ResponseBody
  @RequestMapping(value = "/query/windatas/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo queryWinDatas(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    LttoWinstatDataExample example = new LttoWinstatDataExample();
    example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
        .andDataStatusEqualTo(Status.UploadStatus.UPLOADED_SUCCESS);
    List<LttoWinstatData> list = lottWinstatDataService.selectByExample(example);
    if (!CommonUtils.isEmpty(list)) {
      info.setRetObj(list);
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    return info;
  }

  //实时接口获取中奖统计文件
  private List<LttoWinstatDataRT> rtCollectWinData(String gameCode, String periodNum) {
    //TODO 查询数美插入的数据
    LttoWinstatDataRTExample example = new LttoWinstatDataRTExample();
    example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
    return rtWinDataService.selectByExample(example);
  }

  //判断文件是否已经收集过
  private boolean isUploaded(String gameCode, String periodNum, String provinceId) {
    LttoWinstatDataKey key = new LttoWinstatDataKey();
    key.setGameCode(gameCode);
    key.setPeriodNum(periodNum);
    key.setProvinceId(provinceId);
    LttoWinstatData result = lottWinstatDataService.selectByPrimaryKey(key);
    if (result == null) {
      log.warn("游戏中奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,表信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (result.getDataStatus() == null) {
      log.warn("游戏中奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,DataStatus 信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1.equals(result.getDataStatus())) {
      return true;
    } else {
      return false;
    }
  }
}
