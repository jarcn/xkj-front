package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementExample;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementKey;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean.EqualBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.joyveb.lbos.restful.util.SqlMaker;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/lttoLotteryAnnouncement")
public class LttoLotteryAnnouncementCtrl {
    private final String CONDITION_GAMECODE = "gameCode";
    private final String CONDITION_PERIODNUM = "periodNum";

    private @Resource
    LttoLotteryAnnouncementService dbService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody LttoLotteryAnnouncement info, HttpServletRequest req) {
        try {
            dbService.insert(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl insert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody LttoLotteryAnnouncement info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl update error..", e);

        }
        return ReturnInfo.Faild;
    }


    @RequestMapping(value = "/getLotteryAnn/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public Object getLotteryAnn(@PathVariable String gameCode, @PathVariable String periodNum) {
        LttoLotteryAnnouncementExample lttoLotteryAnnouncementExample = new LttoLotteryAnnouncementExample();
        lttoLotteryAnnouncementExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        List<LttoLotteryAnnouncement> selectByExample = dbService.selectByExample(lttoLotteryAnnouncementExample);
        if (selectByExample != null) {
            return selectByExample;
        }
        log.warn("奖等金额查询失败");
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
            dc.setEntityClass(LttoLotteryAnnouncement.class);
            dc.setKeyClass(LttoLotteryAnnouncementKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl get error..", e);

        }
        if (para.isPage()) {
            return new ListInfo<>(totalCount, list, para);
        } else {
            return list;
        }
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/dynamicFund", method = RequestMethod.GET)
    @ResponseBody
    public Object queryDynamicFund(@RequestJsonParam(value = "query", required = false) QueryMapperBean info,
                                   @RequestJsonParam(value = "fields", required = false) FieldsMapperBean fmb,
                                   PageInfo para, HttpServletRequest req) {
        String gameCode = null;
        String periodNum = null;
        if (info != null && info.getEquals() != null) {
            List<EqualBean> equalBeans = info.getEquals();
            for (EqualBean equalBean : equalBeans) {
                String value = equalBean.getValue().toString().replace(",", "/");
                boolean hasInjection = SqlMaker.hasInjection(value);
                if (hasInjection) {
                    return ReturnInfo.Faild;
                }
                String key = equalBean.getFieldName();
                if (key.equals(CONDITION_GAMECODE)) {
                    gameCode = value;
                } else if (key.equals(CONDITION_PERIODNUM)) {
                    periodNum = value;
                }
            }
        }
        List<HashMap<String, String>> list = dbService.queryDynamicFund(periodNum, gameCode);
        return list;
    }

    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchDelete(@RequestBody List<String> data, HttpServletRequest req) {
        try {
            List<LttoLotteryAnnouncement> list = new ArrayList<LttoLotteryAnnouncement>();
            for (String id : data) {
                LttoLotteryAnnouncement info = new LttoLotteryAnnouncement();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody LttoLotteryAnnouncements data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody LttoLotteryAnnouncements data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<LttoLotteryAnnouncement> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<LttoLotteryAnnouncement> list = new ArrayList<>();
        try {
            LttoLotteryAnnouncement info = new LttoLotteryAnnouncement();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            LttoLotteryAnnouncement info = new LttoLotteryAnnouncement();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody LttoLotteryAnnouncement info, HttpServletRequest req) {
        try {
            LttoLotteryAnnouncement oldPojo = null;
            if (info != null) {
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoLotteryAnnouncementCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_LTTO_LOTTERY_ANNOUNCEMENT";
    }

    @SuppressWarnings("serial")
    public static class LttoLotteryAnnouncements extends ArrayList<LttoLotteryAnnouncement> {
        public LttoLotteryAnnouncements() {
            super();
        }
    }


    /**
     * 查询资金动态表
     * 促销一等奖余额详情
     *
     * @param gameCode
     * @param periodNum
     * @param resp
     * @return
     */
    @RequestMapping(value = "/prom1DynamicFund/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryProm1DynamicFund(@PathVariable String gameCode, @PathVariable String periodNum,
                                            HttpServletResponse resp) {
        List<HashMap<String, Object>> hashMaps = dbService.queryPro1DynamicFund(gameCode, periodNum);
        if (!CommonUtils.isEmpty(hashMaps)) {
            return new ReturnInfo("一等奖派奖奖池信息", 0, hashMaps, true);
        } else {
            return new ReturnInfo(false);
        }
    }

    /**
     * 查询资金动态表
     * 促销六等奖余额详情
     *
     * @param gameCode
     * @param periodNum
     * @param resp
     * @return
     */
    @RequestMapping(value = "/prom6DynamicFund/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryProm6DynamicFund(@PathVariable String gameCode, @PathVariable String periodNum,
                                            HttpServletResponse resp) {
        List<HashMap<String, Object>> hashMaps = dbService.queryPro6DynamicFund(gameCode, periodNum);
        if (!CommonUtils.isEmpty(hashMaps)) {
            return new ReturnInfo("六等奖派奖奖池信息", 0, hashMaps, true);
        } else {
            return new ReturnInfo(false);
        }
    }

    /**
     * 查询派奖奖池余额
     *
     * @param gameCode
     * @param periodNum
     * @param resp
     * @return
     */
    @RequestMapping(value = "/promPoolTotalBalance/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo promPoolTotalBalance(@PathVariable String gameCode, @PathVariable String periodNum,
                                           HttpServletResponse resp) {
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
            List<LttoLotteryAnnouncement> lttoLotteryAnnouncements = dbService.queryProTotalBalance(periodNum);
            if (CommonUtils.isNotEmpty(lttoLotteryAnnouncements)) {
                Collections.sort(lttoLotteryAnnouncements, new Comparator<LttoLotteryAnnouncement>() {
                    @Override
                    public int compare(LttoLotteryAnnouncement o1, LttoLotteryAnnouncement o2) {
                        return Integer.parseInt(o1.getGameCode())-Integer.parseInt(o2.getGameCode());
                    }
                });
                LttoLotteryAnnouncement announcement = lttoLotteryAnnouncements.get(0);
                announcement.setPoolTotal(dbService.queryPoolTotalPro1Balance(gameCode, periodNum));
                return new ReturnInfo("派奖奖池余额", 0, lttoLotteryAnnouncements, true);
            } else {
                return new ReturnInfo(false);
            }
        } else {
            return new ReturnInfo("七乐彩不参与派奖", false);
        }
    }
}
