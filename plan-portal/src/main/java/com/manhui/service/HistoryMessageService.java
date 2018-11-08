package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.HistoryMessage;


/**
 * @ClassName: HistoryMessageService
 * @description: 历史消息Service层
 * @author:	RanMaoKun
 * @date Create in 2017年11月3日14:37:40
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface HistoryMessageService {
   
	//新增历史消息
    int saveHistoryMessage(HistoryMessage record);
    
    //通过id查询历史消息
    HistoryMessage selectByPrimaryKey(Integer historyMessageId);
    
    //通过接收人查询历史消息
    List<HistoryMessage> findByReceivePerson(String receivePerson);
    
    //通过接收人查询历史消息数量
    int count(String receivePerson);

}