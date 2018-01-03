package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLogRemark;
import com.cwlrdc.commondb.ltto.entity.LttoLogRemarkExample;
import com.cwlrdc.commondb.ltto.entity.LttoLogRemarkKey;
import com.cwlrdc.commondb.opet.entity.OpetAddressBook;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.calc.bean.LotteryPersonBean;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoLogRemarkService;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
import com.cwlrdc.front.opet.service.OpetAddressBookService;
import com.cwlrdc.front.para.service.OperatorsService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/lttoLogRemark")
public class LttoLogRemarkCtrl {

  private
  @Resource
  LttoLogRemarkService dbService;
  private @Resource
  LttoRunFlowService lttoRunFlowService;
  private @Resource
  OperatorsService operatorsService;
  @Resource
  private ParaProvinceInfoService provinceInfoService;
  @Resource
  private OperatorsLogManager operatorsLogManager;


  /**
   * 判断是否选择开奖人
   * @param gameCode
   * @param periodNum
   * @param provinceId
   * @return
   */
  @RequestMapping(value = "/lotteryPerson/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
  @ResponseBody
  public ReturnInfo hasOpenLotteryMan(@PathVariable String gameCode
      , @PathVariable String periodNum, @PathVariable String provinceId){
    LttoLogRemark lttoLogRemark = dbService.selectLogRemarkByKey(gameCode, periodNum, provinceId);
    if (null == lttoLogRemark){
      return new ReturnInfo("未选择开奖人",false);
    }
    if(StringUtils.isBlank(lttoLogRemark.getLotteryPerson())){
      return new ReturnInfo("未选择开奖人",false);
    }
    return ReturnInfo.Success;
  }

  @RequestMapping(value = "/insertAndUpdate", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo insertAndUpdate(@RequestBody LttoLogRemarks data, HttpServletRequest req) {
    try {
      long start = System.currentTimeMillis();
      for (LttoLogRemark info : data) {
        LttoLogRemark remark = dbService.selectByPrimaryKey(info);
        if (null == remark) {
          info.setOperateTime(System.currentTimeMillis());
          dbService.insert(info);
        } else {
          if (remark.getOperateTime() == null) {
            info.setOperateTime(System.currentTimeMillis());
          } else {
            info.setOperateTime(remark.getOperateTime());
          }
          dbService.updateByPrimaryKeySelective(info);
        }
      }
      if (CommonUtils.isNotEmpty(data)) {
        String periodNum = data.get(0).getPeriodNum();
        String gameCode = data.get(0).getGameCode();
        String provinceId = data.get(0).getProvinceId();
        if (StringUtils.isBlank(periodNum) || StringUtils.isBlank(gameCode)
            || StringUtils.isBlank(provinceId)) {
          return ReturnInfo.Faild;
        }
        operatorsService.setOperators(periodNum, provinceId, gameCode);
      }
      log.info(operatorsLogManager.getLogInfo("通用页面", "修改开奖人日志记录", start));
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl insertAndUpdate error..", e);
      return ReturnInfo.Faild;
    }
  }

  @RequestMapping(value = "insertUpdate", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public ReturnInfo insert(@RequestBody LttoLogRemark info, HttpServletRequest req) {
    try {
      LttoLogRemark logRemark = dbService.selectByPrimaryKey(info);
      info.setLotteryTime(System.currentTimeMillis());
      if (null == logRemark) {
        dbService.insert(info);
        return ReturnInfo.Success;
      } else {
        dbService.updateByPrimaryKeySelective(info);
        return ReturnInfo.Success;
      }
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl insertUpdate error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/getadminoperator/{periodNum}/{gameCode}/{opetType}/{provinceId}", method = RequestMethod.GET)
  @ResponseBody
  public Object getLotteryPerson(@PathVariable String periodNum, @PathVariable String gameCode,
      @PathVariable int opetType, @PathVariable String provinceId) {
    LttoLogRemarkExample lttoLogRemarkExample = new LttoLogRemarkExample();
    lttoLogRemarkExample.createCriteria().andPeriodNumEqualTo(periodNum)
        .andGameCodeEqualTo(gameCode).
        andOpetTypeEqualTo(opetType).andProvinceIdEqualTo(provinceId);
    List<LttoLogRemark> selectByExample = dbService.selectByExample(lttoLogRemarkExample);
    if (selectByExample != null) {
      return selectByExample;
    }
    log.warn("全国开奖人员记录人员查询失败");
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/getadminoperatorAllProvince/{periodNum}/{gameCode}/{opetType}", method = RequestMethod.GET)
  @ResponseBody
  public Object getLotteryPersonAllProvince(@PathVariable String periodNum,
      @PathVariable String gameCode,
      @PathVariable int opetType) {
    LttoLogRemarkExample lttoLogRemarkExample = new LttoLogRemarkExample();
    lttoLogRemarkExample.createCriteria().andPeriodNumEqualTo(periodNum)
        .andGameCodeEqualTo(gameCode).
        andOpetTypeEqualTo(opetType);
    List<LttoLogRemark> selectByExample = dbService.selectByExample(lttoLogRemarkExample);
    if (selectByExample != null) {
      return selectByExample;
    }
    log.warn("全国开奖人员记录人员查询失败");
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/getLottery/{periodNum}/{gameCode}/{provinceId}", method = RequestMethod.GET)
  @ResponseBody
  public Object getLottery(@PathVariable String periodNum, @PathVariable String gameCode,
      @PathVariable String provinceId) {
    LttoLogRemarkExample lttoLogRemarkExample = new LttoLogRemarkExample();
    lttoLogRemarkExample.createCriteria().andPeriodNumEqualTo(periodNum)
        .andGameCodeEqualTo(gameCode).
        andProvinceIdEqualTo(provinceId);
    List<LttoLogRemark> selectByExample = dbService.selectByExample(lttoLogRemarkExample);
    if (selectByExample != null) {
      return selectByExample;
    }
    log.warn("全国开奖记录查询失败");
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/getPersonByProvinceAndPeriodAndGameCode/{provinceId}/{periodNum}/{gameCode}", method = RequestMethod.GET)
  @ResponseBody
  public Object getPersonByProvinceAndPeriodAndGameCode(@PathVariable String periodNum,
      @PathVariable String provinceId, @PathVariable String gameCode) {
    List<HashMap<String, Object>> list = dbService
        .getPersonByProvinceAndPeriodAndGameCode(provinceId, periodNum, gameCode);
    return list;
  }

  @RequestMapping(value = "", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo update(@RequestBody LttoLogRemark info, HttpServletRequest req) {
    try {
      dbService.updateByPrimaryKeySelective(info);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl update error..", e);

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
    try {
      DbCondi dc = new DbCondi();
      dc.setEntityClass(LttoLogRemark.class);
      dc.setKeyClass(LttoLogRemarkKey.class);
      dc.setQmb(info);
      dc.setPageinfo(para);
      dc.setFmb(fmb);
      dc.setTalbeName(getTableName());
      totalCount = dbService.getCount(dc);
      list = dbService.getData(dc);
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl get error..", e);

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
      List<LttoLogRemark> list = new ArrayList<LttoLogRemark>();
      for (String id : data) {
        LttoLogRemark info = new LttoLogRemark();
        KeyExplainHandler.explainKey(id, info);
        list.add(info);
      }
      dbService.batchDelete(list);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl batchDelete error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/batch", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo batchUpdate(@RequestBody LttoLogRemarks data, HttpServletRequest req) {
    try {
      dbService.batchUpdate(data);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl batchUpdate error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/batch", method = RequestMethod.POST)
  @ResponseBody
  public ReturnInfo batchInsert(@RequestBody LttoLogRemarks data, HttpServletRequest req) {
    try {
      dbService.batchInsert(data);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl batchInsert error..", e);

    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.GET)
  @ResponseBody
  public ListInfo<LttoLogRemark> get(@PathVariable String key, HttpServletRequest req) {
    int totalCount = 1;
    List<LttoLogRemark> list = new ArrayList<>();
    try {
      LttoLogRemark info = new LttoLogRemark();
      KeyExplainHandler.explainKey(key, info);
      list.add(dbService.selectByPrimaryKey(info));
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl get by key error..", e);
    }
    return new ListInfo<>(totalCount, list, 0, 1);
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
  @ResponseBody
  public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
    try {
      LttoLogRemark info = new LttoLogRemark();
      KeyExplainHandler.explainKey(key, info);
      dbService.deleteByPrimaryKey(info);
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl delete by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
  @ResponseBody
  public ReturnInfo update(@PathVariable String key, @RequestBody LttoLogRemark info,
      HttpServletRequest req) {
    try {
      LttoLogRemark oldPojo = null;
      if (info != null) {
        KeyExplainHandler.explainKey(key, info);
        oldPojo = dbService.selectByPrimaryKey(info);
        dbService.updateByPrimaryKey(info);
      }
      return ReturnInfo.Success;
    } catch (Exception e) {
      log.warn("  LttoLogRemarkCtrl update by key error..", e);
    }
    return ReturnInfo.Faild;
  }

  private String getControllerName() {
    return this.getClass().getSimpleName();
  }

  private String getTableName() {
    return "T_LTTO_LOG_REMARK";
  }

  @SuppressWarnings("serial")
  public static class LttoLogRemarks extends ArrayList<LttoLogRemark> {

    public LttoLogRemarks() {
      super();
    }
  }


  @Resource
  private OpetAddressBookService addressBookService;
  @Resource
  private ProvinceInfoCache provinceInfoCache;

  /**
   * 查询当期开奖人信息
   */
  @ResponseBody
  @RequestMapping(value = "/selectOpetOfLottery/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
  public ReturnInfo selectOpetOfLottery(@PathVariable String gameCode,
      @PathVariable String periodNum, @PathVariable String provinceId) {
    List<OpetAddressBook> opetAddressBooks = new ArrayList<>();
    LttoLogRemark lttoLogRemark = dbService.selectLogRemarkByKey(gameCode, periodNum, provinceId);
    if (null != lttoLogRemark) {
      String lotteryPerson = lttoLogRemark.getLotteryPerson();
      if (StringUtils.isNotBlank(lotteryPerson)) {
        String[] personList = lotteryPerson.split("[,]");
        for (String personName : personList) {
          opetAddressBooks.addAll(addressBookService.queryPersonInfosByKey(personName, provinceId));
        }
      }
      List<LotteryPersonBean> personInfos = new ArrayList<>();
      for(OpetAddressBook book:opetAddressBooks){
        LotteryPersonBean personBean = this.converBean(lttoLogRemark, book);
        personInfos.add(personBean);
      }
      Map<String, LotteryPersonBean> stringLotteryPersonBeanMap = this.list2Map(personInfos);
      return new ReturnInfo("开奖人信息",0,stringLotteryPersonBeanMap,true);
    }
    return ReturnInfo.Faild;
  }




  @ResponseBody
  @RequestMapping(value = "/allProvince/selectOpetOfLottery/{gameCode}/{periodNum}", method = RequestMethod.GET)
  public ReturnInfo selectallProvinceOfLottery(@PathVariable String gameCode,@PathVariable String periodNum) {
    List<Map<String, LotteryPersonBean>> resultList = new ArrayList<>();
    List<ParaProvinceInfo> allProvince = provinceInfoService.findAll();
    for(ParaProvinceInfo provinceInfo : allProvince){
      String provinceId = provinceInfo.getProvinceId();
      LttoLogRemark lttoLogRemark = dbService.selectLogRemarkByKey(gameCode, periodNum,provinceId);
      if (null != lttoLogRemark) {
        List<OpetAddressBook> opetAddressBooks = new ArrayList<>();
        String lotteryPerson = lttoLogRemark.getLotteryPerson();
        if (StringUtils.isNotBlank(lotteryPerson)) {
          String[] personList = lotteryPerson.split("[,]");
          for (String personName : personList) {
            opetAddressBooks.addAll(addressBookService.queryPersonInfosByKey(personName, provinceId));
          }
        }
        List<LotteryPersonBean> personInfos = new ArrayList<>();
        for(OpetAddressBook book:opetAddressBooks){
          LotteryPersonBean personBean = this.converBean(lttoLogRemark, book);
          personInfos.add(personBean);
        }
        Map<String, LotteryPersonBean> stringLotteryPersonBeanMap = this.list2Map(personInfos);
        resultList.add(stringLotteryPersonBeanMap);
      }
    }
    if(CommonUtils.isNotEmpty(resultList)){
      return new ReturnInfo("开奖人信息",0,resultList,true);
    }
    return ReturnInfo.Faild;
  }



  //转换开奖人信息
  private LotteryPersonBean converBean(LttoLogRemark lttoLogRemark, OpetAddressBook addBook) {
    LotteryPersonBean personBean = new LotteryPersonBean();
    String logProvinceId = lttoLogRemark.getProvinceId();
    String addProvinceId = addBook.getProvinceId();
    if (StringUtils.isNotBlank(logProvinceId) && StringUtils.isNotBlank(addProvinceId)) {
      if (logProvinceId.equals(addProvinceId)) {
        personBean.setProvinceId(addProvinceId);
        personBean.setGameCode(lttoLogRemark.getGameCode());
        personBean.setPeriodNum(lttoLogRemark.getPeriodNum());
        personBean.setProvinceName(provinceInfoCache.getProvinceName(logProvinceId));
        personBean.setOpetDate(DateFormatUtils.format(lttoLogRemark.getOperateTime(),"yyyy-MM-dd HH:mm:ss"));
        personBean.setPerosnInfo(addBook.getDrawName()+"("+addBook.getTelePhone()+")");
      }
    }
    return personBean;
  }

  private static final String INFO_FLAG = "man";
  private Map<String,LotteryPersonBean> list2Map(List<LotteryPersonBean> personInfos){
    Map<String,LotteryPersonBean> map = new HashMap<>();
    for(LotteryPersonBean bean:personInfos){
      if(map.keySet().contains(INFO_FLAG+bean.getProvinceId())){
        LotteryPersonBean personBean = map.get(INFO_FLAG+bean.getProvinceId());
        String perosnInfo = personBean.getPerosnInfo();
        personBean.setPerosnInfo(perosnInfo+","+bean.getPerosnInfo());
        map.put(INFO_FLAG+bean.getProvinceId(),personBean);
      }else{
        map.put(INFO_FLAG+bean.getProvinceId(),bean);
      }
    }
    return map;
  }
}
