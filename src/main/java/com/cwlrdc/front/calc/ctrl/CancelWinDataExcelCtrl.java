package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.GenOverDueDataExcelUtil;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoCancelWinStatDataService;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
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
 * 弃奖统计excel文件导出
 * Created by chenjia on 2017/4/26.
 */
@Controller
@Slf4j
public class CancelWinDataExcelCtrl {

  @Resource
  private LttoCancelWinStatDataService lttoCancelWinStatDataService;
  @Resource
  private GameInfoCache gameInfoCache;
  @Resource
  private ProvinceInfoCache provinceInfoCache;
  @Resource
  private LttoRunFlowService lttoRunFlowService;


  /**
   * 弃奖情况excel打印
   * @param request 请求参数
   * @param response 响应参数
   * @return 返回公共信息类
   */
  @ResponseBody
  @RequestMapping(value = "/cancelexportExcel", method = RequestMethod.GET)
  public ReturnInfo exportExcel(HttpServletRequest request, HttpServletResponse response) {
    String gameCode = request.getParameter("gameCode");
    String periodNum = request.getParameter("periodNum");
    List<LttoCancelWinStatData> list = lttoCancelWinStatDataService
        .selectCancelWinDatas(gameCode, periodNum);
    if (!CommonUtils.isEmpty(list)) {
      try {
        String agents = request.getHeader("user-agent");
        String filename = gameCode + "_" + periodNum + "_弃奖统计文件";

        Workbook workbook = GenOverDueDataExcelUtil
            .list2Excel(list, gameCode, periodNum, provinceInfoCache, gameInfoCache);
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
        }
      } catch (Exception e) {
        log.error("[新开奖稽核系统]导出[弃奖报告]异常", e);
        return ReturnInfo.Faild;
      }
      return ReturnInfo.Success;
    } else {
      log.warn("文件未上传");
      return ReturnInfo.Success;
    }
  }


}
