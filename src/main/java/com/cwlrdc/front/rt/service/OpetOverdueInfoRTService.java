package com.cwlrdc.front.rt.service;

import com.cwlrdc.commondb.rt.entity.*;
import com.cwlrdc.commondb.rt.entity.OpetOverdueInfoRTExample.Criteria;
import com.cwlrdc.commondb.rt.mapper.OpetOverdueInfoRTMapper;
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
public class OpetOverdueInfoRTService implements ServiceInterface<OpetOverdueInfoRT, OpetOverdueInfoRTExample, OpetOverdueInfoRTKey> {

    @Resource
    private OpetOverdueInfoRTMapper mapper;
    private @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(OpetOverdueInfoRTExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(OpetOverdueInfoRTExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(OpetOverdueInfoRTKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(OpetOverdueInfoRT record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(OpetOverdueInfoRT record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<OpetOverdueInfoRT> records) {
        for (OpetOverdueInfoRT record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<OpetOverdueInfoRT> records) {
        for (OpetOverdueInfoRT record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<OpetOverdueInfoRT> records) {
        for (OpetOverdueInfoRT record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<OpetOverdueInfoRT> selectByExample(OpetOverdueInfoRTExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public OpetOverdueInfoRT selectByPrimaryKey(OpetOverdueInfoRTKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<OpetOverdueInfoRT> findAll(List<OpetOverdueInfoRT> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new OpetOverdueInfoRTExample());
        }
        List<OpetOverdueInfoRT> list = new ArrayList<>();
        for (OpetOverdueInfoRT record : records) {
            OpetOverdueInfoRT result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(OpetOverdueInfoRT record, OpetOverdueInfoRTExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(OpetOverdueInfoRT record, OpetOverdueInfoRTExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(OpetOverdueInfoRT record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(OpetOverdueInfoRT record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(OpetOverdueInfoRTExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new OpetOverdueInfoRTExample());
    }


    public int getCount(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            resultSet = common.executeSql(SqlMaker.getCountSql(dc));
            return ((Number) resultSet.get(0).get("COUNT")).intValue();
        } catch (Exception e) {
            log.debug("异常", e);
            return 0;
        }
    }

    public List<HashMap<String, Object>> getData(DbCondi dc) {
        List<HashMap<String, Object>> resultSet = null;
        try {
            String sql = SqlMaker.getData(dc);
            resultSet = common.executeSql(sql);
        } catch (IllegalAccessException e) {
            log.debug("异常", e);
        } catch (InvocationTargetException e) {
            log.debug("异常", e);
        }
        return resultSet;
    }

    public List<HashMap<String, Object>> dosql(String sql) {
        List<HashMap<String, Object>> resultSet = common.executeSql(sql);
        return resultSet;
    }

    @Override
    public OpetOverdueInfoRTExample getExample(OpetOverdueInfoRT record) {
        OpetOverdueInfoRTExample example = new OpetOverdueInfoRTExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getGameCode() != null) {
                criteria.andGameCodeEqualTo(record.getGameCode());
            }
            if (record.getPeriodNum() != null) {
                criteria.andPeriodNumEqualTo(record.getPeriodNum());
            }
            if (record.getCancelPeriodNum() != null) {
                criteria.andCancelPeriodNumEqualTo(record.getCancelPeriodNum());
            }
            if (record.getProvinceId() != null) {
                criteria.andProvinceIdEqualTo(record.getProvinceId());
            }
            if (record.getCurrentCancelSum() != null) {
                criteria.andCurrentCancelSumEqualTo(record.getCurrentCancelSum());
            }
            if (record.getAwardNum() != null) {
                criteria.andAwardNumEqualTo(record.getAwardNum());
            }
            if (record.getOverdue1Count() != null) {
                criteria.andOverdue1CountEqualTo(record.getOverdue1Count());
            }
            if (record.getOverdue1Money() != null) {
                criteria.andOverdue1MoneyEqualTo(record.getOverdue1Money());
            }
            if (record.getOverdue2Count() != null) {
                criteria.andOverdue2CountEqualTo(record.getOverdue2Count());
            }
            if (record.getOverdue2Money() != null) {
                criteria.andOverdue2MoneyEqualTo(record.getOverdue2Money());
            }
            if (record.getOverdue3Count() != null) {
                criteria.andOverdue3CountEqualTo(record.getOverdue3Count());
            }
            if (record.getOverdue3Money() != null) {
                criteria.andOverdue3MoneyEqualTo(record.getOverdue3Money());
            }
            if (record.getOverdue4Count() != null) {
                criteria.andOverdue4CountEqualTo(record.getOverdue4Count());
            }
            if (record.getOverdue4Money() != null) {
                criteria.andOverdue4MoneyEqualTo(record.getOverdue4Money());
            }
            if (record.getOverdue5Count() != null) {
                criteria.andOverdue5CountEqualTo(record.getOverdue5Count());
            }
            if (record.getOverdue5Money() != null) {
                criteria.andOverdue5MoneyEqualTo(record.getOverdue5Money());
            }
            if (record.getOverdue6Count() != null) {
                criteria.andOverdue6CountEqualTo(record.getOverdue6Count());
            }
            if (record.getOverdue6Money() != null) {
                criteria.andOverdue6MoneyEqualTo(record.getOverdue6Money());
            }
            if (record.getOverdue7Count() != null) {
                criteria.andOverdue7CountEqualTo(record.getOverdue7Count());
            }
            if (record.getOverdue7Money() != null) {
                criteria.andOverdue7MoneyEqualTo(record.getOverdue7Money());
            }
            if (record.getOverdue8Count() != null) {
                criteria.andOverdue8CountEqualTo(record.getOverdue8Count());
            }
            if (record.getOverdue8Money() != null) {
                criteria.andOverdue8MoneyEqualTo(record.getOverdue8Money());
            }
            if (record.getOverdue9Count() != null) {
                criteria.andOverdue9CountEqualTo(record.getOverdue9Count());
            }
            if (record.getOverdue9Money() != null) {
                criteria.andOverdue9MoneyEqualTo(record.getOverdue9Money());
            }
            if (record.getOverdue10Count() != null) {
                criteria.andOverdue10CountEqualTo(record.getOverdue10Count());
            }
            if (record.getOverdue10Money() != null) {
                criteria.andOverdue10MoneyEqualTo(record.getOverdue10Money());
            }
            if (record.getSystemOperatorId() != null) {
                criteria.andSystemOperatorIdEqualTo(record.getSystemOperatorId());
            }
            if (record.getSystemOperateTime() != null) {
                criteria.andSystemOperateTimeEqualTo(record.getSystemOperateTime());
            }
            if (record.getStatus() != null) {
                criteria.andStatusEqualTo(record.getStatus());
            }
            if (record.getUploadTime() != null) {
                criteria.andUploadTimeEqualTo(record.getUploadTime());
            }
            if (record.getUploadCount() != null) {
                criteria.andUploadCountEqualTo(record.getUploadCount());
            }

        }
        return example;
    }

    public List<OpetOverdueInfoRT> selectDatas(String periodNum, String gameCode, List<String> provinceIds) {
        OpetOverdueInfoRTExample example = new OpetOverdueInfoRTExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andProvinceIdIn(provinceIds);
        return mapper.selectByExample(example);
    }

}
