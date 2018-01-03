package com.cwlrdc.front.ltto.service;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementExample;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncementKey;
import com.cwlrdc.commondb.ltto.mapper.LttoLotteryAnnouncementMapper;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.Constant.GameCode;
import com.cwlrdc.front.common.Constant.PromotionCode;
import com.cwlrdc.front.common.PromotionManager;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.SqlMaker;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LttoLotteryAnnouncementService implements
    ServiceInterface<LttoLotteryAnnouncement, LttoLotteryAnnouncementExample, LttoLotteryAnnouncementKey> {

  @Resource
  private LttoLotteryAnnouncementMapper mapper;
  @Resource
  private CommonSqlMapper common;
  @Resource
  private PromotionManager promotionManager;


  @Override
  public int countByExample(LttoLotteryAnnouncementExample example) {
    return mapper.countByExample(example);
  }

  @Override
  public int deleteByExample(LttoLotteryAnnouncementExample example) {
    return mapper.deleteByExample(example);
  }

  @Override
  public int deleteByPrimaryKey(LttoLotteryAnnouncementKey key) {
    return mapper.deleteByPrimaryKey(key);
  }

  @Override
  public int insert(LttoLotteryAnnouncement record) {
    return mapper.insert(record);
  }

  @Override
  public int insertSelective(LttoLotteryAnnouncement record) {
    return mapper.insertSelective(record);
  }

  @Override
  @Transactional
  public int batchInsert(List<LttoLotteryAnnouncement> records) {
    for (LttoLotteryAnnouncement record : records) {
      mapper.insert(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchUpdate(List<LttoLotteryAnnouncement> records) {
    for (LttoLotteryAnnouncement record : records) {
      mapper.updateByPrimaryKeySelective(record);
    }
    return records.size();
  }

  @Override
  @Transactional
  public int batchDelete(List<LttoLotteryAnnouncement> records) {
    for (LttoLotteryAnnouncement record : records) {
      mapper.deleteByPrimaryKey(record);
    }
    return records.size();
  }

  @Override
  public List<LttoLotteryAnnouncement> selectByExample(LttoLotteryAnnouncementExample example) {
    return mapper.selectByExample(example);
  }

  @Override
  public LttoLotteryAnnouncement selectByPrimaryKey(LttoLotteryAnnouncementKey key) {
    return mapper.selectByPrimaryKey(key);
  }

  @Override
  public List<LttoLotteryAnnouncement> findAll(List<LttoLotteryAnnouncement> records) {
    if (records == null || records.size() <= 0) {
      return mapper.selectByExample(new LttoLotteryAnnouncementExample());
    }
    List<LttoLotteryAnnouncement> list = new ArrayList<>();
    for (LttoLotteryAnnouncement record : records) {
      LttoLotteryAnnouncement result = mapper.selectByPrimaryKey(record);
      if (result != null) {
        list.add(result);
      }
    }
    return list;
  }

  @Override
  public int updateByExampleSelective(LttoLotteryAnnouncement record,
      LttoLotteryAnnouncementExample example) {
    return mapper.updateByExampleSelective(record, example);
  }

  @Override
  public int updateByExample(LttoLotteryAnnouncement record,
      LttoLotteryAnnouncementExample example) {
    return mapper.updateByExample(record, example);
  }

  @Override
  public int updateByPrimaryKeySelective(LttoLotteryAnnouncement record) {
    return mapper.updateByPrimaryKeySelective(record);
  }

  @Override
  public int updateByPrimaryKey(LttoLotteryAnnouncement record) {
    return mapper.updateByPrimaryKey(record);
  }

  @Override
  public int sumByExample(LttoLotteryAnnouncementExample example) {
    return 0;
  }

  @Override
  public void deleteAll() {
    mapper.deleteByExample(new LttoLotteryAnnouncementExample());
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
  public LttoLotteryAnnouncementExample getExample(LttoLotteryAnnouncement record) {
    LttoLotteryAnnouncementExample example = new LttoLotteryAnnouncementExample();
    if (record != null) {
      Criteria criteria = example.createCriteria();
      if (record.getGameCode() != null) {
        criteria.andGameCodeEqualTo(record.getGameCode());
      }
      if (record.getPeriodNum() != null) {
        criteria.andPeriodNumEqualTo(record.getPeriodNum());
      }
      if (record.getWinGroupCount() != null) {
        criteria.andWinGroupCountEqualTo(record.getWinGroupCount());
      }
      if (record.getAllPrize1Detail() != null) {
        criteria.andAllPrize1DetailEqualTo(record.getAllPrize1Detail());
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
      if (record.getSaleMoneyTotal() != null) {
        criteria.andSaleMoneyTotalEqualTo(record.getSaleMoneyTotal());
      }
      if (record.getOverdueMoneyTotal() != null) {
        criteria.andOverdueMoneyTotalEqualTo(record.getOverdueMoneyTotal());
      }
      if (record.getFundBgn() != null) {
        criteria.andFundBgnEqualTo(record.getFundBgn());
      }
      if (record.getGetIntBalanceAll() != null) {
        criteria.andGetIntBalanceAllEqualTo(record.getGetIntBalanceAll());
      }
      if (record.getGetIntBalance1() != null) {
        criteria.andGetIntBalance1EqualTo(record.getGetIntBalance1());
      }
      if (record.getFund2PoolAutoAll() != null) {
        criteria.andFund2PoolAutoAllEqualTo(record.getFund2PoolAutoAll());
      }
      if (record.getFund2PoolAuto1() != null) {
        criteria.andFund2PoolAuto1EqualTo(record.getFund2PoolAuto1());
      }
      if (record.getFund2PoolHand() != null) {
        criteria.andFund2PoolHandEqualTo(record.getFund2PoolHand());
      }
      if (record.getFund2Prize() != null) {
        criteria.andFund2PrizeEqualTo(record.getFund2Prize());
      }
      if (record.getFundTempIn() != null) {
        criteria.andFundTempInEqualTo(record.getFundTempIn());
      }
      if (record.getFundTempOut() != null) {
        criteria.andFundTempOutEqualTo(record.getFundTempOut());
      }
      if (record.getFundTotal() != null) {
        criteria.andFundTotalEqualTo(record.getFundTotal());
      }
      if (record.getPoolBgn() != null) {
        criteria.andPoolBgnEqualTo(record.getPoolBgn());
      }
      if (record.getPoolCurrent() != null) {
        criteria.andPoolCurrentEqualTo(record.getPoolCurrent());
      }
      if (record.getUnshotOtherFloatPrize() != null) {
        criteria.andUnshotOtherFloatPrizeEqualTo(record.getUnshotOtherFloatPrize());
      }
      if (record.getExcessOtherFloatPrize() != null) {
        criteria.andExcessOtherFloatPrizeEqualTo(record.getExcessOtherFloatPrize());
      }
      if (record.getPoolTempIn() != null) {
        criteria.andPoolTempInEqualTo(record.getPoolTempIn());
      }
      if (record.getPoolTempOut() != null) {
        criteria.andPoolTempOutEqualTo(record.getPoolTempOut());
      }
      if (record.getPoolTotal() != null) {
        criteria.andPoolTotalEqualTo(record.getPoolTotal());
      }
      if (record.getFundNote() != null) {
        criteria.andFundNoteEqualTo(record.getFundNote());
      }
      if (record.getBullNote() != null) {
        criteria.andBullNoteEqualTo(record.getBullNote());
      }
      if (record.getFlag() != null) {
        criteria.andFlagEqualTo(record.getFlag());
      }
      if (record.getFtpLasttime() != null) {
        criteria.andFtpLasttimeEqualTo(record.getFtpLasttime());
      }
      if (record.getProcessStatus() != null) {
        criteria.andProcessStatusEqualTo(record.getProcessStatus());
      }

    }
    return example;
  }

  public LttoLotteryAnnouncement selectByKey(String gameCode, String periodNum) {
    LttoLotteryAnnouncementKey key = new LttoLotteryAnnouncementKey();
    key.setGameCode(gameCode);
    key.setPeriodNum(periodNum);
    return mapper.selectByPrimaryKey(key);
  }

  public List<HashMap<String, String>> queryDynamicFund(String periodNum, String gameCode) {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.PERIOD_NUM periodNum,");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.GAME_CODE gameCode,");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND_BGN allocationAddStartbalance,");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.GET_INT_BALANCE_ALL allocationAddFloatbalance, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND_TOTAL allocationEndBalance, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND2_PRIZE allocationSubPayspecialwin, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND_TEMP_OUT allocationSubChange, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND2_POOL_AUTO1 allocationSubIntojackpot, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND2_POOL_AUTO_ALL allocationSubRollout, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.FUND_TEMP_IN allocationAddChange, ");
    sql.append(
        " T_LTTO_LOTTERY_ANNOUNCEMENT.SALE_MONEY_TOTAL*T_OPET_REPORT_PARAMETER.`ADJUST_FUND`/100 allocationAddCurrentperiod, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.POOL_BGN jaclpotAddStartbalance, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.POOL_CURRENT jaclpotAddCurrentperiod, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.UNSHOT_OTHER_FLOAT_PRIZE jaclpotAddNotwinfloat, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.POOL_TOTAL jaclpotEndBalance, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.POOL_TEMP_IN jaclpotAddChange, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.POOL_TEMP_OUT jaclpotSubChange, ");
    sql.append(
        " T_LTTO_LOTTERY_ANNOUNCEMENT.PRIZE1_COUNT*T_LTTO_LOTTERY_ANNOUNCEMENT.PRIZE1_MONEY jaclpotSubFirstwinmoney, ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.GET_INT_BALANCE1 jaclpotSubFirstwinbalance, ");
    sql.append(
        " T_LTTO_LOTTERY_ANNOUNCEMENT.PRIZE1_COUNT*T_LTTO_LOTTERY_ANNOUNCEMENT.PRIZE1_MONEY jaclpotSubFirstwinmoney ");
    sql.append(" FROM T_LTTO_LOTTERY_ANNOUNCEMENT ");
    sql.append(" JOIN T_OPET_REPORT_PARAMETER ON ");
    sql.append(" T_LTTO_LOTTERY_ANNOUNCEMENT.`PERIOD_NUM`='" + periodNum
        + "' and T_LTTO_LOTTERY_ANNOUNCEMENT.`GAME_CODE`='" + gameCode + "' ");
    sql.append(
        "  AND T_OPET_REPORT_PARAMETER.`GAME_CODE`=T_LTTO_LOTTERY_ANNOUNCEMENT.`GAME_CODE` ");
    List<HashMap<String, Object>> list = common.executeSql(sql.toString());
    List<HashMap<String, String>> dealList = new ArrayList<>();
    for (HashMap<String, Object> map : list) {
      HashMap<String, String> dealMap = new HashMap<>();
      for (Entry<String, Object> entry : map.entrySet()) {
        String key = entry.getKey();
        if (null == entry.getValue()) {
          dealMap.put(key, "0.00");
        } else {
          if ("periodNum".equals(key) || "gameCode".equals(key)) {
            BigDecimal num = new BigDecimal(entry.getValue().toString());
            dealMap.put(key, num.setScale(0) + "");
          } else {
            BigDecimal num = new BigDecimal(entry.getValue().toString());
            dealMap.put(key, num.setScale(2) + "");
          }

        }
      }
      dealList.add(dealMap);
    }
    return dealList;
  }

  //奖池信息
  public LttoLotteryAnnouncement getAnnocementData(String gameCode, String periodNum) {
    LttoLotteryAnnouncementKey key = new LttoLotteryAnnouncementKey();
    key.setGameCode(gameCode);
    key.setPeriodNum(periodNum);
    LttoLotteryAnnouncement announcement = mapper.selectByPrimaryKey(key);
    if (announcement != null) {
      return announcement;
    }
    return null;
  }

  /**
   * 查询一等奖派奖奖池信息
   */
  public List<HashMap<String, Object>> queryPro1DynamicFund(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT ");
    sb.append("Fund_Bgn fundBgn1,"); //派奖调节基金期初余额
    sb.append("Get_Int_Balance_All getIntBalanceAll1,"); //调节基金派奖取整余额
    sb.append("Get_Int_Balance1 getIntBalance1,");//派奖奖池派奖取整余额
    sb.append("Fund_Total fundTotal1,");//派奖调节基金期末余额
    sb.append("Pool_Bgn poolBgn1,");//派奖奖池期初余额
    sb.append("Pool_Temp_In poolTempIn1,");//派奖本期注入
    sb.append("Pool_Total poolTotal1,");//派奖奖池期末余额
    sb.append("PRIZE7_COUNT*PRIZE7_MONEY winTotal1 ");//中出派奖总金额
    if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode)) {
      sb.append(" FROM T_LTTO_LOTTERY_ANNOUNCEMENT WHERE GAME_CODE = 10033 AND PERIOD_NUM = ");
    } else {
      sb.append(" FROM T_LTTO_LOTTERY_ANNOUNCEMENT WHERE GAME_CODE = 13002 AND PERIOD_NUM = ");
    }
    sb.append(periodNum);
    return this.converMap(common.executeSql(sb.toString()));
  }

  /**
   * 查询六等奖派奖奖池信息
   */
  public List<HashMap<String, Object>> queryPro6DynamicFund(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT ");
    sb.append("Fund_Bgn fundBgn6,"); //派奖调节基金期初余额
    sb.append("Get_Int_Balance_All getIntBalanceAll6,"); //调节基金派奖取整余额
    sb.append("Get_Int_Balance1 getIntBalance6,");//派奖奖池派奖取整余额
    sb.append("Fund_Total fundTotal6,");//派奖调节基金期末余额
    sb.append("Pool_Bgn poolBgn6,");//派奖奖池期初余额
    sb.append("Pool_Temp_In poolTempIn6,");//派奖本期注入
    sb.append("Pool_Total poolTotal6,");//派奖奖池期末余额
    sb.append("PRIZE8_COUNT*PRIZE8_MONEY winTotal6 ");//中出派奖总金额
    sb.append(" FROM T_LTTO_LOTTERY_ANNOUNCEMENT WHERE GAME_CODE = 10034 AND PERIOD_NUM = ");
    sb.append(periodNum);
    return this.converMap(common.executeSql(sb.toString()));
  }

  /**
   * 查询派奖奖金总余额
   */
  public List<LttoLotteryAnnouncement> queryProTotalBalance(String periodNum) {
    LttoLotteryAnnouncementExample example = new LttoLotteryAnnouncementExample();
    List<String> proGameCodes = new ArrayList<>();
    proGameCodes.add(Constant.PromotionCode.PRIZE1_GAME_CODE);
    proGameCodes.add(Constant.PromotionCode.PRIZE6_GAME_CODE);
    example.createCriteria().andPeriodNumEqualTo(periodNum).andGameCodeIn(proGameCodes);
    return mapper.selectByExample(example);
  }


  private List<HashMap<String, Object>> converMap(List<HashMap<String, Object>> list) {
    List<HashMap<String, Object>> result = null;
    HashMap<String, Object> dealMap = new HashMap<>();
    for (HashMap<String, Object> map : list) {
      if (null != map) {
        if (result == null) {
          result = new ArrayList<>();
        }
        for (Entry<String, Object> entry : map.entrySet()) {
          String key = entry.getKey();
          if (StringUtils.isNotBlank(key)) {
            String val = entry.getValue().toString();
            if (StringUtils.isNotBlank(val)) {
              dealMap.put(key, val);
            }
          }
        }
      }
    }
    if (result != null) {
      result.add(dealMap);
    }
    return result;
  }

  /**
   * 查询一等奖余额
   */
  public BigDecimal queryPoolTotalPro1Balance(String gameCode, String periodNum) {
    StringBuilder sb = new StringBuilder();
    if (GameCode.GAME_CODE_SLTO.equals(gameCode)) {
      LttoLotteryAnnouncement announcement = this
          .selectByKey(PromotionCode.PRIZE1_GAME_CODE, periodNum);
      if (Integer.parseInt(periodNum) > Integer.parseInt(PromotionCode.PROMOTION_PERIOD_START)
          && null != announcement && announcement.getPoolTotal().doubleValue() > 0) {
        sb.append(
            " SELECT SUM(Pool_Temp_In) poolTotalSum FROM T_LTTO_LOTTERY_ANNOUNCEMENT WHERE GAME_CODE = 10033 AND PERIOD_NUM > ");
        sb.append(periodNum);
      } else {
        sb.append(" SELECT SUM(0) poolTotalSum");
      }
      List<HashMap<String, Object>> list = common.executeSql(sb.toString());
      HashMap<String, Object> hashMap = list.get(0);
      Object poolTotalSum = hashMap.get("poolTotalSum");
      double poolTmpInSum = 0.00;
      if (poolTotalSum != null) {
        poolTmpInSum = Double.parseDouble(poolTotalSum.toString());
      }
      BigDecimal poolTotal;
      if (announcement != null) {
        poolTotal = announcement.getPoolTotal();
        if (poolTotal != null) {
          return poolTotal.add(BigDecimal.valueOf(poolTmpInSum));
        } else {
          return BigDecimal.valueOf(0.00);
        }
      }
    }
    return BigDecimal.valueOf(0.00);
  }
}
