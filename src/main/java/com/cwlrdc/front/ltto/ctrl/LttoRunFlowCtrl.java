package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoRunFlow;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowExample;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowKey;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lttoRunFlow")
public class LttoRunFlowCtrl {

    @Resource
    private LttoRunFlowService dbService;


    //判断指定流程节点是否完成
    @RequestMapping(value = "/isdone/{gameCode}/{periodNum}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryRunFlowStatus(@PathVariable String gameCode,@PathVariable String periodNum,@PathVariable String type){

        LttoRunFlowKey key  = new LttoRunFlowKey();
        key.setFlowType(Integer.valueOf(type));
        key.setPeriodNum(periodNum);
        key.setGameCode(gameCode);
        LttoRunFlow runFlow = dbService.selectByPrimaryKey(key);
        if(runFlow!=null && Constant.Status.FLOW_RUN_COMPLETE.equals(String.valueOf(runFlow.getFlowStatus()))){
            return ReturnInfo.Success;
        }else{
            return ReturnInfo.Faild;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody LttoRunFlow info, HttpServletRequest req) {
        try {
            dbService.insert(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl insert error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/isTrue/{gameCode}/{periodNum}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public String isTrue(@PathVariable String gameCode, @PathVariable String periodNum, @PathVariable String type, HttpServletRequest req) {
        LttoRunFlowExample lttoRunFlowExample = new LttoRunFlowExample();
        lttoRunFlowExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andFlowTypeEqualTo(Integer.parseInt(type)).andFlowStatusEqualTo(1);
        List<LttoRunFlow> lttoRunFlows = dbService.selectByExample(lttoRunFlowExample);
        if (null != lttoRunFlows && lttoRunFlows.size() > 0) {
            return "success";
        }
        return "fail";
    }

    @RequestMapping(value = "/getTime/{periodNum}/{gameCode}", method = RequestMethod.GET)
    @ResponseBody
    public Object getTime(@PathVariable String periodNum, @PathVariable String gameCode) {
        LttoRunFlowExample lttoRunFlowExample = new LttoRunFlowExample();
        lttoRunFlowExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        List<LttoRunFlow> lttoRunFlows = dbService.selectByExample(lttoRunFlowExample);
        if (lttoRunFlows != null) {
            return lttoRunFlows;
        }
        log.warn("日志记录时间查询失败");
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody LttoRunFlow info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl update error..", e);

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
            dc.setEntityClass(LttoRunFlow.class);
            dc.setKeyClass(LttoRunFlowKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl get error..", e);

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
            List<LttoRunFlow> list = new ArrayList<LttoRunFlow>();
            for (String id : data) {
                LttoRunFlow info = new LttoRunFlow();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody LttoRunFlows data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody LttoRunFlows data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<LttoRunFlow> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<LttoRunFlow> list = new ArrayList<>();
        try {
            LttoRunFlow info = new LttoRunFlow();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            LttoRunFlow info = new LttoRunFlow();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody LttoRunFlow info, HttpServletRequest req) {
        try {
            LttoRunFlow oldPojo = null;
            if (info != null) {
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoRunFlowCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_LTTO_RUN_FLOW";
    }

    @SuppressWarnings("serial")
    public static class LttoRunFlows extends ArrayList<LttoRunFlow> {
        public LttoRunFlows() {
            super();
        }
    }
}
