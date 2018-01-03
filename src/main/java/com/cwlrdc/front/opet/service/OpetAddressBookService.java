package com.cwlrdc.front.opet.service;

import com.cwlrdc.commondb.opet.entity.OpetAddressBook;
import com.cwlrdc.commondb.opet.entity.OpetAddressBookExample;
import com.cwlrdc.commondb.opet.entity.OpetAddressBookExample.Criteria;
import com.cwlrdc.commondb.opet.entity.OpetAddressBookKey;
import com.cwlrdc.commondb.opet.mapper.OpetAddressBookMapper;
import com.cwlrdc.front.common.ServiceInterface;
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
public class OpetAddressBookService implements ServiceInterface<OpetAddressBook, OpetAddressBookExample, OpetAddressBookKey> {

	@Resource
	private OpetAddressBookMapper mapper;
	private @Resource CommonSqlMapper common;


	@Override
	public int countByExample(OpetAddressBookExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(OpetAddressBookExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(OpetAddressBookKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(OpetAddressBook record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(OpetAddressBook record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<OpetAddressBook> records)
	{
		for(OpetAddressBook record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<OpetAddressBook> records)
	{
		for(OpetAddressBook record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<OpetAddressBook> records)
	{
		for(OpetAddressBook record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<OpetAddressBook> selectByExample(OpetAddressBookExample example)
	{
		return mapper.selectByExample(example);
	}

	@Override
	public OpetAddressBook selectByPrimaryKey(OpetAddressBookKey key)
	{
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<OpetAddressBook> findAll(List<OpetAddressBook> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new OpetAddressBookExample());
		}
		List<OpetAddressBook> list = new ArrayList<>();
		for(OpetAddressBook record : records){
			OpetAddressBook result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(OpetAddressBook record, OpetAddressBookExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(OpetAddressBook record, OpetAddressBookExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(OpetAddressBook record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OpetAddressBook record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(OpetAddressBookExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new OpetAddressBookExample());
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

	public List<HashMap<String,Object>> getDataOrder(Integer skip,Integer limit,DbCondi dc,String infoCondition){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT LOGIN_NAME loginName,REMARK remark,FACSI_MILE facsiMile,SORT sort,DRAW_NAME drawName,PROVINCE_ID provinceId,UUID uuid,TELE_PHONE telePhone,STATUS status FROM T_OPET_ADDRESS_BOOK"+" "+ infoCondition +"ORDER BY PROVINCEID-0, SORT-0 LIMIT"+" "+skip+","+limit;
		resultSet = common.executeSql(sql);
		KeyExplainHandler.addId(resultSet, dc.getKeyClass(),dc.getEntityClass());//add key
		return resultSet;
	}

	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public OpetAddressBookExample getExample(OpetAddressBook record) {
		OpetAddressBookExample example = new OpetAddressBookExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
			if(record.getUuid()!=null){
				criteria.andUuidEqualTo(record.getUuid());
			}
			if(record.getDrawName()!=null){
				criteria.andDrawNameEqualTo(record.getDrawName());
			}
			if(record.getProvinceId()!=null){
				criteria.andProvinceIdEqualTo(record.getProvinceId());
			}
			if(record.getTelePhone()!=null){
				criteria.andTelePhoneEqualTo(record.getTelePhone());
			}
			if(record.getFacsiMile()!=null){
				criteria.andFacsiMileEqualTo(record.getFacsiMile());
			}
			if(record.getStatus()!=null){
				criteria.andStatusEqualTo(record.getStatus());
			}
			if(record.getRemark()!=null){
				criteria.andRemarkEqualTo(record.getRemark());
			}
			if(record.getSort()!=null){
				criteria.andSortEqualTo(record.getSort());
			}
			if(record.getLoginName()!=null){
				criteria.andLoginNameEqualTo(record.getLoginName());
			}

		}
		return example;
	}


	/**
	 * 查询开奖人具体信息
	 * @param name
	 * @param provinceId
	 * @return
	 */
	private static final int OPEN_LOTTERY = 0;
	public List<OpetAddressBook> queryPersonInfosByKey(String name, String provinceId){
		OpetAddressBookExample example = new OpetAddressBookExample();
		example.createCriteria().andDrawNameEqualTo(name).andProvinceIdEqualTo(provinceId)
		.andStatusEqualTo(OPEN_LOTTERY);
		return mapper.selectByExample(example);
	}

}
