package com.manhui.service.jurisdictionManager;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.jurisdictionManager.JurisdictionManager;
import com.manhui.model.planCooperation.SkipInfo;

/**
 * @ClassName: JurisdictionManagerService
 * @description: 权限管理 Service 接口
 * @author:	Jiangxiaosong	
 * @date Create in 上午11:21:02 2017年12月21日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface JurisdictionManagerService {

	//查找基础层级设置
	JurisdictionManager findJurisdictionManager();
	
	//保存基础层级设置
	void updateJurisdictionManager(JurisdictionManager jm);
}
