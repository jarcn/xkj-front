package com.cwlrdc.front.ltto.service;

import static com.cwlrdc.front.common.FlowType.getTypeName;
import static java.lang.System.currentTimeMillis;

import com.cwlrdc.commondb.ltto.entity.LttoRunFlow;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowExample;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowExample.Criteria;
import com.cwlrdc.commondb.ltto.entity.LttoRunFlowKey;
import com.cwlrdc.commondb.ltto.mapper.LttoRunFlowMapper;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.FlowType;
import com.cwlrdc.front.common.ServiceInterface;
import com.joyveb.lbos.restful.common.CommonSqlMapper;
import com.joyveb.lbos.restful.common.DbCondi;
import com.joyveb.lbos.restful.util.SqlMaker;
import com.unlto.twls.commonutil.component.CommonUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class LttoRunFlowService implements ServiceInterface<LttoRunFlow, LttoRunFlowExample, LttoRunFlowKey>{

	@Resource
	private LttoRunFlowMapper mapper;
	private @Resource CommonSqlMapper common;
	/**
	 * @Description 根据游戏期号初始化每期流程 14条
	 * @Author mafengge
	 * @Date 2017/5/10 11:52
	 */
	public void insertFlow(String gameCode,String periodNum){
		List<LttoRunFlow> list = new ArrayList<LttoRunFlow>();
		for(FlowType ft : FlowType.values()){
			LttoRunFlowKey lttoRunFlowKey = new LttoRunFlowKey();
			lttoRunFlowKey.setGameCode(gameCode);
			lttoRunFlowKey.setPeriodNum(periodNum);
			LttoRunFlow lttoRunFlow = new LttoRunFlow();
			lttoRunFlow.setGameCode(gameCode);
			lttoRunFlow.setPeriodNum(periodNum);
			lttoRunFlow.setFlowTime(currentTimeMillis());
			lttoRunFlow.setFlowStatus(0);
			lttoRunFlow.setFlowType(ft.getTypeNum());
			lttoRunFlow.setMarks(ft.getTypeName());
			lttoRunFlowKey.setFlowType(ft.getTypeNum());
			if(null==mapper.selectByPrimaryKey(lttoRunFlowKey)){
				list.add(lttoRunFlow);
			}
		}
		if(!CommonUtils.isEmpty(list)){
			for(LttoRunFlow record : list){
				mapper.insert(record);
			}
		}
	}
	/**
	 * @Description 根据类型修改状态
	 * @Author mafengge
	 * @Date 2017/5/10 15:07
	 */
	public void updateStatus(String gameCode,String periodNum,String flowType){
		LttoRunFlow lttoRunFlow = new LttoRunFlow();
		lttoRunFlow.setGameCode(gameCode);
		lttoRunFlow.setPeriodNum(periodNum);
		lttoRunFlow.setFlowType(Integer.parseInt(flowType));
		lttoRunFlow.setFlowStatus(Constant.Status.TASK_LTTOERY_FLOW_0);
		lttoRunFlow.setFlowTime(currentTimeMillis());
		lttoRunFlow.setMarks(FlowType.getTypeName(Integer.parseInt(flowType)));
		LttoRunFlow runFlow = mapper.selectByPrimaryKey(lttoRunFlow);
		if(null!=runFlow){
			lttoRunFlow.setFlowStatus(Constant.Status.TASK_LTTOERY_FLOW_1);
			mapper.updateByPrimaryKey(lttoRunFlow);
			log.debug("[{}][{}][{}]已执行",getTypeName(Integer.parseInt(flowType)),gameCode,periodNum);
		}else{
			log.debug("[{}][{}][{}]T_LTTO_RUN_FLOW表未初始化数据",getTypeName(Integer.parseInt(flowType)),gameCode,periodNum);
		}
	}
	@Override
	public int countByExample(LttoRunFlowExample example) {
		return mapper.countByExample(example);
	}

	@Override
	public int deleteByExample(LttoRunFlowExample example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(LttoRunFlowKey key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(LttoRunFlow record)  {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(LttoRunFlow record)  {
		return mapper.insertSelective(record);
	}

	@Override
	@Transactional
	public int batchInsert(List<LttoRunFlow> records)
			 {
		for(LttoRunFlow record : records){
			mapper.insert(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchUpdate(List<LttoRunFlow> records)
			 {
		for(LttoRunFlow record : records){
			mapper.updateByPrimaryKeySelective(record);
		}
		return records.size();
	}

	@Override
	@Transactional
	public int batchDelete(List<LttoRunFlow> records)
			 {
		for(LttoRunFlow record : records){
			mapper.deleteByPrimaryKey(record);
		}
		return records.size();
	}

	@Override
	public List<LttoRunFlow> selectByExample(LttoRunFlowExample example)
			 {
		return mapper.selectByExample(example);
	}

	@Override
	public LttoRunFlow selectByPrimaryKey(LttoRunFlowKey key)
			 {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public List<LttoRunFlow> findAll(List<LttoRunFlow> records) {
		if(records==null||records.size()<=0){
			return mapper.selectByExample(new LttoRunFlowExample());
		}
		List<LttoRunFlow> list = new ArrayList<>();
		for(LttoRunFlow record : records){
			LttoRunFlow result = mapper.selectByPrimaryKey(record);
			if(result!=null){
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public int updateByExampleSelective(LttoRunFlow record, LttoRunFlowExample example)  {
		return mapper.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(LttoRunFlow record, LttoRunFlowExample example) {
		return mapper.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(LttoRunFlow record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(LttoRunFlow record) {
		return mapper.updateByPrimaryKey(record);
	}

	@Override
	public int sumByExample(LttoRunFlowExample example) {
		return 0;
	}

	@Override
	public void deleteAll()  {
		mapper.deleteByExample(new LttoRunFlowExample());
	}
	
	
	public int getCount(DbCondi dc){
		List<HashMap<String, Object>> resultSet = null;
		try {
			resultSet = common.executeSql(SqlMaker.getCountSql(dc));
			return ((Number) resultSet.get(0).get("COUNT")).intValue();
		} catch (Exception e) {
			log.error("异常",e);
			return 0;
		}
	}
	
	public List<HashMap<String,Object>> getData(DbCondi dc){
		 List<HashMap<String, Object>> resultSet = null;
                try {
                    String sql = SqlMaker.getData(dc);
                    resultSet = common.executeSql(sql);
                } catch (IllegalAccessException e) {
                    log.error("异常",e);
                } catch (InvocationTargetException e) {
                    log.error("异常",e);
                }
                return resultSet;
	}
	
	public List<HashMap<String,Object>> dosql(String sql){
		List<HashMap<String,Object>> resultSet = common.executeSql(sql);
		return resultSet;
	}
	@Override
	public LttoRunFlowExample getExample(LttoRunFlow record) {
		LttoRunFlowExample example = new LttoRunFlowExample();
		if(record!=null){
			Criteria criteria = example.createCriteria();
							if(record.getGameCode()!=null){
				criteria.andGameCodeEqualTo(record.getGameCode());
				}
				if(record.getPeriodNum()!=null){
				criteria.andPeriodNumEqualTo(record.getPeriodNum());
				}
				if(record.getFlowType()!=null){
				criteria.andFlowTypeEqualTo(record.getFlowType());
				}
				if(record.getFlowStatus()!=null){
				criteria.andFlowStatusEqualTo(record.getFlowStatus());
				}
				if(record.getFlowTime()!=null){
				criteria.andFlowTimeEqualTo(record.getFlowTime());
				}
				if(record.getFlowSequece()!=null){
				criteria.andFlowSequeceEqualTo(record.getFlowSequece());
				}
				if(record.getMarks()!=null){
				criteria.andMarksEqualTo(record.getMarks());
				}

		}
		return example;
	}
}
