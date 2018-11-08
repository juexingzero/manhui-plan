package com.manhui.model;

import java.math.BigDecimal;

/**
 * @ClassName: PlanAssess
 * @description: 考核类型表
 * @author:	RanMaoKun
 * @date Create in 2018年1月10日16:14:25
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */

public class PlanAssess {
	
    private Integer id;						//考核编号

    private Integer planId;					//关联计划id

    private Integer checkItem;				//考核类型			1.计划未按期开始 2. 计划未按期完成

    private Integer checkManner;			//考核方法			1.单次		2.按天累加	

    private BigDecimal singleAmount;		//单次考核金额

    private BigDecimal cumulativeDays;		//按天累加天数

    private BigDecimal cumulativeAmount;	//按天累加考核金额

    private Integer status;					//状态

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(Integer checkItem) {
        this.checkItem = checkItem;
    }

    public Integer getCheckManner() {
        return checkManner;
    }

    public void setCheckManner(Integer checkManner) {
        this.checkManner = checkManner;
    }

    public BigDecimal getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(BigDecimal singleAmount) {
        this.singleAmount = singleAmount;
    }

    public BigDecimal getCumulativeDays() {
        return cumulativeDays;
    }

    public void setCumulativeDays(BigDecimal cumulativeDays) {
        this.cumulativeDays = cumulativeDays;
    }

    public BigDecimal getCumulativeAmount() {
        return cumulativeAmount;
    }

    public void setCumulativeAmount(BigDecimal cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}