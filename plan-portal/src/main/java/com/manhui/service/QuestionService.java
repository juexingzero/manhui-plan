package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Question;

/**
 * @ClassName: QuestionService
 * @description: 待处理问题表的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日21:14:36
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface QuestionService {
	
	//保存待解决的问题
   int saveQuestion(Question question);
   
   //根据汇报id查询对应的待解决问题
   List<Question> findByReportId(Integer planId);
   
   int deleteQuestion(Integer questionId);
   
   int updateQuestion(Question question);
}