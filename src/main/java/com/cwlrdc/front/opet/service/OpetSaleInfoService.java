package com.cwlrdc.front.opet.service;

import com.cwlrdc.commondb.opet.entity.OpetSaleInfo;
import com.cwlrdc.commondb.opet.entity.OpetSaleInfoExample;
import com.cwlrdc.commondb.opet.entity.OpetSaleInfoExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetSaleInfoKey;
import com.cwlrdc.commondb.opet.mapper.OpetSaleInfoMapper;
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
public class OpetSaleInfoService implements ServiceInterface<OpetSaleInfo, OpetSaleInfoExample, OpetSaleInfoKey>{

	@Resource
	private OpetSaleInfoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(OpetSaleInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetSaleInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetSaleInfoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetSaleInfo record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetSaleInfo record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetSaleInfo> records)
			 {
		for(OpetSaleInfo record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetSaleInfo> records)
			 {
		for(OpetSaleInfo record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetSaleInfo> records)
			 {
		for(OpetSaleInfo record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetSaleInfo> selectByExample(OpetSaleInfoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public OpetSaleInfo selectByPrimaryKey(OpetSaleInfoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetSaleInfo> findAll(List<OpetSaleInfo> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetSaleInfoExample());
		}
		List<OpetSaleInfo> list = new ArrayList<>();
		for(OpetSaleInfo record : records){
			OpetSaleInfo result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetSaleInfo record, OpetSaleInfoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetSaleInfo record, OpetSaleInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetSaleInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetSaleInfo record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetSaleInfoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetSaleInfoExample());
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
	public OpetSaleInfoExample getExample(OpetSaleInfo record) {
		OpetSaleInfoExample example = new OpetSaleInfoExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getAmount()!=null){
				criteria.andAmountEqualTo(record.getAmount());
				}
				if(record.getCancelMoney()!=null){
				criteria.andCancelMoneyEqualTo(record.getCancelMoney());
				}
				if(record.getOperatePersion()!=null){
				criteria.andOperatePersionEqualTo(record.getOperatePersion());
				}
				if(record.getOperateTime()!=null){
				criteria.andOperateTimeEqualTo(record.getOperateTime());
				}
				if(record.getIsHandle()!=null){
				criteria.andIsHandleEqualTo(record.getIsHandle());
				}
				if(record.getUploadStatus()!=null){
				criteria.andUploadStatusEqualTo(record.getUploadStatus());
				}
				if(record.getDetailAmount()!=null){
				criteria.andDetailAmountEqualTo(record.getDetailAmount());
				}
				if(record.getDetallCancelMoney()!=null){
				criteria.andDetallCancelMoneyEqualTo(record.getDetallCancelMoney());
				}
				if(record.getFtpLasttime()!=null){
				criteria.andFtpLasttimeEqualTo(record.getFtpLasttime());
				}
				if(record.getUploadTime()!=null){
				criteria.andUploadTimeEqualTo(record.getUploadTime());
				}
				if(record.getUploadCount()!=null){
				criteria.andUploadCountEqualTo(record.getUploadCount());
				}
				if(record.getCahash()!=null){
				criteria.andCahashEqualTo(record.getCahash());
				}

		}
		return example;
	}
}
