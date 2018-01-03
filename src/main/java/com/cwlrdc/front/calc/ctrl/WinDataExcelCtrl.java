package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.GenWinDataExcelUtil;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.common.Status;
import com.cwlrdc.front.ltto.service.LttoWinstatDataService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 中奖文件汇总数据excel导出
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
@Controller
public class WinDataExcelCtrl {

  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoWinstatDataService lttoWinstatDataService;
  @Resource
  private OperatorsLogManager operatorsLogManager;

  /**
   * 获取所有省份中奖统计文件
   */
  @ResponseBody
  @RequestMapping(value = "/windataexport", method = RequestMethod.GET)
  public Object salesDataCollect(HttpServletRequest request, HttpServletResponse response) {
    long start = System.currentTimeMillis();
    String gameCode = request.getParameter("gameCode");
    String periodNum = request.getParameter("periodNum");
    log.debug("[新开奖系统]开始导出中奖情况报告单");
    List<LttoWinstatData> list = lttoWinstatDataService
        .select2datas(gameCode, periodNum, Status.UploadStatus.UPLOADED_SUCCESS);
    try {
      if (!CommonUtils.isEmpty(list)) {
        String agents = request.getHeader("user-agent");
        String filename = gameInfoCache.getGameName(gameCode) + periodNum + "中奖情况";
        Workbook workbook = null;
        workbook = GenWinDataExcelUtil
            .list2Excel(list, gameCode, periodNum, gameInfoCache, provinceInfoCache);
        if (agents.contains("Firefox")) {
          filename = Base64Encode.base64EncodeFileName(filename + ".xls");
        } else {
          filename = URLEncoder.encode(filename + ".xls", "utf-8");
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
        try (OutputStream outputStream = response.getOutputStream()) {
          workbook.write(outputStream);
          outputStream.close();
          log.info(operatorsLogManager.getLogInfo("导出文件", "查看中奖情况报告单", start));
          return ReturnInfo.Success;
        } catch (Exception e) {
          log.debug("中奖结果导出异常", e);
          return ReturnInfo.Faild;
        }
      }
    } catch (Exception e1) {
      log.debug("中奖结果导出异常", e1);
    }
    return ReturnInfo.Faild;
  }



}
