package com.cwlrdc.front.common;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yangqiju on 2017/9/7.
 */
@Slf4j
@Component
public class PeriodManager {

    @Autowired
    private ParaGamePeriodInfoService periodInfoService;

    public ParaGamePeriodInfo getCurrentGameAndPeriod() {
        List<ParaGamePeriodInfo> paraGamePeriodInfos = periodInfoService.select2current();
        if (paraGamePeriodInfos == null || paraGamePeriodInfos.size() <= 0) {
            log.warn("系统错误,游戏没有当前期,请联系运维人员");
            return null;
        }

        ParaGamePeriodInfo gamePeriodInfo = null;
        int openFlowCount = 0;
        for (ParaGamePeriodInfo info : paraGamePeriodInfos) {
            //强行开关
            if (StringUtils.isNotBlank(info.getFlowNode()) && Status.FlowNode.OPEN.equals(info.getFlowNode())) {
                gamePeriodInfo = info;
                openFlowCount++;
            }
        }

        if(openFlowCount>1){
            log.warn("系统错误,请联系运维人员.多个强制开期状态[FLOW_NODE]");
            return null;
        }

        if (gamePeriodInfo == null) {
            //根据时间选择当天期截正确的期号
            String todayDate = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
            for (ParaGamePeriodInfo info : paraGamePeriodInfos) {
                String periodEndTime = info.getPeriodEndTime();
                if (todayDate.equals(periodEndTime)) {
                    gamePeriodInfo = info;
                    break;
                }
            }
        }

        if (gamePeriodInfo == null) {
            //没有当天期截正确的期号,则随机选择一个
            if(paraGamePeriodInfos.size()>0){
                return paraGamePeriodInfos.get(0);
            }
            log.warn("系统错误,游戏没有当前期,请联系运维人员");
            return null;
        }
        return gamePeriodInfo;
    }
}
