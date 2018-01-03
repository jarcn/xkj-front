package com.cwlrdc.front.rt.service;

import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRT;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRTExample;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRTExample.Criteria;
import com.cwlrdc.commondb.rt.entity.LttoProvinceSalesDataRTKey;
import com.cwlrdc.commondb.rt.mapper.LttoProvinceSalesDataRTMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.SqlMaker;
import com.unlto.twls.commonutil.component.CommonUtils;
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
public class LttoProvinceSalesDataRTService implements ServiceInterface<LttoProvinceSalesDataRT, LttoProvinceSalesDataRTExample, LttoProvinceSalesDataRTKey> {

	@Resource
	private LttoProvinceSalesDataRTMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoProvinceSalesDataRTExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoProvinceSalesDataRTExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoProvinceSalesDataRTKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoProvinceSalesDataRT record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoProvinceSalesDataRT record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoProvinceSalesDataRT> records)
			 {
		for(LttoProvinceSalesDataRT record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoProvinceSalesDataRT> records)
			 {
		for(LttoProvinceSalesDataRT record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoProvinceSalesDataRT> records)
			 {
		for(LttoProvinceSalesDataRT record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoProvinceSalesDataRT> selectByExample(LttoProvinceSalesDataRTExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoProvinceSalesDataRT selectByPrimaryKey(LttoProvinceSalesDataRTKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoProvinceSalesDataRT> findAll(List<LttoProvinceSalesDataRT> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoProvinceSalesDataRTExample());
		}
		List<LttoProvinceSalesDataRT> list = new ArrayList<>();
		for(LttoProvinceSalesDataRT record : records){
			LttoProvinceSalesDataRT result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoProvinceSalesDataRT record, LttoProvinceSalesDataRTExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoProvinceSalesDataRT record, LttoProvinceSalesDataRTExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoProvinceSalesDataRT record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoProvinceSalesDataRT record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoProvinceSalesDataRTExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoProvinceSalesDataRTExample());
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
	public LttoProvinceSalesDataRTExample getExample(LttoProvinceSalesDataRT record) {
		LttoProvinceSalesDataRTExample example = new LttoProvinceSalesDataRTExample();
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
				if(record.getAmountDetail()!=null){
				criteria.andAmountDetailEqualTo(record.getAmountDetail());
				}
				if(record.getCancelMoneyDetail()!=null){
				criteria.andCancelMoneyDetailEqualTo(record.getCancelMoneyDetail());
				}
				if(record.getDataStatus()!=null){
				criteria.andDataStatusEqualTo(record.getDataStatus());
				}
				if(record.getUploadTime()!=null){
				criteria.andUploadTimeEqualTo(record.getUploadTime());
				}
				if(record.getFilePath()!=null){
				criteria.andFilePathEqualTo(record.getFilePath());
				}

		}
		return example;
	}

	public LttoProvinceSalesDataRT selectByKey(String periodNum, String gameCode, String provinceId) {
		LttoProvinceSalesDataRTKey key = new LttoProvinceSalesDataRTKey();
		key.setGameCode(gameCode);
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(key);
	}

	public List<LttoProvinceSalesDataRT> selectDatas(String periodNum, String gameCode, List<String> provinceIds) {
		LttoProvinceSalesDataRTExample example = new LttoProvinceSalesDataRTExample();
		if(!CommonUtils.isEmpty(provinceIds)){
			example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andProvinceIdIn(provinceIds);
		}else{
			example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
		}
		return mapper.selectByExample(example);
	}
}
