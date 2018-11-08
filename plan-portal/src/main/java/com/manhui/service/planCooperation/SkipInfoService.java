package com.manhui.service.planCooperation;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.planCooperation.SkipInfo;

/**
 * @ClassName: SkipInfoService
 * @description: 跳级信息 Service 接口
 * @author:	HeJiayan
 * @date Create in 上午11:21:02 2017年12月2日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface SkipInfoService {

    void deleteByPrimaryKey(Integer skipId);

    void insertSelective(SkipInfo skipInfo);

    SkipInfo selectByPrimaryKey(Integer skipId);

    void updateByPrimaryKeySelective(SkipInfo skipInfo);

	List<SkipInfo> findSkipInfosByCoopId(Integer coopId);

	List<SkipInfo> findAllSkipInfosByCoopId(Integer coopId);

}
