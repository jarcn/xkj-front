package com.cwlrdc.front.stat.service;

import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoKey;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoExample;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocation;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationExample;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationExample.Criteria;
import com.cwlrdc.commondb.stat.entity.StatBonusAllocationKey;
import com.cwlrdc.commondb.stat.mapper.StatBonusAllocationMapper;
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
public class StatBonusAllocationService implements ServiceInterface<StatBonusAllocation, StatBonusAllocationExample, StatBonusAllocationKey> {

    @Resource
    private StatBonusAllocationMapper mapper;
    private
    @Resource
    CommonSqlMapper common;
    @Resource
    private ParaGamePeriodInfoService service;
    @Resource 
    private ParaProvinceInfoService provinceService;


    @Override
    public int countByExample(StatBonusAllocationExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(StatBonusAllocationExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(StatBonusAllocationKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(StatBonusAllocation record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(StatBonusAllocation record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<StatBonusAllocation> records) {
        for (StatBonusAllocation record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<StatBonusAllocation> records) {
        for (StatBonusAllocation record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<StatBonusAllocation> records) {
        for (StatBonusAllocation record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<StatBonusAllocation> selectByExample(StatBonusAllocationExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public StatBonusAllocation selectByPrimaryKey(StatBonusAllocationKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<StatBonusAllocation> findAll(List<StatBonusAllocation> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new StatBonusAllocationExample());
        }
        List<StatBonusAllocation> list = new ArrayList<>();
        for (StatBonusAllocation record : records) {
            StatBonusAllocation result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(StatBonusAllocation record, StatBonusAllocationExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(StatBonusAllocation record, StatBonusAllocationExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(StatBonusAllocation record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(StatBonusAllocation record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(StatBonusAllocationExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new StatBonusAllocationExample());
    }
    public Map<String,String> getAwardAdjustBaseInfo(String gameCode,String periodNum,String periodYear){
    	Map<String, String> periodInfo=new HashMap<>();
    	StringBuilder sql=new StringBuilder();
    	sql.append(" SELECT MIN(DEALGAMEINFO.PERIOD_NUM) minPeriodNum,MAX(DEALGAMEINFO.PERIOD_NUM) maxPeriodNum FROM  ");
    	sql.append(" (SELECT T_PARA_GAME_PERIOD_INFO.`PERIOD_NUM`,T_PARA_GAME_PERIOD_INFO.`GAME_CODE` ");
    	sql.append(" FROM T_PARA_GAME_PERIOD_INFO  ");
    	sql.append(" JOIN ");
    	sql.append(" (SELECT   GAME_CODE, PERIOD_WEEK,  PERIOD_YEAR FROM T_PARA_GAME_PERIOD_INFO WHERE PERIOD_NUM = '"+periodNum+"'  AND GAME_CODE = '"+gameCode+"'   AND PERIOD_YEAR = '"+periodYear+"') GAME_INFO ");
    	sql.append(" ON T_PARA_GAME_PERIOD_INFO.PERIOD_WEEK=GAME_INFO.PERIOD_WEEK AND T_PARA_GAME_PERIOD_INFO.PERIOD_YEAR=GAME_INFO.PERIOD_YEAR AND T_PARA_GAME_PERIOD_INFO.GAME_CODE=GAME_INFO.GAME_CODE) DEALGAMEINFO JOIN  ");
    	sql.append(" (SELECT GAME_CODE,PERIOD_NUM FROM T_STAT_BONUS_ALLOCATION_WEEK GROUP BY GAME_CODE,PERIOD_NUM ) BONUS_ALLOCATION ON DEALGAMEINFO.GAME_CODE=BONUS_ALLOCATION.`GAME_CODE`AND DEALGAMEINFO.PERIOD_NUM=BONUS_ALLOCATION.PERIOD_NUM ");
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


    public List<HashMap<String,Object>> getAwardAdjust(String gameCode,String periodNum,String periodYear){
    	StringBuilder sql=new StringBuilder();
    	sql.append(" SELECT T_STAT_BONUS_ALLOCATION_WEEK.PROVINCE_ID provinceId,PROVINCE_NAME provinceName,SUM(SALE_MONEY) saleMoney,SUM(REWARD_BONUS_MONEY) bonusMoney,SUM(REGULATION_FUND_MONEY) regulationFundMoney,SUM(TOTAL_MONEY) totalMoney,SUM(WIN_BONUS_MONEY) winBonusMoney,SUM(ALLOCATION_BONUS_MONEY) allocationBonusMoney,SUM(REALITY_PAY_CASH) realityPayCash FROM ( ");
    	sql.append(" SELECT  T_PARA_GAME_PERIOD_INFO.`GAME_CODE`, T_PARA_GAME_PERIOD_INFO.`PERIOD_NUM` FROM T_PARA_GAME_PERIOD_INFO  ");
    	sql.append(" JOIN ");
    	sql.append("  (SELECT GAME_CODE, PERIOD_WEEK, PERIOD_YEAR FROM T_PARA_GAME_PERIOD_INFO WHERE PERIOD_NUM = '"+periodNum+"'  AND GAME_CODE = '"+gameCode+"'  AND PERIOD_YEAR = '"+periodYear+"') GAME_INFO ");
    	sql.append(" ON T_PARA_GAME_PERIOD_INFO.PERIOD_WEEK=GAME_INFO.PERIOD_WEEK AND T_PARA_GAME_PERIOD_INFO.PERIOD_YEAR=GAME_INFO.PERIOD_YEAR AND T_PARA_GAME_PERIOD_INFO.GAME_CODE=GAME_INFO.GAME_CODE) DEALGAMEINFO  ");
    	sql.append(" JOIN T_STAT_BONUS_ALLOCATION_WEEK ON DEALGAMEINFO.GAME_CODE=T_STAT_BONUS_ALLOCATION_WEEK.`GAME_CODE` AND DEALGAMEINFO.PERIOD_NUM=T_STAT_BONUS_ALLOCATION_WEEK.`PERIOD_NUM` JOIN T_PARA_PROVINCE_INFO ON T_PARA_PROVINCE_INFO.`PROVINCE_ID`=T_STAT_BONUS_ALLOCATION_WEEK.`PROVINCE_ID` GROUP BY provinceId,provinceName ");
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
    		item.put("saleMoney","-");
    		item.put("bonusMoney","-");
    		item.put("regulationFundMoney","-");
    		item.put("totalMoney","-");
    		item.put("winBonusMoney","-");
    		item.put("allocationBonusMoney","-");
    		item.put("realityPayCash","-");
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
    public int getCount(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            resultSet = common.executeSql(SqlMaker.getCountSql(dc));
            return ((Number) resultSet.get(0).get("COUNT")).intValue();
        } catch (Exception e) {
            log.warn("异常",e);
            return 0;
        }
    }

    public List<HashMap<String, Object>> getData(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            String sql = SqlMaker.getData(dc);
            resultSet = common.executeSql(sql);
        } catch (IllegalAccessException e) {
            log.warn("异常",e);
        } catch (InvocationTargetException e) {
            log.warn("异常",e);
        }
        return resultSet;
    }

    public List<HashMap<String, Object>> dosql(String sql) {
        List<HashMap<String, Object>> resultSet = common.executeSql(sql);
        return resultSet;
    }

    @Override
    public StatBonusAllocationExample getExample(StatBonusAllocation record) {
        StatBonusAllocationExample example = new StatBonusAllocationExample();
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
            if (record.getSaleMoney() != null) {
                criteria.andSaleMoneyEqualTo(record.getSaleMoney());
            }
            if (record.getRewardBonusMoney() != null) {
                criteria.andRewardBonusMoneyEqualTo(record.getRewardBonusMoney());
            }
            if (record.getRegulationFundMoney() != null) {
                criteria.andRegulationFundMoneyEqualTo(record.getRegulationFundMoney());
            }
            if (record.getTotalMoney() != null) {
                criteria.andTotalMoneyEqualTo(record.getTotalMoney());
            }
            if (record.getWinBonusMoney() != null) {
                criteria.andWinBonusMoneyEqualTo(record.getWinBonusMoney());
            }
            if (record.getAllocationBonusMoney() != null) {
                criteria.andAllocationBonusMoneyEqualTo(record.getAllocationBonusMoney());
            }
            if (record.getRealityPayCash() != null) {
                criteria.andRealityPayCashEqualTo(record.getRealityPayCash());
            }

        }
        return example;
    }
}
