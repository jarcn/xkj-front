package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaFtpInfoExample;
import com.cwlrdc.commondb.para.entity.ParaFtpInfoExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaFtpInfoKey;
import com.cwlrdc.commondb.para.mapper.ParaFtpInfoMapper;
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
public class ParaFtpInfoService implements ServiceInterface<ParaFtpInfo, ParaFtpInfoExample, ParaFtpInfoKey> {

	@Resource
	private ParaFtpInfoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(ParaFtpInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(ParaFtpInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(ParaFtpInfoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ParaFtpInfo record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(ParaFtpInfo record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<ParaFtpInfo> records)
			 {
		for(ParaFtpInfo record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<ParaFtpInfo> records)
			 {
		for(ParaFtpInfo record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<ParaFtpInfo> records)
			 {
		for(ParaFtpInfo record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<ParaFtpInfo> selectByExample(ParaFtpInfoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public ParaFtpInfo selectByPrimaryKey(ParaFtpInfoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<ParaFtpInfo> findAll(List<ParaFtpInfo> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new ParaFtpInfoExample());
		}
		List<ParaFtpInfo> list = new ArrayList<>();
		for(ParaFtpInfo record : records){
			ParaFtpInfo result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(ParaFtpInfo record, ParaFtpInfoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(ParaFtpInfo record, ParaFtpInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(ParaFtpInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ParaFtpInfo record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(ParaFtpInfoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new ParaFtpInfoExample());
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
	public ParaFtpInfoExample getExample(ParaFtpInfo record) {
		ParaFtpInfoExample example = new ParaFtpInfoExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getFtpUsername()!=null){
				criteria.andFtpUsernameEqualTo(record.getFtpUsername());
				}
				if(record.getFtpPassword()!=null){
				criteria.andFtpPasswordEqualTo(record.getFtpPassword());
				}
				if(record.getFtpIp()!=null){
				criteria.andFtpIpEqualTo(record.getFtpIp());
				}
				if(record.getFlag()!=null){
				criteria.andFlagEqualTo(record.getFlag());
				}
				if(record.getSltoTime()!=null){
				criteria.andSltoTimeEqualTo(record.getSltoTime());
				}
				if(record.getLotoTime()!=null){
				criteria.andLotoTimeEqualTo(record.getLotoTime());
				}
				if(record.getFtpPort()!=null){
				criteria.andFtpPortEqualTo(record.getFtpPort());
				}
				if(record.getFtpPath()!=null){
				criteria.andFtpPathEqualTo(record.getFtpPath());
				}

		}
		return example;
	}

	public List<ParaFtpInfo> findAll() {
		return mapper.selectByExample(new ParaFtpInfoExample());
	}

}
