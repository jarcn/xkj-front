package com.cwlrdc.front.opet.service;

import com.cwlrdc.commondb.opet.entity.OpetReportParameter;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterExample;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetReportParameterKey;
import com.cwlrdc.commondb.opet.mapper.OpetReportParameterMapper;
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
public class OpetReportParameterService implements ServiceInterface<OpetReportParameter, OpetReportParameterExample, OpetReportParameterKey> {

	@Resource
	private OpetReportParameterMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(OpetReportParameterExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetReportParameterExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetReportParameterKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetReportParameter record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetReportParameter record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetReportParameter> records)
			 {
		for(OpetReportParameter record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetReportParameter> records)
			 {
		for(OpetReportParameter record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetReportParameter> records)
			 {
		for(OpetReportParameter record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetReportParameter> selectByExample(OpetReportParameterExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public OpetReportParameter selectByPrimaryKey(OpetReportParameterKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetReportParameter> findAll(List<OpetReportParameter> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetReportParameterExample());
		}
		List<OpetReportParameter> list = new ArrayList<>();
		for(OpetReportParameter record : records){
			OpetReportParameter result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetReportParameter record, OpetReportParameterExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetReportParameter record, OpetReportParameterExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetReportParameter record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetReportParameter record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetReportParameterExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetReportParameterExample());
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
	public OpetReportParameterExample getExample(OpetReportParameter record) {
		OpetReportParameterExample example = new OpetReportParameterExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getUuid()!=null){
				criteria.andUuidEqualTo(record.getUuid());
				}
				if(record.getMoneyReturn()!=null){
				criteria.andMoneyReturnEqualTo(record.getMoneyReturn());
				}
				if(record.getAdjustFund()!=null){
				criteria.andAdjustFundEqualTo(record.getAdjustFund());
				}
				if(record.getBonusSubtotal()!=null){
				criteria.andBonusSubtotalEqualTo(record.getBonusSubtotal());
				}
				if(record.getLottery()!=null){
				criteria.andLotteryEqualTo(record.getLottery());
				}
				if(record.getProvince()!=null){
				criteria.andProvinceEqualTo(record.getProvince());
				}
				if(record.getBettingShop()!=null){
				criteria.andBettingShopEqualTo(record.getBettingShop());
				}
				if(record.getOfferingSubtotal()!=null){
				criteria.andOfferingSubtotalEqualTo(record.getOfferingSubtotal());
				}
				if(record.getSaleExtract()!=null){
				criteria.andSaleExtractEqualTo(record.getSaleExtract());
				}
				if(record.getStartPeriod()!=null){
				criteria.andStartPeriodEqualTo(record.getStartPeriod());
				}
				if(record.getEndPeriod()!=null){
				criteria.andEndPeriodEqualTo(record.getEndPeriod());
				}
				if(record.getRamark()!=null){
				criteria.andRamarkEqualTo(record.getRamark());
				}

		}
		return example;
	}
}
