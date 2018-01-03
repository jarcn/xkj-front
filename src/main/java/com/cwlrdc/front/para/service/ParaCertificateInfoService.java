package com.cwlrdc.front.para.service;
import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.commondb.para.entity.ParaCertificateInfo;
import com.cwlrdc.commondb.para.entity.ParaCertificateInfoExample;
import com.cwlrdc.commondb.para.entity.ParaCertificateInfoExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaCertificateInfoKey;
import com.cwlrdc.commondb.para.mapper.ParaCertificateInfoMapper;
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
public class ParaCertificateInfoService implements ServiceInterface<ParaCertificateInfo, ParaCertificateInfoExample, ParaCertificateInfoKey>{

	@Resource
	private ParaCertificateInfoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(ParaCertificateInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(ParaCertificateInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(ParaCertificateInfoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ParaCertificateInfo record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(ParaCertificateInfo record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<ParaCertificateInfo> records)
			 {
		for(ParaCertificateInfo record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<ParaCertificateInfo> records)
			 {
		for(ParaCertificateInfo record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<ParaCertificateInfo> records)
			 {
		for(ParaCertificateInfo record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<ParaCertificateInfo> selectByExample(ParaCertificateInfoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public ParaCertificateInfo selectByPrimaryKey(ParaCertificateInfoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<ParaCertificateInfo> findAll(List<ParaCertificateInfo> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new ParaCertificateInfoExample());
		}
		List<ParaCertificateInfo> list = new ArrayList<>();
		for(ParaCertificateInfo record : records){
			ParaCertificateInfo result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(ParaCertificateInfo record, ParaCertificateInfoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(ParaCertificateInfo record, ParaCertificateInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(ParaCertificateInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ParaCertificateInfo record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(ParaCertificateInfoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new ParaCertificateInfoExample());
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
	public ParaCertificateInfoExample getExample(ParaCertificateInfo record) {
		ParaCertificateInfoExample example = new ParaCertificateInfoExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getUuid()!=null){
				criteria.andUuidEqualTo(record.getUuid());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getUkeyId()!=null){
				criteria.andUkeyIdEqualTo(record.getUkeyId());
				}
				if(record.getIsUse()!=null){
				criteria.andIsUseEqualTo(record.getIsUse());
				}
				if(record.getUkeyPath()!=null){
				criteria.andUkeyPathEqualTo(record.getUkeyPath());
				}
				if(record.getRamark()!=null){
				criteria.andRamarkEqualTo(record.getRamark());
				}

		}
		return example;
	}
}
