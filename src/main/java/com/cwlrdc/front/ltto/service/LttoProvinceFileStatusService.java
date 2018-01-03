package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.*;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatusExample.Criteria;
import com.cwlrdc.commondb.ltto.mapper.LttoProvinceFileStatusMapper;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.common.Status;
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
public class LttoProvinceFileStatusService implements ServiceInterface<LttoProvinceFileStatus, LttoProvinceFileStatusExample, LttoProvinceFileStatusKey> {

    @Resource
    private LttoProvinceFileStatusMapper mapper;
    private @Resource
    CommonSqlMapper common;


    @Override
    public int countByExample(LttoProvinceFileStatusExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(LttoProvinceFileStatusExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(LttoProvinceFileStatusKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(LttoProvinceFileStatus record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(LttoProvinceFileStatus record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<LttoProvinceFileStatus> records) {
        for (LttoProvinceFileStatus record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<LttoProvinceFileStatus> records) {
        for (LttoProvinceFileStatus record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<LttoProvinceFileStatus> records) {
        for (LttoProvinceFileStatus record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<LttoProvinceFileStatus> selectByExample(LttoProvinceFileStatusExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public LttoProvinceFileStatus selectByPrimaryKey(LttoProvinceFileStatusKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<LttoProvinceFileStatus> findAll(List<LttoProvinceFileStatus> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new LttoProvinceFileStatusExample());
        }
        List<LttoProvinceFileStatus> list = new ArrayList<>();
        for (LttoProvinceFileStatus record : records) {
            LttoProvinceFileStatus result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(LttoProvinceFileStatus record, LttoProvinceFileStatusExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(LttoProvinceFileStatus record, LttoProvinceFileStatusExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(LttoProvinceFileStatus record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(LttoProvinceFileStatus record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(LttoProvinceFileStatusExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new LttoProvinceFileStatusExample());
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

    public List<HashMap<String, Object>> dosql(String sql) {
        List<HashMap<String, Object>> resultSet = common.executeSql(sql);
        return resultSet;
    }

    @Override
    public LttoProvinceFileStatusExample getExample(LttoProvinceFileStatus record) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
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
            if (record.getFileType() != null) {
                criteria.andFileTypeEqualTo(record.getFileType());
            }
            if (record.getFilePath() != null) {
                criteria.andFilePathEqualTo(record.getFilePath());
            }
            if (record.getUploadTime() != null) {
                criteria.andUploadTimeEqualTo(record.getUploadTime());
            }
            if (record.getFileSize() != null) {
                criteria.andFileSizeEqualTo(record.getFileSize());
            }
            if (record.getUploadStatus() != null) {
                criteria.andUploadStatusEqualTo(record.getUploadStatus());
            }

        }
        return example;
    }

    public List<LttoProvinceFileStatus> select2NotEqualStatus(String periodNum, String gameCode, List<String> provinceIds, Integer uploadStatus) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode).andProvinceIdIn(provinceIds).andUploadStatusNotEqualTo(uploadStatus);
        return mapper.selectByExample(example);
    }

    public List<LttoProvinceFileStatus> select2Infos(String periodNum, String gameCode, String provinceId) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode).andProvinceIdEqualTo(provinceId);
        return mapper.selectByExample(example);
    }

    public List<LttoProvinceFileStatus> select2Infos(String periodNum, String gameCode) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode)
                .andUploadStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.selectByExample(example);
    }


    public List<LttoProvinceFileStatus> selectAllInfos(String periodNum, String gameCode) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode);
        return mapper.selectByExample(example);
    }


    public LttoProvinceFileStatus select2key(String periodNum, String gameCode, String provinceId) {
        LttoProvinceFileStatusKey key = new LttoProvinceFileStatusKey();
        key.setProvinceId(provinceId);
        key.setPeriodNum(periodNum);
        key.setGameCode(gameCode);
        return mapper.selectByPrimaryKey(key);
    }


    public List<LttoProvinceFileStatus> selectDownLoading(String periodNum, String gameCode) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeEqualTo(gameCode)
                .andUploadStatusEqualTo(Status.UploadStatus.DOWNLOADING);
        return mapper.selectByExample(example);
    }

    public int selectUploadSuccessCount(String gameCode, String periodNum) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andUploadStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }


    public int selectUploadSuccessCount(String gameCode, String periodNum, List<String> provinces) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andUploadStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1)
                .andProvinceIdIn(provinces);
        return mapper.countByExample(example);
    }

    public int selectUploadFaildCount(String gameCode, String periodNum) {
        LttoProvinceFileStatusExample example = new LttoProvinceFileStatusExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
            .andUploadStatusNotEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }


}
