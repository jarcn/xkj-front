package com.cwlrdc.front.stat.service;

import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.commondb.stat.entity.StatFundsAllocation;
import com.cwlrdc.commondb.stat.entity.StatFundsAllocationExample;
import com.cwlrdc.commondb.stat.entity.StatFundsAllocationExample.Criteria;
import com.cwlrdc.commondb.stat.entity.StatFundsAllocationKey;
import com.cwlrdc.commondb.stat.mapper.StatFundsAllocationMapper;
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
public class StatFundsAllocationService implements ServiceInterface<StatFundsAllocation, StatFundsAllocationExample, StatFundsAllocationKey> {

    @Resource
    private StatFundsAllocationMapper mapper;
    private
    @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(StatFundsAllocationExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(StatFundsAllocationExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(StatFundsAllocationKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(StatFundsAllocation record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(StatFundsAllocation record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<StatFundsAllocation> records) {
        for (StatFundsAllocation record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<StatFundsAllocation> records) {
        for (StatFundsAllocation record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<StatFundsAllocation> records) {
        for (StatFundsAllocation record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<StatFundsAllocation> selectByExample(StatFundsAllocationExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public StatFundsAllocation selectByPrimaryKey(StatFundsAllocationKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<StatFundsAllocation> findAll(List<StatFundsAllocation> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new StatFundsAllocationExample());
        }
        List<StatFundsAllocation> list = new ArrayList<>();
        for (StatFundsAllocation record : records) {
            StatFundsAllocation result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(StatFundsAllocation record, StatFundsAllocationExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(StatFundsAllocation record, StatFundsAllocationExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(StatFundsAllocation record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(StatFundsAllocation record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(StatFundsAllocationExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new StatFundsAllocationExample());
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
    public StatFundsAllocationExample getExample(StatFundsAllocation record) {
        StatFundsAllocationExample example = new StatFundsAllocationExample();
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
            if (record.getBonusAwardMoney() != null) {
                criteria.andBonusAwardMoneyEqualTo(record.getBonusAwardMoney());
            }
            if (record.getBonusRegulationfundMoney() != null) {
                criteria.andBonusRegulationfundMoneyEqualTo(record.getBonusRegulationfundMoney());
            }
            if (record.getBonusTotalMoney() != null) {
                criteria.andBonusTotalMoneyEqualTo(record.getBonusTotalMoney());
            }
            if (record.getCenterLotteryIssueMoney() != null) {
                criteria.andCenterLotteryIssueMoneyEqualTo(record.getCenterLotteryIssueMoney());
            }
            if (record.getProvinceIssueMoney() != null) {
                criteria.andProvinceIssueMoneyEqualTo(record.getProvinceIssueMoney());
            }
            if (record.getWagerStatoinIssueMoney() != null) {
                criteria.andWagerStatoinIssueMoneyEqualTo(record.getWagerStatoinIssueMoney());
            }
            if (record.getIssueTotalMoney() != null) {
                criteria.andIssueTotalMoneyEqualTo(record.getIssueTotalMoney());
            }
            if (record.getCommunityChestMoney() != null) {
                criteria.andCommunityChestMoneyEqualTo(record.getCommunityChestMoney());
            }

        }
        return example;
    }
}
