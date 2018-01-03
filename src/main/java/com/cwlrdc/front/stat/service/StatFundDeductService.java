package com.cwlrdc.front.stat.service;

import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoKey;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoExample;
import com.cwlrdc.commondb.stat.entity.StatFundDeduct;
import com.cwlrdc.commondb.stat.entity.StatFundDeductExample;
import com.cwlrdc.commondb.stat.entity.StatFundDeductExample.Criteria;
import com.cwlrdc.commondb.stat.entity.StatFundDeductKey;
import com.cwlrdc.commondb.stat.mapper.StatFundDeductMapper;
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
import java.util.Map;
import java.util.Map.Entry;
@Slf4j
@Service
public class StatFundDeductService implements ServiceInterface<StatFundDeduct, StatFundDeductExample, StatFundDeductKey> {

    @Resource
    private StatFundDeductMapper mapper;
    private
    @Resource
    CommonSqlMapper common;
    @Resource
    private ParaGamePeriodInfoService service;
    @Resource 
    private ParaProvinceInfoService provinceService;


    @Override
    public int countByExample(StatFundDeductExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(StatFundDeductExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(StatFundDeductKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(StatFundDeduct record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(StatFundDeduct record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<StatFundDeduct> records) {
        for (StatFundDeduct record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<StatFundDeduct> records) {
        for (StatFundDeduct record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<StatFundDeduct> records) {
        for (StatFundDeduct record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<StatFundDeduct> selectByExample(StatFundDeductExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public StatFundDeduct selectByPrimaryKey(StatFundDeductKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<StatFundDeduct> findAll(List<StatFundDeduct> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new StatFundDeductExample());
        }
        List<StatFundDeduct> list = new ArrayList<>();
        for (StatFundDeduct record : records) {
            StatFundDeduct result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(StatFundDeduct record, StatFundDeductExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(StatFundDeduct record, StatFundDeductExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(StatFundDeduct record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(StatFundDeduct record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(StatFundDeductExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new StatFundDeductExample());
    }


    public int getCount(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            resultSet = common.executeSql(SqlMaker.getCountSql(dc));
            return ((Number) resultSet.get(0).get("COUNT")).intValue();
        } catch (Exception e) {
            log.error("异常",e);
            return 0;
        }
    }

    public List<HashMap<String, Object>> getData(DbCondi dc) {
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

    public List<HashMap<String, Object>> dosql(String sql) {
        List<HashMap<String, Object>> resultSet = common.executeSql(sql);
        return resultSet;
    }

    @Override
    public StatFundDeductExample getExample(StatFundDeduct record) {
        StatFundDeductExample example = new StatFundDeductExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getProvinceId() != null) {
                criteria.andProvinceIdEqualTo(record.getProvinceId());
            }
            if (record.getGameCode() != null) {
                criteria.andGameCodeEqualTo(record.getGameCode());
            }
            if (record.getPeriodNum() != null) {
                criteria.andPeriodNumEqualTo(record.getPeriodNum());
            }
            if (record.getType() != null) {
                criteria.andTypeEqualTo(record.getType());
            }
            if (record.getRealityPayCash() != null) {
                criteria.andRealityPayCashEqualTo(record.getRealityPayCash());
            }

        }
        return example;
    }
    public List<HashMap<String,Object>> fundDeduct(String gameCode,String periodNum,String periodYear,String type){
        StringBuilder sql=new StringBuilder();
    	sql.append(" SELECT T_STAT_FUND_DEDUCT.PROVINCE_ID provinceId,PROVINCE_NAME provinceName,SUM(REALITY_PAY_CASH) cash FROM ( ");
    	sql.append(" SELECT  T_PARA_GAME_PERIOD_INFO.`GAME_CODE`, T_PARA_GAME_PERIOD_INFO.`PERIOD_NUM` FROM T_PARA_GAME_PERIOD_INFO  ");
    	sql.append(" JOIN  (SELECT  GAME_CODE, PERIOD_WEEK, PERIOD_YEAR FROM T_PARA_GAME_PERIOD_INFO ");
    	sql.append(" WHERE PERIOD_NUM = '"+periodNum+"' AND GAME_CODE = '"+gameCode+"'  AND PERIOD_YEAR = '"+periodYear+"') GAME_INFO ");
    	sql.append(" ON T_PARA_GAME_PERIOD_INFO.PERIOD_WEEK=GAME_INFO.PERIOD_WEEK AND T_PARA_GAME_PERIOD_INFO.PERIOD_YEAR=GAME_INFO.PERIOD_YEAR AND T_PARA_GAME_PERIOD_INFO.GAME_CODE=GAME_INFO.GAME_CODE ) DEALGAMEINFO    JOIN ");
    	sql.append(" T_STAT_FUND_DEDUCT ON T_STAT_FUND_DEDUCT.`TYPE`='"+type+"' AND DEALGAMEINFO.GAME_CODE=T_STAT_FUND_DEDUCT.`GAME_CODE` AND DEALGAMEINFO.PERIOD_NUM=T_STAT_FUND_DEDUCT.`PERIOD_NUM` JOIN T_PARA_PROVINCE_INFO ON T_PARA_PROVINCE_INFO.`PROVINCE_ID`=T_STAT_FUND_DEDUCT.`PROVINCE_ID` GROUP BY provinceId,provinceName ");
    	List<HashMap<String,Object>> list=common.executeSql(sql.toString());
    	List<String> provinceIds=new ArrayList<String>();
    	if(list.size()>0){
    		for(HashMap<String,Object> map:list){
    			provinceIds.add(map.get("provinceId").toString());
    		}
    	}
    	List<String> noProvinces=new ArrayList<String>();
    	Map<String, String> map=this.getProvinceMap();
    	
    	if(provinceIds.size()>0){
    		for(Entry<String, String> entry:map.entrySet()){
    			String key=entry.getKey();
    			String value=entry.getValue();
    			if(!provinceIds.contains(key)){
    				noProvinces.add(key);
    			}
    		}
    	}else{
    		for(Entry<String, String> entry:map.entrySet()){
    			String key=entry.getKey();
    			String value=entry.getValue();
    			noProvinces.add(key);
    		}
    	}
    	for(String provinceId:noProvinces){
    		HashMap<String, Object> item=new HashMap<String, Object>();
    		item.put("provinceName", map.get(provinceId));
    		item.put("cash","-");
    		list.add(item);
    	}
    	return list;
    }
    public Map<String, String> getProvinceMap(){
    	ParaProvinceInfoExample example=new ParaProvinceInfoExample();
    	example.createCriteria().andProvinceIdNotEqualTo("00");
    	List<ParaProvinceInfo> province=provinceService.selectByExample(example);
    	Map<String, String> map=new HashMap<String, String>();
    	for(ParaProvinceInfo info:province){
    		map.put(info.getProvinceId(), info.getProvinceName());
    	}
    	return map;
    }
    public Map<String, String> fundDeductBaseInfo(String gameCode,String periodNum,String periodYear,String type){
    	Map<String, String> periodInfo=new HashMap<String, String>();
        StringBuilder sql=new StringBuilder();
    	sql.append(" SELECT MIN(PERIOD_NUM) minPeriodNum,MAX(PERIOD_NUM) maxPeriodNum FROM  ");
    	sql.append(" (SELECT T_PARA_GAME_PERIOD_INFO.`PERIOD_NUM`,T_PARA_GAME_PERIOD_INFO.`GAME_CODE` ");
    	sql.append(" FROM T_PARA_GAME_PERIOD_INFO  ");
    	sql.append(" JOIN ");
    	sql.append(" (SELECT   GAME_CODE, PERIOD_WEEK,  PERIOD_YEAR FROM T_PARA_GAME_PERIOD_INFO WHERE PERIOD_NUM = '"+periodNum+"'  AND GAME_CODE = '"+gameCode+"'   AND PERIOD_YEAR = '"+periodYear+"') GAME_INFO ");
    	sql.append(" ON T_PARA_GAME_PERIOD_INFO.PERIOD_WEEK=GAME_INFO.PERIOD_WEEK AND T_PARA_GAME_PERIOD_INFO.PERIOD_YEAR=GAME_INFO.PERIOD_YEAR AND T_PARA_GAME_PERIOD_INFO.GAME_CODE=GAME_INFO.GAME_CODE) DEALGAMEINFO JOIN  ");
    	sql.append(" (SELECT GAME_CODE FROM T_STAT_FUND_DEDUCT WHERE T_STAT_FUND_DEDUCT.`TYPE`='"+type+"'  GROUP BY GAME_CODE ) FUND_DEDUCT ON DEALGAMEINFO.GAME_CODE=FUND_DEDUCT.`GAME_CODE` ");
    	List<HashMap<String,Object>> list=common.executeSql(sql.toString());
    	if(list.size()>0){
    		HashMap<String,Object> map=list.get(0);
    		if(null!=map){
    		if(map.containsKey("minPeriodNum")&&map.containsKey("maxPeriodNum")){
        		String minPeriodNum=map.get("minPeriodNum").toString();
        		String maxPeriodNum=map.get("maxPeriodNum").toString();
        		ParaGamePeriodInfoKey min=new ParaGamePeriodInfoKey();
        		min.setGameCode(gameCode);
        		min.setPeriodNum(minPeriodNum);
        		min.setPeriodYear(Integer.parseInt(periodYear));
        		ParaGamePeriodInfo minPeriodInfo=service.selectByPrimaryKey(min);
        		ParaGamePeriodInfoKey max=new ParaGamePeriodInfoKey();
        		max.setGameCode(gameCode);
        		max.setPeriodNum(maxPeriodNum);
        		max.setPeriodYear(Integer.parseInt(periodYear));
        		ParaGamePeriodInfo maxPeriodInfo=service.selectByPrimaryKey(max);
        		String periodStr="";
        		if(minPeriodNum.equals(maxPeriodNum)){
        			periodStr=minPeriodNum+"~"+minPeriodNum;
        		}else{
        			periodStr=minPeriodNum+"~"+maxPeriodNum;
        		}
        		String timeStr="";
        		if(minPeriodInfo.getPeriodBeginTime().equals(maxPeriodInfo.getPeriodEndTime())){
        			timeStr=minPeriodInfo.getPeriodBeginTime();
        		}else{
        			timeStr=minPeriodInfo.getPeriodBeginTime()+" 20:00:00 ~ "+maxPeriodInfo.getPeriodEndTime()+" 19:00:00";
        		}
        		periodInfo.put("periodNums", periodStr);
        		periodInfo.put("saleTime", timeStr);
    		}else{
    			periodInfo.put("periodNums",periodNum);
        		periodInfo.put("saleTime", "");
    		}
    		}else{
    			periodInfo.put("periodNums",periodNum);
        		periodInfo.put("saleTime", "");
    		}
    	}else{
    		periodInfo.put("periodNums",periodNum);
    		periodInfo.put("saleTime", "");
    	}
    	return periodInfo;
    }
}
