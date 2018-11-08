package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Accessory;

/**
 * @ClassName: AccessoryService
 * @description: TODO
 * @author:	HeJiayan
 * @date Create in 下午6:44:58 2017年12月5日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface AccessoryService {

    int deleteByPrimaryKey(Integer id);

    int insert(Accessory record);

    int insertSelective(Accessory acce);

    Accessory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Accessory acce);

    int updateByPrimaryKey(Accessory acce);

	List<Accessory> selectByAccessorySelective(Accessory acce);

	void deleteByModelAndId(Accessory acce);

	void updateByModelAndId(Accessory acce);
}
