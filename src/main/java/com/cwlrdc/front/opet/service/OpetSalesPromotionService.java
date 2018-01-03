package com.cwlrdc.front.opet.service;

import com.cwlrdc.commondb.opet.entity.OpetSalesPromotion;
import com.cwlrdc.commondb.opet.entity.OpetSalesPromotionExample;
import com.cwlrdc.commondb.opet.entity.OpetSalesPromotionExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetSalesPromotionKey;
import com.cwlrdc.commondb.opet.mapper.OpetSalesPromotionMapper;
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

public class OpetSalesPromotionService implements ServiceInterface<OpetSalesPromotion, OpetSalesPromotionExample, OpetSalesPromotionKey>{

	@Resource
	private OpetSalesPromotionMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(OpetSalesPromotionExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetSalesPromotionExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetSalesPromotionKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetSalesPromotion record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetSalesPromotion record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetSalesPromotion> records)
			 {
		for(OpetSalesPromotion record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetSalesPromotion> records)
			 {
		for(OpetSalesPromotion record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetSalesPromotion> records)
			 {
		for(OpetSalesPromotion record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetSalesPromotion> selectByExample(OpetSalesPromotionExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public OpetSalesPromotion selectByPrimaryKey(OpetSalesPromotionKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetSalesPromotion> findAll(List<OpetSalesPromotion> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetSalesPromotionExample());
		}
		List<OpetSalesPromotion> list = new ArrayList<>();
		for(OpetSalesPromotion record : records){
			OpetSalesPromotion result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetSalesPromotion record, OpetSalesPromotionExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetSalesPromotion record, OpetSalesPromotionExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetSalesPromotion record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetSalesPromotion record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetSalesPromotionExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetSalesPromotionExample());
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
	public OpetSalesPromotionExample getExample(OpetSalesPromotion record) {
		OpetSalesPromotionExample example = new OpetSalesPromotionExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
			if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
			}
			if(record.getPromotionId()!=null){
				criteria.andPromotionIdEqualTo(record.getPromotionId());
			}
			if(record.getYear()!=null){
				criteria.andYearEqualTo(record.getYear());
			}
			if(record.getStartPeriod()!=null){
				criteria.andStartPeriodEqualTo(record.getStartPeriod());
			}
			if(record.getEndPeriod()!=null){
				criteria.andEndPeriodEqualTo(record.getEndPeriod());
			}
			if(record.getSendPrizeAmount()!=null){
				criteria.andSendPrizeAmountEqualTo(record.getSendPrizeAmount());
			}
			if(record.getPromotionPrizeLevel()!=null){
				criteria.andPromotionPrizeLevelEqualTo(record.getPromotionPrizeLevel());
			}
			if(record.getBetType()!=null){
				criteria.andBetTypeEqualTo(record.getBetType());
			}
			if(record.getBetMaxAmount()!=null){
				criteria.andBetMaxAmountEqualTo(record.getBetMaxAmount());
			}
			if(record.getRamark()!=null){
				criteria.andRamarkEqualTo(record.getRamark());
			}
			if(record.getStatus()!=null){
				criteria.andStatusEqualTo(record.getStatus());
			}

		}
		return example;
	}


	public OpetSalesPromotion selectByKey(String gameCode,String year,String promotionId){
		OpetSalesPromotionKey key = new OpetSalesPromotionKey();
		key.setGameCode(gameCode);
		key.setPromotionId(promotionId);
		key.setYear(year);
		return  mapper.selectByPrimaryKey(key);
	}

}
