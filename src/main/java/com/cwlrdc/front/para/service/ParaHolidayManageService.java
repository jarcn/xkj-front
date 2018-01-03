package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaHolidayManage;
import com.cwlrdc.commondb.para.entity.ParaHolidayManageExample;
import com.cwlrdc.commondb.para.entity.ParaHolidayManageExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaHolidayManageKey;
import com.cwlrdc.commondb.para.mapper.ParaHolidayManageMapper;
import com.cwlrdc.front.calc.util.DateUtil;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.joyveb.lbos.restful.util.SqlMaker;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Service
public class ParaHolidayManageService implements ServiceInterface<ParaHolidayManage, ParaHolidayManageExample, ParaHolidayManageKey> {

	@Resource
	private ParaHolidayManageMapper mapper;
	private @Resource CommonSqlMapper common;
	
	public static final String COMMA = ",";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	
	@Override
	public int countByExample(ParaHolidayManageExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(ParaHolidayManageExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(ParaHolidayManageKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ParaHolidayManage record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(ParaHolidayManage record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<ParaHolidayManage> records)
			 {
		for(ParaHolidayManage record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<ParaHolidayManage> records)
			 {
		for(ParaHolidayManage record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<ParaHolidayManage> records)
			 {
		for(ParaHolidayManage record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<ParaHolidayManage> selectByExample(ParaHolidayManageExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public ParaHolidayManage selectByPrimaryKey(ParaHolidayManageKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<ParaHolidayManage> findAll(List<ParaHolidayManage> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new ParaHolidayManageExample());
		}
		List<ParaHolidayManage> list = new ArrayList<>();
		for(ParaHolidayManage record : records){
			ParaHolidayManage result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(ParaHolidayManage record, ParaHolidayManageExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(ParaHolidayManage record, ParaHolidayManageExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(ParaHolidayManage record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ParaHolidayManage record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(ParaHolidayManageExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new ParaHolidayManageExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.error("异常",e);
			return 0;
		}
	}
	
	public List<HashMap<String,Object>> getData(DbCondi dc) {
		List<HashMap<String, Object>> resultSet = null;
        try {
            String sql = SqlMaker.getData(dc);
            resultSet = common.executeSql(sql);
            KeyExplainHandler.addId(resultSet, dc.getKeyClass(),dc.getEntityClass());//add key
        } catch (IllegalAccessException e) {
            log.error("异常",e);
        } catch (InvocationTargetException e) {
            log.error("异常",e);
        }
        return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	
	public List<String> buildSupsenedList(ParaHolidayManage holiday) throws ParseException {
		return convertDateRangeToList(holiday);
	}

	public List<String> convertDateRangeToList(ParaHolidayManage holiday) throws ParseException {
		List<String> result = new ArrayList<>();
		String startdate = holiday.getHolidayStartDate();
		String enddate = holiday.getHolidayEndDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parseDate(startdate, DATE_FORMAT));
		
		Calendar calend = Calendar.getInstance();
		calend.setTime(DateUtils.parseDate(enddate, DATE_FORMAT));
		
		while (cal.compareTo(calend) <= 0) {
			result.add(DateUtil.formatDate(cal.getTime(), DATE_FORMAT));
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}

	public Set<String> buildWorkingdayList(ParaHolidayManage holiday) throws ParseException {
		Set<String> result = new HashSet<>();
		String workingday = holiday.getHolidayTxDate();
		if (StringUtils.isNotBlank(workingday)) {
			String[] workingdays = workingday.split(COMMA);
			for (String working : workingdays) {
				result.add(working);
			}
		}
		return result;
	}

	public List<String> buildNonWorkingdayList(ParaHolidayManage holiday) throws ParseException {
		return convertDateRangeToList(holiday);
	}

	@Override
	public ParaHolidayManageExample getExample(ParaHolidayManage record) {
		ParaHolidayManageExample example = new ParaHolidayManageExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getYear()!=null){
				criteria.andYearEqualTo(record.getYear());
				}
				if(record.getHolidayName()!=null){
				criteria.andHolidayNameEqualTo(record.getHolidayName());
				}
				if(record.getHolidayStartDate()!=null){
				criteria.andHolidayStartDateEqualTo(record.getHolidayStartDate());
				}
				if(record.getCreateDate()!=null){
				criteria.andCreateDateEqualTo(record.getCreateDate());
				}
				if(record.getRemark()!=null){
				criteria.andRemarkEqualTo(record.getRemark());
				}
				if(record.getHolidayEndDate()!=null){
				criteria.andHolidayEndDateEqualTo(record.getHolidayEndDate());
				}

		}
		return example;
	}
}
