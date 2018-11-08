package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.ReportContent;

/**
 * @ClassName: ReportContentService
 * @description: 汇报内容表的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日21:14:36
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface ReportContentService {
	
	//保存汇报内容
   int saveReportContent(ReportContent content);
   
   //修改汇报内容
   int updateReportContent(ReportContent content);
   
   //通过对应的汇报id查询汇报内容
   List<ReportContent>	findByReportId(Integer reportId);
}