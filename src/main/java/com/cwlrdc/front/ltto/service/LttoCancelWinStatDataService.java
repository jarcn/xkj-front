package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatDataExample;
import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatDataExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatDataKey;
import com.cwlrdc.commondb.ltto.mapper.LttoCancelWinStatDataMapper;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.common.Status;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
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
public class LttoCancelWinStatDataService implements ServiceInterface<LttoCancelWinStatData, LttoCancelWinStatDataExample, LttoCancelWinStatDataKey>{

	@Resource
	private LttoCancelWinStatDataMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoCancelWinStatDataExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoCancelWinStatDataExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoCancelWinStatDataKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoCancelWinStatData record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoCancelWinStatData record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoCancelWinStatData> records)
			 {
		for(LttoCancelWinStatData record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoCancelWinStatData> records)
			 {
		for(LttoCancelWinStatData record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoCancelWinStatData> records)
			 {
		for(LttoCancelWinStatData record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoCancelWinStatData> selectByExample(LttoCancelWinStatDataExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoCancelWinStatData selectByPrimaryKey(LttoCancelWinStatDataKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoCancelWinStatData> findAll(List<LttoCancelWinStatData> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoCancelWinStatDataExample());
		}
		List<LttoCancelWinStatData> list = new ArrayList<>();
		for(LttoCancelWinStatData record : records){
			LttoCancelWinStatData result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoCancelWinStatData record, LttoCancelWinStatDataExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoCancelWinStatData record, LttoCancelWinStatDataExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoCancelWinStatData record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoCancelWinStatData record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoCancelWinStatDataExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoCancelWinStatDataExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.warn("警告",e);
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
	public LttoCancelWinStatDataExample getExample(LttoCancelWinStatData record) {
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
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
				if(record.getAllCanceledMoney()!=null){
				criteria.andAllCanceledMoneyEqualTo(record.getAllCanceledMoney());
				}
				if(record.getGradeCount()!=null){
				criteria.andGradeCountEqualTo(record.getGradeCount());
				}
				if(record.getCanceled1Count()!=null){
				criteria.andCanceled1CountEqualTo(record.getCanceled1Count());
				}
				if(record.getCanceled1Money()!=null){
				criteria.andCanceled1MoneyEqualTo(record.getCanceled1Money());
				}
				if(record.getCanceled2Count()!=null){
				criteria.andCanceled2CountEqualTo(record.getCanceled2Count());
				}
				if(record.getCanceled2Money()!=null){
				criteria.andCanceled2MoneyEqualTo(record.getCanceled2Money());
				}
				if(record.getCanceled3Count()!=null){
				criteria.andCanceled3CountEqualTo(record.getCanceled3Count());
				}
				if(record.getCanceled3Money()!=null){
				criteria.andCanceled3MoneyEqualTo(record.getCanceled3Money());
				}
				if(record.getCanceled4Count()!=null){
				criteria.andCanceled4CountEqualTo(record.getCanceled4Count());
				}
				if(record.getCanceled4Money()!=null){
				criteria.andCanceled4MoneyEqualTo(record.getCanceled4Money());
				}
				if(record.getCanceled5Count()!=null){
				criteria.andCanceled5CountEqualTo(record.getCanceled5Count());
				}
				if(record.getCanceled5Money()!=null){
				criteria.andCanceled5MoneyEqualTo(record.getCanceled5Money());
				}
				if(record.getCanceled6Count()!=null){
				criteria.andCanceled6CountEqualTo(record.getCanceled6Count());
				}
				if(record.getCanceled6Money()!=null){
				criteria.andCanceled6MoneyEqualTo(record.getCanceled6Money());
				}
				if(record.getCanceled7Count()!=null){
				criteria.andCanceled7CountEqualTo(record.getCanceled7Count());
				}
				if(record.getCanceled7Money()!=null){
				criteria.andCanceled7MoneyEqualTo(record.getCanceled7Money());
				}
				if(record.getCanceled8Count()!=null){
				criteria.andCanceled8CountEqualTo(record.getCanceled8Count());
				}
				if(record.getCanceled8Money()!=null){
				criteria.andCanceled8MoneyEqualTo(record.getCanceled8Money());
				}
				if(record.getCanceled9Count()!=null){
				criteria.andCanceled9CountEqualTo(record.getCanceled9Count());
				}
				if(record.getCanceled9Money()!=null){
				criteria.andCanceled9MoneyEqualTo(record.getCanceled9Money());
				}
				if(record.getCanceled10Count()!=null){
				criteria.andCanceled10CountEqualTo(record.getCanceled10Count());
				}
				if(record.getCanceled10Money()!=null){
				criteria.andCanceled10MoneyEqualTo(record.getCanceled10Money());
				}
				if(record.getUploadTime()!=null){
				criteria.andUploadTimeEqualTo(record.getUploadTime());
				}
				if(record.getDataStatus()!=null){
				criteria.andDataStatusEqualTo(record.getDataStatus());
				}
				if(record.getFilePath()!=null){
				criteria.andFilePathEqualTo(record.getFilePath());
				}

		}
		return example;
	}

	public List<LttoCancelWinStatData> selectByGameCodeAndPeriodNum(String gameCode,String periodNum){
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
		return mapper.selectByExample(example);
	}

	public List<LttoCancelWinStatData> selectByGameCodeAndPeriodNum(String gameCode,String periodNum,Integer status){
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
		.andDataStatusEqualTo(status);
		return mapper.selectByExample(example);
	}

	public LttoCancelWinStatData selectByKey(String gameCode,String periodNum,String provinceId)
	{
		LttoCancelWinStatDataKey example = new LttoCancelWinStatDataKey();
		example.setGameCode(gameCode);
		example.setPeriodNum(periodNum);
		example.setProvinceId(provinceId);
		return mapper.selectByPrimaryKey(example);
	}
	
	public List<LttoCancelWinStatData> selectDatas(String periodNum, String gameCode, List<String> provinceIds) {
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andProvinceIdIn(provinceIds);
        return mapper.selectByExample(example);
    }

	public List<LttoCancelWinStatData> selectExcelDatas(String periodNum, String gameCode, List<String> provinceIds) {
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		List<Integer> status = new ArrayList<>();
		status.add(Status.OvduDataStatus.UPLOADED_SUCCESS);
		status.add(Status.OvduDataStatus.NOFENQIOVDU);
		example.createCriteria().andGameCodeEqualTo(gameCode)
				.andDataStatusIn(status)
				.andPeriodNumEqualTo(periodNum)
				.andProvinceIdIn(provinceIds);
		return mapper.selectByExample(example);
	}

	public List<LttoCancelWinStatData> selectCancelWinDatas(String gameCode,String periodNum){
		List<Integer> status = new ArrayList<>();
		status.add(Status.OvduDataStatus.UPLOADED_SUCCESS);
		status.add(Status.OvduDataStatus.NOFENQIOVDU);
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
				.andDataStatusIn(status);
		return mapper.selectByExample(example);
	}

	public int selectUploadSuccessCount(String gameCode,String periodNum){
		List<Integer> status = new ArrayList<>();
		status.add(Status.OvduDataStatus.UPLOADED_SUCCESS);
		status.add(Status.OvduDataStatus.NOFENQIOVDU);
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
				.andDataStatusIn(status);
		return mapper.countByExample(example);
	}

	public int selectUploadFaildCount(String gameCode,String periodNum){
		LttoCancelWinStatDataExample example = new LttoCancelWinStatDataExample();
		example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
				.andDataStatusNotEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
		return mapper.countByExample(example);
	}

}
