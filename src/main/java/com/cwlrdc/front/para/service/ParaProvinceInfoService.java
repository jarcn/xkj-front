package com.cwlrdc.front.para.service;

import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoExample;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoExample.Criteria;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfoKey;
import com.cwlrdc.commondb.para.mapper.ParaProvinceInfoMapper;
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

@Slf4j
@Service
public class ParaProvinceInfoService implements ServiceInterface<ParaProvinceInfo, ParaProvinceInfoExample, ParaProvinceInfoKey> {

    @Resource
    private ParaProvinceInfoMapper mapper;
    private
    @Resource
    CommonSqlMapper common;
    @Resource
    ParaGameinfoService gameinfoService;


    @Override
    public int countByExample(ParaProvinceInfoExample example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(ParaProvinceInfoExample example) {
        return mapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(ParaProvinceInfoKey key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(ParaProvinceInfo record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(ParaProvinceInfo record) {
        return mapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int batchInsert(List<ParaProvinceInfo> records) {
        for (ParaProvinceInfo record : records) {
            mapper.insert(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchUpdate(List<ParaProvinceInfo> records) {
        for (ParaProvinceInfo record : records) {
            mapper.updateByPrimaryKeySelective(record);
        }
        return records.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<ParaProvinceInfo> records) {
        for (ParaProvinceInfo record : records) {
            mapper.deleteByPrimaryKey(record);
        }
        return records.size();
    }

    @Override
    public List<ParaProvinceInfo> selectByExample(ParaProvinceInfoExample example) {
        return mapper.selectByExample(example);
    }

    @Override
    public ParaProvinceInfo selectByPrimaryKey(ParaProvinceInfoKey key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<ParaProvinceInfo> findAll(List<ParaProvinceInfo> records) {
        if (records == null || records.size() <= 0) {
            return mapper.selectByExample(new ParaProvinceInfoExample());
        }
        List<ParaProvinceInfo> list = new ArrayList<>();
        for (ParaProvinceInfo record : records) {
            ParaProvinceInfo result = mapper.selectByPrimaryKey(record);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    public List<ParaProvinceInfo> findAll() {
        return mapper.selectByExample(new ParaProvinceInfoExample());
    }

    @Override
    public int updateByExampleSelective(ParaProvinceInfo record, ParaProvinceInfoExample example) {
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(ParaProvinceInfo record, ParaProvinceInfoExample example) {
        return mapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(ParaProvinceInfo record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ParaProvinceInfo record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int sumByExample(ParaProvinceInfoExample example) {
        return 0;
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample(new ParaProvinceInfoExample());
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
    public ParaProvinceInfoExample getExample(ParaProvinceInfo record) {
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        if (record != null) {
            Criteria criteria = example.createCriteria();
            if (record.getProvinceId() != null) {
                criteria.andProvinceIdEqualTo(record.getProvinceId());
            }
            if (record.getProvinceName() != null) {
                criteria.andProvinceNameEqualTo(record.getProvinceName());
            }
            if (record.getGameSupport() != null) {
                criteria.andGameSupportEqualTo(record.getGameSupport());
            }
            if (record.getStatus() != null) {
                criteria.andStatusEqualTo(record.getStatus());
            }
            if (record.getIsFtp() != null) {
                criteria.andIsFtpEqualTo(record.getIsFtp());
            }
            if (record.getSaledetailType() != null) {
                criteria.andSaledetailTypeEqualTo(record.getSaledetailType());
            }

        }
        return example;
    }

    public List<ParaProvinceInfo> findByFTP(Integer type) {
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        example.createCriteria().andIsFtpEqualTo(type);
        return mapper.selectByExample(example);
    }


    public int getMuchByGame(String gameCode) {
        int sum = 0;
        //查询支持该游戏的所有省，返回总条数
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        List<ParaProvinceInfo> list = mapper.selectByExample(example);
        if (!CommonUtils.isEmpty(list)) {
            for (ParaProvinceInfo info : list) {
                if (Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(info.getProvinceId())) {
                    continue;
                }
                String supportGame = info.getGameSupport();
                if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                    if (supportGame.contains(Constant.GameCode.GAME_NAME_SLTO)) {
                        sum++;
                    }
                } else {
                    if (supportGame.contains(Constant.GameCode.GAME_NAME_LOTO)) {
                        sum++;
                    }
                }
            }
        }
        return sum;
    }

    /**
     * 查询支持该游戏的所有省以及为FTP模式,返回总条数
     *
     * @param gameCode
     * @return
     */
    public int getFtpMatchByGame(String gameCode) {
        int sum = 0;
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        List<ParaProvinceInfo> list = mapper.selectByExample(example);
        if (!CommonUtils.isEmpty(list)) {
            for (ParaProvinceInfo info : list) {
                if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(info.getProvinceId())) {
                    if (Constant.Model.RPT_FILE_FTP.equals(info.getIsFtp())) {
                        String supportGame = info.getGameSupport();
                        if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                            if (supportGame.contains(Constant.GameCode.GAME_NAME_SLTO) && Constant.Model.RPT_FILE_FTP.equals(info.getIsFtp())) {
                                sum++;
                            }
                        } else {
                            if (supportGame.contains(Constant.GameCode.GAME_NAME_LOTO) && Constant.Model.RPT_FILE_FTP.equals(info.getIsFtp())) {
                                sum++;
                            }
                        }
                    }
                }
            }
        }
        return sum;
    }

    /**
     * 查询支持该游戏的实时模式省份,返回总条数
     *
     * @param gameCode
     * @return
     */
    public int getRtMatchByGame(String gameCode) {
        int sum = 0;
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        List<ParaProvinceInfo> list = mapper.selectByExample(example);
        if (!CommonUtils.isEmpty(list)) {
            for (ParaProvinceInfo info : list) {
                if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(info.getProvinceId())) {
                    String supportGame = info.getGameSupport();
                    if (Constant.GameCode.GAME_CODE_SLTO.equalsIgnoreCase(gameCode)) {
                        if (supportGame.contains(Constant.GameCode.GAME_NAME_SLTO) && Constant.Model.RPT_FILE_RT.equals(info.getIsFtp())) {
                            sum++;
                        }
                    } else {
                        if (supportGame.contains(Constant.GameCode.GAME_NAME_LOTO) && Constant.Model.RPT_FILE_RT.equals(info.getIsFtp())) {
                            sum++;
                        }
                    }
                }
            }
        }
        return sum;
    }

    /**
     * 查询所有实时接口模式的省份
     *
     * @return
     */
    public List<String> getAllRtProvinces() {
        List<String> provinces = new ArrayList<>();
        ParaProvinceInfoExample example = new ParaProvinceInfoExample();
        example.createCriteria().andIsFtpEqualTo(Constant.Model.RPT_FILE_RT);
        List<ParaProvinceInfo> list = mapper.selectByExample(example);
        for (ParaProvinceInfo info : list) {
            provinces.add(info.getProvinceId());
        }
        return provinces;
    }


    public ParaProvinceInfo select2Key(String provinceId) {
        ParaProvinceInfoKey key = new ParaProvinceInfoKey();
        key.setProvinceId(provinceId);
        return mapper.selectByPrimaryKey(key);
    }

}
