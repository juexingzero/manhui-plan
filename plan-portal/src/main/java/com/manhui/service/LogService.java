package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Log;

/**
 * @ClassName: LogService
 * @description: 日志接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月19日18:49:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface LogService {
	
	int findLogCountByPlanIdAndCreatePerson(Log log);

	List<Log> findLogByPlanIdAndCreatePerson(Log log);
	
	Log findLogByLogId(Integer logId);
	
	int saveLog(Log log);
	
	int findMaxLogId();
	
}
