package com.cwlrdc.front.ltto.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.joyveb.lbos.restful.common.CommonSqlMapper;

@Service
public class LttoProvinceLookOverCountryService {
	
	private @Resource CommonSqlMapper common;
	
	//销售/弃奖传真
	public List<HashMap<String,Object>> getTlfputone(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlfputone "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
      "WHERE tpgpi.GAME_CODE = "+ gameCode +
          " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlfp.UPLOAD_TIME   tlfputone, "+
               "tlfp.GAME_CODE, "+
               "tlfp.PERIOD_NUM, "+
               "tlfp.PROVINCE_ID "+
             "FROM T_LTTO_FAX_PICTURE tlfp WHERE tlfp.PICTURE_TYPE = '1') tlfp "+
    "ON main.gamecode = tlfp.GAME_CODE "+
     " AND main.perodnum = tlfp.PERIOD_NUM "+
      " AND main.provinceid = tlfp.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
	//销售统计文件
	public List<HashMap<String,Object>> getTlpsdut(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlpsdut "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
      "WHERE tpgpi.GAME_CODE = " + gameCode +
          " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlpsd.UPLOAD_TIME   tlpsdut, "+
               "tlpsd.GAME_CODE, "+
               "tlpsd.PERIOD_NUM, "+
               "tlpsd.PROVINCE_ID "+
             "FROM T_LTTO_PROVINCE_SALES_DATA tlpsd) tlpsda "+
    "ON main.gamecode = tlpsda.GAME_CODE "+
      " AND main.perodnum = tlpsda.PERIOD_NUM "+
      " AND main.provinceid = tlpsda.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
	//销售明细
	public List<HashMap<String,Object>> getTlpfsut(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlpfsut "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
      "WHERE tpgpi.GAME_CODE = " + gameCode +
          " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlpfs.UPLOAD_TIME   tlpfsut, "+
               "tlpfs.GAME_CODE, "+
               "tlpfs.PERIOD_NUM, "+
               "tlpfs.PROVINCE_ID "+
             "FROM T_LTTO_PROVINCE_FILE_STATUS tlpfs ) tlpfsa "+
    "ON main.gamecode = tlpfsa.GAME_CODE "+
      " AND main.perodnum = tlpfsa.PERIOD_NUM "+
      " AND main.provinceid = tlpfsa.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
	//弃奖统计文件
	public List<HashMap<String,Object>> getTlcsdut(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlcsdut "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
        "WHERE tpgpi.GAME_CODE = " + gameCode +
        " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlcsd.UPLOAD_TIME   tlcsdut, "+
               "tlcsd.GAME_CODE, "+
               "tlcsd.PERIOD_NUM, "+
               "tlcsd.PROVINCE_ID "+
             "FROM T_LTTO_CANCELWIN_STAT_DATA tlcsd ) tlcsda "+
    "ON main.gamecode = tlcsda.GAME_CODE "+
      " AND main.perodnum = tlcsda.PERIOD_NUM "+
      " AND main.provinceid = tlcsda.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
	//中奖结果传真
	public List<HashMap<String,Object>> getTlfputtwo(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlfputtwo "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
        "WHERE tpgpi.GAME_CODE = " + gameCode +
        " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlfp.UPLOAD_TIME   tlfputtwo, "+
               "tlfp.GAME_CODE, "+
               "tlfp.PERIOD_NUM, "+
               "tlfp.PROVINCE_ID "+
             "FROM T_LTTO_FAX_PICTURE tlfp WHERE tlfp.PICTURE_TYPE = '2') tlfp "+
    "ON main.gamecode = tlfp.GAME_CODE "+
      " AND main.perodnum = tlfp.PERIOD_NUM "+
      " AND main.provinceid = tlfp.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
	//中奖结果文件
	public List<HashMap<String,Object>> getTlpfsuttwo(String gameCode,String provinceId){
		List<HashMap<String, Object>> resultSet = null;
		String sql = "SELECT "+
  "main.*,tlpfsuttwo "+
"FROM (SELECT "+
        "tpgpi.GAME_CODE     gamecode, "+
        "tpgpi.PERIOD_NUM    perodnum, "+
        "tppi.PROVINCE_ID    provinceid "+
      "FROM T_PARA_PROVINCE_INFO tppi "+
        "INNER JOIN T_PARA_GAME_PERIOD_INFO tpgpi "+
        "WHERE tpgpi.GAME_CODE = " + gameCode +
        " AND tpgpi.PERIOD_NUM = " + provinceId +
          " AND tppi.PROVINCE_ID <> '00' "+
          " AND tppi.PROVINCE_ID <> '90' " +
      "ORDER BY provinceid - 0) main "+
  "LEFT JOIN (SELECT "+
               "tlpfs.UPLOAD_TIME   tlpfsuttwo, "+
               "tlpfs.GAME_CODE, "+
               "tlpfs.PERIOD_NUM, "+
               "tlpfs.PROVINCE_ID "+
             "FROM T_LTTO_WINSTAT_DATA tlpfs ) tlpfsa "+
    "ON main.gamecode = tlpfsa.GAME_CODE "+
      " AND main.perodnum = tlpfsa.PERIOD_NUM "+
      " AND main.provinceid = tlpfsa.PROVINCE_ID";
		resultSet = common.executeSql(sql);
		return resultSet;
	}
}
