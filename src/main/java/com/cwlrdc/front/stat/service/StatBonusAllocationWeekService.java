package com.cwlrdc.front.stat.service;

import com.cwlrdc.commondb.stat.entity.StatBonusAllocationWeek;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationWeekExample;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationWeekExample.Criteria;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationWeekKey;
import com.cwlrdc.commondb.stat.mapper.StatBonusAllocationWeekMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.SqlMaker;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StatBonusAllocationWeekService implements
		ServiceInterface<StatBonusAllocationWeek, StatBonusAllocationWeekExample, StatBonusAllocationWeekKey> {

	@Resource
	private StatBonusAllocationWeekMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(StatBonusAllocationWeekExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(StatBonusAllocationWeekExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(StatBonusAllocationWeekKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(StatBonusAllocationWeek record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(StatBonusAllocationWeek record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<StatBonusAllocationWeek> records)
			 {
		for(StatBonusAllocationWeek record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<StatBonusAllocationWeek> records)
			 {
		for(StatBonusAllocationWeek record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<StatBonusAllocationWeek> records)
			 {
		for(StatBonusAllocationWeek record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<StatBonusAllocationWeek> selectByExample(StatBonusAllocationWeekExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public StatBonusAllocationWeek selectByPrimaryKey(StatBonusAllocationWeekKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<StatBonusAllocationWeek> findAll(List<StatBonusAllocationWeek> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new StatBonusAllocationWeekExample());
		}
		List<StatBonusAllocationWeek> list = new ArrayList<>();
		for(StatBonusAllocationWeek record : records){
			StatBonusAllocationWeek result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(StatBonusAllocationWeek record, StatBonusAllocationWeekExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(StatBonusAllocationWeek record, StatBonusAllocationWeekExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(StatBonusAllocationWeek record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(StatBonusAllocationWeek record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(StatBonusAllocationWeekExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new StatBonusAllocationWeekExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.debug("查新异常",e);
			return 0;
		}
	}
	
	public List<HashMap<String,Object>> getData(DbCondi dc){
		 List<HashMap<String, Object>> resultSet = null;
                try {
                    String sql = SqlMaker.getData(dc);
                    resultSet = common.executeSql(sql);
                } catch (IllegalAccessException e) {
									log.debug("查新异常",e);
                } catch (InvocationTargetException e) {
									log.debug("查新异常",e);
                }
                return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public StatBonusAllocationWeekExample getExample(StatBonusAllocationWeek record) {
		StatBonusAllocationWeekExample example = new StatBonusAllocationWeekExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getSaleMoney()!=null){
				criteria.andSaleMoneyEqualTo(record.getSaleMoney());
				}
				if(record.getRewardBonusMoney()!=null){
				criteria.andRewardBonusMoneyEqualTo(record.getRewardBonusMoney());
				}
				if(record.getRegulationFundMoney()!=null){
				criteria.andRegulationFundMoneyEqualTo(record.getRegulationFundMoney());
				}
				if(record.getTotalMoney()!=null){
				criteria.andTotalMoneyEqualTo(record.getTotalMoney());
				}
				if(record.getWinBonusMoney()!=null){
				criteria.andWinBonusMoneyEqualTo(record.getWinBonusMoney());
				}
				if(record.getAllocationBonusMoney()!=null){
				criteria.andAllocationBonusMoneyEqualTo(record.getAllocationBonusMoney());
				}
				if(record.getRealityPayCash()!=null){
				criteria.andRealityPayCashEqualTo(record.getRealityPayCash());
				}

		}
		return example;
	}
}
