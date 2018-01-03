package com.cwlrdc.front.calc.ctrl;

import static com.cwlrdc.front.calc.util.FileUtils.createFileName;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataExample;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataKey;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRT;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRTExample;
import com.cwlrdc.front.calc.bean.SalesStatReqBean;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.FtpInfoCache;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.MigratingDataManager;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.cwlrdc.front.rt.service.LttoProvinceSalesDataRTService;
import com.google.common.io.Files;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 销售汇总数据集中
 * 文件命名规则 11_10001_2016124_SALE.RPT
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
@Controller
public class SalesDataCollectCtrl {

  private final static String SALE_STAT_FILE_SUFFIX = ".RPT";
  private final static String SALE_STAT_FILE_TYPE = "SALE";
  private final static String FILE_NAME_CONNECTOR = "_";
  @Resource
  private LttoProvinceSalesDataService lttoProvinceSalesDataService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private LttoProvinceSalesDataRTService rtSalesDataService;
  @Resource
  private ParaSysparameCache systemParaCache;
  @Resource
  private ParaProvinceInfoService provinceInfoService;
  @Resource
  private FtpInfoCache ftpInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private MigratingDataManager migratingDataManager;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  /* 获取所有省份销量统计文件*/
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/salesCollect", method = RequestMethod.GET)
  public ReturnInfo salesDataCollect(@RequestParam("periodNum") String periodNum,
      @RequestParam("gameCode") String gameCode) {
    //处理文件
    ReturnInfo info = new ReturnInfo(true);
    try {
      long start = System.currentTimeMillis();
      this.migratDatas(gameCode, periodNum);
      this.processFiles(gameCode, periodNum);
      List<LttoProvinceSalesData> lttoProvinceSalesData =
          lttoProvinceSalesDataService.selectDatas(periodNum, gameCode);
      info.setRetObj(lttoProvinceSalesData);
      log.info(operatorsLogManager.getLogInfo("销售数据集中", "数据汇总", start));
    } catch (Exception e) {
      log.warn("获取所有省份销量统计文件异常", e);
      return new ReturnInfo("程序异常", false);
    }
    return info;
  }

