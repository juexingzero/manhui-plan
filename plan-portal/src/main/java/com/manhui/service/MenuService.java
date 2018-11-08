package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Menu;

/**
 * @ClassName: MenuService
 * @description: 菜单 Service 接口
 * @author: HeJiayan
 * @date Create in 15:29 2017/9/29
 * @version: v1.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface MenuService {

    List<Menu> findByUser(Integer userId);
	
	List<Menu> findByPrentId(Integer parentId);

}