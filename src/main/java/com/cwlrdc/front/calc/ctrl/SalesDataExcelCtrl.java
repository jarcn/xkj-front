package com.cwlrdc.front.calc.ctrl;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlow;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowKey;
import com.cwlrdc.front.calc.util.Base64Encode;
import com.cwlrdc.front.calc.util.GenPrintNoticeExcelUtil;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.FlowType;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.cwlrdc.front.ltto.service.LttoProvinceSalesDataService;
import com.cwlrdc.front.ltto.service.LttoRunFlowService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 销售统计文件导出
 * Created by chenjia on 2017/4/26.
 */

@Slf4j
@Controller
public class SalesDataExcelCtrl {

	@Resource
	private LttoProvinceSalesDataService lttoProvinceSalesDataService;
	@Resource
	private GameInfoCache gameInfoCache;
	@Resource
	private ProvinceInfoCache provinceInfoCache;
	@Resource
	private LttoRunFlowService lttoRunFlowService;
	@Resource
	private OperatorsLogManager operatorsLogManager;

	@ResponseBody
	@RequestMapping(value = "/exportExcel", method = RequestMethod.GET)
	public ReturnInfo exportExcel(HttpServletRequest request, HttpServletResponse response) {
		String gameCode = request.getParameter("gameCode");
		String periodNum = request.getParameter("periodNum");
		try {
			long start=System.currentTimeMillis();
			boolean result = printNoticeExcel(gameCode, periodNum, request, response);
			if (result) {
				log.info(operatorsLogManager.getLogInfo("页面通用","打印摇奖通知单", start));
				return ReturnInfo.Success;
			}
		} catch (Exception e) {
			log.error("打印摇奖通知单异常", e);
		}
		return ReturnInfo.Faild;
	}


	/**
	 * 摇奖现场打印摇奖通知单
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/prizelive/exportnotice", method = RequestMethod.GET)
	public ReturnInfo prizeliveExport(HttpServletRequest request, HttpServletResponse response) {
		ReturnInfo info = new ReturnInfo();
		String gameCode = request.getParameter("gameCode");
		String periodNum = request.getParameter("periodNum");
		LttoRunFlowKey flowKey = new LttoRunFlowKey();
		flowKey.setGameCode(gameCode);
		flowKey.setPeriodNum(periodNum);
		flowKey.setFlowType(FlowType.LOOK_CANCEL_FAX.getTypeNum());
		LttoRunFlow result = lttoRunFlowService.selectByPrimaryKey(flowKey);
		if (result != null) {
			Integer status = result.getFlowStatus();
			if (Constant.Status.TASK_LTTOERY_FLOW_1.equals(status)) {
				//打印摇奖报告单
				try {
					printNoticeExcel(gameCode, periodNum, request, response);
				} catch (Exception e) {
					log.error("打印摇奖通知单异常", e);
				}
				info.setSuccess(true);
				info.setDescription("摇奖通知单打印完成");
				return info;
			} else {
				info.setRetcode(status);
				info.setSuccess(false);
				info.setDescription("请等待中心通知");
				return info;
			}
		} else {
			return ReturnInfo.Faild;
		}
	}

	/**
	 * 判断打印摇奖通知单前置条件
	 * 各省销售数据是否核对完成
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/prizelive/judgeCollate", method = RequestMethod.GET)
	public ReturnInfo judgeCollate(HttpServletRequest request, HttpServletResponse response) {
		ReturnInfo info = new ReturnInfo();
		String gameCode = request.getParameter("gameCode");
		String periodNum = request.getParameter("periodNum");
		LttoRunFlowKey flowKey = new LttoRunFlowKey();
		flowKey.setGameCode(gameCode);
		flowKey.setPeriodNum(periodNum);
		flowKey.setFlowType(FlowType.LOOK_CANCEL_FAX.getTypeNum());
		LttoRunFlow result = lttoRunFlowService.selectByPrimaryKey(flowKey);
		if (result != null) {
			Integer status = result.getFlowStatus();
			if (Constant.Status.TASK_LTTOERY_FLOW_1.equals(status)) {
				info.setSuccess(true);
				info.setRetcode(status);
				info.setDescription("摇奖通知单打印完成");
				return info;
			} else {
				info.setRetcode(status);
				info.setSuccess(false);
				info.setDescription("销量核对未完成,请等待中彩中心通知");
				return info;
			}
		} else {
			log.debug("开奖流程操作错误");
			info.setSuccess(false);
			info.setDescription("开奖流程节点初始化操作错");
			return info;
		}
	}

	private boolean printNoticeExcel(String gameCode, String periodNum, HttpServletRequest request, HttpServletResponse response) {
		List<LttoProvinceSalesData> list = lttoProvinceSalesDataService.selectByPeriodNumAndGameCode(periodNum,gameCode);
		String agents = request.getHeader("user-agent");
		String filename = gameInfoCache.getGameName(gameCode)+ periodNum + "销售额";
		Workbook workbook = null;
		if (!CommonUtils.isEmpty(list)) {
			workbook = GenPrintNoticeExcelUtil.list2Excel(list, gameCode, periodNum,gameInfoCache,provinceInfoCache);
			try {
				if (agents.contains("Firefox")) {
					filename = Base64Encode.base64EncodeFileName(filename + ".xls");
				} else {
					filename = URLEncoder.encode(filename + ".xls", "utf-8");
				}
			} catch (UnsupportedEncodingException e1) {
				log.error("摇奖通知单生成异常", e1);
			}
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
			try(OutputStream  outputStream = response.getOutputStream();) {
				workbook.write(outputStream);
			} catch (IOException e) {
				log.error("[新开奖稽核系统]导出[摇奖通知单]异常", e);
			}
			return true;
		}
		return false;
	}


}