  /**
   * 获取指定省份销量统计数据
   * 错误文件备份（重命名)
   * 更新数据库对应的游戏、期号、省码的销量汇总数据
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/salesCollectById", method = RequestMethod.POST)
  public ReturnInfo getSaleFileById(@RequestBody SalesStatReqBean reqBean,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    String provinceId = reqBean.getProvinceId();
    String gameCode = reqBean.getGameCode();
    String periodNum = reqBean.getPeriodNum();
    try {
      this.fileBackup(gameCode, periodNum, provinceId);
      ParaProvinceInfo provinceInfo = provinceInfoService.select2Key(provinceId);
      if (supportGame(provinceInfo.getGameSupport(), gameCode)) {
        log.debug("[新开奖系统] 开始 获取省码[{}]游戏[{}]期号[{}]的销量统计文件", provinceId, gameCode, periodNum);
        this.processFile(gameCode, periodNum, provinceId);
        log.debug("[新开奖系统] 完成 获取省码[{}]游戏[{}]期号[{}]的销量统计文件", provinceId, gameCode, periodNum);
      }
      LttoProvinceSalesData result =
          lttoProvinceSalesDataService.selectByKey(periodNum, gameCode, provinceId);
      if (result == null) {
        info.setSuccess(false);
        return info;
      }
      info.setSuccess(true);
      info.setRetObj(result);
    } catch (Exception e) {
      log.warn(" 获取省码[" + provinceId + "]游戏[" + gameCode + "]期号[" + periodNum + "]的销量统计文件", e);
      return new ReturnInfo("程序异常", false);
    }
    return info;
  }

  /**
   * 查询初始化信息
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/initProvinceInfo/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo initProvinceInfo(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo(true);
    try {
      List<LttoProvinceSalesData> provinceSaleData =
          lttoProvinceSalesDataService.initProvinceSaleData(gameCode, periodNum);
      info.setRetObj(provinceSaleData);
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
        migratingDataManager.migratProvinceSaleDataRt2Ftp(gameCode, periodNum);
      } else {
        log.debug("[{}]省,[{}]期,[{}]游戏 销售汇总实时数据已收集", provinceId, periodNum, gameCode);
      }
    }
  }

  private void processFiles(String gameCode, String periodNum) {
    List<ParaProvinceInfo> paraProvinceInfos =
        provinceInfoService.findByFTP(Constant.Model.RPT_FILE_FTP);
    for (ParaProvinceInfo provinceInfo : paraProvinceInfos) {
      if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(provinceInfo.getProvinceId())) {
        if (supportGame(provinceInfo.getGameSupport(), gameCode) && !isUploaded(gameCode, periodNum,
            provinceInfo.getProvinceId())) {
          this.processFile(gameCode, periodNum, provinceInfo.getProvinceId());
        } else {
          log.debug("[{}]省,[{}]期,[{}]游戏 销售汇总文件已收集"
              , provinceInfo.getProvinceId(), periodNum, gameCode);
        }
      }
    }
  }

  private void processFile(String gameCode, String periodNum, String provinceId) {
    try {
      String localFileName = this.download(gameCode, periodNum, provinceId);
      this.readFileAndDb(localFileName, gameCode, periodNum, provinceId);
    } catch (Exception e) {
      log.warn("文件处理失败", e);
      throw e;
    }
  }


  private boolean isFtpType(ParaProvinceInfo provinceInfo) {
    return Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp());
  }

  private boolean supportGame(String gameSupport, String gameCode) {
    return gameSupport.contains(gameInfoCache.getGameName(gameCode));
  }

  private void fileBackup(String gameCode, String periodNum, String provinceId) {
    String localPath = this.getLocalPath(gameCode, periodNum);
    String fileName = this.getFileName(provinceId, gameCode, periodNum);
    File localFile = new File(localPath + fileName);
    if (localFile.exists()) {
      log.debug("开始备份[{}]省,[{}]期,[{}]游戏销售汇总文件...", provinceId, periodNum, gameCode);
      boolean b = localFile.renameTo(new File(localPath + fileName + "." + System.currentTimeMillis() + ".BAK"));
      if(b){
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏销售汇总文件...", provinceId, periodNum, gameCode);
      }else{
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏销售汇总文件失败", provinceId, periodNum, gameCode);
      }
    }
  }

  //实时接口获取销售统计文件
  private List<LttoProvinceSalesDataRT> rtCollectSaleData(String gameCode, String periodNum) {
    //TODO 查询数美插入的数据
    LttoProvinceSalesDataRTExample example = new LttoProvinceSalesDataRTExample();
    example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
    return rtSalesDataService.selectByExample(example);
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
      log.warn("ftp 异常", e);
    }
    return localFileName;
  }

  //解析文件
  private void readFileAndDb(String filePath, String gameCode, String periodNum,
      String provinceId) {
    LttoProvinceSalesData saleData = new LttoProvinceSalesData();
    saleData.setGameCode(gameCode);
    saleData.setPeriodNum(periodNum);
    saleData.setProvinceId(provinceId);
    //游戏编码，省码，期号 校验
    try {
      if (new File(filePath).exists()) {
        saleData.setFilePath(filePath);
        saleData.setUploadTime(System.currentTimeMillis());
        saleData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        String firstLine = Files.readFirstLine(new File(filePath), Charset.forName("UTF-8"));
        String[] saledatas = firstLine.split(",");
        if (saledatas.length > 0) {
          //解析异常 打印对应错误信息
          String game = saledatas[0].trim();
          String period = saledatas[1].trim();
          String province = saledatas[2].trim();
          if (gameCode.equalsIgnoreCase(game)
              && periodNum.equalsIgnoreCase(period)
              && provinceId.equalsIgnoreCase(province)) {
            saleData.setAmount(BigDecimal.valueOf(Double.valueOf(saledatas[3].trim())));
            saleData.setCancelMoney(BigDecimal.valueOf(Double.valueOf(saledatas[4].trim())));
          }
          if (!gameCode.equalsIgnoreCase(game)) {
            saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_4);
          }
          if (!periodNum.equalsIgnoreCase(period)) {
            saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_2);
          }
          if (!provinceId.equalsIgnoreCase(province)) {
            saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_3);
          }
        } else {
          saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_5); //文件内容错误
        }
      } else {
        saleData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_FAILED_0); //文件内容错误
      }
    } catch (Exception e) {
      log.debug("文件处理异常", e);
      saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_5);
    }
    log.debug("省[{}]销售汇总数据开始入库,数据详情:[{}]", saleData.getProvinceId(),
        JsonUtil.bean2JsonString(saleData));
    lttoProvinceSalesDataService.updateByPrimaryKey(saleData);
    log.debug("销售数据入库完成");
  }

  private String getFileName(String provinceId, String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(provinceId).append("_");
    sb.append(gameCode).append("_");
    sb.append(periodNum).append("_");
    sb.append(SALE_STAT_FILE_TYPE);
    sb.append(SALE_STAT_FILE_SUFFIX);
    return sb.toString();
  }

  private String getLocalPath(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(systemParaCache.getFtpLocalPath()).append(File.separator);
    sb.append(gameCode).append(File.separator);
    sb.append(gameCode).append("_").append(periodNum).append(File.separator);
    return sb.toString();
  }

  //ftp文件获取销售统计文件
  private void ftpCollectSaleData(String gameCode, String periodNum, String provinceId) {
    ParaFtpInfo bean = ftpInfoCache.getFtpInfo(provinceId); //省对应的ftp信息
    if (null != bean) {
      LttoProvinceSalesData salesData = null;
      String localFileName = "";
      if (Constant.Model.COLLECT_FILE_FTP.equals(bean.getFlag())) { //ftp模式
        String host = bean.getFtpIp();
        String username = bean.getFtpUsername();
        String password = bean.getFtpPassword();
        Integer port = Integer.valueOf(bean.getFtpPort());
        String localPath =
            systemParaCache.getValue(Constant.File.FILE_LOCAL_PATH) + File.separator + gameCode
                + File.separator + gameCode + "_" + periodNum + File.separator;
        String ftpDirPath = bean.getFtpPath();
        String ftpFileName = FileUtils.createFileName(FILE_NAME_CONNECTOR,
            new String[]{provinceId, gameCode, periodNum, SALE_STAT_FILE_TYPE,
                SALE_STAT_FILE_SUFFIX});
        try (FtpService ftpClient = new FtpService();) {
          if (!new File(localPath + ftpFileName).exists()) {
            ftpClient.getConnect(host, port, username, password);
            ftpClient.download(ftpDirPath, ftpFileName, localPath);
            localFileName = localPath + ftpFileName;
          }
        } catch (Exception e) {
          log.error("省[{}]下载销量汇总文件异常", provinceId, e);
        }
      }
      if (Constant.Model.COLLECT_FILE_LOCAL.equals(bean.getFlag())) { //本地模式收集文件
        localFileName =
            systemParaCache.getValue(Constant.File.FILE_LOCAL_PATH) + File.separator + gameCode
                + File.separator + gameCode + "_" + periodNum + File.separator + createFileName(
                FILE_NAME_CONNECTOR,
                new String[]{provinceId, gameCode, periodNum, SALE_STAT_FILE_TYPE,
                    SALE_STAT_FILE_SUFFIX});
      }
      salesData = this.parseFlie(localFileName, provinceId, gameCode, periodNum);
      if (null != salesData) {
        this.saveData2DB(salesData);
      }
    } else {
      log.warn("省[{}]未配置ftp参数信息", provinceId);
    }
  }

  //判断文件是否已经收集过
  private boolean isUploaded(String gameCode, String periodNum, String provinceId) {
    LttoProvinceSalesData result =
        lttoProvinceSalesDataService.selectByKey(periodNum, gameCode, provinceId);
    if (result == null) {
      log.warn("游戏销售汇总文件[{}]省,[{}]期,[{}]数据存储错误,表信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (result.getDataStatus() == null) {
      log.warn("游戏销售汇总文件[{}]省,[{}]期,[{}]数据存储错误,DataStatus 信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1.equals(result.getDataStatus())) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 摇奖现场查询全国销售汇总数据上传状态
   */
  @ResponseBody
  @RequestMapping(value = "/prizelive/querycollect/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public Object prizeLiveCollect(@PathVariable String gameCode, @PathVariable String periodNum) {
    LttoProvinceSalesDataExample salesDataExample = new LttoProvinceSalesDataExample();
    salesDataExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
    List<LttoProvinceSalesData> list = lttoProvinceSalesDataService
        .selectByExample(salesDataExample);
    if (!CommonUtils.isEmpty(list) && list.size() == 33) {
      return list;
    }
    return list;
  }

