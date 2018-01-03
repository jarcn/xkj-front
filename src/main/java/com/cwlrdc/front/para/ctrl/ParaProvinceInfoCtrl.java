package com.cwlrdc.front.para.ctrl;

import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoExample;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoKey;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
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
import java.util.Collections;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/paraProvinceInfo")
public class ParaProvinceInfoCtrl {
    @Resource
    private ParaProvinceInfoService dbService;
    @Resource
    private LttoProvinceFileStatusService zipFileStatusService;
    @Autowired
    private ProvinceInfoCache provinceInfoCache;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody ParaProvinceInfo info, HttpServletRequest req) {
        try {
            ParaProvinceInfo provinceInfo = dbService.selectByPrimaryKey(info);
            if (null != provinceInfo) {
                return new ReturnInfo("记录已经存在", false);
            }
            dbService.insert(info);
            return ReturnInfo.Success;
        } catch (DuplicateKeyException e) {
            log.trace("主键冲突", e);
            return new ReturnInfo("添加失败,不可重复添加", false);
        } catch (Exception e) {
            log.warn("  paraProvinceInfo insert error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody ParaProvinceInfo info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl update error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/ppic/{provinceId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getDate(@PathVariable String provinceId) {
        ParaProvinceInfoExample paraProvinceInfoExample = new ParaProvinceInfoExample();
        paraProvinceInfoExample.createCriteria().andProvinceIdEqualTo(provinceId);
        List<ParaProvinceInfo> selectByExample = dbService.selectByExample(paraProvinceInfoExample);
        if (selectByExample != null) {
            return selectByExample;
        }
        log.warn("省通讯录查询失败");
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    @ResponseBody
    public Object get() {
        List<HashMap<String, Object>> list = null;
        try {
            String sql = "SELECT PROVINCE_NAME provinceName,PROVINCE_ID provinceId  FROM T_PARA_PROVINCE_INFO WHERE PROVINCE_ID <> '00'";
            list = dbService.dosql(sql);
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl get error..", e);

        }
        return list;
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
            dc.setEntityClass(ParaProvinceInfo.class);
            dc.setKeyClass(ParaProvinceInfoKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl get error..", e);

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
            List<ParaProvinceInfo> list = new ArrayList<>();
            for (String id : data) {
                ParaProvinceInfo info = new ParaProvinceInfo();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody ParaProvinceInfos data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody ParaProvinceInfos data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<ParaProvinceInfo> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<ParaProvinceInfo> list = new ArrayList<>();
        try {
            ParaProvinceInfo info = new ParaProvinceInfo();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value="/muchByGame/{key}",method=RequestMethod.GET)
    @ResponseBody
    public ReturnInfo getMuchByGame(@PathVariable String key,HttpServletRequest req) {
        int much=dbService.getMuchByGame(key);
        return new ReturnInfo(much+"", true);
    }


    @RequestMapping(value = "/matchFtpByGame/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo getFtpMatchByGame(@PathVariable String gameCode,@PathVariable String periodNum, HttpServletRequest req) {
        int much = dbService.getFtpMatchByGame(gameCode);
        List<String> ftpTypeProvinces = provinceInfoCache.getFtpTypeProvinces();
        if(!CommonUtils.isEmpty(ftpTypeProvinces)){
            int uploadSuccessCount = zipFileStatusService.selectUploadSuccessCount(gameCode, periodNum,ftpTypeProvinces);
            return new ReturnInfo(uploadSuccessCount+"/"+much, true);
        }else {
            return new ReturnInfo("0/0", true);
        }
    }

    @RequestMapping(value = "/matchRtByGame/{gameCode}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo getRtMatchByGame(@PathVariable String gameCode, HttpServletRequest req) {
        int sum = dbService.getRtMatchByGame(gameCode);
        return new ReturnInfo(sum+"/"+sum, true);
    }

    @RequestMapping(value = "/queryAllRtProvinces", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryAllRtProvinces(HttpServletRequest req) {
        List<String> allRtProvinces = dbService.getAllRtProvinces();
        return new ReturnInfo("实时模式省份",allRtProvinces.size(),allRtProvinces,true);
    }


    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            ParaProvinceInfo info = new ParaProvinceInfo();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody ParaProvinceInfo info, HttpServletRequest req) {
        try {
            ParaProvinceInfo oldPojo = null;
            if (info != null) {
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaProvinceInfoCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_PARA_PROVINCE_INFO";
    }

    @SuppressWarnings("serial")
    public static class ParaProvinceInfos extends ArrayList<ParaProvinceInfo> {
        public ParaProvinceInfos() {
            super();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/selectAllProvinceId", method = RequestMethod.GET)
    public ReturnInfo selectAllProvinceId(){
        List<ParaProvinceInfo> all = dbService.findAll();
        if (CommonUtils.isNotEmpty(all)){
            Collections.sort(all, new Comparator<ParaProvinceInfo>() {
                @Override
                public int compare(ParaProvinceInfo o1, ParaProvinceInfo o2) {
                    return Integer.parseInt(o1.getProvinceId())-Integer.parseInt(o2.getProvinceId());
                }
            });

            return new ReturnInfo("省码信息",200,all,true);
        }
        return ReturnInfo.Faild;
    }
}
