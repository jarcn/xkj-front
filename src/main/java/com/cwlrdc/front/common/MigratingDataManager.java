package com.cwlrdc.front.common;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqi;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.rt.entity.LttoCancelwinStatDataRT;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRT;
import com.cwlrdc.commondb.rt.entity.LttoWinstatDataRT;
import com.cwlrdc.commondb.rt.entity.OpetOverdueInfoRT;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoDatOvduFenqiService;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.rt.service.LttoCancelwinStatDataRTService;
import com.cwlrdc.front.rt.service.LttoProvinceSalesDataRTService;
import com.cwlrdc.front.rt.service.LttoWinstatDataRTService;
import com.cwlrdc.front.rt.service.OpetOverdueInfoRTService;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by chenjia on 2017/10/16.
 */
@Slf4j
@Component
public class MigratingDataManager {

  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoCancelwinStatDataRTService rtCancelwinStatDataService;
  @Resource
  private LttoCancelWinStatDataService ftpCancelWinStatDataService;
  @Resource
  private LttoWinstatDataRTService rtWinstatDataService;
  @Resource
  private LttoWinstatDataService ftpWinstatDataService;
  @Resource
  private LttoProvinceSalesDataRTService rtProvinceSalesDataService;
  @Resource
  private LttoProvinceSalesDataService ftpProvinceSalesDataService;
  @Resource
  private LttoDatOvduFenqiService ftpOvduFenqiService;
  @Resource
  private OpetOverdueInfoRTService rtOverdueInfoService;
  @Resource
  private LttoProvinceFileStatusService zipFileStatusService;

