package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatDataKey;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
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
@RequestMapping("/lttoCancelWinStatData")
public class LttoCancelWinStatDataCtrl {

	@Resource
	private LttoCancelWinStatDataService dbService;

	@Resource
	private ParaGamePeriodInfoService periodInfoService;

	/**
	 * 查询当期弃奖文件上传成功情况
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
		return new ReturnInfo("弃奖文件上传情况", i, null,true);
	}

	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody LttoCancelWinStatData info,HttpServletRequest req) {
		try {
			long uploadTime = System.currentTimeMillis();
			info.setUploadTime(uploadTime);
			dbService.insert(info);
			return ReturnInfo.Success;
		}catch (DuplicateKeyException e){
			log.trace("主键冲突",e);
			return new ReturnInfo("添加失败,不可重复添加",false);
		} catch (Exception e) {
			log.warn("  lttoCancelWinStatData insert error..", e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody LttoCancelWinStatData info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl update error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public Object get(@RequestJsonParam(value = "query",required=false) QueryMapperBean info,
			@RequestJsonParam(value = "fields",required=false) FieldsMapperBean fmb,
			PageInfo para, HttpServletRequest req) {
		int totalCount = 0;
		List<HashMap<String,Object>> list = null;
		try {
			DbCondi dc = new DbCondi();
			dc.setEntityClass(LttoCancelWinStatData.class);
			dc.setKeyClass(LttoCancelWinStatDataKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl get error..",e);
			
		}
		if(para.isPage()){
			return new ListInfo<>(totalCount, list, para);
		}else{
			return list;
		}
	}
	
	@RequestMapping(value="/batch/delete",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchDelete(@RequestBody List<String> data,HttpServletRequest req) {
		try {
			List<LttoCancelWinStatData> list = new ArrayList<LttoCancelWinStatData>();
			for(String id :data){
				LttoCancelWinStatData info = new LttoCancelWinStatData();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl batchDelete error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody LttoCancelWinStatDatas data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl batchUpdate error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody LttoCancelWinStatDatas data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
		    log.warn("  LttoCancelWinStatDataCtrl batchInsert error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<LttoCancelWinStatData> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<LttoCancelWinStatData> list = new ArrayList<>();
		try {
			LttoCancelWinStatData info = new LttoCancelWinStatData();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			LttoCancelWinStatData info = new LttoCancelWinStatData();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody LttoCancelWinStatData info,HttpServletRequest req) {
		try {
			LttoCancelWinStatData oldPojo = null;
			if(info!=null){
				info.setUploadTime(System.currentTimeMillis());
				info.setDataStatus(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoCancelWinStatDataCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	private String getControllerName(){
		return this.getClass().getSimpleName();
	}
	
	private String getTableName(){
		return "T_LTTO_CANCELWIN_STAT_DATA";
	}
	
	@SuppressWarnings("serial")
	public static class LttoCancelWinStatDatas extends ArrayList<LttoCancelWinStatData> {  
	    public LttoCancelWinStatDatas() { super(); }  
	}
}
