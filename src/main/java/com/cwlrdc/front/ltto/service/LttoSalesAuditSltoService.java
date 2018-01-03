package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditSlto;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditSltoExample;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditSltoExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditSltoKey;
import com.cwlrdc.commondb.ltto.mapper.LttoSalesAuditSltoMapper;
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

@Service
@Slf4j
public class LttoSalesAuditSltoService implements ServiceInterface<LttoSalesAuditSlto, LttoSalesAuditSltoExample, LttoSalesAuditSltoKey> {

	@Resource
	private LttoSalesAuditSltoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoSalesAuditSltoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoSalesAuditSltoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoSalesAuditSltoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoSalesAuditSlto record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoSalesAuditSlto record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoSalesAuditSlto> records)
			 {
		for(LttoSalesAuditSlto record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoSalesAuditSlto> records)
			 {
		for(LttoSalesAuditSlto record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoSalesAuditSlto> records)
			 {
		for(LttoSalesAuditSlto record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoSalesAuditSlto> selectByExample(LttoSalesAuditSltoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoSalesAuditSlto selectByPrimaryKey(LttoSalesAuditSltoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoSalesAuditSlto> findAll(List<LttoSalesAuditSlto> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoSalesAuditSltoExample());
		}
		List<LttoSalesAuditSlto> list = new ArrayList<>();
		for(LttoSalesAuditSlto record : records){
			LttoSalesAuditSlto result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoSalesAuditSlto record, LttoSalesAuditSltoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoSalesAuditSlto record, LttoSalesAuditSltoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoSalesAuditSlto record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoSalesAuditSlto record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoSalesAuditSltoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoSalesAuditSltoExample());
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
	public LttoSalesAuditSltoExample getExample(LttoSalesAuditSlto record) {
		LttoSalesAuditSltoExample example = new LttoSalesAuditSltoExample();
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
	public ReturnInfo getSltoSaleData(ReturnInfo info, String periodNum) {
		LttoSalesAuditSltoExample example = new LttoSalesAuditSltoExample();
		example.createCriteria().andPeriodNumEqualTo(periodNum);
		List<LttoSalesAuditSlto> results = mapper.selectByExample(example);
		info.setRetObj(results);
		info.setSuccess(true);
		return info;
	}

	public LttoSalesAuditSlto selectByKey(String provinceId,String periodNum){
		LttoSalesAuditSltoKey  key = new LttoSalesAuditSltoKey();
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(key);
	}
}
