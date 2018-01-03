package com.cwlrdc.front.opet.service;

import com.cwlrdc.commondb.opet.entity.OpetPrizeInfo;
import com.cwlrdc.commondb.opet.entity.OpetPrizeInfoExample;
import com.cwlrdc.commondb.opet.entity.OpetPrizeInfoExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetPrizeInfoKey;
import com.cwlrdc.commondb.opet.mapper.OpetPrizeInfoMapper;
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
public class OpetPrizeInfoService implements ServiceInterface<OpetPrizeInfo, OpetPrizeInfoExample, OpetPrizeInfoKey> {

	@Resource
	private OpetPrizeInfoMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(OpetPrizeInfoExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetPrizeInfoExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetPrizeInfoKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetPrizeInfo record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetPrizeInfo record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetPrizeInfo> records)
			 {
		for(OpetPrizeInfo record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetPrizeInfo> records)
			 {
		for(OpetPrizeInfo record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetPrizeInfo> records)
			 {
		for(OpetPrizeInfo record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetPrizeInfo> selectByExample(OpetPrizeInfoExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public OpetPrizeInfo selectByPrimaryKey(OpetPrizeInfoKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetPrizeInfo> findAll(List<OpetPrizeInfo> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetPrizeInfoExample());
		}
		List<OpetPrizeInfo> list = new ArrayList<>();
		for(OpetPrizeInfo record : records){
			OpetPrizeInfo result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetPrizeInfo record, OpetPrizeInfoExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetPrizeInfo record, OpetPrizeInfoExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetPrizeInfo record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetPrizeInfo record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetPrizeInfoExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetPrizeInfoExample());
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
	public OpetPrizeInfoExample getExample(OpetPrizeInfo record) {
		OpetPrizeInfoExample example = new OpetPrizeInfoExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getWinNum()!=null){
				criteria.andWinNumEqualTo(record.getWinNum());
				}
				if(record.getAllPrizeMoney()!=null){
				criteria.andAllPrizeMoneyEqualTo(record.getAllPrizeMoney());
				}
				if(record.getGradeNum()!=null){
				criteria.andGradeNumEqualTo(record.getGradeNum());
				}
				if(record.getPrize1Count()!=null){
				criteria.andPrize1CountEqualTo(record.getPrize1Count());
				}
				if(record.getPrize1Money()!=null){
				criteria.andPrize1MoneyEqualTo(record.getPrize1Money());
				}
				if(record.getPrize2Count()!=null){
				criteria.andPrize2CountEqualTo(record.getPrize2Count());
				}
				if(record.getPrize2Money()!=null){
				criteria.andPrize2MoneyEqualTo(record.getPrize2Money());
				}
				if(record.getPrize3Count()!=null){
				criteria.andPrize3CountEqualTo(record.getPrize3Count());
				}
				if(record.getPrize3Money()!=null){
				criteria.andPrize3MoneyEqualTo(record.getPrize3Money());
				}
				if(record.getPrize4Count()!=null){
				criteria.andPrize4CountEqualTo(record.getPrize4Count());
				}
				if(record.getPrize4Money()!=null){
				criteria.andPrize4MoneyEqualTo(record.getPrize4Money());
				}
				if(record.getPrize5Count()!=null){
				criteria.andPrize5CountEqualTo(record.getPrize5Count());
				}
				if(record.getPrize5Money()!=null){
				criteria.andPrize5MoneyEqualTo(record.getPrize5Money());
				}
				if(record.getPrize6Count()!=null){
				criteria.andPrize6CountEqualTo(record.getPrize6Count());
				}
				if(record.getPrize6Money()!=null){
				criteria.andPrize6MoneyEqualTo(record.getPrize6Money());
				}
				if(record.getPrize7Count()!=null){
				criteria.andPrize7CountEqualTo(record.getPrize7Count());
				}
				if(record.getPrize7Money()!=null){
				criteria.andPrize7MoneyEqualTo(record.getPrize7Money());
				}
				if(record.getPrize8Count()!=null){
				criteria.andPrize8CountEqualTo(record.getPrize8Count());
				}
				if(record.getPrize8Money()!=null){
				criteria.andPrize8MoneyEqualTo(record.getPrize8Money());
				}
				if(record.getPrize9Count()!=null){
				criteria.andPrize9CountEqualTo(record.getPrize9Count());
				}
				if(record.getPrize9Money()!=null){
				criteria.andPrize9MoneyEqualTo(record.getPrize9Money());
				}
				if(record.getPrize10Count()!=null){
				criteria.andPrize10CountEqualTo(record.getPrize10Count());
				}
				if(record.getPrize10Money()!=null){
				criteria.andPrize10MoneyEqualTo(record.getPrize10Money());
				}
				if(record.getSystemOperatorId()!=null){
				criteria.andSystemOperatorIdEqualTo(record.getSystemOperatorId());
				}
				if(record.getSystemOperateTime()!=null){
				criteria.andSystemOperateTimeEqualTo(record.getSystemOperateTime());
				}
				if(record.getUploadStatus()!=null){
				criteria.andUploadStatusEqualTo(record.getUploadStatus());
				}
				if(record.getUploadData()!=null){
				criteria.andUploadDataEqualTo(record.getUploadData());
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
