package com.cwlrdc.front.para.ctrl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaHolidayManage;
import com.cwlrdc.commondb.para.entity.ParaHolidayManageKey;
import com.cwlrdc.front.calc.util.DateUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaHolidayManageService;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.PageInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.spring.FieldsMapperBean;
import com.joyveb.lbos.restful.spring.QueryMapperBean;
import com.joyveb.lbos.restful.spring.RequestJsonParam;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.unlto.twls.commonutil.component.BeanCopyUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/paraHolidayManage")
public class ParaHolidayManageCtrl {

    @Resource
    private ParaHolidayManageService dbService;
    @Resource
    private ParaGamePeriodInfoService periodInfoSerivce;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo insert(@RequestBody ParaHolidayManage info, HttpServletRequest req) {
    	ParaHolidayManage holidayManage=dbService.selectByPrimaryKey(info);
    	if(null != holidayManage) {
    		return new ReturnInfo("记录已经存在", false);
    	}
    	try {
    		info.setCreateDate(System.currentTimeMillis()+"");
            int insertRows = dbService.insert(info);
            if (insertRows > 0) {
            	//更新操作
            	updateLastYearPeriodCashEnd(info);
            }
            return ReturnInfo.Success;
        }catch (DuplicateKeyException e){
            log.trace("主键冲突",e);
            return new ReturnInfo("添加失败,不可重复添加",false);
        } catch (Exception e) {
            log.warn("  paraHolidayManage insert error..", e);
        }
        return ReturnInfo.Faild;
    }

    //修改节假日
    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@PathVariable String key, @RequestBody ParaHolidayManage info, HttpServletRequest req) {
        try {
            ParaHolidayManage oldPojo = null;
            if (info != null) {
                KeyExplainHandler.explainKey(key, info);
                oldPojo = dbService.selectByPrimaryKey(info);
                BeanCopyUtils.copyProperties(oldPojo,info);
                oldPojo.setHolidayEndDate(DateFormatUtils.format(Long.parseLong(info.getHolidayStartDate()),"yyyy-MM-dd"));
                oldPojo.setHolidayEndDate(DateFormatUtils.format(Long.parseLong(info.getHolidayEndDate()),"yyyy-MM-dd"));
                dbService.updateByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaHolidayManageCtrl update by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnInfo update(@RequestBody ParaHolidayManage info, HttpServletRequest req) {
        try {
            ParaHolidayManage oldholidy = dbService.selectByPrimaryKey(info);
            dbService.updateByExample(info, dbService.getExample(info));
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaHolidayManageCtrl update error..", e);

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
            dc.setEntityClass(ParaHolidayManage.class);
            dc.setKeyClass(ParaHolidayManageKey.class);
            dc.setQmb(info);
            dc.setPageinfo(para);
            dc.setFmb(fmb);
            dc.setTalbeName(getTableName());
            totalCount = dbService.getCount(dc);
            list = dbService.getData(dc);
        } catch (Exception e) {
            log.warn("  ParaHolidayManageCtrl get error..", e);

        }
        if (para.isPage()) {
            return new ListInfo<>(totalCount, list, para);
        } else {
            return list;
        }
    }

    //批量删除节假日
    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    @ResponseBody
    public ReturnInfo batchDelete(@RequestBody List<String> data, HttpServletRequest req) {
        try {
            for (String id : data) {
                ParaHolidayManage info = new ParaHolidayManage();
                KeyExplainHandler.explainKey(id, info);
                dbService.deleteByPrimaryKey(info);
            }
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("  ParaHolidayManageCtrl batchDelete error..", e);
        }
        return ReturnInfo.Faild;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ListInfo<ParaHolidayManage> get(@PathVariable String key, HttpServletRequest req) {
        int totalCount = 1;
        List<ParaHolidayManage> list = new ArrayList<>();
        try {
            ParaHolidayManage info = new ParaHolidayManage();
            KeyExplainHandler.explainKey(key, info);
            list.add(dbService.selectByPrimaryKey(info));
        } catch (Exception e) {
            log.warn("  ParaHolidayManageCtrl get by key error..", e);
        }
        return new ListInfo<>(totalCount, list, 0, 1);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnInfo delete(@PathVariable String key, HttpServletRequest req) {
        try {
            ParaHolidayManage info = new ParaHolidayManage();

            KeyExplainHandler.explainKey(key, info);
            dbService.deleteByPrimaryKey(info);
            return ReturnInfo.Success;
        } catch (Exception e) {
            log.warn("ParaHolidayManageCtrl delete by key error..", e);
        }
        return ReturnInfo.Faild;
    }

    
    private void updateLastYearPeriodCashEnd(ParaHolidayManage holiday) throws ParseException {
    	
    	String startDate = holiday.getHolidayStartDate();
    	String year = holiday.getYear();
    	
    	List<String> nonWorkingDayList = new ArrayList<>();
    	List<String> workingDayList = new ArrayList<>();
    	List<ParaGamePeriodInfo> gamePeriodInfos = periodInfoSerivce.selectByCashEndTimeAndYear(Integer.parseInt(year) - 1, startDate);
    	if (gamePeriodInfos != null && gamePeriodInfos.size() > 0) {
    		nonWorkingDayList.addAll(dbService.buildNonWorkingdayList(holiday));
    		workingDayList.addAll(dbService.buildWorkingdayList(holiday));
    		
    		for (ParaGamePeriodInfo period : gamePeriodInfos) {
    			String cashEndTime = period.getCashEndTime();
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(DateUtil.parseDate(cashEndTime, "yyyy-MM-dd"));
    			boolean updateFlag = false;
    			for (int i = 0; i < Constant.Key.CASHEND_DEADLINE; i ++) {
    				String hopeabandoncal = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
    				int hopeDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		            if (workingDayList.contains(hopeabandoncal) ||
		                (hopeDayOfWeek != Calendar.SUNDAY && hopeDayOfWeek != Calendar.SATURDAY
		                    && !nonWorkingDayList.contains(hopeabandoncal))) {
		                break;
		            } else {
		                cal.add(Calendar.DAY_OF_YEAR, 1);
		                updateFlag = true;
		            }
    			}
    			if (updateFlag) {
    				String periodEndTime = period.getPeriodEndTime();
    				Date periodEndDate = DateUtil.parseDate(periodEndTime, "yyyy-MM-dd");
    				long durationlongvalue = cal.getTimeInMillis() - periodEndDate.getTime();
    				long cashdeadlineduration = durationlongvalue / (1000 * 3600 * 24) + 1;
    				
    				period.setCashEndTime(DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd"));
    				period.setCashTerm((int)cashdeadlineduration);
    				period.setCancelWinDate(cal.getTimeInMillis());
    				periodInfoSerivce.updateByPrimaryKeySelective(period);
    			}
    		}
    	}
    }

    private String getControllerName() {
        return this.getClass().getSimpleName();
    }

    private String getTableName() {
        return "T_PARA_HOLIDAY_MANAGE";
    }

    @SuppressWarnings("serial")
    public static class ParaHolidayManages extends ArrayList<ParaHolidayManage> {
        public ParaHolidayManages() {
            super();
        }
    }

}
