package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataExample;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesDataKey;
import com.cwlrdc.commondb.ltto.mapper.LttoProvinceSalesDataMapper;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.joyveb.lbos.restful.util.SqlMaker;
import com.unlto.twls.commonutil.component.CommonUtils;
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
public class LttoProvinceSalesDataService implements ServiceInterface<LttoProvinceSalesData, LttoProvinceSalesDataExample, LttoProvinceSalesDataKey> {

    @Resource
    private LttoProvinceSalesDataMapper mapper;
    private
    @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(LttoProvinceSalesDataExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(LttoProvinceSalesDataExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(LttoProvinceSalesDataKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(LttoProvinceSalesData record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(LttoProvinceSalesData record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<LttoProvinceSalesData> records) {
        for (LttoProvinceSalesData record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<LttoProvinceSalesData> records) {
        for (LttoProvinceSalesData record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<LttoProvinceSalesData> records) {
        for (LttoProvinceSalesData record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<LttoProvinceSalesData> selectByExample(LttoProvinceSalesDataExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public LttoProvinceSalesData selectByPrimaryKey(LttoProvinceSalesDataKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<LttoProvinceSalesData> findAll(List<LttoProvinceSalesData> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new LttoProvinceSalesDataExample());
        }
        List<LttoProvinceSalesData> list = new ArrayList<>();
        for (LttoProvinceSalesData record : records) {
            LttoProvinceSalesData result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(LttoProvinceSalesData record, LttoProvinceSalesDataExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(LttoProvinceSalesData record, LttoProvinceSalesDataExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(LttoProvinceSalesData record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(LttoProvinceSalesData record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(LttoProvinceSalesDataExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new LttoProvinceSalesDataExample());
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
    public LttoProvinceSalesDataExample getExample(LttoProvinceSalesData record) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getGameCode() != null) {
                criteria.andGameCodeEqualTo(record.getGameCode());
            }
            if (record.getPeriodNum() != null) {
                criteria.andPeriodNumEqualTo(record.getPeriodNum());
            }
            if (record.getProvinceId() != null) {
                criteria.andProvinceIdEqualTo(record.getProvinceId());
            }
            if (record.getAmount() != null) {
                criteria.andAmountEqualTo(record.getAmount());
            }
            if (record.getCancelMoney() != null) {
                criteria.andCancelMoneyEqualTo(record.getCancelMoney());
            }
            if (record.getAmountDetail() != null) {
                criteria.andAmountDetailEqualTo(record.getAmountDetail());
            }
            if (record.getCancelMoneyDetail() != null) {
                criteria.andCancelMoneyDetailEqualTo(record.getCancelMoneyDetail());
            }
            if (record.getDataStatus() != null) {
                criteria.andDataStatusEqualTo(record.getDataStatus());
            }
            if (record.getUploadTime() != null) {
                criteria.andUploadTimeEqualTo(record.getUploadTime());
            }
            if (record.getFilePath() != null) {
                criteria.andFilePathEqualTo(record.getFilePath());
            }

        }
        return example;
    }

    public List<LttoProvinceSalesData> select2SaleDatas(String periodNum, String gameCode, List<String> provinceIds) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode).andProvinceIdIn(provinceIds);
        return mapper.selectByExample(example);
    }

    public List<LttoProvinceSalesData> select2Reload(String periodNum, String gameCode, List<String> provinceIds, Integer dataStatus) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode).andProvinceIdIn(provinceIds).andDataStatusNotEqualTo(dataStatus);
        return mapper.selectByExample(example);
    }

    public List<LttoProvinceSalesData> selectByPeriodNumAndGameCode(String periodNum, String gameCode) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode)
        .andDataStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.selectByExample(example);
    }

    public LttoProvinceSalesData selectByKey(String periodNum, String gameCode,String provinceId) {
        LttoProvinceSalesDataKey key = new LttoProvinceSalesDataKey();
        key.setGameCode(gameCode);
        key.setPeriodNum(periodNum);
        key.setProvinceId(provinceId);
        return mapper.selectByPrimaryKey(key);
    }

    public List<LttoProvinceSalesData> selectDatas(String periodNum, String gameCode, List<String> provinceIds) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andProvinceIdIn(provinceIds);
        return mapper.selectByExample(example);
    }

    public List<LttoProvinceSalesData> selectDatas(String periodNum, String gameCode) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        return mapper.selectByExample(example);
    }

    //省销售数据当期
    public List<LttoProvinceSalesData> getProvinceSaleData(String gameCode, String periodNum) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        List<LttoProvinceSalesData> saleList = mapper.selectByExample(example);
        if (!CommonUtils.isEmpty(saleList)) {
            return saleList;
        }
        return null;
    }

    public List<LttoProvinceSalesData> initProvinceSaleData(String gameCode, String periodNum) {
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        List<LttoProvinceSalesData> saleList = mapper.selectByExample(example);
        if (!CommonUtils.isEmpty(saleList)) {
            return saleList;
        }
        return null;
    }


    public int selectUploadFaildCount(String gameCode,String periodNum){
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusNotEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }

    public int selectUploadSuccessCount(String gameCode,String periodNum){
        LttoProvinceSalesDataExample example = new LttoProvinceSalesDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }

}
