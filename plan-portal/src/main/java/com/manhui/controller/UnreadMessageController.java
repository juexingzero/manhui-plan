package com.manhui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manhui.model.UnreadMessage;
import com.manhui.service.UnreadMessageService;

/**
 * @ClassName: UnreadMessageController
 * @description: 未读信息控制层
 * @author:	RanMaoKun
 * @date Create in 2017年11月3日14:50:10
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class UnreadMessageController {
	
	//引入未读消息
	@Autowired
	private UnreadMessageService unreadMessageService;
	
	/**
	 *  未读消息展示
	 * @param model
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @return
	 */
	@RequestMapping("/tofindUnreadMessage_{pageCurrent}_{pageSize}_{pageCount}")
	public String findUnreadMessage(Model model,@PathVariable Integer pageCurrent,
			@PathVariable Integer pageSize,@PathVariable Integer pageCount){
		UnreadMessage unreadMessage=new UnreadMessage();
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		//需要传入登录用户作接收人查询
		int rows=this.unreadMessageService.count(unreadMessage);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		unreadMessage.setStart((pageCurrent - 1)*pageSize);
		unreadMessage.setEnd(pageSize);
		List<UnreadMessage> messages=this.unreadMessageService.findByReceivePerson(unreadMessage);
		model.addAttribute("messages",messages);
		return "pages/unreadMessage/unreadMessage";
	}
	
	/**
	 * 查询登录用户有多少未读消息
	 * @return
	 */
	@RequestMapping("/unreadMessageCount")
	public @ResponseBody Integer findUnreadMessageCount(){
		UnreadMessage unreadMessage=new UnreadMessage();
		//需要传入登录用户作接收人查询
		Integer rows=this.unreadMessageService.count(unreadMessage);
		
		return rows;
	}
	
	
}
