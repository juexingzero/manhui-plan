package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.manhui.model.TemplateType;

import groovy.lang.Delegate;

@Mapper
public interface TemplateTypeService {
	
	
	
	List<TemplateType> findAllTemplateType(TemplateType tt);
	
	
	int count(TemplateType tt);
	
	
	TemplateType findTemplateType(TemplateType tt);
	
	
	int insert(TemplateType tt);
	
	
	int update(TemplateType tt);
	
	
	int delete(Integer id);
	
	//直接查询全部类型
	List<TemplateType> findAllTemplateTypeNoPage();
}
