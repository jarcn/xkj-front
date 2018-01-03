package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqi;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqiKey;
import com.cwlrdc.front.ltto.service.LttoDatOvduFenqiService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lttoDatOvduFenqi")
public class LttoDatOvduFenqiCtrl {

	private @Resource
	LttoDatOvduFenqiService dbService;

	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody LttoDatOvduFenqi info,HttpServletRequest req) {
		try {
			info.setSystemOperateTime(DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
			dbService.insert(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl insert error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody LttoDatOvduFenqi info,HttpServletRequest req) {
		try {
			info.setSystemOperateTime(DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl update error..",e);

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
			dc.setEntityClass(LttoDatOvduFenqi.class);
			dc.setKeyClass(LttoDatOvduFenqiKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl get error..",e);

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
			List<LttoDatOvduFenqi> list = new ArrayList<LttoDatOvduFenqi>();
			for(String id :data){
				LttoDatOvduFenqi info = new LttoDatOvduFenqi();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl batchDelete error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody LttoDatOvduFenqis data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl batchUpdate error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody LttoDatOvduFenqis data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
		    log.warn("  LttoDatOvduFenqiCtrl batchInsert error..",e);

		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<LttoDatOvduFenqi> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<LttoDatOvduFenqi> list = new ArrayList<>();
		try {
			LttoDatOvduFenqi info = new LttoDatOvduFenqi();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}

	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			LttoDatOvduFenqi info = new LttoDatOvduFenqi();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody LttoDatOvduFenqi info,HttpServletRequest req) {
		try {
			LttoDatOvduFenqi oldPojo = null;
			if(info!=null){
				info.setSystemOperateTime(DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  LttoDatOvduFenqiCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}

	private String getControllerName(){
		return this.getClass().getSimpleName();
	}

	private String getTableName(){
		return "T_LTTO_DAT_OVDU_FENQI";
	}

	@SuppressWarnings("serial")
	public static class LttoDatOvduFenqis extends ArrayList<LttoDatOvduFenqi> {
	    public LttoDatOvduFenqis() { super(); }
	}

}
