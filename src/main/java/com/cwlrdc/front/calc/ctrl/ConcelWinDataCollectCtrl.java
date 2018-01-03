package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatDataKey;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqi;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
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
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoDatOvduFenqiService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 弃奖汇总数据集中
 * 省码_10003_游戏期号_OVDU.RPT
 * 11_10003_2017022_OVDU.RPT
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
@Controller
public class ConcelWinDataCollectCtrl {

  private static final  Integer GRADE_COUNT = 10;
  private static final  String CANCEL_STAT_FILE_SUFFIX = ".RPT";
  private static final  String CANCEL_STAT_FILE_TYPE = "OVDU";
  @Resource
  private LttoCancelWinStatDataService lttoCancelWinStatDataService;
  @Resource
  private ParaSysparameCache systemParaCache;
  @Resource
  private FtpInfoCache ftpInfoCache;
  @Resource
  private ParaProvinceInfoService provinceInfoService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private LttoDatOvduFenqiService datOvduFenqiService;
  @Resource
  private MigratingDataManager migratingDataManager;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  /**
   * 获取所有省份弃奖统计文件
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/cancelWinDataCollect", method = RequestMethod.GET)
  public ReturnInfo winDataCollect(@RequestParam("periodNum") String periodNum,
      @RequestParam("gameCode") String gameCode) {
    try {
      long start = System.currentTimeMillis();
      log.debug("[开奖稽核系统]开始收集省弃奖汇总数据...");
      this.migratDatas(gameCode, periodNum);
      this.processFiles(gameCode, periodNum);
      log.debug("[开奖稽核系统]完成收集省弃奖汇总数据...");
      ReturnInfo info = new ReturnInfo(null, true);
      List<LttoCancelWinStatData> list = lttoCancelWinStatDataService
          .selectByGameCodeAndPeriodNum(gameCode, periodNum);
      info.setRetObj(list);
      log.info(operatorsLogManager.getLogInfo("弃奖数据集中", "数据汇总", start));
      return info;
    } catch (Exception e) {
      log.warn("收集省弃奖汇总数据异常", e);
      return new ReturnInfo("程序处理异常", false);
    }
  }

  /**
   * 获取指定省份弃奖统计数据
   */
  @ResponseBody
  @RequestMapping(value = "/CwlAuditApi/rdti/cancelWinDataCollectById", method = RequestMethod.POST)
  public ReturnInfo winDataFileById(@RequestBody SalesStatReqBean reqBean,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    String provinceId = reqBean.getProvinceId();
    String gameCode = reqBean.getGameCode();
    String periodNum = reqBean.getPeriodNum();
    this.fileBackup(gameCode, periodNum, provinceId);
    ParaProvinceInfo provinceInfo = provinceInfoService.select2Key(provinceId);
    if (supportGame(provinceInfo.getGameSupport(), gameCode)) {
      log.debug("[开奖稽核系统] 开始 获取省码:[{}],游戏:[{}],期号:[{}]弃奖统计文件", provinceId, gameCode, periodNum);
      this.processFile(gameCode, periodNum, provinceId);
      log.debug("[开奖稽核系统] 完成 获取省码[{}]游戏[{}]期号[{}]弃奖统计文件", provinceId, gameCode, periodNum);
    }
    LttoCancelWinStatData result = lttoCancelWinStatDataService
        .selectByKey(gameCode, periodNum, provinceId);
    info.setSuccess(true);
    info.setRetObj(result);
    return info;
  }

