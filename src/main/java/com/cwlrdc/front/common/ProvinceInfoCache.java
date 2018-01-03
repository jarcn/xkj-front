package com.cwlrdc.front.common;

import com.cwlrdc.commondb.para.entity.ParaProvinceInfo;
import com.cwlrdc.front.para.service.ParaProvinceInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
public class ProvinceInfoCache implements CoreCache {

    @Resource
    private ParaProvinceInfoService paraProvinceInfoService;
    private Map<String, ParaProvinceInfo> provinces = new ConcurrentHashMap<>();
    private Map<String, ParaProvinceInfo> provincesNotHasCWL = new ConcurrentHashMap<>();
    private List<ParaProvinceInfo> gameinfos = new ArrayList<>();

    @Override
    public void init() {
        gameinfos = paraProvinceInfoService.findAll();
        for (ParaProvinceInfo bean : gameinfos) {
            provinces.put(bean.getProvinceId(), bean);
            if (!Constant.Key.PROVINCEID_OF_CWL.equalsIgnoreCase(bean.getProvinceId())) {
                provincesNotHasCWL.put(bean.getProvinceId(), bean);
            }
        }
    }

    //刷新缓存
    public void reload() {
        provinces.clear();
        init();
    }

    public String getProvinceName(String provinceId) {
        reload();
        return provinces.get(provinceId).getProvinceName();
    }

    public List<ParaProvinceInfo> getAllProvince() {
        reload();
        return gameinfos;
    }

    public ParaProvinceInfo getProvinceInfo(String provinceId) {
        reload();
        return provinces.get(provinceId);
    }


    /**
     * 不包含中心(CWL)
     * @return
     */
    public Map<String, ParaProvinceInfo> getProvincesNotHasCWL() {
        reload();
        return Collections.unmodifiableMap(provincesNotHasCWL);
    }

    public List<ParaProvinceInfo> getProvincesNotHasCWLList(){
        reload();
        ArrayList<ParaProvinceInfo> paraProvinceInfos = Lists.newArrayList(provincesNotHasCWL.values());
        Collections.sort(paraProvinceInfos, new Comparator<ParaProvinceInfo>() {
            @Override
            public int compare(ParaProvinceInfo o1, ParaProvinceInfo o2) {
                return Integer.parseInt(o1.getProvinceId())-Integer.parseInt(o2.getProvinceId());
            }
        });
        return paraProvinceInfos;
    }

    //实时数据模式省份
    public List<String> getRealTimeTypeProvinces(){
        reload();
        List<String> realTimeTypeProvinces = new ArrayList<>();
        List<ParaProvinceInfo> provinces = this.getProvincesNotHasCWLList();
        for (ParaProvinceInfo provinceInfo : provinces) {
            if (!Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
                realTimeTypeProvinces.add(provinceInfo.getProvinceId());
            }
        }
        return realTimeTypeProvinces;
    }

    //ftp模式省份
    public List<String> getFtpTypeProvinces(){
        reload();
        List<String> ftpTypeProvinces = new ArrayList<>();
        List<ParaProvinceInfo> provinces = this.getProvincesNotHasCWLList();
        for (ParaProvinceInfo provinceInfo : provinces) {
            if (Constant.Model.RPT_FILE_FTP.equals(provinceInfo.getIsFtp())) {
                ftpTypeProvinces.add(provinceInfo.getProvinceId());
            }
        }
        return ftpTypeProvinces;
    }

}
