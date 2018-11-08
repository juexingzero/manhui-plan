package com.manhui.service.planCooperation;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.planCooperation.Cooperation;

/**
 * @ClassName: CooperationService
 * @description: 计划协作 Service 接口
 * @author:	HeJiayan
 * @date Create in 下午3:00:10 2017年11月28日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
@SuppressWarnings("rawtypes")
public interface CooperationService {
    
	void deleteByPrimaryKey(Integer coopId);

    void insertSelective(Cooperation coop);

    Cooperation selectByPrimaryKey(Integer coopId);

    void updateByPrimaryKeySelective(Cooperation coop);
    
    void updateByPrimaryKey(Cooperation coop);

	int countApplyList(Map criteria);
    
	List<Cooperation> findApplyList(Map criteria);

	int countHandleList(Map criteria);

	List<Cooperation> findHandleList(Map criteria);

	int countExecuteList(Map criteria);

	List<Cooperation> findExecuteList(Map criteria);

}