  /**
   * 弃奖数据rt表copy到ftp表
   */
  public void migratCancelWinDataRt2Ftp(String gameCode, String periodNum) {
    log.debug("[弃奖汇总数据] 迁移开始 ...");
    List<LttoCancelWinStatData> list = new ArrayList<>();
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
      List<LttoCancelwinStatDataRT> rtCancelWinDatas = rtCancelwinStatDataService
          .selectDatas(periodNum, gameCode, realTimeTypeProvinces);
      for (LttoCancelwinStatDataRT rt : rtCancelWinDatas) {
        if (Status.UploadStatus.UPLOADED_SUCCESS.equals(rt.getDataStatus())) {
          LttoCancelWinStatData data = new LttoCancelWinStatData();
          BeanCopyUtils.copyProperties(rt, data);
          list.add(data);
        } else {
          log.debug("省[{}]实时数据未上报", rt.getProvinceId());
        }
      }
      ftpCancelWinStatDataService.batchUpdate(list);
      log.debug("[弃奖汇总数据] 迁移完成");
    } else {
      log.info("无实时接口模式省份");
    }
  }

  /**
   * 弃奖分期数据rt表copy到ftp表
   */
  public void migratOvduFenqiFtp2Rt(String gameCode, String periodNum) {
    List<LttoDatOvduFenqi> list = new ArrayList<>();
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
      List<OpetOverdueInfoRT> opetOverdueInfoRTS =
          rtOverdueInfoService.selectDatas(periodNum, gameCode, realTimeTypeProvinces);
      for (OpetOverdueInfoRT rtInfo : opetOverdueInfoRTS) {
        LttoDatOvduFenqi ovduFenqi = this.rtFenqi2OverdueFtp(rtInfo);
        list.add(ovduFenqi);
      }
      try {
        log.debug("[弃奖分期数据] 迁移开始 ...");
        ftpOvduFenqiService.batchInsert(list);
        log.debug("[弃奖分期数据] 迁移完成");
      } catch (Exception e) {
        log.info("弃奖分期数据已经从RT表复制到FTP表中");
      }
    } else {
      log.info("无省份使用实时接口模式");
    }

  }


  /**
   * rt模式弃奖分期数据bean转换成Ftp模式bean
   */
  private LttoDatOvduFenqi rtFenqi2OverdueFtp(OpetOverdueInfoRT opetOverdueInfoRT) {
    LttoDatOvduFenqi ovduFenqi = new LttoDatOvduFenqi();
    ovduFenqi.setGameCode(opetOverdueInfoRT.getGameCode());
    ovduFenqi.setPeriodNum(opetOverdueInfoRT.getPeriodNum());
    ovduFenqi.setQiNo(opetOverdueInfoRT.getCancelPeriodNum());
    ovduFenqi.setProvinceId(opetOverdueInfoRT.getProvinceId());
    ovduFenqi.setGradeNum(opetOverdueInfoRT.getAwardNum().toString());
    ovduFenqi.setOverdue1Num(opetOverdueInfoRT.getOverdue1Count().longValue());
    ovduFenqi.setOverdue1Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue1Money()));
    ovduFenqi.setOverdue2Num(opetOverdueInfoRT.getOverdue2Count().longValue());
    ovduFenqi.setOverdue2Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue2Money()));
    ovduFenqi.setOverdue3Num(opetOverdueInfoRT.getOverdue3Count().longValue());
    ovduFenqi.setOverdue3Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue3Money()));
    ovduFenqi.setOverdue4Num(opetOverdueInfoRT.getOverdue4Count());
    ovduFenqi.setOverdue4Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue4Money()));
    ovduFenqi.setOverdue5Num(opetOverdueInfoRT.getOverdue5Count().longValue());
    ovduFenqi.setOverdue5Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue5Money()));
    ovduFenqi.setOverdue6Num(opetOverdueInfoRT.getOverdue6Count().longValue());
    ovduFenqi.setOverdue6Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue6Money()));
    ovduFenqi.setOverdue7Num(opetOverdueInfoRT.getOverdue7Count().longValue());
    ovduFenqi.setOverdue7Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue7Money()));
    ovduFenqi.setOverdue8Num(opetOverdueInfoRT.getOverdue8Count().longValue());
    ovduFenqi.setOverdue8Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue8Money()));
    ovduFenqi.setOverdue9Num(opetOverdueInfoRT.getOverdue9Count().longValue());
    ovduFenqi.setOverdue9Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue9Money()));
    ovduFenqi.setOverdue10Num(opetOverdueInfoRT.getOverdue10Count().longValue());
    ovduFenqi.setOverdue10Money(BigDecimal.valueOf(opetOverdueInfoRT.getOverdue10Money()));
    ovduFenqi.setAllOverdueMoney(BigDecimal.valueOf(opetOverdueInfoRT.getCurrentCancelSum()));
    return ovduFenqi;
  }

  /**
   * 销售额汇总数据rt表copy到ftp表
   */
  public void migratProvinceSaleDataRt2Ftp(String gameCode, String periodNum) {
    log.debug("[销售汇总数据] 迁移开始 ...");
    List<LttoProvinceSalesData> list = new ArrayList<>();
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
      List<LttoProvinceSalesDataRT> rtDatas = rtProvinceSalesDataService
          .selectDatas(periodNum, gameCode, realTimeTypeProvinces);
      for (LttoProvinceSalesDataRT rtData : rtDatas) {
        if (Status.UploadStatus.UPLOADED_SUCCESS.equals(rtData.getDataStatus())) {
          LttoProvinceSalesData data = new LttoProvinceSalesData();
          BeanCopyUtils.copyProperties(rtData, data);
          list.add(data);
        } else {
          log.debug("省[{}]实时数据未上报", rtData.getProvinceId());
        }
      }
      ftpProvinceSalesDataService.batchUpdate(list);
      log.debug("[销售汇总数据] 迁移完成");
    } else {
      log.info("无省份使用实时接口模式");
    }

  }


  /**
   * 中奖汇总数据rt表copy到ftp表
   */
  public void migratWinDataRt2Ftp(String gameCode, String periodNum) {
    log.debug("[中奖汇总数据] 迁移开始 ...");
    List<LttoWinstatData> list = new ArrayList<>();
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    if (!CommonUtils.isEmpty(realTimeTypeProvinces)) {
      List<LttoWinstatDataRT> lttoWinstatDataRTS = rtWinstatDataService
          .selectDatas(periodNum, gameCode, realTimeTypeProvinces);
      for (LttoWinstatDataRT rtData : lttoWinstatDataRTS) {
        if (Status.UploadStatus.UPLOADED_SUCCESS.equals(rtData.getDataStatus())) {
          LttoWinstatData ftpData = new LttoWinstatData();
          BeanCopyUtils.copyProperties(rtData, ftpData);
          list.add(ftpData);
        } else {
          log.debug("省[{}]实时数据未上报", rtData.getProvinceId());
        }
      }
      ftpWinstatDataService.batchUpdate(list);
      log.debug("[中奖汇总数据] 迁移完成");
    } else {
      log.info("无使用实时接口的省份");
    }
  }

  /**
   * 销售明细数据实时省份数据上传情况copy到ftp记录表中
   */
  public void migratZipSaleDataStatus(String gameCode, String periodNum) {
    List<LttoProvinceFileStatus> list = new ArrayList<>();
    List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
    for (String provinceId : realTimeTypeProvinces) {
      LttoProvinceFileStatus zipStatus = new LttoProvinceFileStatus();
      zipStatus.setGameCode(gameCode);
      zipStatus.setPeriodNum(periodNum);
      zipStatus.setProvinceId(provinceId);
      zipStatus.setUploadStatus(Status.UploadStatus.UPLOADED_SUCCESS);
      list.add(zipStatus);
    }
    zipFileStatusService.batchUpdate(list);
    log.debug("[实时省份销售明细数据状态更新完成]");
  }
}
