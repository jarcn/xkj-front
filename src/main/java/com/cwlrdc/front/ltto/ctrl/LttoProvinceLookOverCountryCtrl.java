package com.cwlrdc.front.ltto.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cwlrdc.front.ltto.service.LttoProvinceLookOverCountryService;
import com.joyveb.lbos.restful.common.ReturnInfo;


@Slf4j
@Controller
@RequestMapping("/lttoProvinceLookOverCountry")
public class LttoProvinceLookOverCountryCtrl {
	
	private @Resource LttoProvinceLookOverCountryService dbService;
	
	@RequestMapping(value = "/lookOverCountry/{gameCode}/{provinceId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getAllData(@PathVariable String gameCode,@PathVariable String provinceId) {
		try {
		List<List> list = new ArrayList();
		List<HashMap<String,Object>> tlfputone = dbService.getTlfputone(gameCode, provinceId);
		List<HashMap<String,Object>> tlpsdut = dbService.getTlpsdut(gameCode, provinceId);
		List<HashMap<String,Object>> tlpfsut = dbService.getTlpfsut(gameCode, provinceId);
		List<HashMap<String,Object>> tlcsdut = dbService.getTlcsdut(gameCode, provinceId);
		List<HashMap<String,Object>> tlfputtwo = dbService.getTlfputtwo(gameCode, provinceId);
		List<HashMap<String,Object>> tlpfsuttwo = dbService.getTlpfsuttwo(gameCode, provinceId);
		list.add(tlfputone);
		list.add(tlpsdut);
		list.add(tlpfsut);
		list.add(tlcsdut);
		list.add(tlfputtwo);
		list.add(tlpfsuttwo);
		return list;
		} catch (Exception e) {
			log.warn("  lttoProvinceLookOverCountry  lookOverCountry getAllData error..",e);
		}
		return ReturnInfo.Faild;
	}
}
