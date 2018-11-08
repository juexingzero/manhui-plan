package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.UnreadMessage;


/**
 * @ClassName: UnreadMessageService
 * @description: 未读消息Service层
 * @author:	RanMaoKun
 * @date Create in 2017年11月3日14:36:59
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface UnreadMessageService {
	
	//通过id删除未读的消息
    int deleteByPrimaryKey(Integer unreadMessageId);
    
    //新增未读消息
    int saveUnreadMessage(UnreadMessage unreadMessage);
    
    //批量新增未读消息
    int saveUnreadMessages(List<UnreadMessage> unreadMessages);
    
    //通过id查询未读消息
    UnreadMessage selectByPrimaryKey(Integer unreadMessageId);
    
    //通过接收人查询登录用户接收的未读消息
    List<UnreadMessage> findByReceivePerson(UnreadMessage unreadMessage);
    
    //通过接收人查询登录用户接收的未读消息数量
    int count(UnreadMessage unreadMessage);
}