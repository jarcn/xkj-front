package com.cwlrdc.front.rt.service;

import com.cwlrdc.commondb.rt.entity.*;
import com.cwlrdc.commondb.rt.entity.LttoSalesAuditLotoRTExample.Criteria;
import com.cwlrdc.commondb.rt.mapper.LttoSalesAuditLotoRTMapper;
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
public class LttoSalesAuditLotoRTService implements ServiceInterface<LttoSalesAuditLotoRT, LttoSalesAuditLotoRTExample, LttoSalesAuditLotoRTKey> {

	@Resource
	private LttoSalesAuditLotoRTMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoSalesAuditLotoRTExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoSalesAuditLotoRTExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoSalesAuditLotoRTKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoSalesAuditLotoRT record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoSalesAuditLotoRT record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoSalesAuditLotoRT> records)
			 {
		for(LttoSalesAuditLotoRT record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoSalesAuditLotoRT> records)
			 {
		for(LttoSalesAuditLotoRT record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoSalesAuditLotoRT> records)
			 {
		for(LttoSalesAuditLotoRT record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoSalesAuditLotoRT> selectByExample(LttoSalesAuditLotoRTExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoSalesAuditLotoRT selectByPrimaryKey(LttoSalesAuditLotoRTKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoSalesAuditLotoRT> findAll(List<LttoSalesAuditLotoRT> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoSalesAuditLotoRTExample());
		}
		List<LttoSalesAuditLotoRT> list = new ArrayList<>();
		for(LttoSalesAuditLotoRT record : records){
			LttoSalesAuditLotoRT result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoSalesAuditLotoRT record, LttoSalesAuditLotoRTExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoSalesAuditLotoRT record, LttoSalesAuditLotoRTExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoSalesAuditLotoRT record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoSalesAuditLotoRT record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoSalesAuditLotoRTExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoSalesAuditLotoRTExample());
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
	public LttoSalesAuditLotoRTExample getExample(LttoSalesAuditLotoRT record) {
		LttoSalesAuditLotoRTExample example = new LttoSalesAuditLotoRTExample();
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


	//实时接口 查询七乐彩销售统计数据
	public ReturnInfo getRTLotoSaleData(ReturnInfo info, String periodNum) {
		LttoSalesAuditLotoRTExample example = new LttoSalesAuditLotoRTExample();
		example.createCriteria().andPeriodNumEqualTo(periodNum);
		List<LttoSalesAuditLotoRT> results = mapper.selectByExample(example);
		info.setRetObj(results);
		info.setSuccess(true);
		return info;
	}

	public LttoSalesAuditLotoRT selectByKey(String provinceId, String periodNum){
		LttoSalesAuditLotoRTKey key = new LttoSalesAuditLotoRTKey();
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(key);
	}
}
