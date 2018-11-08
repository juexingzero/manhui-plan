package com.manhui.model;

import java.util.Date;


/**
 * @ClassName: HistoryMessage
 * @description: 历史消息表
 * @author:	RanMaoKun
 * @date Create in 2017年11月3日13:52:31
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class HistoryMessage extends BaseObject{
	
    private Integer historyMessageId;

    private String receivePerson;

    private Date submitTime;

    private Integer messageType;

    private String content;

    public Integer getHistoryMessageId() {
        return historyMessageId;
    }

    public void setHistoryMessageId(Integer historyMessageId) {
        this.historyMessageId = historyMessageId;
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