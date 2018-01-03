package com.cwlrdc.front.common;

import com.cwlrdc.commondb.ltto.entity.LttoLogRemark;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant.Key;
import com.cwlrdc.front.ltto.service.LttoLogRemarkService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class OperatorsLogManager {

  @Resource
  private LttoLogRemarkService logRemarkService;
  @Resource
  private PeriodManager periodManager;

  private String getOperators() {
    HttpServletRequest req = ((ServletRequestAttributes)
        RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession();
    String peoples = (String) session.getAttribute(Constant.OPERATIORS.OPERATIORS);
    if (StringUtils.isBlank(peoples)) {
      ParaGamePeriodInfo currentGameAndPeriod = periodManager.getCurrentGameAndPeriod();
      String gameCode = currentGameAndPeriod.getGameCode();
      String periodNum = currentGameAndPeriod.getPeriodNum();
      LttoLogRemark lttoLogRemark =
          logRemarkService.selectLogRemarkByKey(gameCode, periodNum, Key.PROVINCEID_OF_CWL);
      if (null == lttoLogRemark || StringUtils.isBlank(lttoLogRemark.getLotteryPerson())) {
        peoples = "未选择开奖人";
      } else {
        peoples = lttoLogRemark.getLotteryPerson();
      }
    }
    return peoples;
  }

  public String getLogInfo(String page, String action, long startMillions) {
    String peoples = getOperators();
    long endMillions = System.currentTimeMillis();
    long time = endMillions - startMillions;
    String info = String.format("操作人[%s],操作页面[%s],操作[%s],耗时[%d毫秒]", peoples, page, action, time);
    return info;
  }
}
