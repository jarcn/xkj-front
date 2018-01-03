package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoFaxPicture;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureExample;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureKey;
import com.cwlrdc.commondb.ltto.mapper.LttoFaxPictureMapper;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.SqlMaker;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class LttoFaxPictureService implements
    ServiceInterface<LttoFaxPicture, LttoFaxPictureExample, LttoFaxPictureKey> {

  @Resource
  private LttoFaxPictureMapper mapper;
  private @Resource
  CommonSqlMapper common;
  private @Resource
  ProvinceInfoCache provinceInfoCache;

  public void initFaxPic(String gameCode, String periodNum, int picType) {
    List<ParaProvinceInfo> allProvince = provinceInfoCache.getAllProvince();
    for (ParaProvinceInfo ppi : allProvince) {
      LttoFaxPictureExample lttoFaxPictureExample = new LttoFaxPictureExample();
      lttoFaxPictureExample.createCriteria().andGameCodeEqualTo(gameCode)
          .andPeriodNumEqualTo(periodNum).andProvinceIdEqualTo(ppi.getProvinceId())
          .andPictureTypeBetween(1, 4);
      List<LttoFaxPicture> lttoFaxPictures = mapper.selectByExample(lttoFaxPictureExample);
      if (null == lttoFaxPictures || lttoFaxPictures.size() == 0) {
        LttoFaxPicture lttoFaxPicture = new LttoFaxPicture();
        lttoFaxPicture.setGameCode(gameCode);
        lttoFaxPicture.setPeriodNum(periodNum);
        lttoFaxPicture.setProvinceId(ppi.getProvinceId());
        lttoFaxPicture.setStatus(0);
        lttoFaxPicture.setPictureSize(0);
        lttoFaxPicture.setPictureType(picType);
        mapper.insert(lttoFaxPicture);
      }
    }
  }

  @Override
  public int countByExample(LttoFaxPictureExample example) {
    return mapper.countByExample(example);
  }

  @Override
  public int deleteByExample(LttoFaxPictureExample example) {
    return mapper.deleteByExample(example);
  }

  @Override
  public int deleteByPrimaryKey(LttoFaxPictureKey key) {
    return mapper.deleteByPrimaryKey(key);
  }

  @Override
  public int insert(LttoFaxPicture record) {
    return mapper.insert(record);
  }

  @Override
  public int insertSelective(LttoFaxPicture record) {
    return mapper.insertSelective(record);
  }

  @Override
  @Transactional
  public int batchInsert(List<LttoFaxPicture> records) {
    for (LttoFaxPicture record : records) {
      mapper.insert(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchUpdate(List<LttoFaxPicture> records) {
    for (LttoFaxPicture record : records) {
      mapper.updateByPrimaryKeySelective(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchDelete(List<LttoFaxPicture> records) {
    for (LttoFaxPicture record : records) {
      mapper.deleteByPrimaryKey(record);
    }
    return records.size();
  }

  @Override
  public List<LttoFaxPicture> selectByExample(LttoFaxPictureExample example) {
    return mapper.selectByExample(example);
  }

  @Override
  public LttoFaxPicture selectByPrimaryKey(LttoFaxPictureKey key) {
    return mapper.selectByPrimaryKey(key);
  }

  @Override
  public List<LttoFaxPicture> findAll(List<LttoFaxPicture> records) {
    if (records == null || records.size() <= 0) {
      return mapper.selectByExample(new LttoFaxPictureExample());
    }
    List<LttoFaxPicture> list = new ArrayList<>();
    for (LttoFaxPicture record : records) {
      LttoFaxPicture result = mapper.selectByPrimaryKey(record);
      if (result != null) {
        list.add(result);
      }
    }
    return list;
  }

  @Override
  public int updateByExampleSelective(LttoFaxPicture record, LttoFaxPictureExample example) {
    return mapper.updateByExampleSelective(record, example);
  }

  @Override
  public int updateByExample(LttoFaxPicture record, LttoFaxPictureExample example) {
    return mapper.updateByExample(record, example);
  }

  @Override
  public int updateByPrimaryKeySelective(LttoFaxPicture record) {
    return mapper.updateByPrimaryKeySelective(record);
  }

  @Override
  public int updateByPrimaryKey(LttoFaxPicture record) {
    return mapper.updateByPrimaryKey(record);
  }

  @Override
  public int sumByExample(LttoFaxPictureExample example) {
    return 0;
  }

  @Override
  public void deleteAll() {
    mapper.deleteByExample(new LttoFaxPictureExample());
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
  public LttoFaxPictureExample getExample(LttoFaxPicture record) {
    LttoFaxPictureExample example = new LttoFaxPictureExample();
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
      if (record.getPictureType() != null) {
        criteria.andPictureTypeEqualTo(record.getPictureType());
      }
      if (record.getPicturePath() != null) {
        criteria.andPicturePathEqualTo(record.getPicturePath());
      }
      if (record.getPictureName() != null) {
        criteria.andPictureNameEqualTo(record.getPictureName());
      }
      if (record.getStatus() != null) {
        criteria.andStatusEqualTo(record.getStatus());
      }
      if (record.getUploadTime() != null) {
        criteria.andUploadTimeEqualTo(record.getUploadTime());
      }
      if (record.getPictureSize() != null) {
        criteria.andPictureSizeEqualTo(record.getPictureSize());
      }

    }
    return example;
  }

  public LttoFaxPicture select2Key(String gameCode, String periodNum, String provinceId,
      String fileType) {
    LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
    lttoFaxPictureKey.setGameCode(gameCode);
    lttoFaxPictureKey.setPeriodNum(periodNum);
    lttoFaxPictureKey.setProvinceId(provinceId);
    lttoFaxPictureKey.setPictureType(Integer.valueOf(fileType));
    return mapper.selectByPrimaryKey(lttoFaxPictureKey);
  }

  public int delete2Key(String gameCode, String periodNum, String provinceId, String fileType) {
    LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
    lttoFaxPictureKey.setGameCode(gameCode);
    lttoFaxPictureKey.setPeriodNum(periodNum);
    lttoFaxPictureKey.setProvinceId(provinceId);
    lttoFaxPictureKey.setPictureType(Integer.valueOf(fileType));
    return mapper.deleteByPrimaryKey(lttoFaxPictureKey);
  }

  //保存图片到指定位置
  public boolean savePictrue(String picFilePath,String picName, MultipartFile file) {
    File dir = new File(picFilePath);
    if(dir.isDirectory() && !dir.exists()){
      dir.mkdirs();
    }
    String picFile = picFilePath+picName;
    try {
      file.transferTo(new File(picFile));
      return true;
    } catch (IOException e) {
      log.debug("文件保存失败",e);
    }
    return false;
  }

}
