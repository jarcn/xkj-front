package com.cwlrdc.front.opet.service;
import com.cwlrdc.commondb.opet.entity.OpetLotteryLog;
import com.cwlrdc.commondb.opet.entity.OpetLotteryLogExample;
import com.cwlrdc.commondb.opet.entity.OpetLotteryLogExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetLotteryLogKey;
import com.cwlrdc.commondb.opet.mapper.OpetLotteryLogMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
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
public class OpetLotteryLogService implements ServiceInterface<OpetLotteryLog, OpetLotteryLogExample, OpetLotteryLogKey>{

	@Resource
	private OpetLotteryLogMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(OpetLotteryLogExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetLotteryLogExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetLotteryLogKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetLotteryLog record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetLotteryLog record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetLotteryLog> records)
			 {
		for(OpetLotteryLog record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetLotteryLog> records)
			 {
		for(OpetLotteryLog record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetLotteryLog> records)
			 {
		for(OpetLotteryLog record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetLotteryLog> selectByExample(OpetLotteryLogExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public OpetLotteryLog selectByPrimaryKey(OpetLotteryLogKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetLotteryLog> findAll(List<OpetLotteryLog> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetLotteryLogExample());
		}
		List<OpetLotteryLog> list = new ArrayList<>();
		for(OpetLotteryLog record : records){
			OpetLotteryLog result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetLotteryLog record, OpetLotteryLogExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetLotteryLog record, OpetLotteryLogExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetLotteryLog record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetLotteryLog record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetLotteryLogExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetLotteryLogExample());
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
	public OpetLotteryLogExample getExample(OpetLotteryLog record) {
		OpetLotteryLogExample example = new OpetLotteryLogExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getInterfaceType()!=null){
				criteria.andInterfaceTypeEqualTo(record.getInterfaceType());
				}
				if(record.getSendCount()!=null){
				criteria.andSendCountEqualTo(record.getSendCount());
				}
				if(record.getUkeyId()!=null){
				criteria.andUkeyIdEqualTo(record.getUkeyId());
				}
				if(record.getRequestPara()!=null){
				criteria.andRequestParaEqualTo(record.getRequestPara());
				}
				if(record.getResponsePara()!=null){
				criteria.andResponseParaEqualTo(record.getResponsePara());
				}
				if(record.getCreateTime()!=null){
				criteria.andCreateTimeEqualTo(record.getCreateTime());
				}
				if(record.getRamark()!=null){
				criteria.andRamarkEqualTo(record.getRamark());
				}

		}
		return example;
	}
}
