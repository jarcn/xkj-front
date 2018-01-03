package com.cwlrdc.front.para.ctrl;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cwlrdc.front.ltto.service.LttoLogRemarkService;
import com.cwlrdc.front.para.service.OperatorsService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/operatorsCtrl")
@Slf4j
public class OperatorsCtrl {
	@Resource
	private LttoLogRemarkService service;
	
	@Resource
	private OperatorsService operatorsParameters;
	
	@RequestMapping(value="/operators",method=RequestMethod.POST)
	@ResponseBody
	public ReturnInfo setOperators(@RequestBody OperatorsParameters parameters) {
		Boolean judge;
		try {
			judge = operatorsParameters.setOperators(parameters.getProvinceId(), parameters.getPeriodNum(), parameters.getGameCode());
			if(judge) {
				return new ReturnInfo("获取操作人成功", true);
			}else{
				return new ReturnInfo("获取操作人失败", false);
			}
		} catch (Exception e) {
			log.warn("获取操作人异常", e);
			return new ReturnInfo("获取操作人异常", false);
		}
		
	}
}

class OperatorsParameters{
	private String periodNum;
	private String provinceId;
	private String gameCode;
	public String getPeriodNum() {
		return periodNum;
	}
	public void setPeriodNum(String periodNum) {
		this.periodNum = periodNum;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getGameCode() {
		return gameCode;
	}
	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}

}
