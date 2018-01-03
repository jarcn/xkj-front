package com.cwlrdc.front.opet.ctrl;

import com.cwlrdc.commondb.opet.entity.OpetAddressBook;
import com.cwlrdc.commondb.opet.entity.OpetAddressBookExample;
import com.cwlrdc.commondb.opet.entity.OpetAddressBookKey;
import com.cwlrdc.front.opet.service.OpetAddressBookService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.unlto.twls.commonutil.component.IDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 */
@Slf4j
@Controller
@RequestMapping("/opetAddressBook")
public class OpetAddressBookCtrl {
	
	private @Resource OpetAddressBookService dbService;
	
	@RequestMapping(value="",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo insert(@RequestBody OpetAddressBook info,HttpServletRequest req) {
		try {
            String generate = IDGenerator.getInstance().generate();
            info.setUuid(generate);
            dbService.insert(info);
			return ReturnInfo.Success;
		}catch (DuplicateKeyException e){
			log.trace("主键冲突",e);
			return new ReturnInfo("添加失败,不可重复添加",false);
		} catch (Exception e) {
			log.warn("  opetAddressBook insert error..", e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@RequestBody OpetAddressBook info,HttpServletRequest req) {
		try {
			dbService.updateByExample(info,dbService.getExample(info));
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl update error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public Object get(@RequestJsonParam(value = "query",required=false) QueryMapperBean info,
			@RequestJsonParam(value = "fields",required=false) FieldsMapperBean fmb,
			PageInfo para, HttpServletRequest req) {
		int totalCount = 0;
		List<HashMap<String,Object>> list = null;
		try {
			DbCondi dc = new DbCondi();
			dc.setEntityClass(OpetAddressBook.class);
			dc.setKeyClass(OpetAddressBookKey.class);
			dc.setQmb(info);
			dc.setPageinfo(para);
			dc.setFmb(fmb);
			dc.setTalbeName(getTableName());
			totalCount = dbService.getCount(dc);
			list = dbService.getData(dc);
			for(HashMap<String,Object> map : list){
				StringBuilder provinceIdsort = new StringBuilder();
				provinceIdsort.append(map.get("provinceId"));
				String sort = "0";
				if (map.get("sort") != null && map.get("sort").toString().length() > 0) {
					sort = (String) map.get("sort");
				}
				provinceIdsort.append(sort);
				map.put("provinceIdsort", provinceIdsort.toString());
			}
			Collections.sort(list, new Comparator<HashMap<String,Object>>() {
				@Override
				public int compare(HashMap<String,Object> o1, HashMap<String,Object> o2) {
					return Integer.parseInt(o1.get("provinceIdsort").toString()) - Integer.parseInt(o2.get("provinceIdsort").toString());
				}
			});
		} catch (Exception e) {
			log.warn("  OpetLotteryLogCtrl get error..",e);
			
		}
		if(para.isPage()){
			return new ListInfo<>(totalCount, list, para);
		}else{
			return list;
		}
	}
	
	@RequestMapping(value = "/null", method = RequestMethod.GET)
	@ResponseBody
	public Object getNull(@RequestJsonParam(value = "query",required=false) QueryMapperBean info,
			@RequestJsonParam(value = "fields",required=false) FieldsMapperBean fmb,
			PageInfo para, HttpServletRequest req) {
		int totalCount = 0;
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		return new ListInfo<>(totalCount, list, para);

	}
	
	@RequestMapping(value = "/men/{provinceId}/{status}", method = RequestMethod.GET)
	@ResponseBody
	public Object getMen(@PathVariable String provinceId,@PathVariable Integer status) {
		OpetAddressBookExample opetAddressBookExample = new OpetAddressBookExample();
		opetAddressBookExample.createCriteria().andProvinceIdEqualTo(provinceId).andStatusEqualTo(status);
		List<OpetAddressBook> selectByExample = dbService.selectByExample(opetAddressBookExample);
		return selectByExample;
	}
	@RequestMapping(value = "/menAll/{provinceId}/{drawName}", method = RequestMethod.GET)
	@ResponseBody
	public Object getMenAll(@PathVariable String provinceId,@PathVariable String drawName) {
		OpetAddressBookExample opetAddressBookExample = new OpetAddressBookExample();
		opetAddressBookExample.createCriteria().andProvinceIdEqualTo(provinceId).andDrawNameEqualTo(drawName);
		List<OpetAddressBook> selectByExample = dbService.selectByExample(opetAddressBookExample);
		if(selectByExample != null){
			return selectByExample;
		}
		log.warn("全国开奖人员记录人员查询失败");
		return ReturnInfo.Faild;
	}

	@RequestMapping(value = "/menAllProvince/{drawName}", method = RequestMethod.GET)
	@ResponseBody
	public Object getMenAllProvince(@PathVariable String drawName) {
		OpetAddressBookExample opetAddressBookExample = new OpetAddressBookExample();
		opetAddressBookExample.createCriteria().andDrawNameEqualTo(drawName);
		List<OpetAddressBook> selectByExample = dbService.selectByExample(opetAddressBookExample);
		if(selectByExample != null){
			return selectByExample;
		}
		log.warn("全国开奖人员记录人员查询失败");
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch/delete",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchDelete(@RequestBody List<String> data,HttpServletRequest req) {
		try {
			List<OpetAddressBook> list = new ArrayList<OpetAddressBook>();
			for(String id :data){
				OpetAddressBook info = new OpetAddressBook();
				KeyExplainHandler.explainKey(id, info);
				list.add(info);
			}
			dbService.batchDelete( list);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl batchDelete error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo batchUpdate(@RequestBody OpetAddressBooks data,HttpServletRequest req) {
		try {
			dbService.batchUpdate(data);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl batchUpdate error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/batch",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo batchInsert(@RequestBody OpetAddressBooks data,HttpServletRequest req) {
		try {
			dbService.batchInsert( data);
			return ReturnInfo.Success;
		} catch (Exception e) {
		    log.warn("  OpetAddressBookCtrl batchInsert error..",e);
			
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.GET)
	@ResponseBody
	public ListInfo<OpetAddressBook> get(@PathVariable String key,HttpServletRequest req) {
		int totalCount = 1;
		List<OpetAddressBook> list = new ArrayList<>();
		try {
			OpetAddressBook info = new OpetAddressBook();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl get by key error..",e);
		}
		return  new ListInfo<>(totalCount, list, 0, 1);
	}

	/**
	 * @Description 根据主键修改状态 是否开奖
	 * @Author mafengge
	 * @Date 2017/4/22 17:01
	 */
	@RequestMapping(value = "updateStatus/{uuid}/{status}",method = RequestMethod.GET)
	@ResponseBody
	public ReturnInfo updataStatusByUser(@PathVariable String uuid,@PathVariable String status,HttpServletRequest req){
		OpetAddressBookKey oabk = new OpetAddressBookKey();
		oabk.setUuid(uuid);
		OpetAddressBook opetAddressBook = dbService.selectByPrimaryKey(oabk);
		if(StringUtils.isNotBlank(status)){
			opetAddressBook.setStatus(Integer.parseInt(status));
			dbService.updateByPrimaryKey(opetAddressBook);
			return ReturnInfo.Success;
		}else{
			return ReturnInfo.Faild;
		}
	}
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	@ResponseBody
	public ReturnInfo delete(@PathVariable String key,HttpServletRequest req) {
		try {
			OpetAddressBook info = new OpetAddressBook();
			KeyExplainHandler.explainKey(key, info);
			dbService.deleteByPrimaryKey(info);
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl delete by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	@RequestMapping(value="/{key}",method=RequestMethod.PUT)
	@ResponseBody
	public ReturnInfo update(@PathVariable String key,@RequestBody OpetAddressBook info,HttpServletRequest req) {
		try {
			OpetAddressBook oldPojo = null;
			if(info!=null){
				KeyExplainHandler.explainKey(key, info);
				oldPojo = dbService.selectByPrimaryKey(info);
				dbService.updateByPrimaryKey(info);
			}
			return ReturnInfo.Success;
		} catch (Exception e) {
			log.warn("  OpetAddressBookCtrl update by key error..",e);
		}
		return ReturnInfo.Faild;
	}
	
	private String getControllerName(){
		return this.getClass().getSimpleName();
	}
	
	private String getTableName(){
		return "T_OPET_ADDRESS_BOOK";
	}
	
	@SuppressWarnings("serial")
	public static class OpetAddressBooks extends ArrayList<OpetAddressBook> {  
	    public OpetAddressBooks() { super(); }  
	}
}
