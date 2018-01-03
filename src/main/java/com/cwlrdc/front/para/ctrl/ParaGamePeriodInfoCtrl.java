package com.cwlrdc.front.para.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoExample;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoKey;
import com.cwlrdc.commondb.para.entity.ParaHolidayManage;
import com.cwlrdc.commondb.para.entity.ParaHolidayManageExample;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.BatchGenPeriodNumUtil;
import com.cwlrdc.front.calc.util.DateUtil;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.calc.util.GenPeriodInfoExcelUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.GameCode;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.GamePeriodBatch;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PeriodManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.para.service.OperatorsService;
import com.cwlrdc.front.para.service.ParaFtpInfoService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaHolidayManageService;
import com.cwlrdc.front.task.LttoProvinceFileDownloadContainer;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.unlto.twls.commonutil.component.BeanCopyUtils;
import com.unlto.twls.commonutil.component.CommonUtils;
import com.unlto.twls.commonutil.component.JsonUtil;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/paraGamePeriodInfo")
public class ParaGamePeriodInfoCtrl {

  @Resource
  private OperatorsLogManager operatorsLogManager;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private ParaGamePeriodInfoService dbService;
  @Resource
  private ParaSysparameCache sysparameCache;
  @Resource
  private ParaFtpInfoService ftpInfoService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ParaHolidayManageService holidayManagerService;
  @Resource
  private OperatorsService operatorsService;
  @Resource
  private LttoLotteryAnnouncementService announcementService;
  @Autowired
  private PeriodManager periodManager;
  @Resource
  private LttoProvinceFileDownloadContainer fileDownloadContainer;

  public static final String HOLIDAY_SUSPENDED = "1";
  public static final String WORKINGDAY_TYPE = "1";
  public static final String COMMA = ",";
  public static final String COLON = ":";
  public static final int STARTPERIOD_NUM = 1;

