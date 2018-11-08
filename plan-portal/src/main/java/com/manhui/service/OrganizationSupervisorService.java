package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.OrganizationSupervisor;

/**
 * @ClassName: OrganizationSupervisorService
 * @description: 机构公司及对应监察人信息 Service 接口
 * @author:	HeJiayan
 * @date Create in 下午9:21:30 2017年12月11日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface OrganizationSupervisorService {

    void deleteByPrimaryKey(String id);

    void insert(OrganizationSupervisor record);

    void insertSelective(OrganizationSupervisor record);

    OrganizationSupervisor selectByPrimaryKey(String id);

    void updateByPrimaryKeySelective(OrganizationSupervisor record);

    void updateByPrimaryKey(OrganizationSupervisor record);

	List<OrganizationSupervisor> findTopOs();

	List<OrganizationSupervisor> findSubsByParentId(String parentId);
}
