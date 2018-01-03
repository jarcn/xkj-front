package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaSysparame;
import com.cwlrdc.commondb.para.entity.ParaSysparameExample;
import com.cwlrdc.commondb.para.entity.ParaSysparameExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaSysparameKey;
import com.cwlrdc.commondb.para.mapper.ParaSysparameMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.joyveb.lbos.restful.util.SqlMaker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Slf4j
@Service
public class ParaSysparameService implements ServiceInterface<ParaSysparame, ParaSysparameExample, ParaSysparameKey>{

	@Resource
	private ParaSysparameMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(ParaSysparameExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(ParaSysparameExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(ParaSysparameKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ParaSysparame record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(ParaSysparame record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<ParaSysparame> records)
			 {
		for(ParaSysparame record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<ParaSysparame> records)
			 {
		for(ParaSysparame record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<ParaSysparame> records)
			 {
		for(ParaSysparame record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<ParaSysparame> selectByExample(ParaSysparameExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public ParaSysparame selectByPrimaryKey(ParaSysparameKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<ParaSysparame> findAll(List<ParaSysparame> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new ParaSysparameExample());
		}
		List<ParaSysparame> list = new ArrayList<>();
		for(ParaSysparame record : records){
			ParaSysparame result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(ParaSysparame record, ParaSysparameExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(ParaSysparame record, ParaSysparameExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(ParaSysparame record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ParaSysparame record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(ParaSysparameExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new ParaSysparameExample());
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
	
	public List<HashMap<String,Object>> getData(DbCondi dc){
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
	@Override
	public ParaSysparameExample getExample(ParaSysparame record) {
		ParaSysparameExample example = new ParaSysparameExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getThkey()!=null){
				criteria.andThkeyEqualTo(record.getThkey());
				}
				if(record.getValue()!=null){
				criteria.andValueEqualTo(record.getValue());
				}
				if(record.getMarks()!=null){
				criteria.andMarksEqualTo(record.getMarks());
				}

		}
		return example;
	}
}
