package com.cwlrdc.front.stat.service;

import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.commondb.stat.entity.StatWinAllocation;
import com.cwlrdc.commondb.stat.entity.StatWinAllocationExample;
import com.cwlrdc.commondb.stat.entity.StatWinAllocationExample.Criteria;
import com.cwlrdc.commondb.stat.entity.StatWinAllocationKey;
import com.cwlrdc.commondb.stat.mapper.StatWinAllocationMapper;
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

@Service
@Slf4j
public class StatWinAllocationService implements ServiceInterface<StatWinAllocation, StatWinAllocationExample, StatWinAllocationKey> {

    @Resource
    private StatWinAllocationMapper mapper;
    private
    @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(StatWinAllocationExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(StatWinAllocationExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(StatWinAllocationKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(StatWinAllocation record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(StatWinAllocation record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<StatWinAllocation> records) {
        for (StatWinAllocation record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<StatWinAllocation> records) {
        for (StatWinAllocation record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<StatWinAllocation> records) {
        for (StatWinAllocation record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<StatWinAllocation> selectByExample(StatWinAllocationExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public StatWinAllocation selectByPrimaryKey(StatWinAllocationKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<StatWinAllocation> findAll(List<StatWinAllocation> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new StatWinAllocationExample());
        }
        List<StatWinAllocation> list = new ArrayList<>();
        for (StatWinAllocation record : records) {
            StatWinAllocation result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(StatWinAllocation record, StatWinAllocationExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(StatWinAllocation record, StatWinAllocationExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(StatWinAllocation record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(StatWinAllocation record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(StatWinAllocationExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new StatWinAllocationExample());
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
    public StatWinAllocationExample getExample(StatWinAllocation record) {
        StatWinAllocationExample example = new StatWinAllocationExample();
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
            if (record.getFloatingWinMoney() != null) {
                criteria.andFloatingWinMoneyEqualTo(record.getFloatingWinMoney());
            }
            if (record.getFixedWinMoney() != null) {
                criteria.andFixedWinMoneyEqualTo(record.getFixedWinMoney());
            }
            if (record.getTotalMoney() != null) {
                criteria.andTotalMoneyEqualTo(record.getTotalMoney());
            }
            if (record.getOneWinCount() != null) {
                criteria.andOneWinCountEqualTo(record.getOneWinCount());
            }
            if (record.getOneWinMoney() != null) {
                criteria.andOneWinMoneyEqualTo(record.getOneWinMoney());
            }
            if (record.getTwoWinCount() != null) {
                criteria.andTwoWinCountEqualTo(record.getTwoWinCount());
            }
            if (record.getTwoWinMoney() != null) {
                criteria.andTwoWinMoneyEqualTo(record.getTwoWinMoney());
            }
            if (record.getThreeWinCount() != null) {
                criteria.andThreeWinCountEqualTo(record.getThreeWinCount());
            }
            if (record.getThreeWinMoney() != null) {
                criteria.andThreeWinMoneyEqualTo(record.getThreeWinMoney());
            }
            if (record.getFourWinCount() != null) {
                criteria.andFourWinCountEqualTo(record.getFourWinCount());
            }
            if (record.getFourWinMoney() != null) {
                criteria.andFourWinMoneyEqualTo(record.getFourWinMoney());
            }
            if (record.getFiveWinCount() != null) {
                criteria.andFiveWinCountEqualTo(record.getFiveWinCount());
            }
            if (record.getFiveWinMoney() != null) {
                criteria.andFiveWinMoneyEqualTo(record.getFiveWinMoney());
            }
            if (record.getSixWinCount() != null) {
                criteria.andSixWinCountEqualTo(record.getSixWinCount());
            }
            if (record.getSixWinMoney() != null) {
                criteria.andSixWinMoneyEqualTo(record.getSixWinMoney());
            }
            if (record.getSevenWinMoney() != null) {
                criteria.andSevenWinMoneyEqualTo(record.getSevenWinMoney());
            }
            if (record.getEightWinCount() != null) {
                criteria.andEightWinCountEqualTo(record.getEightWinCount());
            }
            if (record.getEightWinMoney() != null) {
                criteria.andEightWinMoneyEqualTo(record.getEightWinMoney());
            }
            if (record.getSevenWinCount() != null) {
                criteria.andSevenWinCountEqualTo(record.getSevenWinCount());
            }

        }
        return example;
    }
}