  /**
   * 查询销售数据
   */
  @ResponseBody
  @RequestMapping(value = "/query/cancel/datas/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo querySaleDatas(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest request) {
    ReturnInfo info = new ReturnInfo();
    List<LttoCancelWinStatData> lttoCancelWinStatData = lttoCancelWinStatDataService
        .selectByGameCodeAndPeriodNum(gameCode, periodNum);
    if (!CommonUtils.isEmpty(lttoCancelWinStatData)) {
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    info.setRetObj(lttoCancelWinStatData);
    return info;
  }


  private void migratDatas(String gameCode, String periodNum) {
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    for (String provinceId : realTimeTypeProvinces) {
      if (!isUploaded(gameCode, periodNum, provinceId)) {
        migratingDataManager.migratCancelWinDataRt2Ftp(gameCode, periodNum);
        migratingDataManager.migratOvduFenqiFtp2Rt(gameCode, periodNum);
      } else {
        log.debug("[{}]省,[{}]期,[{}]游戏 弃奖汇总实时数据已收集", provinceId, periodNum, gameCode);
      }
    }
  }


  private void processFiles(String gameCode, String periodNum) {
    List<ParaProvinceInfo> paraProvinceInfos = provinceInfoService
        .findByFTP(Constant.Model.RPT_FILE_FTP);
    for (ParaProvinceInfo provinceInfo : paraProvinceInfos) {
      if (Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(provinceInfo.getProvinceId())) {
        continue;
      }
      if (supportGame(provinceInfo.getGameSupport(), gameCode) && !isUploaded(gameCode, periodNum,
          provinceInfo.getProvinceId())) {
        this.processFile(gameCode, periodNum, provinceInfo.getProvinceId());
      } else {
        log.debug("[{}]省,[{}]期,[{}]游戏 弃奖文件已收集", provinceInfo.getProvinceId(), periodNum, gameCode);
      }
    }
  }

  private boolean supportGame(String gameSupport, String gameCode) {
    return gameSupport.contains(gameInfoCache.getGameName(gameCode));
  }

  private void processFile(String gameCode, String periodNum, String provinceId) {
    try {
      String localFileName = this.download(gameCode, periodNum, provinceId);
      this.parseFlie(localFileName, gameCode, periodNum, provinceId);
    } catch (Exception e) {
      log.warn("文件处理失败", e);
      throw e;
    }
  }

  private String getLocalPath(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(systemParaCache.getFtpLocalPath()).append(File.separator);
    sb.append(gameCode).append(File.separator);
    sb.append(gameCode).append("_").append(periodNum).append(File.separator);
    return sb.toString();
  }

  private String getFileName(String provinceId, String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(provinceId).append("_");
    sb.append(gameCode).append("_");
    sb.append(periodNum).append("_");
    sb.append(CANCEL_STAT_FILE_TYPE);
    sb.append(CANCEL_STAT_FILE_SUFFIX);
    return sb.toString();
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
      log.error("文件处理异常", e);
    }
    return localFileName;
  }

