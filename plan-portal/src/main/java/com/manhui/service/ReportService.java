package com.manhui.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Report;

/**
 * @ClassName: ReportService
 * @description: 进度汇报接口
 * @author:	RanMaoKun
 * @date Create in 2017年12月8日09:54:47
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface ReportService {
    int deleteByPrimaryKey(Integer reportId);


    int insertReport(Report report);
    
    Integer maxPlanReportId(Integer planId);
    
    Report selectByReportId(Integer reportId);

    List<Report> selectByplanId(Integer planId);

    int updateReport(Report report);
    
	//查询我需要汇报的进度
    @SuppressWarnings("rawtypes")
    List<Report> findMyReport(Map criteria);
    
    @SuppressWarnings("rawtypes")
	Integer findMyReportCount(Map criteria);
    
    //查询可以查看的进度
    @SuppressWarnings("rawtypes")
    List<Report> findLookReport(Map criteria);
    
    @SuppressWarnings("rawtypes")
    Integer findLookReportCount(Map criteria);
    
    //查询所有未汇报的汇报
    List<Report> findReport();

}