  /**
   * 查询销售数据
   */
  @ResponseBody
  @RequestMapping(value = "/query/saledatas/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo querySaleDatas(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    List<LttoProvinceSalesData> list = lttoProvinceSalesDataService
        .initProvinceSaleData(gameCode, periodNum);
    if (!CommonUtils.isEmpty(list)) {
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    info.setRetObj(list);
    return info;
  }

  /**
   * 查询销售数据
   */
  @ResponseBody
  @RequestMapping(value = "/query/saledatas/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
  public ReturnInfo querySaleDatasById(@PathVariable String gameCode,
      @PathVariable String periodNum, @PathVariable String provinceId, HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    LttoProvinceSalesDataKey key = new LttoProvinceSalesDataKey();
    key.setPeriodNum(periodNum);
    key.setGameCode(gameCode);
    key.setProvinceId(provinceId);
    LttoProvinceSalesData result = lttoProvinceSalesDataService.selectByPrimaryKey(key);
    if (result != null) {
      info.setRetObj(result);
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    return info;
  }

  //解析文件
  private LttoProvinceSalesData parseFlie(String filePath, String provinceId, String gameCode,
      String periodNum) {
    LttoProvinceSalesData saleData = null;
    if (FileUtils.fileExist(filePath)) {
      log.debug("[开奖稽核系统]解析[{}]售汇总文件", filePath);
      saleData = new LttoProvinceSalesData();
      saleData.setFilePath(filePath);
      saleData.setUploadTime(System.currentTimeMillis());
      saleData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(filePath)), Charset.forName("UTF-8"));
          BufferedReader buffReader = new BufferedReader(isr)) {
        String strline = "";
        while ((strline = buffReader.readLine()) != null) {
          //根据文件格式解析
          String[] saledatas = strline.split(",");
          //游戏编码，省码，期号 校验
          if (saledatas.length > 0) {
            //解析异常 打印对应错误信息
            String game = saledatas[0].trim();
            String period = saledatas[1].trim();
            String province = saledatas[2].trim();
            if (gameCode.equalsIgnoreCase(game) && periodNum.equalsIgnoreCase(period) && provinceId
                .equalsIgnoreCase(province)) {
              saleData.setGameCode(game);
              saleData.setPeriodNum(period);
              saleData.setProvinceId(province);
              saleData.setAmount(new BigDecimal(saledatas[3].trim()));
              saleData.setCancelMoney(new BigDecimal(saledatas[4].trim()));
              saleData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
            } else {
              saleData.setGameCode(gameCode);
              saleData.setPeriodNum(periodNum);
              saleData.setProvinceId(provinceId);
              saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_3); //文件内容错误
            }
          }
        }
      } catch (IOException e) {
        log.error("省[{}]销售汇总文件解析异常", provinceId, filePath, e);
        saleData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_4); //文件内容错误
      }
    }
    return saleData;
  }

  //数据入库
  private void saveData2DB(LttoProvinceSalesData saleData) {
    log.debug("省[{}]销售汇总数据开始入库,数据详情:[{}]", saleData.getProvinceId(),
        JsonUtil.bean2JsonString(saleData));
    lttoProvinceSalesDataService.updateByPrimaryKey(saleData);
    log.debug("销售数据入库完成");
  }

}
