package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoExample;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfoKey;
import com.cwlrdc.commondb.para.mapper.ParaGamePeriodInfoMapper;
import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.common.Status;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.common.ReturnInfo;
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
public class ParaGamePeriodInfoService implements ServiceInterface<ParaGamePeriodInfo, ParaGamePeriodInfoExample, ParaGamePeriodInfoKey> {

    @Resource
    private ParaGamePeriodInfoMapper mapper;
    private @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(ParaGamePeriodInfoExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(ParaGamePeriodInfoExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(ParaGamePeriodInfoKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(ParaGamePeriodInfo record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(ParaGamePeriodInfo record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<ParaGamePeriodInfo> records) {
        for (ParaGamePeriodInfo record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<ParaGamePeriodInfo> records) {
        for (ParaGamePeriodInfo record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<ParaGamePeriodInfo> records) {
        for (ParaGamePeriodInfo record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<ParaGamePeriodInfo> selectByExample(ParaGamePeriodInfoExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public ParaGamePeriodInfo selectByPrimaryKey(ParaGamePeriodInfoKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<ParaGamePeriodInfo> findAll(List<ParaGamePeriodInfo> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new ParaGamePeriodInfoExample());
        }
        List<ParaGamePeriodInfo> list = new ArrayList<>();
        for (ParaGamePeriodInfo record : records) {
            ParaGamePeriodInfo result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(ParaGamePeriodInfo record, ParaGamePeriodInfoExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(ParaGamePeriodInfo record, ParaGamePeriodInfoExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(ParaGamePeriodInfo record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ParaGamePeriodInfo record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(ParaGamePeriodInfoExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new ParaGamePeriodInfoExample());
    }


    public int getCount(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            resultSet = common.executeSql(SqlMaker.getCountSql(dc));
            return ((Number) resultSet.get(0).get("COUNT")).intValue();
        } catch (Exception e) {
            log.error("异常", e);
            return 0;
        }
    }

    public List<HashMap<String, Object>> getData(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            String sql = SqlMaker.getData(dc);
            resultSet = common.executeSql(sql);
            KeyExplainHandler.addId(resultSet, dc.getKeyClass(), dc.getEntityClass());//add key
        } catch (IllegalAccessException e) {
            log.error("异常", e);
        } catch (InvocationTargetException e) {
            log.error("异常", e);
        }
        return resultSet;
    }

    public List<HashMap<String, Object>> dosql(String sql) {
        List<HashMap<String, Object>> resultSet = common.executeSql(sql);
        return resultSet;
    }

    @Override
    public ParaGamePeriodInfoExample getExample(ParaGamePeriodInfo record) {
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getGameCode() != null) {
                criteria.andGameCodeEqualTo(record.getGameCode());
            }
            if (record.getPeriodNum() != null) {
                criteria.andPeriodNumEqualTo(record.getPeriodNum());
            }
            if (record.getPromotionStatus() != null) {
                criteria.andPromotionStatusEqualTo(record.getPromotionStatus());
            }
            if (record.getWinNum() != null) {
                criteria.andWinNumEqualTo(record.getWinNum());
            }
            if (record.getPeriodBeginTime() != null) {
                criteria.andPeriodBeginTimeEqualTo(record.getPeriodBeginTime());
            }
            if (record.getPeriodCycle() != null) {
                criteria.andPeriodCycleEqualTo(record.getPeriodCycle());
            }
            if (record.getPeriodEndTime() != null) {
                criteria.andPeriodEndTimeEqualTo(record.getPeriodEndTime());
            }
            if (record.getCashTerm() != null) {
                criteria.andCashTermEqualTo(record.getCashTerm());
            }
            if (record.getCashEndTime() != null) {
                criteria.andCashEndTimeEqualTo(record.getCashEndTime());
            }
            if (record.getLotteryDate() != null) {
                criteria.andLotteryDateEqualTo(record.getLotteryDate());
            }
            if (record.getOverduePeriod() != null) {
                criteria.andOverduePeriodEqualTo(record.getOverduePeriod());
            }
            if (record.getStatus() != null) {
                criteria.andStatusEqualTo(record.getStatus());
            }
            if (record.getFlowNode() != null) {
                criteria.andFlowNodeEqualTo(record.getFlowNode());
            }
            if (record.getCancelWinDate() != null) {
                criteria.andCancelWinDateEqualTo(record.getCancelWinDate());
            }

        }
        return example;
    }

    public List<ParaGamePeriodInfo> selectCurrentGame(String gameCode) {
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andStatusEqualTo(Status.Period.CURRENT)
                .andGameCodeEqualTo(gameCode);
        return  mapper.selectByExample(example);
    }


    public List<ParaGamePeriodInfo> select2current(){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andStatusEqualTo(Status.Period.CURRENT);
        return mapper.selectByExample(example);
    }

    public String nextPeriodNum(String gameCode,String currPeriodNum){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumGreaterThan(currPeriodNum);
        example.setOrderByClause("PERIOD_NUM");
        example.setLimit(1);
        List<ParaGamePeriodInfo> periodInfos = mapper.selectByExample(example);
        if(periodInfos==null || periodInfos.size() <=0 ){
            return null;
        }
        return periodInfos.get(0).getPeriodNum();
    }

    public ParaGamePeriodInfo nextPeriodInfo(String gameCode,String currPeriodNum){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumGreaterThan(currPeriodNum);
        example.setOrderByClause("PERIOD_NUM");
        example.setLimit(1);
        List<ParaGamePeriodInfo> periodInfos = mapper.selectByExample(example);
        if(periodInfos==null || periodInfos.size() <=0 ){
            return null;
        }
        return periodInfos.get(0);
    }

    public String prevPeriodNum(String gameCode,String currPeriodNum){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumLessThan(currPeriodNum);
        example.setOrderByClause("PERIOD_NUM desc");
        example.setLimit(1);
        List<ParaGamePeriodInfo> periodInfos = mapper.selectByExample(example);
        if(periodInfos==null || periodInfos.size() <=0 ){
            return null;
        }
        return periodInfos.get(0).getPeriodNum();
    }

    public ParaGamePeriodInfo prevPeriodInfo(String gameCode,String currPeriodNum){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumLessThan(currPeriodNum);
        example.setOrderByClause("PERIOD_NUM desc");
        example.setLimit(1);
        List<ParaGamePeriodInfo> periodInfos = mapper.selectByExample(example);
        if(periodInfos==null || periodInfos.size() <=0 ){
            return null;
        }
        return periodInfos.get(0);
    }

    public ParaGamePeriodInfo queryWinNum(String gameCode,String periodNum){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        List<ParaGamePeriodInfo> periodInfos = mapper.selectByExample(example);
        if(periodInfos==null || periodInfos.size() <=0 ){
            return null;
        }
        if(periodInfos.size()>1){
            throw new IllegalArgumentException("参数错误,数据包括相同期号["+periodNum+"] 游戏["+gameCode+"]");
        }
        return periodInfos.get(0);
    }

    public ParaGamePeriodInfo selectbyKey(String gameCode,String periodNum){
        ParaGamePeriodInfoKey infoKey = new ParaGamePeriodInfoKey();
        infoKey.setGameCode(gameCode);
        infoKey.setPeriodNum(periodNum);
        infoKey.setPeriodYear(Integer.valueOf(periodNum.substring(0,4)));
        return  mapper.selectByPrimaryKey(infoKey);
    }
	@Transactional
	public ReturnInfo updatePeriod(String openGameCode,String openPeriodNum,String closeGameCode,String closePeriodNum) {
		try{
			ParaGamePeriodInfo openInfo = new ParaGamePeriodInfo();
			String openYear=openPeriodNum.substring(0,4);
			openInfo.setGameCode(openGameCode);
			openInfo.setPeriodNum(openPeriodNum);
			openInfo.setPeriodYear(Integer.parseInt(openYear));
			openInfo.setFlowNode("1");
			this.updateByPrimaryKeySelective(openInfo);
			ParaGamePeriodInfo closeInfo = new ParaGamePeriodInfo();
			String closeYear=closePeriodNum.substring(0,4);
			closeInfo.setGameCode(closeGameCode);
			closeInfo.setPeriodNum(closePeriodNum);
			closeInfo.setPeriodYear(Integer.parseInt(closeYear));
			closeInfo.setFlowNode("0");
			this.updateByPrimaryKeySelective(closeInfo);
			return ReturnInfo.Success;
		}catch(Exception e){
		    log.error("程序异常",e);
			return ReturnInfo.Faild;
		}
	}


	public List<ParaGamePeriodInfo> selectBetweenPeriod(String gameCode,String sPeriod,String ePeriod){
        ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumBetween(sPeriod, ePeriod);
        return mapper.selectByExample(example);
    }
	
	public List<ParaGamePeriodInfo> selectByCashEndTimeAndYear(int year, String cashEndTime) {
		 ParaGamePeriodInfoExample example = new ParaGamePeriodInfoExample();
		 example.createCriteria().andPeriodYearEqualTo(year).andCashEndTimeGreaterThanOrEqualTo(cashEndTime).andStatusEqualTo(0);
		 return mapper.selectByExample(example);
	}
}
