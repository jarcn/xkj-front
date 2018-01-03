package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataKey;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lttoProvinceSalesData")
public class LttoProvinceSalesDataCtrl {
    private @Resource
    LttoProvinceSalesDataService dbService;

    @Resource
    private ParaGamePeriodInfoService periodInfoService;


    /**
     * 查询当期销售明细文件上传成功情况
     * @param gameCode
     * @param periodNum
     * @return
     */
    @RequestMapping(value="queryUploads/{gameCode}/{periodNum}",method=RequestMethod.GET)
    @ResponseBody
    public ReturnInfo get(@PathVariable String gameCode,@PathVariable String periodNum,HttpServletRequest req) {
        ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
        if(null == periodInfo){
            return new ReturnInfo("游戏或期号错误",false);
        }
        Integer i = dbService.selectUploadSuccessCount(gameCode, periodNum);
        return new ReturnInfo("销售明细文件上传情况", i, null,true);
    }


    @RequestMapping(value = "/query/sale/data/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo querySaleData(@PathVariable String gameCode,@PathVariable String periodNum, HttpServletRequest req) {
        ReturnInfo info=new ReturnInfo(false);
        List<LttoProvinceSalesData> list = dbService.initProvinceSaleData(gameCode,periodNum);
        if(!CommonUtils.isEmpty(list)){
        	info.setRetObj(list);
        	info.setSuccess(true);
        }
        return info;
    }



    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody LttoProvinceSalesData info, HttpServletRequest req) {
        try {
            long uploadTime = System.currentTimeMillis();
            info.setUploadTime(uploadTime);
            info.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
            dbService.insert(info);
            return ReturnInfo.Success;
        }catch (DuplicateKeyException e){
            log.trace("主键冲突",e);
            return new ReturnInfo("添加失败,不可重复添加",false);
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl insert error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody LttoProvinceSalesData info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl update error..", e);

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
            dc.setEntityClass(LttoProvinceSalesData.class);
            dc.setKeyClass(LttoProvinceSalesDataKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl get error..", e);

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
            List<LttoProvinceSalesData> list = new ArrayList<LttoProvinceSalesData>();
            for (String id : data) {
                LttoProvinceSalesData info = new LttoProvinceSalesData();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody LttoProvinceSalesDatas data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody LttoProvinceSalesDatas data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<LttoProvinceSalesData> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<LttoProvinceSalesData> list = new ArrayList<>();
        try {
            LttoProvinceSalesData info = new LttoProvinceSalesData();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            LttoProvinceSalesData info = new LttoProvinceSalesData();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody LttoProvinceSalesData info, HttpServletRequest req) {
        try {
            LttoProvinceSalesData oldPojo = null;
            if (info != null) {
    			info.setUploadTime(System.currentTimeMillis());
    			info.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoProvinceSalesDataCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_LTTO_PROVINCE_SALES_DATA";
    }

    @SuppressWarnings("serial")
    public static class LttoProvinceSalesDatas extends ArrayList<LttoProvinceSalesData> {
        public LttoProvinceSalesDatas() {
            super();
        }
    }
}
