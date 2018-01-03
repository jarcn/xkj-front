package com.cwlrdc.front.stat.ctrl;

import com.cwlrdc.commondb.stat.entity.StatBonusAllocation;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationKey;
import com.cwlrdc.front.stat.service.StatBonusAllocationService;
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
import java.util.Map;

/**
 * 奖金调配
 */
@Slf4j
@Controller
@RequestMapping("/statBonusAllocation")
public class StatBonusAllocationCtrl {

	private @Resource
	StatBonusAllocationService dbService;

	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody StatBonusAllocation info,HttpServletRequest req) {
		try {
			dbService.insert(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl insert error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody StatBonusAllocation info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl update error..",e);

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
			dc.setEntityClass(StatBonusAllocation.class);
			dc.setKeyClass(StatBonusAllocationKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl get error..",e);

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
			List<StatBonusAllocation> list = new ArrayList<StatBonusAllocation>();
			for(String id :data){
				StatBonusAllocation info = new StatBonusAllocation();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl batchDelete error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody StatBonusAllocations data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl batchUpdate error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody StatBonusAllocations data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl batchInsert error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<StatBonusAllocation> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<StatBonusAllocation> list = new ArrayList<>();
		try {
			StatBonusAllocation info = new StatBonusAllocation();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}
	@RequestMapping(value="/awardAdjust/{gameCode}/{periodNum}",method=RequestMethod.GET)
	@ResponseBody
	public List<HashMap<String,Object>> getAwardAdjust(@PathVariable String gameCode,@PathVariable String periodNum,HttpServletRequest req) {
		String periodYear=periodNum.substring(0, 4);
		List<HashMap<String,Object>> list=dbService.getAwardAdjust(gameCode, periodNum, periodYear);
		return list;
	}
	@RequestMapping(value = "/awardAdjustBaseInfo/{gameCode}/{periodNum}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getAwardAdjustBaseInfo(@PathVariable String gameCode, @PathVariable String periodNum) {
		String periodYear=periodNum.substring(0, 4);
		Map<String, String> map=dbService.getAwardAdjustBaseInfo(gameCode, periodNum, periodYear);
		return map;
	}
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			StatBonusAllocation info = new StatBonusAllocation();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody StatBonusAllocation info,HttpServletRequest req) {
		try {
			StatBonusAllocation oldPojo = null;
			if(info!=null){
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  StatBonusAllocationCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	private String getControllerName(){
		return this.getClass().getSimpleName();
	}

	private String getTableName(){
		return "T_STAT_BONUS_ALLOCATION";
	}

	@SuppressWarnings("serial")
	public static class StatBonusAllocations extends ArrayList<StatBonusAllocation> {  
		public StatBonusAllocations() { super(); }  
	}
}
