package com.cwlrdc.front.ltto.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.task.LttoProvinceFileDownloadManager;
import com.joyveb.lbos.restful.common.ListInfo;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.joyveb.lbos.restful.util.KeyExplainHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lttoProvinceFileStatus")
public class LttoProvinceFileStatusCtrl {
	@Resource
	private LttoProvinceFileStatusService dbService;
	@Resource
	private LttoProvinceFileDownloadManager lttoProvinceFileDownloadManager;
	@Resource
	private ParaGamePeriodInfoService periodInfoService;
	@Resource
	private MigratingDataManager migratingDataManager;
	@Resource
	private ProvinceInfoCache provinceInfoCache;
	@Resource
	private OperatorsLogManager operatorsLogManager;

	/**
	 * 查询当期销售明细文件上传成功情况
	 * @param gameCode
	 * @param periodNum
	 * @return
	 */
	@RequestMapping(value="queryUploads/{gameCode}/{periodNum}",method=RequestMethod.GET)
	@ResponseBody
	public ReturnInfo get(@PathVariable String gameCode,@PathVariable String periodNum,HttpServletRequest req) {
		ParaGamePeriodInfo periodInfo = periodInfoService.selectbyKey(gameCode, periodNum);
		if(null == periodInfo){
			return new ReturnInfo("游戏或期号错误",false);
		}
		Integer i = dbService.selectUploadSuccessCount(gameCode, periodNum);
		return new ReturnInfo("销售明细文件上传情况", i, null,true);
	}

	/**
	 * 查询销售明细文件上传情况
	 * @Author mafengge
	 * @Date 2017/4/28 16:57
	 */
	@RequestMapping(value = "/queryzip/{gameCode}/{periodNum}", method = RequestMethod.GET)
	@ResponseBody
	public List<LttoProvinceFileStatus> getUpload(@PathVariable String gameCode, @PathVariable String periodNum, HttpServletRequest req) {
		List<LttoProvinceFileStatus> list;
		try {
			long start=System.currentTimeMillis();
			this.migratZipStatus(gameCode, periodNum);
			list= dbService.selectAllInfos(periodNum, gameCode);
			log.info(operatorsLogManager.getLogInfo("销售稽核", "文件上传情况", start));
			return list;
		}catch (Exception e) {
			list=new ArrayList<LttoProvinceFileStatus>();
			log.info("文件上传情况异常",e);
			return list;
		}
	}

	@RequestMapping(value = "/download/{provinceId}/{gameCode}/{periodNum}", method = RequestMethod.GET)
	@ResponseBody
	public ReturnInfo download(@PathVariable String provinceId,@PathVariable String gameCode,@PathVariable String periodNum) {
		ReturnInfo info = new ReturnInfo(false);
		try {
			LttoProvinceFileStatus status = dbService.select2key(periodNum,gameCode,provinceId);
			if(status!=null && status.getUploadStatus().equals(Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1)){
				this.fileBackup(status);
			}
			lttoProvinceFileDownloadManager.downloadFile(gameCode, periodNum, provinceId);
			LttoProvinceFileStatus newstatus = dbService.select2key(periodNum,gameCode,provinceId);
			if(Status.UploadStatus.UPLOADED_SUCCESS.equals(newstatus.getUploadStatus())){
				return new ReturnInfo("获取成功",Status.UploadStatus.UPLOADED_SUCCESS,newstatus,true);
			}
			if(Status.UploadStatus.DOWNLOADING.equals(newstatus.getUploadStatus())){
				return new ReturnInfo("下载中",Status.UploadStatus.UPLOADED_SUCCESS,newstatus,false);
			}
			if(Status.UploadStatus.NOT_UPLOADED.equals(newstatus.getUploadStatus())){
				return new ReturnInfo("未上传",Status.UploadStatus.UPLOADED_SUCCESS,newstatus,false);
			}
		} catch (Exception e) {
			log.warn("下载文件错误,gameCode[" + gameCode + "]periodNum[" + periodNum + "]provinceId[" + provinceId + "]", e);
			info.setDescription("获取失败");
		}
		return info;
	}

	private void fileBackup(LttoProvinceFileStatus status) {
		if(StringUtils.isNotBlank(status.getFilePath())){
			File localFile = new File(status.getFilePath());
			if (localFile.exists()) {
				log.debug("开始备份[{}]省,[{}]期,[{}]游戏销售明细文件...",status.getProvinceId(),status.getPeriodNum(),status.getGameCode());
				boolean b = localFile.renameTo(new File(status.getFilePath() + "." + System.currentTimeMillis() + ".BAK"));
				if (b){
					log.debug("完成备份[{}]省,[{}]期,[{}]游戏销售明细文件",status.getProvinceId(),status.getPeriodNum(),status.getGameCode());
				}else{
					log.debug("备份[{}]省,[{}]期,[{}]游戏销售明细文件失败",status.getProvinceId(),status.getPeriodNum(),status.getGameCode());
				}
			}
		}else {
			log.debug("无本地文件");
		}

	}

	@RequestMapping(value = "/{key}", method = RequestMethod.GET)
	@ResponseBody
	public ListInfo<LttoProvinceFileStatus> get(@PathVariable String key, HttpServletRequest req) {
		int totalCount = 1;
		List<LttoProvinceFileStatus> list = new ArrayList<>();
		try {
			LttoProvinceFileStatus info = new LttoProvinceFileStatus();
			KeyExplainHandler.explainKey(key, info);
			list.add(dbService.selectByPrimaryKey(info));
		} catch (Exception e) {
			log.warn("  LttoProvinceFileStatusCtrl get by key error..", e);
		}
		return new ListInfo<>(totalCount, list, 0, 1);
	}


	private void migratZipStatus(String gameCode,String periodNum){
		List<String> realTimeTypeProvinces = provinceInfoCache.getRealTimeTypeProvinces();
		for(String provinceId:realTimeTypeProvinces){
			if(!isUploaded(gameCode, periodNum, provinceId)){
				migratingDataManager.migratZipSaleDataStatus(gameCode, periodNum);
			}else {
				log.debug("[{}]省,[{}]期,[{}]游戏 销售明细实时数据已收集", provinceId, periodNum, gameCode);
			}
		}
	}

	//判断文件是否已经收集过
	private boolean isUploaded(String gameCode, String periodNum, String provinceId) {
		LttoProvinceFileStatus key = new LttoProvinceFileStatus();
		key.setGameCode(gameCode);
		key.setPeriodNum(periodNum);
		key.setProvinceId(provinceId);
		LttoProvinceFileStatus result = dbService.selectByPrimaryKey(key);
		if (result == null) {
			log.warn("游戏中奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,表信息为空", provinceId, periodNum, gameCode);
			return false;
		}
		if (result.getUploadStatus() == null) {
			log.warn("游戏中奖汇总文件[{}]省,[{}]期,[{}]数据存储错误,DataStatus 信息为空", provinceId, periodNum, gameCode);
			return false;
		}
		if (Constant.File.FILE_UPLOAD_STATUS_SUCCESS_1.equals(result.getUploadStatus())) {
			return true;
		} else {
			return false;
		}
	}

	private String getControllerName() {
		return this.getClass().getSimpleName();
	}

	private String getTableName() {
		return "T_LTTO_PROVINCE_FILE_STATUS";
	}

	@SuppressWarnings("serial")
	public static class LttoProvinceFileStatuss extends ArrayList<LttoProvinceFileStatus> {
		public LttoProvinceFileStatuss() {
			super();
		}
	}
}
