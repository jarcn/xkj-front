package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrieval;
import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrievalExample;
import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrievalExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoWinningRetrievalKey;
import com.cwlrdc.commondb.ltto.mapper.LttoWinningRetrievalMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
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
public class LttoWinningRetrievalService implements ServiceInterface<LttoWinningRetrieval, LttoWinningRetrievalExample, LttoWinningRetrievalKey> {

	@Resource
	private LttoWinningRetrievalMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoWinningRetrievalExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoWinningRetrievalExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoWinningRetrievalKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoWinningRetrieval record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoWinningRetrieval record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoWinningRetrieval> records)
			 {
		for(LttoWinningRetrieval record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoWinningRetrieval> records)
			 {
		for(LttoWinningRetrieval record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoWinningRetrieval> records)
			 {
		for(LttoWinningRetrieval record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoWinningRetrieval> selectByExample(LttoWinningRetrievalExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoWinningRetrieval selectByPrimaryKey(LttoWinningRetrievalKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoWinningRetrieval> findAll(List<LttoWinningRetrieval> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoWinningRetrievalExample());
		}
		List<LttoWinningRetrieval> list = new ArrayList<>();
		for(LttoWinningRetrieval record : records){
			LttoWinningRetrieval result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoWinningRetrieval record, LttoWinningRetrievalExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoWinningRetrieval record, LttoWinningRetrievalExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoWinningRetrieval record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoWinningRetrieval record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoWinningRetrievalExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoWinningRetrievalExample());
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
	public LttoWinningRetrievalExample getExample(LttoWinningRetrieval record) {
		LttoWinningRetrievalExample example = new LttoWinningRetrievalExample();
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
				if(record.getTicketType()!=null){
				criteria.andTicketTypeEqualTo(record.getTicketType());
				}
				if(record.getWinDetail()!=null){
				criteria.andWinDetailEqualTo(record.getWinDetail());
				}
				if(record.getAllPrizeMoney()!=null){
				criteria.andAllPrizeMoneyEqualTo(record.getAllPrizeMoney());
				}
				if(record.getWinLevel()!=null){
				criteria.andWinLevelEqualTo(record.getWinLevel());
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
				if(record.getProcessStatus()!=null){
				criteria.andProcessStatusEqualTo(record.getProcessStatus());
				}

		}
		return example;
	}


	public List<LttoWinningRetrieval> selectbyKey(String gameCode,String provinceId,String periodNum){
		LttoWinningRetrievalExample example = new LttoWinningRetrievalExample();
		example.createCriteria().andGameCodeEqualTo(gameCode)
				.andProvinceIdEqualTo(provinceId)
				.andPeriodNumEqualTo(periodNum);
		return mapper.selectByExample(example);
	}


	public List<LttoWinningRetrieval> selec2winDatas(String gameCode,String periodNum){
		LttoWinningRetrievalExample winningExample = new LttoWinningRetrievalExample();
		winningExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
		return  mapper.selectByExample(winningExample);
	}


	public List<LttoWinningRetrieval> selec2SmRetrivalRsuts(String gameCode,String periodNum,int type){
		LttoWinningRetrievalExample winningExample = new LttoWinningRetrievalExample();
		winningExample.createCriteria().andGameCodeEqualTo(gameCode)
				.andPeriodNumEqualTo(periodNum)
				.andTicketTypeEqualTo(type);
		return mapper.selectByExample(winningExample);
	}

	public List<LttoWinningRetrieval> selec2datas(String gameCode,String periodNum,List<Integer> tickeTypes,List<String> provinces){
		LttoWinningRetrievalExample effectExample = new LttoWinningRetrievalExample();
		effectExample.createCriteria().andGameCodeEqualTo(gameCode)
				.andPeriodNumEqualTo(periodNum)
				.andTicketTypeIn(tickeTypes)
				.andProvinceIdIn(provinces);
		return mapper.selectByExample(effectExample);
	}

}
