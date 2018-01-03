package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementKey;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataKey;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoLotteryAnnouncementService;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lttoWinstatData")
public class LttoWinstatDataCtrl {


    @Resource
    private LttoWinstatDataService dbService;
    @Resource
    private LttoLotteryAnnouncementService announcementService;
    @Resource
    private ParaGamePeriodInfoService periodInfoService;
    @Resource
    private LttoCancelWinStatDataService cancelWinStatDataService;
    private @Resource
	LttoLotteryAnnouncementService lttoLotteryAnnouncemenService;


    /**
     * 查询当期中奖统计文件上传成功情况
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
        return new ReturnInfo("中奖文件上传情况", i, null,true);
    }




    @RequestMapping(value = "/updateBonus/{gameCode}/{periodNum}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdateBonus(@PathVariable String gameCode,@PathVariable String periodNum) {
        try { 
          	LttoLotteryAnnouncementKey lttoLotteryAnnouncementKey = new LttoLotteryAnnouncementKey();
          	lttoLotteryAnnouncementKey.setGameCode(gameCode);
          	lttoLotteryAnnouncementKey.setPeriodNum(periodNum);
          	LttoLotteryAnnouncement selectByExample = lttoLotteryAnnouncemenService.selectByPrimaryKey(lttoLotteryAnnouncementKey);
          	if(selectByExample == null) {
          		log.warn("中奖金额未统计");
          		return ReturnInfo.Faild;
          	}
          	String sql = "UPDATE T_LTTO_WINSTAT_DATA SET PRIZE1_MONEY = "+selectByExample.getPrize1Money() +
          		",PRIZE2_MONEY = "+ selectByExample.getPrize2Money() +",PRIZE3_MONEY = "+ selectByExample.getPrize3Money() +
          		",PRIZE4_MONEY = "+ selectByExample.getPrize4Money() +",PRIZE5_MONEY = "+ selectByExample.getPrize5Money() +
    		    		",PRIZE6_MONEY = "+ selectByExample.getPrize6Money() +",PRIZE7_MONEY = "+ selectByExample.getPrize7Money() +
    		    		",PRIZE8_MONEY = "+ selectByExample.getPrize8Money() +" where  GAME_CODE = "+ gameCode +" and PERIOD_NUM = "+ periodNum;
    		    dbService.dosql(sql);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl batchUpdateBonus error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody LttoWinstatData info, HttpServletRequest req) {
        try {
        	long uploadTime = System.currentTimeMillis();
			info.setUploadTime(uploadTime);
            dbService.insert(info);
            return ReturnInfo.Success;
        }catch (DuplicateKeyException e) {
            log.trace("中奖统计维护添加主键冲突",e);
            return new ReturnInfo("添加失败,主键冲突",false);
        }catch(Exception e) {
            log.warn("  LttoWinstatDataCtrl insert error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody LttoWinstatData info, HttpServletRequest req) {
        try {
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl update error..", e);

        }
        return ReturnInfo.Faild;
    }


    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/query/windata/{provinceId}/{gameCode}/{periodNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo queryWinDataByPro(@PathVariable String provinceId, @PathVariable String gameCode,
                                        @PathVariable String periodNum, HttpServletRequest req) {
        ReturnInfo info = new ReturnInfo();
        LttoWinstatData lttoWinstatData = dbService.selectByKey(periodNum, gameCode, provinceId);
        if (null!=lttoWinstatData) {
            info.setRetObj(lttoWinstatData);
            info.setSuccess(true);
        } else {
            info.setSuccess(false);
        }
        return info;
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
            dc.setEntityClass(LttoWinstatData.class);
            dc.setKeyClass(LttoWinstatDataKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl get error..", e);

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
            List<LttoWinstatData> list = new ArrayList<LttoWinstatData>();
            for (String id : data) {
                LttoWinstatData info = new LttoWinstatData();
                KeyExplainHandler.explainKey(id, info);
                list.add(info);
            }
            dbService.batchDelete(list);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl batchDelete error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo batchUpdate(@RequestBody LttoWinstatDatas data, HttpServletRequest req) {
        try {
            dbService.batchUpdate(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl batchUpdate error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchInsert(@RequestBody LttoWinstatDatas data, HttpServletRequest req) {
        try {
            dbService.batchInsert(data);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl batchInsert error..", e);

        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<LttoWinstatData> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<LttoWinstatData> list = new ArrayList<>();
        try {
            LttoWinstatData info = new LttoWinstatData();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            LttoWinstatData info = new LttoWinstatData();
            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody LttoWinstatData info, HttpServletRequest req) {
        try {
            LttoWinstatData oldPojo = null;
            if (info != null) {
    			info.setUploadTime(System.currentTimeMillis());
    			info.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  LttoWinstatDataCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_LTTO_WINSTAT_DATA";
    }

    @SuppressWarnings("serial")
    public static class LttoWinstatDatas extends ArrayList<LttoWinstatData> {
        public LttoWinstatDatas() {
            super();
        }
    }
}
