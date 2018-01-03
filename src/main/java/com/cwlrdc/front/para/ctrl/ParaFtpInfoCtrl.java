package com.cwlrdc.front.para.ctrl;

import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaFtpInfoKey;
import com.cwlrdc.front.common.FtpInfoCache;
import com.cwlrdc.front.para.service.ParaFtpInfoService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
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
@RequestMapping("/paraFtpInfo")
public class ParaFtpInfoCtrl {

	@Resource
	private  ParaFtpInfoService dbService;
	@Autowired
	private FtpInfoCache ftpInfoCache;
	
	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody ParaFtpInfo info,HttpServletRequest req) {
		try {
			dbService.insert(info);
			ftpInfoCache.reload();
			return ReturnInfo.Success;
		}catch (DuplicateKeyException e){
			log.trace("主键冲突",e);
			return new ReturnInfo("添加失败,主键冲突",false);
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl insert error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody ParaFtpInfo info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			ftpInfoCache.reload();
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl update error..",e);
			
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
			dc.setEntityClass(ParaFtpInfo.class);
			dc.setKeyClass(ParaFtpInfoKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl get error..",e);
			
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
			List<ParaFtpInfo> list = new ArrayList<ParaFtpInfo>();
			for(String id :data){
				ParaFtpInfo info = new ParaFtpInfo();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl batchDelete error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody ParaFtpInfos data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl batchUpdate error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody ParaFtpInfos data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
		    log.warn("  ParaFtpInfoCtrl batchInsert error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<ParaFtpInfo> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<ParaFtpInfo> list = new ArrayList<>();
		try {
			ParaFtpInfo info = new ParaFtpInfo();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			ParaFtpInfo info = new ParaFtpInfo();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			ftpInfoCache.reload();
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody ParaFtpInfo info,HttpServletRequest req) {
		try {
			ParaFtpInfo oldPojo = null;
			if(info!=null){
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
				ftpInfoCache.reload();
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  ParaFtpInfoCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	private String getControllerName(){
		return this.getClass().getSimpleName();
	}
	
	private String getTableName(){
		return "T_PARA_FTP_INFO";
	}
	
	@SuppressWarnings("serial")
	public static class ParaFtpInfos extends ArrayList<ParaFtpInfo> {  
	    public ParaFtpInfos() { super(); }  
	}
}
