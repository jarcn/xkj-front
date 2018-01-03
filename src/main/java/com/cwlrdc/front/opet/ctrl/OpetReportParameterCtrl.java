package com.cwlrdc.front.opet.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cwlrdc.commondb.opet.entity.OpetReportParameter;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterExample;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterKey;
import com.cwlrdc.front.opet.service.OpetReportParameterService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.IDGenerator;
import com.joyveb.lbos.restful.util.KeyExplainHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/opetReportParameter")
public class OpetReportParameterCtrl {

	private @Resource OpetReportParameterService dbService;

	@RequestMapping(value = "/bygameselect/{gameCode}", method = RequestMethod.GET)
	@ResponseBody
	public OpetReportParameter select(@PathVariable String gameCode) {
		OpetReportParameter opetReportParameter = null;
		try {
			OpetReportParameterKey key=new OpetReportParameterKey();
			key.setGameCode(gameCode);
			opetReportParameter=dbService.selectByPrimaryKey(key);
			return opetReportParameter;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl bygameselect error..", e);
		}
		return opetReportParameter;
	}

	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody OpetReportParameter info,HttpServletRequest req) {
		try {
			info.setUuid(IDGenerator.getInstance().generate());
			dbService.insert(info);
			return ReturnInfo.Success;
		}catch (DuplicateKeyException e){
			log.trace("主键冲突",e);
			return new ReturnInfo("添加失败,不可重复添加",false);
		} catch (Exception e) {
			log.warn("  opetReportParameter insert error..", e);
		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody OpetReportParameter info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl update error..",e);

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
			dc.setEntityClass(OpetReportParameter.class);
			dc.setKeyClass(OpetReportParameterKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl get error..",e);

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
			List<OpetReportParameter> list = new ArrayList<OpetReportParameter>();
			for(String id :data){
				OpetReportParameter info = new OpetReportParameter();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl batchDelete error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody OpetReportParameters data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl batchUpdate error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody OpetReportParameters data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl batchInsert error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<OpetReportParameter> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<OpetReportParameter> list = new ArrayList<>();
		try {
			OpetReportParameter info = new OpetReportParameter();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}

	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			OpetReportParameter info = new OpetReportParameter();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody OpetReportParameter info,HttpServletRequest req) {
		try {
			OpetReportParameter oldPojo = null;
			if(info!=null){
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetReportParameterCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	private String getControllerName(){
		return this.getClass().getSimpleName();
	}

	private String getTableName(){
		return "T_OPET_REPORT_PARAMETER";
	}

	@SuppressWarnings("serial")
	public static class OpetReportParameters extends ArrayList<OpetReportParameter> {  
		public OpetReportParameters() { super(); }  
	}
}
