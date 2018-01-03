package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureBak;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureBakExample;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureBakExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureBakKey;
import com.cwlrdc.commondb.ltto.mapper.LttoFaxPictureBakMapper;
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
public class LttoFaxPictureBakService implements ServiceInterface<LttoFaxPictureBak, LttoFaxPictureBakExample, LttoFaxPictureBakKey> {

	@Resource
	private LttoFaxPictureBakMapper mapper;
	private @Resource CommonSqlMapper common;
	
	
	@Override
	public int countByExample(LttoFaxPictureBakExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoFaxPictureBakExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoFaxPictureBakKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoFaxPictureBak record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoFaxPictureBak record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoFaxPictureBak> records)
			 {
		for(LttoFaxPictureBak record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoFaxPictureBak> records)
			 {
		for(LttoFaxPictureBak record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoFaxPictureBak> records)
			 {
		for(LttoFaxPictureBak record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoFaxPictureBak> selectByExample(LttoFaxPictureBakExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoFaxPictureBak selectByPrimaryKey(LttoFaxPictureBakKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoFaxPictureBak> findAll(List<LttoFaxPictureBak> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoFaxPictureBakExample());
		}
		List<LttoFaxPictureBak> list = new ArrayList<>();
		for(LttoFaxPictureBak record : records){
			LttoFaxPictureBak result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoFaxPictureBak record, LttoFaxPictureBakExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoFaxPictureBak record, LttoFaxPictureBakExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoFaxPictureBak record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoFaxPictureBak record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoFaxPictureBakExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoFaxPictureBakExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.trace("查询发生异常",e);
			return 0;
		}
	}
	
	public List<HashMap<String,Object>> getData(DbCondi dc){
		 List<HashMap<String, Object>> resultSet = null;
                try {
                    String sql = SqlMaker.getData(dc);
                    resultSet = common.executeSql(sql);
                } catch (IllegalAccessException |InvocationTargetException e) {
					log.trace("查询发生异常",e);
                }
                return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public LttoFaxPictureBakExample getExample(LttoFaxPictureBak record) {
		LttoFaxPictureBakExample example = new LttoFaxPictureBakExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getUuid()!=null){
				criteria.andUuidEqualTo(record.getUuid());
				}
				if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
				}
				if(record.getPicturePath()!=null){
				criteria.andPicturePathEqualTo(record.getPicturePath());
				}
				if(record.getPictureName()!=null){
				criteria.andPictureNameEqualTo(record.getPictureName());
				}
				if(record.getStatus()!=null){
				criteria.andStatusEqualTo(record.getStatus());
				}
				if(record.getUploadTime()!=null){
				criteria.andUploadTimeEqualTo(record.getUploadTime());
				}
				if(record.getPictureSize()!=null){
				criteria.andPictureSizeEqualTo(record.getPictureSize());
				}
				if(record.getPictureType()!=null){
				criteria.andPictureTypeEqualTo(record.getPictureType());
				}

		}
		return example;
	}
}
