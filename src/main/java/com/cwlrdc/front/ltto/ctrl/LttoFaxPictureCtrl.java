package com.cwlrdc.front.ltto.ctrl;

import static com.cwlrdc.front.common.PictureType.getPicName;

import com.cwlrdc.commondb.ltto.entity.LttoFaxPicture;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureBak;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureExample;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureKey;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlow;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowExample;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.common.FlowType;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoFaxPictureBakService;
import com.cwlrdc.front.ltto.service.LttoFaxPictureService;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/lttoFaxPicture")
public class LttoFaxPictureCtrl {

  private @Resource
  LttoFaxPictureService dbService;
  private @Resource
  LttoRunFlowService lttoRunFlowService;
  private @Resource
  ProvinceInfoCache provinceInfoCache;
  private @Resource
  LttoFaxPictureBakService bakService;
  @Resource
  private OperatorsLogManager operatorsLogManager;
  @Resource
  private LttoProvinceSalesDataService salesDataService;
  @Resource
  private LttoCancelWinStatDataService cancelDataService;
  @Resource
  private LttoWinstatDataService winDataService;
  @Resource
  private LttoProvinceFileStatusService fileStatusService;

  @RequestMapping(value = "", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo insert(@RequestBody LttoFaxPicture info, HttpServletRequest req) {
    try {
      dbService.insert(info);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl insert error..", e);

    }
    return ReturnInfo.Faild;
  }


  @RequestMapping(value = "/updateStatus/{gameCode}/{periodNum}/{flowType}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo updateStatus(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String flowType) {
    try {
      long start = System.currentTimeMillis();
      if (FlowType.SALES_DATA_SUM.getTypeNum() == Integer.parseInt(flowType)) {
        int saleRptCount = salesDataService.selectUploadFaildCount(gameCode, periodNum);
        if (saleRptCount == 0) {
          lttoRunFlowService.updateStatus(gameCode, periodNum, flowType);
        }
      } else if (FlowType.CANCEL_DATA_SUM.getTypeNum() == Integer.parseInt(flowType)) {
        int cancleRptCount = cancelDataService.selectUploadFaildCount(gameCode, periodNum);
        if (cancleRptCount == 0) {
          lttoRunFlowService.updateStatus(gameCode, periodNum, flowType);
        }
      } else if (FlowType.DETAIL_FILE_UPLOAD.getTypeNum() == Integer.parseInt(flowType)) {
        int detailZipCount = fileStatusService.selectUploadFaildCount(gameCode, periodNum);
        if (detailZipCount == 0) {
          lttoRunFlowService.updateStatus(gameCode, periodNum, flowType);
        }
      } else if (FlowType.LOOK_WINRESULT_FAX_CHECK.getTypeNum() == Integer.parseInt(flowType)) {
        int winRptCount = winDataService.selectUploadFaildCount(gameCode, periodNum);
        if (winRptCount == 0) {
          lttoRunFlowService.updateStatus(gameCode, periodNum, flowType);
        }
      } else {
        lttoRunFlowService.updateStatus(gameCode, periodNum, flowType);
      }
      log.info(operatorsLogManager.getLogInfo("页面通用", "更新开奖流程图", start));
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.info("更新开奖流程图异常", e);
      return ReturnInfo.Faild;
    }
  }

  @RequestMapping(value = "/updateFlow/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo updateFlow(@PathVariable String gameCode, @PathVariable String periodNum) {
    LttoFaxPictureExample lttoFaxPictureExample = new LttoFaxPictureExample();
    lttoFaxPictureExample.createCriteria().andGameCodeEqualTo(gameCode)
        .andPeriodNumEqualTo(periodNum).andStatusNotEqualTo(4);//4代表已核对
    List<LttoFaxPicture> lttoFaxPictures = dbService.selectByExample(lttoFaxPictureExample);
    if (!CommonUtils.isEmpty(lttoFaxPictures)) {
      for (LttoFaxPicture l : lttoFaxPictures) {
        l.setStatus(4);
      }
      dbService.batchUpdate(lttoFaxPictures);
      log.debug("");
    }
    return ReturnInfo.Success;
  }

  @RequestMapping(value = "/select/{gameCode}/{periodNum}", method = RequestMethod.GET)
  @ResponseBody
  public int select(@PathVariable String gameCode, @PathVariable String periodNum) {
    try {
      LttoRunFlowExample ltt = new LttoRunFlowExample();
      ltt.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
          .andFlowTypeEqualTo(4).andFlowStatusEqualTo(1);
      List<LttoRunFlow> lttoRunFlows = lttoRunFlowService.selectByExample(ltt);
      if (!lttoRunFlows.isEmpty()) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl select error..", e);
    }
    return 0;
  }

  @RequestMapping(value = "", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo update(@RequestBody LttoFaxPicture info, HttpServletRequest req) {
    try {
      dbService.updateByExample(info, dbService.getExample(info));
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl update error..", e);

    }
    return ReturnInfo.Faild;
  }

  @SuppressWarnings("rawtypes")
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseBody
  public Object get(@RequestJsonParam(value = "query", required = false) QueryMapperBean info,
      @RequestJsonParam(value = "fields", required = false) FieldsMapperBean fmb,
      PageInfo para, HttpServletRequest req) {
    int totalCount = 0;
    List<HashMap<String, Object>> list = null;
    QueryMapperBean.EqualBean equalBean = info.getEquals().get(0);
    QueryMapperBean.EqualBean equalBean1 = info.getEquals().get(1);
    QueryMapperBean.EqualBean equalBean2 = info.getEquals().get(2);
    String gameCode = (String) equalBean.getValue();
    String periodNum = (String) equalBean1.getValue();
    String picType = (String) equalBean2.getValue();
    //初始化数据 图片类型 1:销售汇总弃奖数据图片，2:中奖结果传真3:开奖公告图片，4：开奖号码图片'
    dbService.initFaxPic(gameCode, periodNum, Integer.parseInt(picType));
    try {
      DbCondi dc = new DbCondi();
      dc.setEntityClass(LttoFaxPicture.class);
      dc.setKeyClass(LttoFaxPictureKey.class);
      dc.setQmb(info);
      dc.setPageinfo(para);
      dc.setFmb(fmb);
      dc.setTalbeName(getTableName());
      totalCount = dbService.getCount(dc);
      list = dbService.getData(dc);
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl get error..", e);
    }
    if (para.isPage()) {
      return new ListInfo<>(totalCount, list, para);
    } else {
      return list;
    }
  }

  @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo batchDelete(@RequestBody List<String> data, HttpServletRequest req) {
    try {
      List<LttoFaxPicture> list = new ArrayList<LttoFaxPicture>();
      for (String id : data) {
        LttoFaxPicture info = new LttoFaxPicture();
        KeyExplainHandler.explainKey(id, info);
        list.add(info);
      }
      dbService.batchDelete(list);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl batchDelete error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/batch", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo batchUpdate(@RequestBody LttoFaxPictures data, HttpServletRequest req) {
    try {
      dbService.batchUpdate(data);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl batchUpdate error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/batch", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo batchInsert(@RequestBody LttoFaxPictures data, HttpServletRequest req) {
    try {
      dbService.batchInsert(data);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl batchInsert error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.GET)
  @ResponseBody
  public ListInfo<LttoFaxPicture> get(@PathVariable String key, HttpServletRequest req) {
    int totalCount = 1;
    List<LttoFaxPicture> list = new ArrayList<>();
    try {
      LttoFaxPicture info = new LttoFaxPicture();
      KeyExplainHandler.explainKey(key, info);
      list.add(dbService.selectByPrimaryKey(info));
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl get by key error..", e);
    }
    return new ListInfo<>(totalCount, list, 0, 1);
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
  @ResponseBody
  public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
    try {
      LttoFaxPicture info = new LttoFaxPicture();
      KeyExplainHandler.explainKey(key, info);
      dbService.deleteByPrimaryKey(info);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl delete by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo update(@PathVariable String key, @RequestBody LttoFaxPicture info,
      HttpServletRequest req) {
    try {
      LttoFaxPicture oldPojo = null;
      if (info != null) {
        KeyExplainHandler.explainKey(key, info);
        oldPojo = dbService.selectByPrimaryKey(info);
        dbService.updateByPrimaryKey(info);
      }
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoFaxPictureCtrl update by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  //删除图片前备份
  @RequestMapping(value = "/deleteData/{gameCode}/{periodNum}/{provinceId}/{fileType}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo deleteData(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String provinceId, @PathVariable String fileType) {
    log.debug("[新开奖系统] 开始备份 省码[{}],游戏[{}],期号[{}] 图片", provinceId, gameCode, periodNum);
    boolean backUpPic = backUpPic(gameCode, periodNum, provinceId, fileType);
    log.debug("[新开奖系统] 完成备份 省码[{}],游戏[{}],期号[{}] 图片", provinceId, gameCode, periodNum);
    if (backUpPic) {
      int i = dbService.delete2Key(gameCode, periodNum, provinceId, fileType);
      if (i > 0) {
        return ReturnInfo.Success;
      }
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/querypicture/{gameCode}/{periodNum}/{fileType}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo querypicture(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String fileType) {
    ReturnInfo info = new ReturnInfo();
    LttoFaxPictureExample example = new LttoFaxPictureExample();
    example.createCriteria().andPeriodNumEqualTo(periodNum)
        .andGameCodeEqualTo(gameCode)
        .andPictureTypeEqualTo(Integer.valueOf(fileType));
    List<LttoFaxPicture> pictuers = dbService.selectByExample(example);
    if (!CommonUtils.isEmpty(pictuers)) {
      info.setSuccess(true);
      info.setRetObj(pictuers);
      return info;
    }
    return ReturnInfo.Faild;
  }


  @RequestMapping(value = "/querypicbase64/{gameCode}/{periodNum}/{provinceId}/{fileType}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo newQueryBase64(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String provinceId, @PathVariable String fileType) {
    ReturnInfo returnInfo = new ReturnInfo();
    LttoFaxPicture lttoFaxPicture = this.getLttoFax(gameCode, fileType, provinceId, periodNum);
    if (null != lttoFaxPicture) {
      String imageBinary = lttoFaxPicture.getPicturePath();
      if (StringUtils.isBlank(lttoFaxPicture.getPicturePath())) {
        log.info("图片为空,配置不存在或参数错误,游戏[{}]期号[{}]省份[{}]图片类型[{}]",
            gameCode, periodNum, provinceId, getPicName(Integer.parseInt(fileType)));
        return ReturnInfo.Faild;
      }
      try {
        byte[] data = FileUtils.readFile(lttoFaxPicture.getPicturePath());
        String imageBase64Data = Base64.encodeBase64String(data);
        returnInfo.setSuccess(true);
        returnInfo.setRetObj(imageBase64Data);
        return returnInfo;
      } catch (Exception e) {
        log.warn("图片展示发生错误,游戏[" + gameCode + "]期号[" + periodNum + "]省份[" + provinceId + "]图片类型["
            + fileType + "]", e);
        return ReturnInfo.Faild;
      }
    }
    return ReturnInfo.Faild;
  }

  /*@RequestMapping(value = "/querypicbase64/{gameCode}/{periodNum}/{provinceId}/{fileType}", method = RequestMethod.GET)
  @ResponseBody*/
  public ReturnInfo queryBase64(@PathVariable String gameCode, @PathVariable String periodNum,
      @PathVariable String provinceId, @PathVariable String fileType) {
    ReturnInfo returnInfo = new ReturnInfo();
    LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
    lttoFaxPictureKey.setPictureType(Integer.parseInt(fileType));
    lttoFaxPictureKey.setProvinceId(provinceId);
    lttoFaxPictureKey.setPeriodNum(periodNum);
    lttoFaxPictureKey.setGameCode(gameCode);
    LttoFaxPicture lttoFaxPicture = dbService.selectByPrimaryKey(lttoFaxPictureKey);
    if (null != lttoFaxPicture) {
      String imageBinary = lttoFaxPicture.getPicturePath();
      returnInfo.setSuccess(true);
      returnInfo.setRetObj(imageBinary);
      return returnInfo;
    }
    return ReturnInfo.Faild;
  }

  private String getControllerName() {
    return this.getClass().getSimpleName();
  }

  private String getTableName() {
    return "T_LTTO_FAX_PICTURE";
  }

  @SuppressWarnings("serial")
  public static class LttoFaxPictures extends ArrayList<LttoFaxPicture> {

    public LttoFaxPictures() {
      super();
    }
  }


  private boolean backUpPic(String gameCode, String periodNum, String provinceId, String fileType) {
    LttoFaxPicture lttoFaxPicture = dbService.select2Key(gameCode, periodNum, provinceId, fileType);
    if(null != lttoFaxPicture && StringUtils.isNotBlank(lttoFaxPicture.getPicturePath())){
      this.backUpPictrue(lttoFaxPicture.getPicturePath());
      LttoFaxPictureBak pictureBak = new LttoFaxPictureBak();
      BeanCopyUtils.copyProperties(lttoFaxPicture, pictureBak);
      pictureBak.setUuid(UUID.randomUUID().toString());
      return bakService.insert(pictureBak) > 0 ? true : false;
    }else{
      return false;
    }
  }

  /**
   * 获取图片数据
   */
  private LttoFaxPicture getLttoFax(String gameCode, String fileType, String provinceId,
      String periodNum) {
    LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
    lttoFaxPictureKey.setPictureType(Integer.parseInt(fileType));
    lttoFaxPictureKey.setProvinceId(provinceId);
    lttoFaxPictureKey.setPeriodNum(periodNum);
    lttoFaxPictureKey.setGameCode(gameCode);
    LttoFaxPicture lttoFaxPicture = dbService.selectByPrimaryKey(lttoFaxPictureKey);
    return lttoFaxPicture;
  }

  private void backUpPictrue(String fileAbsPath) {
    if(StringUtils.isNotBlank(fileAbsPath)){
      File pic = new File(fileAbsPath);
      if (pic.exists()) {
        pic.renameTo(new File(fileAbsPath + "." + System.currentTimeMillis() + ".BAK"));
      }
    }else{
      log.debug("文件路径不能为空[{}]", fileAbsPath);
    }
  }
}
