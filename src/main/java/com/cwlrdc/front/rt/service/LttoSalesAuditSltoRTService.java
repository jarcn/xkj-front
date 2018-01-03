package com.cwlrdc.front.rt.service;

import com.cwlrdc.commondb.rt.entity.LttoSalesAuditSltoRT;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditSltoRTExample;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditSltoRTExample.Criteria;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditSltoRTKey;
import com.cwlrdc.commondb.rt.mapper.LttoSalesAuditSltoRTMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ReturnInfo;
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
public class LttoSalesAuditSltoRTService implements ServiceInterface<LttoSalesAuditSltoRT, LttoSalesAuditSltoRTExample, LttoSalesAuditSltoRTKey> {

	@Resource
	private LttoSalesAuditSltoRTMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoSalesAuditSltoRTExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoSalesAuditSltoRTExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoSalesAuditSltoRTKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoSalesAuditSltoRT record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoSalesAuditSltoRT record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoSalesAuditSltoRT> records)
			 {
		for(LttoSalesAuditSltoRT record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoSalesAuditSltoRT> records)
			 {
		for(LttoSalesAuditSltoRT record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoSalesAuditSltoRT> records)
			 {
		for(LttoSalesAuditSltoRT record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoSalesAuditSltoRT> selectByExample(LttoSalesAuditSltoRTExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoSalesAuditSltoRT selectByPrimaryKey(LttoSalesAuditSltoRTKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoSalesAuditSltoRT> findAll(List<LttoSalesAuditSltoRT> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoSalesAuditSltoRTExample());
		}
		List<LttoSalesAuditSltoRT> list = new ArrayList<>();
		for(LttoSalesAuditSltoRT record : records){
			LttoSalesAuditSltoRT result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoSalesAuditSltoRT record, LttoSalesAuditSltoRTExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoSalesAuditSltoRT record, LttoSalesAuditSltoRTExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoSalesAuditSltoRT record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoSalesAuditSltoRT record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoSalesAuditSltoRTExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoSalesAuditSltoRTExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.debug("异常",e);
			return 0;
		}
	}
	
	public List<HashMap<String,Object>> getData(DbCondi dc){
		 List<HashMap<String, Object>> resultSet = null;
                try {
                    String sql = SqlMaker.getData(dc);
                    resultSet = common.executeSql(sql);
                } catch (IllegalAccessException e) {
                    log.debug("异常",e);
                } catch (InvocationTargetException e) {
                    log.debug("异常",e);
                }
                return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public LttoSalesAuditSltoRTExample getExample(LttoSalesAuditSltoRT record) {
		LttoSalesAuditSltoRTExample example = new LttoSalesAuditSltoRTExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getRowTotal()!=null){
				criteria.andRowTotalEqualTo(record.getRowTotal());
				}
				if(record.getNocomplete()!=null){
				criteria.andNocompleteEqualTo(record.getNocomplete());
				}
				if(record.getCalEffectiveMoney()!=null){
				criteria.andCalEffectiveMoneyEqualTo(record.getCalEffectiveMoney());
				}
				if(record.getCalMovMoney()!=null){
				criteria.andCalMovMoneyEqualTo(record.getCalMovMoney());
				}
				if(record.getCompareSale()!=null){
				criteria.andCompareSaleEqualTo(record.getCompareSale());
				}
				if(record.getCompareCash()!=null){
				criteria.andCompareCashEqualTo(record.getCompareCash());
				}
				if(record.getProcessStatus()!=null){
				criteria.andProcessStatusEqualTo(record.getProcessStatus());
				}
				if(record.getTotalSaleMoney()!=null){
				criteria.andTotalSaleMoneyEqualTo(record.getTotalSaleMoney());
				}

		}
		return example;
	}

	//查询双色球销售统计数据
	public ReturnInfo getRTSltoSaleDa(ReturnInfo info, String periodNum) {
		LttoSalesAuditSltoRTExample example = new LttoSalesAuditSltoRTExample();
		example.createCriteria().andPeriodNumEqualTo(periodNum);
		List<LttoSalesAuditSltoRT> results = mapper.selectByExample(example);
		info.setRetObj(results);
		info.setSuccess(true);
		return info;
	}


	public LttoSalesAuditSltoRT selectByKey(String provinceId,String periodNum){
		LttoSalesAuditSltoRTKey key = new LttoSalesAuditSltoRTKey();
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(key);
	}
}
