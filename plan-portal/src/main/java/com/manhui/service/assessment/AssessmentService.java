package com.manhui.service.assessment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.assessment.Assessment;

/**
 * @ClassName: AssessmentService
 * @description: 考核信息 Service 接口
 * @author:	HeJiayan
 * @date Create in 上午11:31:36 2017年12月9日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */

@Mapper
@SuppressWarnings("rawtypes")
public interface AssessmentService {

    void deleteByPrimaryKey(Integer assId);

    void insert(Assessment assessment);

    void insertSelective(Assessment assessment);

    List<Assessment> selectByExample(Assessment assessment);

    Assessment selectByPrimaryKey(Integer assId);

    void updateByPrimaryKeySelective(Assessment assessment);

    void updateByPrimaryKey(Assessment assessment);

	int countAllAssessment(Map criteria);

	List<Assessment> findAllAssessment(Map criteria);
}
