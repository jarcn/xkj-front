package com.cwlrdc.front.common;

import com.cwlrdc.commondb.para.entity.ParaSysparame;
import com.cwlrdc.front.para.service.ParaSysparameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
public class ParaSysparameCache implements CoreCache {

    private @Resource ParaSysparameService paraSysparameService;

    private Map<String, ParaSysparame> systemPara = new ConcurrentHashMap<>();

    @Override
    public void init() {
        List<ParaSysparame> gameinfos = paraSysparameService.findAll(null);
        for (ParaSysparame bean : gameinfos) {
            systemPara.put(bean.getThkey(), bean);
        }
    }

    //刷新缓存
    public void reload() {
        systemPara.clear();
        init();
    }

    public String getValue(String thkey){
        reload();
        return systemPara.get(thkey).getValue();
    }

    public String getFtpLocalPath(){
        reload();
        ParaSysparame paraSysparame = systemPara.get(Constant.File.FILE_LOCAL_PATH);
        if(paraSysparame==null){
            throw new  IllegalArgumentException("ftp参数配置错误");
        }
        return paraSysparame.getValue();
    }

    /**
     * 获取文件下载时间，
     * 默认下午六点
     *
     * @param gameCode
     * @return
     */
    public int getDownloadLttoFileTime(String gameCode) {
        reload();
        String keyFormat = "ltto.province.file.%s.download.time";
        String key = String.format(keyFormat, gameCode).toLowerCase();
        ParaSysparame paraSysparame = systemPara.get(key);
        if(paraSysparame==null){
            return 18;
        }
        int hours = NumberUtils.toInt(paraSysparame.getValue(), 18);
        if(hours<0 || hours>24){
            log.warn("参数["+key+"]的参数的值["+hours+"]错误,使用默认参数[18]");
            return 18;
        }
        return hours;
    }

    /**
     *
     * 文件下载等待时间
     * @return
     */
    public long getWaitFileMilliseSeconds() {
        reload();
        String key = "ltto.province.file.wait.millise";
        ParaSysparame paraSysparame = systemPara.get(key);
        if(paraSysparame==null){
            return TimeUnit.MINUTES.toMillis(5);
        }
        return NumberUtils.toLong(paraSysparame.getValue(),TimeUnit.MINUTES.toMillis(5));
    }

    /**
     *
     *
     * 文件下载线程会一直下载,该配置用于最后等待时间
     * 默认20点
     *
     * @param gameCode
     * @return
     */
    public int getGiveUpLttoFileTime(String gameCode) {
        reload();
        String keyFormat = "ltto.province.file.%s.giveup.time";
        String key = String.format(keyFormat, gameCode).toLowerCase();
        ParaSysparame paraSysparame = systemPara.get(key);
        if(paraSysparame==null){
            return 20;
        }
        int hours = NumberUtils.toInt(paraSysparame.getValue(), 20);
        if(hours<0 || hours>24){
            log.warn("参数["+key+"]的参数的值["+hours+"]错误,使用默认参数[20]");
            return 18;
        }
        return hours;
    }

    /**
     * 获得当前属于什么环境
     * @return
     */
    public ENV getEnv(){
        reload();
        ParaSysparame paraSysparame = systemPara.get("env");
        if(paraSysparame == null || paraSysparame.getValue() == null){
            return ENV.test;
        }
        String value = paraSysparame.getValue();
        return ENV.toEnv(value);
    }


}
