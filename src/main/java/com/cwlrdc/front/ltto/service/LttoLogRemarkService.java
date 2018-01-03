package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoLogRemark;
import com.cwlrdc.commondb.ltto.entity.LttoLogRemarkExample;
import com.cwlrdc.commondb.ltto.entity.LttoLogRemarkExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoLogRemarkKey;
import com.cwlrdc.commondb.ltto.mapper.LttoLogRemarkMapper;
import com.cwlrdc.front.common.FlowType;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
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
public class LttoLogRemarkService implements
    ServiceInterface<LttoLogRemark, LttoLogRemarkExample, LttoLogRemarkKey> {

  @Resource
  private LttoLogRemarkMapper mapper;
  private @Resource
  CommonSqlMapper common;


  @Override
  public int countByExample(LttoLogRemarkExample example) {
    return mapper.countByExample(example);
  }

  @Override
  public int deleteByExample(LttoLogRemarkExample example) {
    return mapper.deleteByExample(example);
  }

  @Override
  public int deleteByPrimaryKey(LttoLogRemarkKey key) {
    return mapper.deleteByPrimaryKey(key);
  }

  @Override
  public int insert(LttoLogRemark record) {
    return mapper.insert(record);
  }

  @Override
  public int insertSelective(LttoLogRemark record) {
    return mapper.insertSelective(record);
  }

  @Override
  @Transactional
  public int batchInsert(List<LttoLogRemark> records) {
    for (LttoLogRemark record : records) {
      mapper.insert(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchUpdate(List<LttoLogRemark> records) {
    for (LttoLogRemark record : records) {
      mapper.updateByPrimaryKeySelective(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchDelete(List<LttoLogRemark> records) {
    for (LttoLogRemark record : records) {
      mapper.deleteByPrimaryKey(record);
    }
    return records.size();
  }

  @Override
  public List<LttoLogRemark> selectByExample(LttoLogRemarkExample example) {
    return mapper.selectByExample(example);
  }

  @Override
  public LttoLogRemark selectByPrimaryKey(LttoLogRemarkKey key) {
    return mapper.selectByPrimaryKey(key);
  }

  @Override
  public List<LttoLogRemark> findAll(List<LttoLogRemark> records) {
    if (records == null || records.size() <= 0) {
      return mapper.selectByExample(new LttoLogRemarkExample());
    }
    List<LttoLogRemark> list = new ArrayList<>();
    for (LttoLogRemark record : records) {
      LttoLogRemark result = mapper.selectByPrimaryKey(record);
      if (result != null) {
        list.add(result);
      }
    }
    return list;
  }

  @Override
  public int updateByExampleSelective(LttoLogRemark record, LttoLogRemarkExample example) {
    return mapper.updateByExampleSelective(record, example);
  }

  @Override
  public int updateByExample(LttoLogRemark record, LttoLogRemarkExample example) {
    return mapper.updateByExample(record, example);
  }

  @Override
  public int updateByPrimaryKeySelective(LttoLogRemark record) {
    return mapper.updateByPrimaryKeySelective(record);
  }

  @Override
  public int updateByPrimaryKey(LttoLogRemark record) {
    return mapper.updateByPrimaryKey(record);
  }

  @Override
  public int sumByExample(LttoLogRemarkExample example) {
    return 0;
  }

  @Override
  public void deleteAll() {
    mapper.deleteByExample(new LttoLogRemarkExample());
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
    } catch (IllegalAccessException e) {
      log.error("异常", e);
    } catch (InvocationTargetException e) {
      log.error("异常", e);
    }
    return resultSet;
  }

  public List<HashMap<String, Object>> getPersonByProvinceAndPeriodAndGameCode(String provinceId,
      String periodNum, String gameCode) {
    String sql =
        "SELECT LOTTERY_PERSON lotteryPerson FROM T_LTTO_LOG_REMARK WHERE GAME_CODE='" + gameCode
            + "' AND PERIOD_NUM='" + periodNum + "' AND PROVINCE_ID='" + provinceId
            + "' GROUP BY GAME_CODE,PERIOD_NUM,PROVINCE_ID";
    List<HashMap<String, Object>> list = common.executeSql(sql);
    return list;
  }

  public List<HashMap<String, Object>> dosql(String sql) {
    List<HashMap<String, Object>> resultSet = common.executeSql(sql);
    return resultSet;
  }

  @Override
  public LttoLogRemarkExample getExample(LttoLogRemark record) {
    LttoLogRemarkExample example = new LttoLogRemarkExample();
    if (record != null) {
      Criteria criteria = example.createCriteria();
      if (record.getGameCode() != null) {
        criteria.andGameCodeEqualTo(record.getGameCode());
      }
      if (record.getPeriodNum() != null) {
        criteria.andPeriodNumEqualTo(record.getPeriodNum());
      }
      if (record.getOpetType() != null) {
        criteria.andOpetTypeEqualTo(record.getOpetType());
      }
      if (record.getOpetContent() != null) {
        criteria.andOpetContentEqualTo(record.getOpetContent());
      }
      if (record.getOtherOpet() != null) {
        criteria.andOtherOpetEqualTo(record.getOtherOpet());
      }
      if (record.getLotteryPerson() != null) {
        criteria.andLotteryPersonEqualTo(record.getLotteryPerson());
      }
      if (record.getLotteryTime() != null) {
        criteria.andLotteryTimeEqualTo(record.getLotteryTime());
      }

    }
    return example;
  }

  /**
   * 查询当期开奖人信息
   */
  public LttoLogRemark selectLogRemarkByKey(String gameCode, String periodNum, String provinceId) {
    LttoLogRemarkKey key = new LttoLogRemarkKey();
    key.setGameCode(gameCode);
    key.setOpetType(FlowType.SALES_DATA_SUM.getTypeNum());
    key.setPeriodNum(periodNum);
    key.setProvinceId(provinceId);
    return mapper.selectByPrimaryKey(key);
  }

}
