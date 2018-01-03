package com.cwlrdc.front.task;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceFileStatus;
import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.calc.util.FtpService;
import com.cwlrdc.front.common.*;
import com.cwlrdc.front.ltto.service.LttoProvinceFileStatusService;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.unlto.twls.commonutil.component.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangqiju on 2017/8/22.
 */
@Slf4j
@Component
public class LttoProvinceFileDownloadManager implements InitializingBean, DisposableBean {

    @Resource
    private ParaProvinceInfoService provinceInfoService;
    @Resource
    private ProvinceInfoCache provinceInfoCache;
    @Resource
    private GameInfoCache gameInfoCache;
    @Resource
    private LttoProvinceFileStatusService lttoProvinceFileStatusService;
    @Resource
    private FtpInfoCache ftpInfoCache;
    @Resource
    private ParaSysparameCache sysparameCache;
    @Resource
    private ParaGamePeriodInfoService periodInfoService;

    private volatile boolean stop = false;

    public void execute(String gameCode, String periodNum) {
        while (!stop) {
            List<LttoProvinceFileStatus> lttoProvinceSalesDatas = this.select2DownloadInfo(gameCode, periodNum);
            if (lttoProvinceSalesDatas.isEmpty()) {
                log.debug("各省文件处理完成.");
                break;
            }
            for (LttoProvinceFileStatus data : lttoProvinceSalesDatas) {
                log.debug("gameCode[" + data.getGameCode() + "] periodNum[" + data.getPeriodNum() + "] provinceId[" + data.getProvinceId() + "]文件开始处理.");
                try {
                    ParaProvinceInfo provinceInfo = provinceInfoCache.getProvinceInfo(data.getProvinceId());
                    String gameInfos = provinceInfo.getGameSupport();
                    if (gameInfos.contains(gameInfoCache.getGameName(Constant.GameCode.GAME_CODE_SLTO)) && !data.getUploadStatus().equals(Status.UploadStatus.UPLOADED_SUCCESS)) {
                        this.updateFileStatusDownloading(data);
                        this.downloadFile(data.getGameCode(), data.getPeriodNum(), data.getProvinceId());
                    }
                } catch (Exception e) {
                    log.warn("下载文件异常 gameCode[" + data.getGameCode() + "] periodNum[" + data.getPeriodNum() + "] provinceId[" + data.getProvinceId() + "]", e);
                }
            }
            try {
                long waitFileMilliseSeconds = this.getWaitFileMilliseSeconds();
                log.debug("省投注明细处理线程未到处理时间,等待时间[{}]毫秒", waitFileMilliseSeconds);

                synchronized (lock){
                    lock.wait(waitFileMilliseSeconds);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("线程等待异常,取消等待");
                break;
            }
            long giveUpMilliseSeconds = this.caclGiveUpMilliseSeconds(gameCode);
            if (System.currentTimeMillis() >= giveUpMilliseSeconds){
                log.debug("销量明细文件获取线程取消文件获取,当前时间超过文件放弃时间["+ DateFormatUtils.format(giveUpMilliseSeconds, "HH:mm:ss")+ "]");
                List<LttoProvinceFileStatus> fileStatuses = lttoProvinceFileStatusService.selectDownLoading(periodNum, gameCode);
                for (LttoProvinceFileStatus data : fileStatuses){
                    log.debug("放弃下载zip文件更新[{}]期[{}]省游戏[{}]销售明细文件状态", data.getPeriodNum(), data.getGameCode(), data.getProvinceId());
                    this.updateFileStatusNotUpload(data);
                }
                log.debug("当前时间超过文件放弃时间,停止zip文件下载任务更新文件状态");
                stop = true;
            }
        }
    }

    /**
     * 计算出当前需要的等待时间
     * @param gameCode
     * @return
     */
    private long caclGiveUpMilliseSeconds(String gameCode) {
        int downloadLttoFileTime = sysparameCache.getGiveUpLttoFileTime(gameCode);
        Date date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        date = DateUtils.addHours(date, downloadLttoFileTime);
        return date.getTime();
    }

    /**
     * 计算出当前需要的等待时间
     * @return
     */
    private long getWaitFileMilliseSeconds() {
       return sysparameCache.getWaitFileMilliseSeconds();
    }

    private void updateFileStatusDownloading(LttoProvinceFileStatus data) {
        data.setUploadStatus(Status.UploadStatus.DOWNLOADING);
        lttoProvinceFileStatusService.updateByPrimaryKeySelective(data);
    }

    private void updateFileStatusNotUpload(LttoProvinceFileStatus data) {
        data.setUploadStatus(Status.UploadStatus.NOT_UPLOADED);
        lttoProvinceFileStatusService.updateByPrimaryKeySelective(data);
    }


    public void downloadFile(String gameCode, String periodNum, String provinceId) throws Exception {
        ParaFtpInfo ftpInfo = ftpInfoCache.getFtpInfo(provinceId);
        if (ftpInfo == null) {
            log.warn("省[{}]未配置ftp参数信息", provinceId);
            return;
        }
        String fileLocalPath = this.getLocalPath(gameCode, periodNum, provinceId);
        String fileName = this.getProvinceFileName(gameCode, periodNum, provinceId);
        if (Constant.Model.COLLECT_FILE_FTP.equals(ftpInfo.getFlag())) {
            String usename = ftpInfo.getFtpUsername();
            String passwd = ftpInfo.getFtpPassword();
            String ip = ftpInfo.getFtpIp();
            Integer port = Integer.valueOf(ftpInfo.getFtpPort());
            String directory = ftpInfo.getFtpPath();

            try (FtpService ftpclient = new FtpService();) {
                ftpclient.getConnect(ip, port, usename, passwd);
                long lastModifyTime = ftpclient.getModificationTime(directory, fileName);
                if (lastModifyTime == -1) {
                    log.debug("ftp目录[" + directory + "]中,文件[" + fileName + "]不存在");
                    return;
                }
                synchronized (lock){
                    while (System.currentTimeMillis() - lastModifyTime < TimeUnit.SECONDS.toMillis(30)) {
                        log.debug("文件[" + fileName + "]最后编辑时间为[" + lastModifyTime + "]等待30秒");
                        lock.wait(TimeUnit.SECONDS.toMillis(30));
                    }
                    ftpclient.download(directory, fileName, fileLocalPath);
                }
            }
        }
        if (new File(fileLocalPath+ fileName).exists()) {
            log.debug("销售明细文件[{}]已上传", fileLocalPath+ fileName);
            LttoProvinceFileStatus fileStatus = new LttoProvinceFileStatus();
            fileStatus.setPeriodNum(periodNum);
            fileStatus.setProvinceId(ftpInfo.getProvinceId());
            fileStatus.setGameCode(gameCode);
            fileStatus.setFilePath(fileLocalPath+ fileName);
            fileStatus.setUploadStatus(Status.UploadStatus.UPLOADED_SUCCESS);
            fileStatus.setUploadTime(System.currentTimeMillis());
            try {
                lttoProvinceFileStatusService.insert(fileStatus);
            } catch (DuplicateKeyException e) {
                log.trace("插入省明细文件主键冲突", e);
                lttoProvinceFileStatusService.updateByPrimaryKey(fileStatus);
            }
        } else {
            log.debug("销售明细文件[{}]未上传", fileLocalPath);
        }
    }

    private String getLocalPath(String gameCode, String periodNum, String provinceid) {
        String ftpLocalPath = sysparameCache.getFtpLocalPath();
        StringBuilder sb = new StringBuilder();
        sb.append(ftpLocalPath).append(File.separator);
        sb.append(gameCode).append(File.separator);
        sb.append(gameCode).append("_").append(periodNum).append(File.separator);
        return sb.toString();
    }

    private String getProvinceFileName(String gameCode, String periodNum, String provinceid) {
        StringBuilder sb = new StringBuilder();
        sb.append(provinceid).append("_");
        sb.append(gameCode).append("_");
        sb.append(periodNum).append("_");
        sb.append("SALE.DAT.ZIP");
        return sb.toString();
    }

    private List<LttoProvinceFileStatus> select2DownloadInfo(String gameCode, String periodNum) {
        List<ParaProvinceInfo> provinceInfos = provinceInfoService.findByFTP(Constant.Model.RPT_FILE_FTP);
        List<String> provinceIds = new ArrayList<>();
        for (ParaProvinceInfo info : provinceInfos){
            if (!Constant.Key.PROVINCEID_OF_CWL.equals(info.getProvinceId())){
                provinceIds.add(info.getProvinceId());
            }
        }
        if (!CommonUtils.isEmpty(provinceIds)){
            return lttoProvinceFileStatusService.select2NotEqualStatus(periodNum, gameCode, provinceIds, Status.UploadStatus.UPLOADED_SUCCESS);
        } else{
            return new ArrayList<>();
        }
    }

    @Override
    public void destroy() {
        stop = true;
        this.wakeup();
    }

    private void wakeup(){
        synchronized (lock){
            lock.notify();
        }
    }
    private Object lock = new Object();

    @Override
    public void afterPropertiesSet(){
        stop = false;
    }
}
