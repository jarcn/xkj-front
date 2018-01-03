package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaGameinfo;
import com.cwlrdc.commondb.para.entity.ParaGameinfoExample;
import com.cwlrdc.commondb.para.entity.ParaGameinfoExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaGameinfoKey;
import com.cwlrdc.commondb.para.mapper.ParaGameinfoMapper;
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
public class ParaGameinfoService implements ServiceInterface<ParaGameinfo, ParaGameinfoExample, ParaGameinfoKey>{

	@Resource
	private ParaGameinfoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(ParaGameinfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(ParaGameinfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(ParaGameinfoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ParaGameinfo record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(ParaGameinfo record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<ParaGameinfo> records)
			 {
		for(ParaGameinfo record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<ParaGameinfo> records)
			 {
		for(ParaGameinfo record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<ParaGameinfo> records)
			 {
		for(ParaGameinfo record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<ParaGameinfo> selectByExample(ParaGameinfoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public ParaGameinfo selectByPrimaryKey(ParaGameinfoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<ParaGameinfo> findAll(List<ParaGameinfo> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new ParaGameinfoExample());
		}
		List<ParaGameinfo> list = new ArrayList<>();
		for(ParaGameinfo record : records){
			ParaGameinfo result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(ParaGameinfo record, ParaGameinfoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(ParaGameinfo record, ParaGameinfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(ParaGameinfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ParaGameinfo record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(ParaGameinfoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new ParaGameinfoExample());
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
	public ParaGameinfoExample getExample(ParaGameinfo record) {
		ParaGameinfoExample example = new ParaGameinfoExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getGameName()!=null){
				criteria.andGameNameEqualTo(record.getGameName());
				}
				if(record.getMarks()!=null){
				criteria.andMarksEqualTo(record.getMarks());
				}

		}
		return example;
	}

	public List<ParaGameinfo> selectAllGames(){
		return mapper.selectByExample(new ParaGameinfoExample());
	}
}
