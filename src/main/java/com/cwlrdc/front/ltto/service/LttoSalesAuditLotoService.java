package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditLoto;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditLotoExample;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditLotoExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoSalesAuditLotoKey;
import com.cwlrdc.commondb.ltto.mapper.LttoSalesAuditLotoMapper;
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
public class LttoSalesAuditLotoService implements ServiceInterface<LttoSalesAuditLoto, LttoSalesAuditLotoExample, LttoSalesAuditLotoKey> {

	@Resource
	private LttoSalesAuditLotoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoSalesAuditLotoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoSalesAuditLotoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoSalesAuditLotoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoSalesAuditLoto record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoSalesAuditLoto record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoSalesAuditLoto> records)
			 {
		for(LttoSalesAuditLoto record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoSalesAuditLoto> records)
			 {
		for(LttoSalesAuditLoto record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoSalesAuditLoto> records)
			 {
		for(LttoSalesAuditLoto record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoSalesAuditLoto> selectByExample(LttoSalesAuditLotoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoSalesAuditLoto selectByPrimaryKey(LttoSalesAuditLotoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoSalesAuditLoto> findAll(List<LttoSalesAuditLoto> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoSalesAuditLotoExample());
		}
		List<LttoSalesAuditLoto> list = new ArrayList<>();
		for(LttoSalesAuditLoto record : records){
			LttoSalesAuditLoto result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoSalesAuditLoto record, LttoSalesAuditLotoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoSalesAuditLoto record, LttoSalesAuditLotoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoSalesAuditLoto record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoSalesAuditLoto record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoSalesAuditLotoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoSalesAuditLotoExample());
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
	public LttoSalesAuditLotoExample getExample(LttoSalesAuditLoto record) {
		LttoSalesAuditLotoExample example = new LttoSalesAuditLotoExample();
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


	// 查询七乐彩销售统计数据
	public ReturnInfo getLotoSaleData(ReturnInfo info, String periodNum) {
		LttoSalesAuditLotoExample example = new LttoSalesAuditLotoExample();
		example.createCriteria().andPeriodNumEqualTo(periodNum);
		List<LttoSalesAuditLoto> results = mapper.selectByExample(example);
		info.setRetObj(results);
		info.setSuccess(true);
		return info;
	}

	public LttoSalesAuditLoto selectByKey(String provinceId,String periodNum){
		LttoSalesAuditLotoKey key = new LttoSalesAuditLotoKey();
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(key);
	}

}
