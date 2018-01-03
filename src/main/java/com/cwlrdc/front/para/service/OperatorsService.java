package com.cwlrdc.front.para.service;

import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.ltto.service.LttoLogRemarkService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Service
public class OperatorsService {
	@Resource
	private LttoLogRemarkService service;
	
	public Boolean setOperators(String provinceId,String periodNum,String gameCode){
			HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			List<HashMap<String, Object>> list=(List<HashMap<String, Object>>)service.getPersonByProvinceAndPeriodAndGameCode( provinceId,periodNum, gameCode);
			if(list.size()>0&&null!=list.get(0)) {
				StringBuilder lotteryPersons = new StringBuilder();
				for(HashMap<String, Object> map:list) {
					String lotteryPerson=(String) map.get("lotteryPerson");
					lotteryPersons.append(lotteryPerson+",");
				}
				String operators="";
				if(lotteryPersons.length()>0) {
					operators=lotteryPersons.toString().substring(0, lotteryPersons.length()-1);
				}
				HttpSession session=req.getSession();
				session.setAttribute(Constant.OPERATIORS.OPERATIORS, operators);
				return true;
			}else {
				return false;
			}
	}
}
