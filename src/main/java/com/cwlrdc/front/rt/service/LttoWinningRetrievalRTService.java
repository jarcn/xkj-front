package com.cwlrdc.front.rt.service;

import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRT;
import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRTExample;
import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRTExample.Criteria;
import com.cwlrdc.commondb.rt.entity.LttoWinningRetrievalRTKey;
import com.cwlrdc.commondb.rt.mapper.LttoWinningRetrievalRTMapper;
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
public class LttoWinningRetrievalRTService implements ServiceInterface<LttoWinningRetrievalRT, LttoWinningRetrievalRTExample, LttoWinningRetrievalRTKey> {

    @Resource
    private LttoWinningRetrievalRTMapper mapper;
    private @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(LttoWinningRetrievalRTExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(LttoWinningRetrievalRTExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(LttoWinningRetrievalRTKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(LttoWinningRetrievalRT record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(LttoWinningRetrievalRT record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<LttoWinningRetrievalRT> records) {
        for (LttoWinningRetrievalRT record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<LttoWinningRetrievalRT> records) {
        for (LttoWinningRetrievalRT record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<LttoWinningRetrievalRT> records) {
        for (LttoWinningRetrievalRT record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<LttoWinningRetrievalRT> selectByExample(LttoWinningRetrievalRTExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public LttoWinningRetrievalRT selectByPrimaryKey(LttoWinningRetrievalRTKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<LttoWinningRetrievalRT> findAll(List<LttoWinningRetrievalRT> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new LttoWinningRetrievalRTExample());
        }
        List<LttoWinningRetrievalRT> list = new ArrayList<>();
        for (LttoWinningRetrievalRT record : records) {
            LttoWinningRetrievalRT result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(LttoWinningRetrievalRT record, LttoWinningRetrievalRTExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(LttoWinningRetrievalRT record, LttoWinningRetrievalRTExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(LttoWinningRetrievalRT record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(LttoWinningRetrievalRT record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(LttoWinningRetrievalRTExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new LttoWinningRetrievalRTExample());
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
    public LttoWinningRetrievalRTExample getExample(LttoWinningRetrievalRT record) {
        LttoWinningRetrievalRTExample example = new LttoWinningRetrievalRTExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getGameCode() != null) {
                criteria.andGameCodeEqualTo(record.getGameCode());
            }
            if (record.getProvinceId() != null) {
                criteria.andProvinceIdEqualTo(record.getProvinceId());
            }
            if (record.getPeriodNum() != null) {
                criteria.andPeriodNumEqualTo(record.getPeriodNum());
            }
            if (record.getTicketType() != null) {
                criteria.andTicketTypeEqualTo(record.getTicketType());
            }
            if (record.getWinDetail() != null) {
                criteria.andWinDetailEqualTo(record.getWinDetail());
            }
            if (record.getAllPrizeMoney() != null) {
                criteria.andAllPrizeMoneyEqualTo(record.getAllPrizeMoney());
            }
            if (record.getWinLevel() != null) {
                criteria.andWinLevelEqualTo(record.getWinLevel());
            }
            if (record.getPrize1Count() != null) {
                criteria.andPrize1CountEqualTo(record.getPrize1Count());
            }
            if (record.getPrize1Money() != null) {
                criteria.andPrize1MoneyEqualTo(record.getPrize1Money());
            }
            if (record.getPrize2Count() != null) {
                criteria.andPrize2CountEqualTo(record.getPrize2Count());
            }
            if (record.getPrize2Money() != null) {
                criteria.andPrize2MoneyEqualTo(record.getPrize2Money());
            }
            if (record.getPrize3Count() != null) {
                criteria.andPrize3CountEqualTo(record.getPrize3Count());
            }
            if (record.getPrize3Money() != null) {
                criteria.andPrize3MoneyEqualTo(record.getPrize3Money());
            }
            if (record.getPrize4Count() != null) {
                criteria.andPrize4CountEqualTo(record.getPrize4Count());
            }
            if (record.getPrize4Money() != null) {
                criteria.andPrize4MoneyEqualTo(record.getPrize4Money());
            }
            if (record.getPrize5Count() != null) {
                criteria.andPrize5CountEqualTo(record.getPrize5Count());
            }
            if (record.getPrize5Money() != null) {
                criteria.andPrize5MoneyEqualTo(record.getPrize5Money());
            }
            if (record.getPrize6Count() != null) {
                criteria.andPrize6CountEqualTo(record.getPrize6Count());
            }
            if (record.getPrize6Money() != null) {
                criteria.andPrize6MoneyEqualTo(record.getPrize6Money());
            }
            if (record.getPrize7Count() != null) {
                criteria.andPrize7CountEqualTo(record.getPrize7Count());
            }
            if (record.getPrize7Money() != null) {
                criteria.andPrize7MoneyEqualTo(record.getPrize7Money());
            }
            if (record.getPrize8Count() != null) {
                criteria.andPrize8CountEqualTo(record.getPrize8Count());
            }
            if (record.getPrize8Money() != null) {
                criteria.andPrize8MoneyEqualTo(record.getPrize8Money());
            }
            if (record.getPrize9Count() != null) {
                criteria.andPrize9CountEqualTo(record.getPrize9Count());
            }
            if (record.getPrize9Money() != null) {
                criteria.andPrize9MoneyEqualTo(record.getPrize9Money());
            }
            if (record.getPrize10Count() != null) {
                criteria.andPrize10CountEqualTo(record.getPrize10Count());
            }
            if (record.getPrize10Money() != null) {
                criteria.andPrize10MoneyEqualTo(record.getPrize10Money());
            }
            if (record.getProcessStatus() != null) {
                criteria.andProcessStatusEqualTo(record.getProcessStatus());
            }

        }
        return example;
    }

    public List<LttoWinningRetrievalRT> selec2datas(String gameCode, String periodNum, List<Integer> tickeTypes, List<String> provinces) {
        LttoWinningRetrievalRTExample rtExample = new LttoWinningRetrievalRTExample();
        rtExample.createCriteria().andGameCodeEqualTo(gameCode)
                .andPeriodNumEqualTo(periodNum)
                .andTicketTypeIn(tickeTypes)
                .andProvinceIdIn(provinces);
        return mapper.selectByExample(rtExample);
    }

    public List<LttoWinningRetrievalRT> selec2datas(String gameCode, String periodNum, List<String> provinces) {
        LttoWinningRetrievalRTExample rtExample = new LttoWinningRetrievalRTExample();
        rtExample.createCriteria().andGameCodeEqualTo(gameCode)
                .andPeriodNumEqualTo(periodNum)
                .andProvinceIdIn(provinces);
        return mapper.selectByExample(rtExample);
    }

    public List<LttoWinningRetrievalRT> selec2Results(String gameCode, String periodNum, List<String> provinces, int type) {
        LttoWinningRetrievalRTExample winningExample = new LttoWinningRetrievalRTExample();
        winningExample.createCriteria().andGameCodeEqualTo(gameCode)
                .andPeriodNumEqualTo(periodNum)
                .andTicketTypeEqualTo(type)
                .andProvinceIdIn(provinces);
        return mapper.selectByExample(winningExample);
    }

}
