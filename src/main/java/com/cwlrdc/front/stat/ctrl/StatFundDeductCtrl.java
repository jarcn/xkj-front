package com.cwlrdc.front.stat.ctrl;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoExample;
import com.cwlrdc.commondb.stat.entity.StatFundDeduct;
import com.cwlrdc.commondb.stat.entity.StatFundDeductExample;
import com.cwlrdc.commondb.stat.entity.StatFundDeductKey;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.stat.service.StatFundDeductService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.unlto.twls.commonutil.component.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/*
    资金扣划通知书
 */
@Slf4j
@Controller
@RequestMapping("/statFundDeduct")
public class StatFundDeductCtrl {

    @Resource
    private StatFundDeductService dbService;
    @Resource
    private ParaGamePeriodInfoService periodInfoService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody StatFundDeduct info, HttpServletRequest req) {
        try {
            dbService.insert(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl insert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody StatFundDeduct info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl update error..", e);

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
            dc.setEntityClass(StatFundDeduct.class);
            dc.setKeyClass(StatFundDeductKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl get error..", e);

        }
        if (para.isPage()) {
            return new ListInfo<>(totalCount, list, para);
        } else {
            return list;
        }
    }

    //资金扣划通知书(上缴 type=1，下拨 type=0)
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/fundDeduct/{gameCode}/{periodNum}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String, Object>> fundDeduct(@PathVariable String gameCode, @PathVariable String periodNum, @PathVariable String type) {
        String periodYear = periodNum.substring(0, 4);
        List<HashMap<String, Object>> list = dbService.fundDeduct(gameCode, periodNum, periodYear, type);
        return list;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/fundDeductBaseInfo/{gameCode}/{periodNum}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> fundDeductBaseInfo(@PathVariable String gameCode, @PathVariable String periodNum, @PathVariable String type) {
        String periodYear = periodNum.substring(0, 4);
        Map<String, String> map = dbService.fundDeductBaseInfo(gameCode, periodNum, periodYear, type);
        return map;
    }

    //根据期号，查询对应几期的数据，查询累加
    private List<StatFundDeduct> queryStatData(String gameCode, String periodNum, int count, int type) {
        List<StatFundDeduct> resultFund = new ArrayList<StatFundDeduct>();
        StatFundDeductExample deductExample = new StatFundDeductExample();
        if (count == 1) {
            deductExample.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(periodNum)
                    .andTypeEqualTo(type);
            resultFund = dbService.selectByExample(deductExample);
        }
        if (count == 2) {
            deductExample.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(periodNum)
                    .andTypeEqualTo(type);
            List<StatFundDeduct> fundDeduct1 = dbService.selectByExample(deductExample);
            String lastPeriodNum = this.getLastPeriodNum(periodNum, gameCode);
            StatFundDeductExample deductExample1 = new StatFundDeductExample();
            deductExample1.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(lastPeriodNum)
                    .andTypeEqualTo(type);
            List<StatFundDeduct> fundDeduct2 = dbService.selectByExample(deductExample1);
            resultFund = sumStatFundDeduct(fundDeduct1, fundDeduct2, type);
        }
        if (count == 3) {
            deductExample.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(periodNum)
                    .andTypeEqualTo(type);
            List<StatFundDeduct> fundDeduct1 = dbService.selectByExample(deductExample);
            String lastPeriodNum1 = this.getLastPeriodNum(periodNum, gameCode);
            StatFundDeductExample deductExample1 = new StatFundDeductExample();
            deductExample1.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(lastPeriodNum1)
                    .andTypeEqualTo(type);
            List<StatFundDeduct> fundDeduct2 = dbService.selectByExample(deductExample1);
            StatFundDeductExample deductExample2 = new StatFundDeductExample();
            String lastPeriodNum2 = this.getLastPeriodNum(lastPeriodNum1, gameCode);
            deductExample2.createCriteria().andGameCodeEqualTo(gameCode)
                    .andPeriodNumEqualTo(lastPeriodNum2)
                    .andTypeEqualTo(type);
            List<StatFundDeduct> fundDeduct3 = dbService.selectByExample(deductExample2);
            List<StatFundDeduct> resultFund1 = sumStatFundDeduct(fundDeduct1, fundDeduct2, type);
            resultFund = sumStatFundDeduct(fundDeduct3, resultFund1, type);
        }

        return resultFund;
    }

    //累加资金扣划通知书结果
    private List<StatFundDeduct> sumStatFundDeduct(List<StatFundDeduct> fundDeduct1, List<StatFundDeduct> fundDeduct2, int type) {
        List<StatFundDeduct> sumResult = new ArrayList<>();
        if (!CommonUtils.isEmpty(fundDeduct1) && !CommonUtils.isEmpty(fundDeduct2)) {
            for (StatFundDeduct deduct1 : fundDeduct1) {
                StatFundDeduct deduct = new StatFundDeduct();
                for (StatFundDeduct deduct2 : fundDeduct2) {
                    if (deduct1.getProvinceId().equals(deduct2.getGameCode())
                            && deduct1.getGameCode().equals(deduct2.getGameCode())) {
                        deduct.setType(type);
                        deduct.setPeriodNum(deduct1.getPeriodNum());
                        deduct.setGameCode(deduct1.getGameCode());
                        deduct.setProvinceId(deduct1.getProvinceId());
                        deduct.setRealityPayCash(new BigDecimal(Math.abs(deduct1.getRealityPayCash().doubleValue()) + Math.abs(deduct2.getRealityPayCash().doubleValue())));
                        sumResult.add(deduct);
                    }
                }
            }
        } else {
            if (!CommonUtils.isEmpty(fundDeduct1)) {
                sumResult = fundDeduct1;
            }
            if (!CommonUtils.isEmpty(fundDeduct2)) {
                sumResult = fundDeduct2;
            }
        }
        return sumResult;
    }

    //获取上一期期号
    private String getLastPeriodNum(String curPeriodNum, String gameCode) {
        String lastPeriodNum = "";
        ParaGamePeriodInfoExample gamePeriodInfoExample = new ParaGamePeriodInfoExample();
        gamePeriodInfoExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumLessThan(curPeriodNum);
        List<ParaGamePeriodInfo> periodInfos = periodInfoService.selectByExample(gamePeriodInfoExample);
        if (!CommonUtils.isEmpty(periodInfos)) {
            Collections.sort(periodInfos); //按照期号进行排序
            ParaGamePeriodInfo periodInfo = periodInfos.get(0);
            lastPeriodNum = periodInfo.getPeriodNum();
        }
        return lastPeriodNum;
    }

    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchDelete(@RequestBody List<String> data, HttpServletRequest req) {
        try {
            List<StatFundDeduct> list = new ArrayList<StatFundDeduct>();
            for (String id : data) {
                StatFundDeduct info = new StatFundDeduct();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody StatFundDeducts data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody StatFundDeducts data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<StatFundDeduct> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<StatFundDeduct> list = new ArrayList<>();
        try {
            StatFundDeduct info = new StatFundDeduct();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            StatFundDeduct info = new StatFundDeduct();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody StatFundDeduct info, HttpServletRequest req) {
        try {
            StatFundDeduct oldPojo = null;
            if (info != null) {
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  StatFundDeductCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_STAT_FUND_DEDUCT";
    }

    @SuppressWarnings("serial")
    public static class StatFundDeducts extends ArrayList<StatFundDeduct> {
        public StatFundDeducts() {
            super();
        }
    }
}
