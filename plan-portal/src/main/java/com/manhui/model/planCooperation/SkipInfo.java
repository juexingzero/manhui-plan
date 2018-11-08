package com.manhui.model.planCooperation;

import java.util.Date;

/**
 * @ClassName: Cooperation
 * @description: 协作跳级信息实体类
 * @author:	HeJiayan
 * @date Create in 下午2:12:43 2017年11月28日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class SkipInfo {
    private Integer skipId;               //跳级信息编号

    private Integer coopId;               //协作编号

    private String handlerId;             //跳级处理人编号

    private String skipHandler;           //跳级处理人

    private String handlerBelong;         //跳级处理人所属

    private String skipPost;              //跳级处理人岗位

    private Date skipTime;                //跳级时间
    
    private Integer status;               //状态              0.有效信息              1.无效信息

    public Integer getSkipId() {
        return skipId;
    }

    public void setSkipId(Integer skipId) {
        this.skipId = skipId;
    }

    public Integer getCoopId() {
        return coopId;
    }

    public void setCoopId(Integer coopId) {
        this.coopId = coopId;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId == null ? null : handlerId.trim();
    }

    public String getSkipHandler() {
        return skipHandler;
    }

    public void setSkipHandler(String skipHandler) {
        this.skipHandler = skipHandler == null ? null : skipHandler.trim();
    }

    public String getHandlerBelong() {
        return handlerBelong;
    }

    public void setHandlerBelong(String handlerBelong) {
        this.handlerBelong = handlerBelong == null ? null : handlerBelong.trim();
    }

    public String getSkipPost() {
        return skipPost;
    }

    public void setSkipPost(String skipPost) {
        this.skipPost = skipPost == null ? null : skipPost.trim();
    }

    public Date getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(Date skipTime) {
        this.skipTime = skipTime;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    
}