  @RequestMapping(value = "", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo insert(@RequestBody ParaGamePeriodInfo info, HttpServletRequest req) {
    try {
      info.setPeriodBeginTime(
          DateFormatUtils.format(Long.parseLong(info.getPeriodBeginTime()), "yyyy-MM-dd"));
      info.setPeriodEndTime(
          DateFormatUtils.format(Long.parseLong(info.getPeriodEndTime()), "yyyy-MM-dd"));
      info.setLotteryDate(
          DateFormatUtils.format(Long.parseLong(info.getLotteryDate()), "yyyy-MM-dd"));
      info.setCashEndTime(
          DateFormatUtils.format(Long.parseLong(info.getCashEndTime()), "yyyy-MM-dd"));
      info.setPeriodYear(Integer.valueOf(info.getPeriodNum().substring(0, 4)));
      dbService.insert(info);
      return ReturnInfo.Success;
    } catch (DuplicateKeyException e) {
      log.trace("主键冲突", e);
      return new ReturnInfo("添加失败,不可重复添加", false);
    } catch (Exception e) {
      log.warn("  paraGamePeriodInfo insert error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo batchDelete(@RequestBody List<String> data, HttpServletRequest req) {
    try {
      List<ParaGamePeriodInfo> list = new ArrayList<>();
      for (String id : data) {
        ParaGamePeriodInfo info = new ParaGamePeriodInfo();
        KeyExplainHandler.explainKey(id, info);
        list.add(info);
      }
      dbService.batchDelete(list);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl batchDelete error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
  @ResponseBody
  public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
    try {
      ParaGamePeriodInfo info = new ParaGamePeriodInfo();
      KeyExplainHandler.explainKey(key, info);
      dbService.deleteByPrimaryKey(info);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl delete by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo update(@PathVariable String key, @RequestBody ParaGamePeriodInfo info,
      HttpServletRequest req) {
    try {
      ParaGamePeriodInfo oldPojo = null;
      if (info != null) {
        KeyExplainHandler.explainKey(key, info);
        oldPojo = dbService.selectByPrimaryKey(info);
        BeanCopyUtils.copyProperties(info, oldPojo);
        oldPojo.setCashEndTime(
            DateFormatUtils.format(Long.parseLong(info.getCashEndTime()), "yyyy-MM-dd"));
        oldPojo.setPeriodBeginTime(
            DateFormatUtils.format(Long.parseLong(info.getPeriodBeginTime()), "yyyy-MM-dd"));
        oldPojo.setPeriodEndTime(
            DateFormatUtils.format(Long.parseLong(info.getPeriodEndTime()), "yyyy-MM-dd"));
        oldPojo.setLotteryDate(
            DateFormatUtils.format(Long.parseLong(info.getLotteryDate()), "yyyy-MM-dd"));
        dbService.updateByPrimaryKey(oldPojo);
      }
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl update by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.GET)
  @ResponseBody
  public ListInfo<ParaGamePeriodInfo> get(@PathVariable String key, HttpServletRequest req) {
    int totalCount = 1;
    List<ParaGamePeriodInfo> list = new ArrayList<>();
    try {
      ParaGamePeriodInfo info = new ParaGamePeriodInfo();
      KeyExplainHandler.explainKey(key, info);
      list.add(dbService.selectByPrimaryKey(info));
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl get by key error..", e);
    }
    return new ListInfo<>(totalCount, list, 0, 1);
  }

  @RequestMapping(value = "/select/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public List<ParaGamePeriodInfo> select(@PathVariable String gameCode,
      @PathVariable String periodNum) {
    List<ParaGamePeriodInfo> paraGamePeriodInfo = null;
    try {
      ParaGamePeriodInfoExample paraGamePeriodInfoExample = new ParaGamePeriodInfoExample();
      paraGamePeriodInfoExample.createCriteria().andGameCodeEqualTo(gameCode)
          .andPeriodNumEqualTo(periodNum);
      paraGamePeriodInfo = dbService.selectByExample(paraGamePeriodInfoExample);
      return paraGamePeriodInfo;
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl select error..", e);
    }
    return paraGamePeriodInfo;
  }

  private String getControllerName() {
    return this.getClass().getSimpleName();
  }

  private String getTableName() {
    return "T_PARA_GAME_PERIOD_INFO";
  }

  @SuppressWarnings("serial")
  public static class ParaGamePeriodInfos extends ArrayList<ParaGamePeriodInfo> {

    public ParaGamePeriodInfos() {
      super();
    }
  }


  /**
   * 导出开奖号码文件
   */
  @RequestMapping(value = "/export/winnumfile/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo exportWinNumFile(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req, HttpServletResponse resp) {
    log.debug("[开奖稽核系统]开始导出游戏:{}期号:{}开奖号码文件", gameCode, periodNum);
    long start = System.currentTimeMillis();
    ParaGamePeriodInfo periodInfo = dbService.selectbyKey(gameCode, periodNum);
    String winNum = null;
    if (StringUtils.isNotBlank(periodInfo.getWinNum())) {
      winNum = FileUtils.getCurrWinNum(gameCode, periodInfo.getWinNum());
    } else {
      return new ReturnInfo("开奖号码不存在", false);
    }
    LttoLotteryAnnouncement lttoLotteryAnnouncement = announcementService
        .selectByKey(gameCode, periodNum);
    Integer winGroupCount = lttoLotteryAnnouncement.getWinGroupCount();

    StringBuilder winBuff = this.getNumbDwn(gameCode, periodNum, winNum, winGroupCount);
    //文件导出服务器本地文件夹
    String filePath = sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH) + File.separator
        + gameCode + File.separator + gameCode + "_" + periodNum;
    File filedir = new File(filePath);
    if (!filedir.exists()) {
      boolean mkdirs = filedir.mkdirs();
    }
    String fileName =
        Constant.Key.PROVINCEID_OF_CWL + "_" + gameCode + "_" + periodNum + "_NUMB.DWN";
    String filePathName = filePath + File.separator + fileName;
    File winFile = new File(filePathName);
    try (FileOutputStream fileOut = new FileOutputStream(winFile)) {
      fileOut.write(winBuff.toString().getBytes(Charset.forName("UTF-8")));
    } catch (Exception e) {
      log.error("开奖号码文件导出异常", e);
      return ReturnInfo.Faild;
    }
    log.debug("开奖号码文件导出路径为:{}", filePathName);
    log.info(operatorsLogManager.getLogInfo("发布中奖号码", "导出文件", start));
    return ReturnInfo.Success;
  }

  /**
   * 获取中奖号码文件内容
   */
  private StringBuilder getNumbDwn(String gameCode, String periodNum, String winNum,
      int winNumberNum) {
    StringBuilder winBuff = new StringBuilder();
    winBuff.append(gameCode + ",")
        .append(periodNum + FileUtils.createSpace(12 - periodNum.length()) + ",")
        .append(Constant.Key.PROVINCEID_OF_CWL + ",")
        .append(String.format("%02d", winNumberNum) + ",")
        .append(winNum + FileUtils.createSpace(100 - winNum.length())).append(",");
    return winBuff;
  }

  /**
   * 中奖号码电子文件下发全国
   * 下发到配置ftp有效的下发到ftp服务器，ftp配置无效的下发到本地
   */
  @RequestMapping(value = "/deplay/winnumfile/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo winFileDeplay(@PathVariable String gameCode, @PathVariable String periodNum) {
    ReturnInfo info = new ReturnInfo();
    String fileName =
        Constant.Key.PROVINCEID_OF_CWL + "_" + gameCode + "_" + periodNum + "_NUMB.DWN";
    long start = System.currentTimeMillis();
    //导出本地文件，将本地文件下发至全国各省ftp服务器上
    String localFilePath = this.getLocalPath(gameCode, periodNum, fileName);
    File winNumFile = new File(localFilePath);
    if (winNumFile.exists()) {
      List<ParaFtpInfo> ftpInfos = ftpInfoService.findAll();
      for (ParaFtpInfo ftp : ftpInfos) {
        ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(ftp.getProvinceId());
        if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
          if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(ftp.getProvinceId())) {
            if (Constant.Model.COLLECT_FILE_FTP.equals(ftp.getFlag())) {
              String host = ftp.getFtpIp();
              String username = ftp.getFtpUsername();
              String password = ftp.getFtpPassword();
              Integer port = Integer.valueOf(ftp.getFtpPort());
              try (FtpService ftpClient = new FtpService()) {
                ftpClient.getConnect(host, port, username, password);
                String ftpDir = ftp.getFtpPath();
                ftpClient.upload(ftpDir, localFilePath);
                log.debug("开奖号码文件下发成功,文件路径{}", ftpDir);
                info.setDescription("开奖文件下发全国完成");
                info.setSuccess(true);
              } catch (Exception e) {
                log.error("开奖号码文件下发全国异常", e);
              }
            } else {
              if (new File(localFilePath).exists()) {
                log.debug("开奖号码文件下发成功,文件路径{}", localFilePath);
                info.setDescription("开奖文件下发全国完成");
                info.setSuccess(true);
              }
            }
          }
        } else {
          log.info("省[{}]为实时接口模式", provinceInfo.getProvinceId());
        }
      }
    } else {
      info.setSuccess(false);
      info.setDescription("开奖文件未导出");
    }
    log.info(operatorsLogManager.getLogInfo("发布中奖号码", "下发全国", start));
    return info;
  }


  private String getLocalPath(String gameCode, String periodNum, String fileName) {
    StringBuilder sb = new StringBuilder();
    sb.append(sysparameCache.getValue(Constant.File.FILE_LOCAL_PATH));
    sb.append(File.separator);
    sb.append(gameCode);
    sb.append(File.separator);
    sb.append(gameCode).append("_").append(periodNum);
    sb.append(File.separator);
    sb.append(fileName);
    return sb.toString();
  }


  /**
   * 获取上一期的期信息
   */
  @RequestMapping(value = "/query/last/period/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo queryLastPeriod(@PathVariable String gameCode, @PathVariable String periodNum,
      HttpServletRequest req) {
    ReturnInfo info = new ReturnInfo();
    ParaGamePeriodInfoExample gamePeriodInfoExample = new ParaGamePeriodInfoExample();
    gamePeriodInfoExample.createCriteria().andGameCodeEqualTo(gameCode)
        .andPeriodNumLessThan(periodNum);
    List<ParaGamePeriodInfo> periodInfos = dbService.selectByExample(gamePeriodInfoExample);
    if (!CommonUtils.isEmpty(periodInfos)) {
      Collections.sort(periodInfos); //按照期号进行排序
      ParaGamePeriodInfo periodInfo = periodInfos.get(0);
      info.setRetObj(periodInfo);
      info.setSuccess(true);
    } else {
      info.setSuccess(false);
    }
    return info;
  }

  @RequestMapping(value = "/insertWinNum/{gameCode}/{periodNum}/{redNum}/{blueNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo insertWinNum(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String redNum, @PathVariable String blueNum) {
    try {
      long start = System.currentTimeMillis();
      ParaGamePeriodInfo paraGamePeriodInfo = dbService.queryWinNum(gameCode, periodNum);
      if (paraGamePeriodInfo == null) {
        return new ReturnInfo("处理错误,没有当前期", false);
      }
      if (StringUtils.isNotBlank(redNum)) {
        String[] split = redNum.split(",");
        StringBuilder winN = new StringBuilder();
        String winNum = "";
        for (String s : split) {
          winN.append(s + ",");
        }
        //10003七乐彩 @ 分隔 10001双色球 + 分隔
        if (gameCode.equals(GameCode.GAME_CODE_LOTO)) {
          winNum = winN.toString().substring(0, winN.length() - 1) + "@";
        } else {
          winNum = winN.toString().substring(0, winN.length() - 1) + "+";
        }
        paraGamePeriodInfo.setWinNum(winNum + blueNum);
        dbService.updateByPrimaryKey(paraGamePeriodInfo);
        log.info(operatorsLogManager.getLogInfo("发布中奖号码", "保存号码", start));
        return ReturnInfo.Success;
      }
      return ReturnInfo.Faild;
    } catch (Exception e) {
      log.warn("录入期号错误", e);
      return new ReturnInfo("处理错误,请联系运维人员", false);
    }
  }

  //批量生成期号
  @ResponseBody
  @RequestMapping(value = "/batch/generate/period", method = RequestMethod.POST)
  public ReturnInfo genPeriodNums(@RequestBody GamePeriodBatch info) {
    log.debug("批量生成期号,请求参数:{}", JsonUtil.bean2JsonString(info));
    String gameCode = info.getGameCode();
    String startTime = info.getStartTime();
    String endTime = info.getEndTime();
    boolean startTimeValidFlag = isStartTimeValid(gameCode, startTime);
    if (!startTimeValidFlag) {
      log.warn("开始时间[" + startTime + "]当天不是开奖日.");
      return new ReturnInfo("开始时间必须选择开奖日.", 0, null, false);
    }

    boolean timeRangeFlag = checkTimeRange(startTime, endTime);
    if (!timeRangeFlag) {
      log.warn("开始时间[" + startTime + "] 结束时间[" + endTime + "]时间跨度过大.");
      return new ReturnInfo("时间范围错误.", 0, null, false);
    }

    ParaHolidayManageExample holidayExample = new ParaHolidayManageExample();
    holidayExample.createCriteria().andYearGreaterThanOrEqualTo(endTime.substring(0, 4));
    List<ParaHolidayManage> holidays = holidayManagerService.selectByExample(holidayExample);
    List<String> suspendedlist = new ArrayList<>();
    List<String> workdaylist = new ArrayList<>();
    List<String> nonworkdaylist = new ArrayList<>();
    try {
      if (holidays != null && holidays.size() > 0) {
        for (ParaHolidayManage holiday : holidays) {
          // 非工作日汇总
          nonworkdaylist.addAll(holidayManagerService.buildNonWorkingdayList(holiday));
          if (HOLIDAY_SUSPENDED.equals(holiday.getHolidayType() + "")) {
            suspendedlist.addAll(holidayManagerService.buildSupsenedList(holiday));
          } else {
            workdaylist.addAll(holidayManagerService.buildWorkingdayList(holiday));
          }
        }
      }
      int currentyear = Integer.parseInt(info.getEndTime().substring(0, 4));
      Map<String, Long> lastYearPeriodCancelWindateMap = getLastYearPeriodCancelWindateMap(
          (currentyear - 1), info.getGameCode());
      Map<Integer, Integer> lastYearPeriodweekCountMap = getLastYearPeriodWeekCountMap(
          (currentyear - 1), info.getGameCode());
      List<ParaGamePeriodInfo> periodInfos = BatchGenPeriodNumUtil
          .generate(info, suspendedlist, workdaylist, nonworkdaylist,
              lastYearPeriodCancelWindateMap, lastYearPeriodweekCountMap);
      log.debug("游戏:[{}]生成期号入库开始", info.getGameCode());
      dbService.batchInsert(periodInfos);
      log.debug("游戏:[{}]生成期号入库完成", info.getGameCode());
    } catch (DuplicateKeyException e) {
      log.warn("主键冲突", e);
      return new ReturnInfo("期号已存在!", false);
    } catch (Exception e) {
      log.error("游戏:[{}]生成期号入库异常", info.getGameCode(), e);
      return new ReturnInfo("期号生成失败!", false);
    }
    return ReturnInfo.Success;
  }

  private boolean isStartTimeValid(String gameCode, String startTime) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(DateUtil.parseDate(startTime, "yyyy-MM-dd"));
    int dayOfweek = cal.get(Calendar.DAY_OF_WEEK);
    if (GameCode.GAME_CODE_SLTO.equals(gameCode)
        && (dayOfweek == Calendar.SUNDAY || dayOfweek == Calendar.TUESDAY
            || dayOfweek == Calendar.THURSDAY)) {
      return true;
    } else if (GameCode.GAME_CODE_LOTO.equals(gameCode)
        && (dayOfweek == Calendar.MONDAY || dayOfweek == Calendar.WEDNESDAY
            || dayOfweek == Calendar.FRIDAY)) {
      return true;
    }
    return false;
  }

  private boolean checkTimeRange(String starttime, String endtime) {
    int startYear = Integer.parseInt(starttime.substring(0, 4));
    int endYear = Integer.parseInt(endtime.substring(0, 4));
    if (endYear - startYear > 2 || endYear < startYear) {
      return false;
    }
    return true;
  }

  private Map<String, Long> getLastYearPeriodCancelWindateMap(int lastyear, String gameCode) {
    Map<String, Long> lastyearperiodcancelmap = new HashMap<String, Long>();
    ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
    example.createCriteria().andPeriodYearEqualTo(lastyear).andGameCodeEqualTo(gameCode);
    List<ParaGamePeriodInfo> lastyearPeriod = dbService.selectByExample(example);
    if (lastyearPeriod != null && lastyearPeriod.size() > 0) {
      for (ParaGamePeriodInfo period : lastyearPeriod) {
        lastyearperiodcancelmap.put(period.getPeriodNum(), period.getCancelWinDate());
      }
    }
    return lastyearperiodcancelmap;
  }

  private Map<Integer, Integer> getLastYearPeriodWeekCountMap(int lastyear, String gameCode) {
    Map<Integer, Integer> lastyearperiodcancelmap = new HashMap<Integer, Integer>();
    ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
    example.createCriteria().andPeriodYearEqualTo(lastyear).andGameCodeEqualTo(gameCode);
    List<ParaGamePeriodInfo> lastyearPeriod = dbService.selectByExample(example);
    if (lastyearPeriod != null && lastyearPeriod.size() > 0) {
      for (ParaGamePeriodInfo period : lastyearPeriod) {
        if (lastyearperiodcancelmap.containsKey(period.getPeriodWeek())) {
          int current = lastyearperiodcancelmap.get(period.getPeriodWeek());
          current++;
          lastyearperiodcancelmap.put(period.getPeriodWeek(), current);
        } else {
          lastyearperiodcancelmap.put(period.getPeriodWeek(), 1);
        }
      }
    }
    return lastyearperiodcancelmap;
  }


  @SuppressWarnings("rawtypes")
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseBody
  public Object get(@RequestJsonParam(value = "query", required = false) QueryMapperBean info,
      @RequestJsonParam(value = "fields", required = false) FieldsMapperBean fmb,
      PageInfo para, HttpServletRequest req) {
    int totalCount = 0;
    List<HashMap<String, Object>> list = null;
    try {
      DbCondi dc = new DbCondi();
      dc.setEntityClass(ParaGamePeriodInfo.class);
      dc.setKeyClass(ParaGamePeriodInfoKey.class);
      dc.setQmb(info);
      dc.setPageinfo(para);
      dc.setFmb(fmb);
      dc.setTalbeName(getTableName());
      totalCount = dbService.getCount(dc);
      list = dbService.getData(dc);
    } catch (Exception e) {
      log.warn("  ParaGamePeriodInfoCtrl get error..", e);

    }
    if (para.isPage()) {
      return new ListInfo<>(totalCount, list, para);
    } else {
      return list;
    }
  }

  //首页手动修改当前期游戏 将该期状态修改为5
  @RequestMapping(value = "/updatePeriod/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public void updatePeriod(@PathVariable String gameCode, @PathVariable String periodNum) {
    ParaGamePeriodInfoKey paraGamePeriodInfoKey = new ParaGamePeriodInfoKey();
    paraGamePeriodInfoKey.setGameCode(gameCode);
    paraGamePeriodInfoKey.setPeriodNum(periodNum);
    ParaGamePeriodInfo paraGamePeriodInfo = dbService.selectByPrimaryKey(paraGamePeriodInfoKey);
    ParaGamePeriodInfoExample pgpie = new ParaGamePeriodInfoExample();
    pgpie.createCriteria().andStatusEqualTo(2);//2表示当前开奖期
    List<ParaGamePeriodInfo> paraGamePeriodInfos = dbService.selectByExample(pgpie);
    if (null != paraGamePeriodInfos && paraGamePeriodInfos.size() == 2) {
      for (ParaGamePeriodInfo p : paraGamePeriodInfos) {
        if (p.getGameCode().equals(gameCode) && p.getPeriodNum().equals(periodNum)) {
          if (null != paraGamePeriodInfo) {
            paraGamePeriodInfo.setFlowNode("1");//手动设置为当前期 0 非 1 当前期
            dbService.updateByPrimaryKey(paraGamePeriodInfo);
          } else {
            log.warn("手动设置当前期错误，没有当前游戏期号{}{}", gameCode, periodNum);
          }
        } else {
          p.setFlowNode("0");
          dbService.updateByPrimaryKey(p);
        }
      }
    }
  }

  //首页手动修改当前期游戏 将该期状态修改为5
  @RequestMapping(value = "/hasperiods/{gameCode}/{startPeriodNum}/{endPeriodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo hasPeriods(@PathVariable String gameCode, @PathVariable String startPeriodNum,
      @PathVariable String endPeriodNum) {
    if (StringUtils.isBlank(startPeriodNum) && StringUtils.isBlank(endPeriodNum) && StringUtils
        .isBlank(gameCode)) {
      return new ReturnInfo("参数错误", false);
    }
    if (Integer.valueOf(startPeriodNum).compareTo(Integer.valueOf(endPeriodNum)) > 0) {
      return new ReturnInfo("开始期号不能大于截止期号", false);
    }
    List<ParaGamePeriodInfo> periodInfos = dbService
        .selectBetweenPeriod(gameCode, startPeriodNum, endPeriodNum);
    if (!CommonUtils.isEmpty(periodInfos)) {
      return ReturnInfo.Success;
    } else {
      return ReturnInfo.Faild;
    }
  }

  //导出指定期号的期参数文件
  @RequestMapping(value = "/exportperiodinfo/{gameCode}/{startPeriodNum}/{endPeriodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo exportPeriodInfo2Excel(@PathVariable String gameCode,
      @PathVariable String startPeriodNum,
      @PathVariable String endPeriodNum, HttpServletRequest request, HttpServletResponse response) {
    ReturnInfo info = new ReturnInfo();
    List<ParaGamePeriodInfo> periodInfos = dbService
        .selectBetweenPeriod(gameCode, startPeriodNum, endPeriodNum);
    if (!CommonUtils.isEmpty(periodInfos)) {
      info.setSuccess(true);
      String gameName = gameInfoCache.getGameName(gameCode);
      String startYear = startPeriodNum.substring(0, 4);
      String endYear = endPeriodNum.substring(0, 4);
      String headTile = startYear + "-" + endYear + "年" + gameName + "周期库";
      HSSFWorkbook wb = GenPeriodInfoExcelUtil.genPeriodInfoExcel(periodInfos, headTile);
      //导出报表
      String agents = request.getHeader("user-agent");
      try {
        if (agents.contains("Firefox")) {
          headTile = Base64Encode.base64EncodeFileName(headTile + ".xls");
        } else {
          headTile = URLEncoder.encode(headTile + ".xls", "utf-8");
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + headTile);
        try (OutputStream outputStream = response.getOutputStream()) {
          wb.write(outputStream);
        } catch (Exception e) {
          log.warn("文件导出失败", e);
        }
        info.setSuccess(true);
        log.debug("[新开奖系统] 完成导出开奖数据");
      } catch (IOException e) {
        log.error("[新开奖稽核系统]导出[弃奖报告]异常", e);
      }
    } else {
      info.setSuccess(false);
      info.setDescription("无期信息");
    }
    return info;
  }


  //首页手动修改当前期游戏 将该期状态修改为5
  @RequestMapping(value = "/updatePeriod/{openGameCode}/{openPeriodNum}/{closeGameCode}/{closePeriodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo updatePeriod(@PathVariable String openGameCode,
      @PathVariable String openPeriodNum, @PathVariable String closeGameCode,
      @PathVariable String closePeriodNum) {
    ReturnInfo info = dbService
        .updatePeriod(openGameCode, openPeriodNum, closeGameCode, closePeriodNum);
    return info;
  }

  @RequestMapping(value = "/currentPeriod", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo currentPeriod(@RequestParam("gameCode") String gameCode) {
    ReturnInfo result = null;
    try {
      List<ParaGamePeriodInfo> paraGamePeriodInfos = dbService.selectCurrentGame(gameCode);
      if (paraGamePeriodInfos == null || paraGamePeriodInfos.size() <= 0) {
        return new ReturnInfo("系统错误,游戏[" + gameCode + "]没有当前期,请联系运维人员", false);
      }

      if (paraGamePeriodInfos.size() > 1) {
        return new ReturnInfo("系统错误,游戏[" + gameCode + "]多个当前期,请联系运维人员", false);
      }

      ParaGamePeriodInfo paraGamePeriodInfo = paraGamePeriodInfos.get(0);

      result = new ReturnInfo("查询成功", true);
      result.setRetObj(paraGamePeriodInfo.getPeriodNum());
    } catch (Exception e) {
      log.warn("查询游戏期号异常,gameCode[" + gameCode + "]", e);
      return new ReturnInfo("查询异常", false);
    }
    return result;
  }

  @RequestMapping(value = "/prevPeriod", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo getPrevPeriod(@RequestParam("gameCode") String gameCode,
      @RequestParam("periodNum") String periodNum) {
    ReturnInfo result = null;
    try {
      String prevPeriodNum = dbService.prevPeriodNum(gameCode, periodNum);
      if (prevPeriodNum == null) {
        return new ReturnInfo("系统错误,游戏[" + gameCode + "]没有当前期,请联系运维人员", false);
      }
      result = new ReturnInfo("查询成功", true);
      result.setRetObj(prevPeriodNum);
    } catch (Exception e) {
      log.warn("查询游戏期号异常,gameCode[" + gameCode + "]", e);
      return new ReturnInfo("查询异常", false);
    }
    return result;
  }

@Resource
private LttoProvinceFileDownloadContainer downloadContainer;

  @RequestMapping(value = "/currentGame", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo setCurrentGame(@RequestParam("gameCode") String gameCode) {
    long start = System.currentTimeMillis();
    try {
      List<ParaGamePeriodInfo> paraGamePeriodInfos = dbService.select2current();

      if (paraGamePeriodInfos == null || paraGamePeriodInfos.size() <= 0) {
        return new ReturnInfo("系统错误,游戏没有当前期,请联系运维人员", false);
      }

      for (ParaGamePeriodInfo info : paraGamePeriodInfos) {
        if (info.getGameCode().equals(gameCode)) {
          info.setFlowNode(Status.FlowNode.OPEN);
//          operatorsService.setOperators(info.getPeriodNum(), info.getProvinceId(), gameCode);
        } else {
          info.setFlowNode(Status.FlowNode.CLOSE);
        }
      }
      dbService.batchUpdate(paraGamePeriodInfos);
    } catch (Exception e) {
      log.warn("更新当前期异常", e);
      return new ReturnInfo("更新当前期异常", false);
    }

    try {
      log.info(operatorsLogManager.getLogInfo("首页", "用户自定义选择当前期游戏", start));
      fileDownloadContainer.reload(); //重新加载zip文件下载线程
      log.debug("手动选择游戏[{}],重新加载ZIP文件下载线程", gameCode);
    } catch (RejectedExecutionException e) {
      log.debug("手动切换加载ZIP文件下载线程异常", e);
    }

    return ReturnInfo.Success;
  }


  @RequestMapping(value = "/currentGame", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo getCurrentGame() {
    try {
      ParaGamePeriodInfo currentGameAndPeriod = periodManager.getCurrentGameAndPeriod();
      if (currentGameAndPeriod == null) {
        return new ReturnInfo("系统没有当前期,请联系运维人员", false);
      }

      ReturnInfo result = new ReturnInfo("查询成功", true);

      Map<String, String> infoMap = new HashMap<>();
      infoMap.put("gameCode", currentGameAndPeriod.getGameCode());
      infoMap.put("periodNum", currentGameAndPeriod.getPeriodNum());
      result.setRetObj(infoMap);
      return result;
    } catch (Exception e) {
      log.warn("查询游戏期号异常", e);
      return new ReturnInfo("查询异常", false);
    }
  }


  @RequestMapping(value = "/prevPeriodInfo", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo getPrevPeriodInfo() {
    try {
      ParaGamePeriodInfo currentGameAndPeriod = periodManager.getCurrentGameAndPeriod();
      if (currentGameAndPeriod == null) {
        return new ReturnInfo("系统没有当前期,请联系运维人员", false);
      }

      ParaGamePeriodInfo gamePeriodInfo = dbService
          .prevPeriodInfo(currentGameAndPeriod.getGameCode(), currentGameAndPeriod.getPeriodNum());
      if (gamePeriodInfo == null) {
        return new ReturnInfo("系统无上期期号", true);
      }
      ReturnInfo result = new ReturnInfo("查询成功", true);
      Map<String, String> infoMap = new HashMap<>();
      infoMap.put("gameCode", gamePeriodInfo.getGameCode());
      infoMap.put("periodNum", gamePeriodInfo.getPeriodNum());
      result.setRetObj(infoMap);
      return result;
    } catch (Exception e) {
      log.warn("查询游戏期号异常", e);
      return new ReturnInfo("查询异常", false);
    }
  }

}





