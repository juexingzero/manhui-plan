package com.manhui.model;

import java.util.Date;


/**
 * @ClassName: UnreadMessage
 * @description: 未读消息表
 * @author:	RanMaoKun
 * @date Create in 2017年11月3日13:52:22
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class UnreadMessage extends BaseObject{
	
    private Integer unreadMessageId;

    private String receivePerson;

    private Date submitTime;

    private Integer messageType;

    private String content;

    public Integer getUnreadMessageId() {
        return unreadMessageId;
    }

    public void setUnreadMessageId(Integer unreadMessageId) {
        this.unreadMessageId = unreadMessageId;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson == null ? null : receivePerson.trim();
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}