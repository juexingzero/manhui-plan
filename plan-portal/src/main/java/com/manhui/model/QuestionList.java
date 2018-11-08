package com.manhui.model;

import java.util.List;


/**
 * @ClassName: QuestionList
 * @description: 待解决问题List实体类
 * @author:	RanMaoKun
 * @date Create in 2017年10月31日16:17:28
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class QuestionList {
	
	private List<Question> question;

	public List<Question> getQuestion() {
		return question;
	}

	public void setQuestion(List<Question> question) {
		this.question = question;
	}
	
	
}
