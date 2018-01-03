package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataExample;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatDataKey;
import com.cwlrdc.commondb.ltto.mapper.LttoWinstatDataMapper;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.ServiceInterface;
import com.cwlrdc.front.common.Status;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import com.joyveb.lbos.restful.util.SqlMaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class LttoWinstatDataService implements ServiceInterface<LttoWinstatData, LttoWinstatDataExample, LttoWinstatDataKey> {

    @Resource
    private LttoWinstatDataMapper mapper;
    private @Resource
    CommonSqlMapper common;

    @Resource
    private ProvinceInfoCache provinceInfoCache;


    @Override
    public int countByExample(LttoWinstatDataExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(LttoWinstatDataExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(LttoWinstatDataKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(LttoWinstatData record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(LttoWinstatData record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<LttoWinstatData> records) {
        for (LttoWinstatData record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<LttoWinstatData> records) {
        for (LttoWinstatData record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<LttoWinstatData> records) {
        for (LttoWinstatData record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<LttoWinstatData> selectByExample(LttoWinstatDataExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public LttoWinstatData selectByPrimaryKey(LttoWinstatDataKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<LttoWinstatData> findAll(List<LttoWinstatData> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new LttoWinstatDataExample());
        }
        List<LttoWinstatData> list = new ArrayList<>();
        for (LttoWinstatData record : records) {
            LttoWinstatData result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    @Override
    public int updateByExampleSelective(LttoWinstatData record, LttoWinstatDataExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(LttoWinstatData record, LttoWinstatDataExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(LttoWinstatData record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(LttoWinstatData record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(LttoWinstatDataExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new LttoWinstatDataExample());
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
    public LttoWinstatDataExample getExample(LttoWinstatData record) {
        LttoWinstatDataExample example = new LttoWinstatDataExample();
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
            if (record.getWinDetail() != null) {
                criteria.andWinDetailEqualTo(record.getWinDetail());
            }
            if (record.getAllPrizeMoney() != null) {
                criteria.andAllPrizeMoneyEqualTo(record.getAllPrizeMoney());
            }
            if (record.getGradeCount() != null) {
                criteria.andGradeCountEqualTo(record.getGradeCount());
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
            if (record.getUploadTime() != null) {
                criteria.andUploadTimeEqualTo(record.getUploadTime());
            }
            if (record.getDataStatus() != null) {
                criteria.andDataStatusEqualTo(record.getDataStatus());
            }
            if (record.getFilePath() != null) {
                criteria.andFilePathEqualTo(record.getFilePath());
            }

        }
        return example;
    }

    public LttoWinstatData selectByKey(String periodNum, String gameCode, String provinceId) {
        LttoWinstatDataKey key = new LttoWinstatDataKey();
        key.setGameCode(gameCode);
        key.setPeriodNum(periodNum);
        key.setProvinceId(provinceId);
        return mapper.selectByPrimaryKey(key);
    }

    public List<LttoWinstatData> selectDatas(String periodNum, String gameCode, List<String> provinceIds) {
        LttoWinstatDataExample example = new LttoWinstatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum).andProvinceIdIn(provinceIds);
        return mapper.selectByExample(example);
    }

    public List<LttoWinstatData> select2datas(String gameCode, String periodNum) {
        LttoWinstatDataExample proWinDataExample = new LttoWinstatDataExample();
        proWinDataExample.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum);
        return mapper.selectByExample(proWinDataExample);
    }


    public List<LttoWinstatData> select2datas(String gameCode, String periodNum, Integer uploadStatus) {
        LttoWinstatDataExample proWinDataExample = new LttoWinstatDataExample();
        proWinDataExample.createCriteria().andGameCodeEqualTo(gameCode)
                .andPeriodNumEqualTo(periodNum).andDataStatusEqualTo(uploadStatus);
        return mapper.selectByExample(proWinDataExample);
    }

    public int selectUploadFaildCount(String gameCode, String periodNum) {
        LttoWinstatDataExample example = new LttoWinstatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusNotEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }


    //中奖数据信息
    public HashMap<String, LttoWinstatData> getProvincdWinData(String gameCode, String periodNum) {
        HashMap<String, LttoWinstatData> saleMap = new HashMap<>();
        LttoWinstatDataExample example = new LttoWinstatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusEqualTo(Status.UploadStatus.UPLOADED_SUCCESS);
        List<LttoWinstatData> winDatas = mapper.selectByExample(example);
        for (LttoWinstatData data : winDatas) {
            saleMap.put(data.getProvinceId(), data);
        }
        return saleMap;
    }


    //获取一二等奖年累计注数 //需要根据年份判断
    public HashMap<String, String> getwinSataByYear(String gameCode, String year) {
        HashMap<String, String> resultMap = new HashMap<>();
        LttoWinstatDataExample example = new LttoWinstatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumLike(year + "%");
        Long prize1 = 0L, prize2 = 0L;
        String provinceId = "";
        List<LttoWinstatData> winDatas = mapper.selectByExample(example);
        List<ParaProvinceInfo> provinceInfos = provinceInfoCache.getAllProvince();
        for (ParaProvinceInfo info : provinceInfos) {
            if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId())) {
                for (LttoWinstatData data : winDatas) {
                    if (info.getProvinceId().equals(data.getProvinceId())) {
                        if (null != data.getPrize1Count() && null != data.getPrize2Count()) {
                            prize1 += data.getPrize1Count();
                            prize2 += data.getPrize2Count();
                        }
                    }
                }
            }
            provinceId = info.getProvinceId();
            resultMap.put(provinceId, prize1 + "," + prize2);
        }
        return resultMap;
    }

    //全国一等奖中奖情况
    //浙江1注,河南1注,湖南1注,云南1注,共4注。
    public String getAllPrize1Count(String gameCode, String periodNum) {
        StringBuilder prize1Buff = new StringBuilder();
        StringBuilder pro1Buff = new StringBuilder();
        LttoWinstatDataExample winstatDataExample = new LttoWinstatDataExample();
        winstatDataExample.createCriteria().andGameCodeEqualTo(gameCode)
                .andPeriodNumEqualTo(periodNum).andDataStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        List<LttoWinstatData> list = mapper.selectByExample(winstatDataExample);
        Long count = 0L;
        for (LttoWinstatData data : list) {
            Long prize1Count = data.getPrize1Count();
            if (prize1Count != null && prize1Count > 0) {
                count += prize1Count;
                prize1Buff.append(provinceInfoCache.getProvinceName(data.getProvinceId()) + prize1Count + "注,");
            }
        }
        prize1Buff.append("共" + count + "注。");
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
            for (LttoWinstatData data : list) {
                Long prize7Count = data.getPrize7Count();
                if (prize7Count != null && prize7Count > 0) {
                    pro1Buff.append(provinceInfoCache.getProvinceName(data.getProvinceId()) + prize7Count + "注,");
                }
            }
        }
        if (StringUtils.isNotBlank(pro1Buff.toString())) {
            prize1Buff.append("其中复式投注为：");
            String firstPrizePromotionMsg = pro1Buff.deleteCharAt(pro1Buff.length() - 1).toString();
            prize1Buff.append(firstPrizePromotionMsg+"。");
        }
        return prize1Buff.toString();
    }


    public int selectUploadSuccessCount(String gameCode, String periodNum) {
        LttoWinstatDataExample example = new LttoWinstatDataExample();
        example.createCriteria().andGameCodeEqualTo(gameCode).andPeriodNumEqualTo(periodNum)
                .andDataStatusEqualTo(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1);
        return mapper.countByExample(example);
    }

}
