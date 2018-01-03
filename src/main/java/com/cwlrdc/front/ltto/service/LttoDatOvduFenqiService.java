package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqi;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqiExample;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqiExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoDatOvduFenqiKey;
import com.cwlrdc.commondb.ltto.mapper.LttoDatOvduFenqiMapper;
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
public class LttoDatOvduFenqiService implements ServiceInterface<LttoDatOvduFenqi, LttoDatOvduFenqiExample, LttoDatOvduFenqiKey> {

	@Resource
	private LttoDatOvduFenqiMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoDatOvduFenqiExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoDatOvduFenqiExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoDatOvduFenqiKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoDatOvduFenqi record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoDatOvduFenqi record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoDatOvduFenqi> records)
			 {
		for(LttoDatOvduFenqi record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoDatOvduFenqi> records)
			 {
		for(LttoDatOvduFenqi record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoDatOvduFenqi> records)
			 {
		for(LttoDatOvduFenqi record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoDatOvduFenqi> selectByExample(LttoDatOvduFenqiExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoDatOvduFenqi selectByPrimaryKey(LttoDatOvduFenqiKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoDatOvduFenqi> findAll(List<LttoDatOvduFenqi> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoDatOvduFenqiExample());
		}
		List<LttoDatOvduFenqi> list = new ArrayList<>();
		for(LttoDatOvduFenqi record : records){
			LttoDatOvduFenqi result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoDatOvduFenqi record, LttoDatOvduFenqiExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoDatOvduFenqi record, LttoDatOvduFenqiExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoDatOvduFenqi record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoDatOvduFenqi record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoDatOvduFenqiExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoDatOvduFenqiExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.warn("LttoDatOvduFenqi getCount error",e);
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
					log.warn("LttoDatOvduFenqi getData error",e);
                } catch (InvocationTargetException e) {
					log.warn("LttoDatOvduFenqi getData error",e);
                }
                return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public LttoDatOvduFenqiExample getExample(LttoDatOvduFenqi record) {
		LttoDatOvduFenqiExample example = new LttoDatOvduFenqiExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getQiNo()!=null){
				criteria.andQiNoEqualTo(record.getQiNo());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getAllOverdueMoney()!=null){
				criteria.andAllOverdueMoneyEqualTo(record.getAllOverdueMoney());
				}
				if(record.getGradeNum()!=null){
				criteria.andGradeNumEqualTo(record.getGradeNum());
				}
				if(record.getOverdue1Num()!=null){
				criteria.andOverdue1NumEqualTo(record.getOverdue1Num());
				}
				if(record.getOverdue1Money()!=null){
				criteria.andOverdue1MoneyEqualTo(record.getOverdue1Money());
				}
				if(record.getOverdue2Num()!=null){
				criteria.andOverdue2NumEqualTo(record.getOverdue2Num());
				}
				if(record.getOverdue2Money()!=null){
				criteria.andOverdue2MoneyEqualTo(record.getOverdue2Money());
				}
				if(record.getOverdue3Num()!=null){
				criteria.andOverdue3NumEqualTo(record.getOverdue3Num());
				}
				if(record.getOverdue3Money()!=null){
				criteria.andOverdue3MoneyEqualTo(record.getOverdue3Money());
				}
				if(record.getOverdue4Num()!=null){
				criteria.andOverdue4NumEqualTo(record.getOverdue4Num());
				}
				if(record.getOverdue4Money()!=null){
				criteria.andOverdue4MoneyEqualTo(record.getOverdue4Money());
				}
				if(record.getOverdue5Num()!=null){
				criteria.andOverdue5NumEqualTo(record.getOverdue5Num());
				}
				if(record.getOverdue5Money()!=null){
				criteria.andOverdue5MoneyEqualTo(record.getOverdue5Money());
				}
				if(record.getOverdue6Num()!=null){
				criteria.andOverdue6NumEqualTo(record.getOverdue6Num());
				}
				if(record.getOverdue6Money()!=null){
				criteria.andOverdue6MoneyEqualTo(record.getOverdue6Money());
				}
				if(record.getOverdue7Num()!=null){
				criteria.andOverdue7NumEqualTo(record.getOverdue7Num());
				}
				if(record.getOverdue7Money()!=null){
				criteria.andOverdue7MoneyEqualTo(record.getOverdue7Money());
				}
				if(record.getOverdue8Num()!=null){
				criteria.andOverdue8NumEqualTo(record.getOverdue8Num());
				}
				if(record.getOverdue8Money()!=null){
				criteria.andOverdue8MoneyEqualTo(record.getOverdue8Money());
				}
				if(record.getOverdue9Num()!=null){
				criteria.andOverdue9NumEqualTo(record.getOverdue9Num());
				}
				if(record.getOverdue9Money()!=null){
				criteria.andOverdue9MoneyEqualTo(record.getOverdue9Money());
				}
				if(record.getOverdue10Num()!=null){
				criteria.andOverdue10NumEqualTo(record.getOverdue10Num());
				}
				if(record.getOverdue10Money()!=null){
				criteria.andOverdue10MoneyEqualTo(record.getOverdue10Money());
				}
				if(record.getSystemOperatorId()!=null){
				criteria.andSystemOperatorIdEqualTo(record.getSystemOperatorId());
				}
				if(record.getSystemOperateTime()!=null){
				criteria.andSystemOperateTimeEqualTo(record.getSystemOperateTime());
				}
				if(record.getFlag()!=null){
				criteria.andFlagEqualTo(record.getFlag());
				}

		}
		return example;
	}
}
