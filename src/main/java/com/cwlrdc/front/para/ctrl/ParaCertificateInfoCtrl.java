package com.cwlrdc.front.para.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.joyveb.lbos.restful.util.IDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;

import com.cwlrdc.front.para.service.ParaCertificateInfoService;
import  com.cwlrdc.commondb.para.entity.ParaCertificateInfo;
import  com.cwlrdc.commondb.para.entity.ParaCertificateInfoKey;
import  com.cwlrdc.commondb.para.entity.ParaCertificateInfoExample;

@Slf4j
@Controller
@RequestMapping("/paraCertificateInfo")
public class ParaCertificateInfoCtrl {
	
	private @Resource ParaCertificateInfoService dbService;
	
	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody ParaCertificateInfo info,HttpServletRequest req) {
		try {
			info.setUuid(IDGenerator.getInstance().generate());
			dbService.insert(info);
			return ReturnInfo.Success;
		}catch (DuplicateKeyException e){
			log.trace("主键冲突",e);
			return new ReturnInfo("添加失败,不可重复添加",false);
		} catch (Exception e) {
			log.warn("  paraCertificateInfo insert error..", e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody ParaCertificateInfo info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl update error..",e);
			
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
			dc.setEntityClass(ParaCertificateInfo.class);
			dc.setKeyClass(ParaCertificateInfoKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl get error..",e);
			
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
			List<ParaCertificateInfo> list = new ArrayList<ParaCertificateInfo>();
			for(String id :data){
				ParaCertificateInfo info = new ParaCertificateInfo();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl batchDelete error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody ParaCertificateInfos data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl batchUpdate error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody ParaCertificateInfos data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
		    log.warn("  ParaCertificateInfoCtrl batchInsert error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<ParaCertificateInfo> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<ParaCertificateInfo> list = new ArrayList<>();
		try {
			ParaCertificateInfo info = new ParaCertificateInfo();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			ParaCertificateInfo info = new ParaCertificateInfo();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody ParaCertificateInfo info,HttpServletRequest req) {
		try {
			ParaCertificateInfo oldPojo = null;
			if(info!=null){
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaCertificateInfoCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	private String getControllerName(){
		return this.getClass().getSimpleName();
	}
	
	private String getTableName(){
		return "T_PARA_CERTIFICATE_INFO";
	}
	
	@SuppressWarnings("serial")
	public static class ParaCertificateInfos extends ArrayList<ParaCertificateInfo> {  
	    public ParaCertificateInfos() { super(); }  
	}
}
