package com.cwlrdc.front.common;

import com.cwlrdc.commondb.para.entity.ParaFtpInfo;
import com.cwlrdc.front.para.service.ParaFtpInfoService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjia on 2017/4/21.
 */

@Slf4j
public class FtpInfoCache implements CoreCache {

    @Resource
    private ParaFtpInfoService ftpInfoService;

    private Map<String, ParaFtpInfo> ftpInfoMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        ParaFtpInfos infos = new ParaFtpInfos();
        List<ParaFtpInfo> gameinfos = ftpInfoService.findAll(infos);
        for (ParaFtpInfo bean:gameinfos) {
            ftpInfoMap.put(bean.getProvinceId(),bean);
        }
    }

    //刷新缓存
    public void reload(){
        ftpInfoMap.clear();
        init();
    }

    public ParaFtpInfo getFtpInfo(String provinceId){
        reload();
        return ftpInfoMap.get(provinceId);
    }


    public static class ParaFtpInfos extends ArrayList<ParaFtpInfo> {
        public ParaFtpInfos() { super(); }
    }
}