  //判断文件是否已经收集过
  private boolean isUploaded(String gameCode, String periodNum, String provinceId) {
    LttoCancelWinStatDataKey key = new LttoCancelWinStatDataKey();
    key.setGameCode(gameCode);
    key.setPeriodNum(periodNum);
    key.setProvinceId(provinceId);
    LttoCancelWinStatData result = lttoCancelWinStatDataService.selectByPrimaryKey(key);
    if (result == null) {
      log.warn("游戏弃奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,表信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (result.getDataStatus() == null) {
      log.warn("游戏弃奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,DataStatus 信息为空", provinceId, periodNum, gameCode);
      return false;
    }
    if (Status.OvduDataStatus.UPLOADED_SUCCESS.equals(result.getDataStatus())
        || Status.OvduDataStatus.NOFENQIOVDU.equals(result.getDataStatus())) {
      return true;
    } else {
      return false;
    }
  }


  private void fileBackup(String gameCode, String periodNum, String provinceId) {
    String localPath = this.getLocalPath(gameCode, periodNum);
    String fileName = this.getFileName(provinceId, gameCode, periodNum);
    File localFile = new File(localPath + fileName);
    if (localFile.exists()) {
      log.debug("开始备份[{}]省,[{}]期,[{}]游戏弃奖汇总文件...", provinceId, periodNum, gameCode);
      boolean b = localFile.renameTo(new File(localPath + fileName + "." + System.currentTimeMillis() + ".BAK"));
      if(b){
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏弃奖汇总文件...", provinceId, periodNum, gameCode);
      }else{
        log.debug("完成备份[{}]省,[{}]期,[{}]游戏弃奖汇总文件失败", provinceId, periodNum, gameCode);
      }

    }
  }

  @ResponseBody
  @RequestMapping(value = "/query/cancelwin/data/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
  public ReturnInfo queryCancelDataById(@PathVariable String gameCode,
      @PathVariable String periodNum, @PathVariable String provinceId) {
    ReturnInfo info = new ReturnInfo();
    LttoCancelWinStatDataKey key = new LttoCancelWinStatDataKey();
    key.setGameCode(gameCode);
    key.setPeriodNum(periodNum);
    key.setProvinceId(provinceId);
    LttoCancelWinStatData result = lttoCancelWinStatDataService.selectByPrimaryKey(key);
    if (result != null) {
      info.setRetObj(result);
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    return info;
  }

  /**
   * 孙启智提供代码
   * 根据初步初始化的总弃奖对象，对弃奖文件进行校验解析，返回解析后弃奖对象的List集合。
   * 返回弃奖对象List，当LttoCancelWinStatData对象的periodFenQiNum字段为0时，数据插入总弃奖表，否则，数据插入弃奖分期表
   */
  public void parseFlie(String filePath, String gameCode, String periodNum, String provinceId) {
    List<LttoDatOvduFenqi> fenqiList = new ArrayList<>();
    LttoCancelWinStatData cancelData = new LttoCancelWinStatData();
    cancelData.setProvinceId(provinceId);
    cancelData.setGameCode(gameCode);
    cancelData.setPeriodNum(periodNum);
    if (FileUtils.fileExist(filePath)) { //判断文件是否存在
      cancelData.setFilePath(filePath);
      cancelData.setUploadTime(System.currentTimeMillis());
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(filePath)), Charset.forName("UTF-8"));
          BufferedReader buffReader = new BufferedReader(isr)) {
        String strline = buffReader.readLine(); //读取第一行
        if (strline != null && !"".equals(strline.trim())) {//判断文件是否为空。说明：所有统计文件内容只占一行，且为第一行
          String[] datas = strline.split(","); //拆分字符串
          if (datas.length > 0) { //如果数据长度大于零
            if (datas[0].trim().equals(gameCode)) { //游戏编码是否正确
              if (datas[1].trim().equals(periodNum)) {
                if (datas[2].trim().equals(provinceId)) { //省码是否正确
                  if (!"".equals(datas[3].trim())) { //弃奖金额是否为空
                    if (Integer.valueOf(datas[4].trim()).equals(GRADE_COUNT)) { //弃奖奖级是否为10
                      //-------------------开始-----总弃奖处理---------------------------------
                      try {
                        cancelData.setGradeCount(Long.valueOf(datas[4].trim()));
                        cancelData.setAllCanceledMoney(new BigDecimal(datas[3].trim()));
                        cancelData.setCanceled1Count(Long.valueOf(datas[5].trim()));
                        cancelData.setCanceled1Money(new BigDecimal(datas[6].trim()));
                        cancelData.setCanceled2Count(Long.valueOf(datas[7].trim()));
                        cancelData.setCanceled2Money(new BigDecimal(datas[8].trim()));
                        cancelData.setCanceled3Count(Long.valueOf(datas[9].trim()));
                        cancelData.setCanceled3Money(new BigDecimal(datas[10].trim()));
                        cancelData.setCanceled4Count(Long.valueOf(datas[11].trim()));
                        cancelData.setCanceled4Money(new BigDecimal(datas[12].trim()));
                        cancelData.setCanceled5Count(Long.valueOf(datas[13].trim()));
                        cancelData.setCanceled5Money(new BigDecimal(datas[14].trim()));
                        cancelData.setCanceled6Count(Long.valueOf(datas[15].trim()));
                        cancelData.setCanceled6Money(new BigDecimal(datas[16].trim()));
                        cancelData.setCanceled7Count(Long.valueOf(datas[17].trim()));
                        cancelData.setCanceled7Money(new BigDecimal(datas[18].trim()));
                        cancelData.setCanceled8Count(Long.valueOf(datas[19].trim()));
                        cancelData.setCanceled8Money(new BigDecimal(datas[20].trim()));
                        cancelData.setCanceled9Count(Long.valueOf(datas[21].trim()));
                        cancelData.setCanceled9Money(new BigDecimal(datas[22].trim()));
                        cancelData.setCanceled10Count(Long.valueOf(datas[23].trim()));
                        cancelData.setCanceled10Money(new BigDecimal(datas[24].trim()));
                        cancelData.setCanceledPeriodDetail(datas[25].trim());
                        cancelData.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
                      } catch (Exception e) {
                        cancelData.setDataStatus(Constant.File.FILE_DETAIL_STATUS_FAILED_5);
                        log.warn("弃奖统计文件解析异常", e);
                      }
                      //-------------------结束-----总弃奖处理----------------------------------
                      //-------------------开始-----分期处理--------------------------- --------
                      try {
                        if (cancelData.getCanceledPeriodDetail().trim()
                            .equals(datas[25].trim())) { //分期详情是否正确
                          //0代表本期没有弃奖，即：总弃奖为零、无分期弃奖
                          if (!"0".equals(datas[25].trim())) {
                            int jiqi = Integer.parseInt(datas[25].substring(0, 4)); //获取弃奖期的个数
                            //--------------开始---计算分期弃奖金额总和---------
                            BigDecimal sumOvduMoney = new BigDecimal(0); //弃奖总金额
                            for (int i = 0; i < jiqi; i++) { //计算分期弃奖总金额
                              //分期弃奖金额 弃奖分期详情中各期弃奖总和同当期弃奖总额比较
                              BigDecimal subSumOvduMoney = new BigDecimal(
                                  datas[25 + 22 * i + 1].trim());
                              sumOvduMoney = sumOvduMoney.add(subSumOvduMoney); //累加分期弃奖金额
                            }
                            //--------------结束---计算分期弃奖金额总和---------
                            //--------------开始----计算弃奖个数与分期是否相等----
                            int[] ovduNum = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                            boolean ovduNumIsEqual = true;
                            for (int i = 0; i < cancelData.getGradeCount(); i++) {
                              for (int j = 0; j < jiqi; j++) {
                                ovduNum[i] = ovduNum[i] + Integer
                                    .parseInt(datas[25 + 22 * j + 2 * (i + 1) + 1].trim());
                              }
                            }
                            for (int k = 0; k < cancelData.getGradeCount(); k++) {
                              if (Integer.parseInt(datas[5 + k * 2].trim()) != ovduNum[k]) {
                                cancelData
                                    .setDataStatus(Status.OvduDataStatus.GRADENUMDTETIAL_DIFF_SUM);
                                ovduNumIsEqual = false;
                              }
                            }
                                                        /*--------------结束----计算弃奖个数与分期是否相等----*/
                            if (sumOvduMoney
                                .equals(cancelData.getAllCanceledMoney())) {//判断弃奖分期总金额是否等于总金额
                              if (ovduNumIsEqual) {
                                for (int i = 0; i < jiqi; i++) {
                                  //-------------开始-----计算分期总金额（弃奖个数*弃奖金额 相加）------------------------------
                                  double subSumOvduMoney = 0;
                                  for (int j = 0; j < cancelData.getGradeCount(); j++) {
                                    subSumOvduMoney = subSumOvduMoney + (
                                        Integer.parseInt(datas[25 + 22 * i + (j + 1) * 2 + 1].trim())
                                            * Double
                                            .valueOf(datas[25 + 22 * i + (j + 1) * 2 + 2].trim()));
                                  }
                                  //-------------结束-----计算分期总金额（弃奖个数*弃奖金额 相加）------------------------------
                                  double subOvduMoney = Double
                                      .valueOf(datas[25 + 22 * i + 1].trim());//上报分期弃奖金额

                                  if (BigDecimal.valueOf(subOvduMoney)
                                      .compareTo(BigDecimal.valueOf(subSumOvduMoney))
                                      == 0) {//判断该分期明细金额与该分期的总金额是否相等
                                    if (cancelData.getGradeCount().equals(Long.parseLong(
                                        datas[25 + 22 * i + 2].trim()))) {//判断分期奖级是否为10
                                      try {
                                        LttoDatOvduFenqi subCancelData = new LttoDatOvduFenqi();
                                        subCancelData.setGameCode(cancelData.getGameCode());//游戏编码
                                        subCancelData.setPeriodNum(cancelData.getPeriodNum());//期号
                                        subCancelData.setProvinceId(cancelData.getProvinceId());//省码
                                        subCancelData.setQiNo(cancelData.getCanceledPeriodDetail()
                                            .substring(4, cancelData.getCanceledPeriodDetail()
                                                .length()));//分期期号
                                        subCancelData.setSystemOperateTime(DateFormatUtils
                                            .format(System.currentTimeMillis(),
                                                "yyyy-MM-dd HH:mm:ss"));//入库时间
                                        subCancelData.setGradeNum(
                                            String.valueOf(cancelData.getGradeCount()));//奖级个数
                                        subCancelData.setAllOverdueMoney(
                                            new BigDecimal(datas[25 + 22 * i + 1].trim()));
                                        subCancelData.setOverdue1Num(
                                            Long.valueOf(datas[25 + 22 * i + 3].trim()));
                                        subCancelData.setOverdue1Money(
                                            new BigDecimal(datas[25 + 22 * i + 4].trim()));
                                        subCancelData.setOverdue2Num(
                                            Long.valueOf(datas[25 + 22 * i + 5].trim()));
                                        subCancelData.setOverdue2Money(
                                            new BigDecimal(datas[25 + 22 * i + 6].trim()));
                                        subCancelData.setOverdue3Num(
                                            Long.valueOf(datas[25 + 22 * i + 7].trim()));
                                        subCancelData.setOverdue3Money(
                                            new BigDecimal(datas[25 + 22 * i + 8].trim()));
                                        subCancelData.setOverdue4Num(
                                            Integer.valueOf(datas[25 + 22 * i + 9].trim()));
                                        subCancelData.setOverdue4Money(
                                            new BigDecimal(datas[25 + 22 * i + 10].trim()));
                                        subCancelData.setOverdue5Num(
                                            Long.valueOf(datas[25 + 22 * i + 11].trim()));
                                        subCancelData.setOverdue5Money(
                                            new BigDecimal(datas[25 + 22 * i + 12].trim()));
                                        subCancelData.setOverdue6Num(
                                            Long.valueOf(datas[25 + 22 * i + 13].trim()));
                                        subCancelData.setOverdue6Money(
                                            new BigDecimal(datas[25 + 22 * i + 14].trim()));
                                        subCancelData.setOverdue7Num(
                                            Long.valueOf(datas[25 + 22 * i + 15].trim()));
                                        subCancelData.setOverdue7Money(
                                            new BigDecimal(datas[25 + 22 * i + 16].trim()));
                                        subCancelData.setOverdue8Num(
                                            Long.valueOf(datas[25 + 22 * i + 17].trim()));
                                        subCancelData.setOverdue8Money(
                                            new BigDecimal(datas[25 + 22 * i + 18].trim()));
                                        subCancelData.setOverdue9Num(
                                            Long.valueOf(datas[25 + 22 * i + 19].trim()));
                                        subCancelData.setOverdue9Money(
                                            new BigDecimal(datas[25 + 22 * i + 20].trim()));
                                        subCancelData.setOverdue10Num(
                                            Long.valueOf(datas[25 + 22 * i + 21].trim()));
                                        subCancelData.setOverdue10Money(
                                            new BigDecimal(datas[25 + 22 * i + 22].trim()));
                                        cancelData
                                            .setDataStatus(Status.OvduDataStatus.UPLOADED_SUCCESS);
                                        fenqiList.add(subCancelData);
                                      } catch (Exception e) {
                                        cancelData
                                            .setDataStatus(Status.OvduDataStatus.PERIOD_ERROR);
                                        log.warn("期数据格式错误", e);
                                      }
                                    } else {
                                      cancelData
                                          .setDataStatus(Status.OvduDataStatus.PRIZELEVEL_ERROR);
                                    }
                                  } else {
                                    cancelData.setDataStatus(Status.OvduDataStatus.DETAIL_DIFF_SUM);
                                  }
                                }
                              }
                            } else {
                              cancelData.setDataStatus(Status.OvduDataStatus.DETAIL_DIFF_SUM);
                            }
                          } else {
                            cancelData.setDataStatus(Status.OvduDataStatus.NOFENQIOVDU);
                          }
                        } else {
                          cancelData.setDataStatus(Status.OvduDataStatus.UNKOWN_ERROR);
                        }
                      } catch (Exception e) {
                        cancelData.setDataStatus(Status.OvduDataStatus.UNKOWN_ERROR);
                        log.warn("分期数据格式错误", e);
                      }
                      //-------------------结束-----分期处理--------------------------- --------
                    } else {
                      cancelData.setDataStatus(Status.OvduDataStatus.GRADENUM_ERROR);
                    }
                  } else {
                    cancelData.setDataStatus(Status.OvduDataStatus.OVDUMONEY_ERROR);
                  }
                } else {
                  cancelData.setDataStatus(Status.OvduDataStatus.PROVINCE_ERROR);
                }
              } else {
                cancelData.setDataStatus(Status.OvduDataStatus.PERIOD_ERROR);
              }
            } else {
              cancelData.setDataStatus(Status.OvduDataStatus.GAMECODE_ERROR);
            }
          } else {
            cancelData.setDataStatus(Status.OvduDataStatus.UNKOWN_ERROR);
          }
        } else {
          cancelData.setDataStatus(Status.OvduDataStatus.UNKOWN_ERROR);
        }
      } catch (Exception e) {
        log.warn("弃奖统计文件解析异常", e);
        cancelData.setDataStatus(Status.OvduDataStatus.UNKOWN_ERROR);
      }
    } else {
      cancelData.setDataStatus(Status.OvduDataStatus.NOT_UPLOADED);
    }
    log.debug("省[{}]弃奖汇总数据开始入库,数据详情:[{}]", provinceId, JsonUtil.bean2JsonString(cancelData));
    lttoCancelWinStatDataService.updateByPrimaryKey(cancelData);
    log.debug("省[{}]弃奖汇总数据入库完成", provinceId);
    if (!CommonUtils.isEmpty(fenqiList)) {
      log.debug("省[{}]弃奖分期数据开始入库...", provinceId);
      try {
        datOvduFenqiService.batchInsert(fenqiList);
      } catch (Exception e) {
        log.trace("主键冲突", e);
        datOvduFenqiService.batchUpdate(fenqiList);
      }
      log.debug("省[{}]弃奖分期数据入库完成.", provinceId);
    }
  }

}
