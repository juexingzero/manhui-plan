package com.manhui.controller;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.manhui.util.email.MessageSender;
import com.manhui.util.email.WithAttachmentMessage;

/**
 * @ClassName: SendEmailController.java
 * @description:   邮件发送相关控制器
 * @author: Jiangxiaosong
 * @date Create in 9:23 2017/11/7
 * @version: v1.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class SendEmailController {
	
	/**
	 * 跳转到发邮件
	 * @param model
	 * @return
	 */
	@GetMapping("/toSendMail")
	public String toSendMail(Model model){
		return "pages/email/sendemail";
	}
	

	/**
	 * 发送邮件
	 * @param excelFile
	 * @param context
	 * @param receiveUser
	 * @return
	 */
	@RequestMapping("/sendEmail")
	public String sendEmail(MultipartHttpServletRequest multipartRequest,@RequestParam(value="context", required=true)String context,
			@RequestParam(value="receiveUser", required=true)String receiveUser, Model model,HttpServletRequest request){
		
		File savedFile = null;
		MultiValueMap<String,MultipartFile> map = multipartRequest.getMultiFileMap();// 为了获取文件，这个类是必须的 
	    List<MultipartFile> list = map.get("exFile");// 获取到文件的列表 
		//获取文件名  
        
    	String headName = "计划提醒";
    	String sendUname = "rmk";
    	//应该从接口获取，现目前写死
    	String sendUser = "m15223570231@163.com";
    	String senPwd="19930114r"; 
    	try{
    		List<MimeBodyPart> bodyParts = new ArrayList<MimeBodyPart>();
    		for(MultipartFile exFile : list){
    		  String fileName = exFile.getOriginalFilename();
    		  savedFile = new File(request.getSession().getServletContext().getRealPath("/email"),fileName);
    		  boolean isCreateSuccess = savedFile.createNewFile();
    		  if(isCreateSuccess){
    		      exFile.transferTo(savedFile);  //转存文件
    		  }
    		  String fileNames = savedFile.getPath();
    		  MimeBodyPart part = WithAttachmentMessage.createAttachment(fileNames);
      		  int pointIndex =  fileNames.indexOf(".");  
      		  String fileSuffix = fileNames.substring(pointIndex); 
      		  if("jpg".equals(fileSuffix) || "png".equals(fileSuffix) || "gif".equals(fileSuffix)){
      			  MimeBodyPart bodyPart=WithAttachmentMessage.createContent(context, fileNames);
      			  bodyParts.add(bodyPart);
      		  }
      		  bodyParts.add(part);
    	    }
    		
    		MimeBodyPart content = WithAttachmentMessage.createContent(context);
    		bodyParts.add(content);
    		
    		
    		MessageSender sender = new MessageSender();
    		Session session = sender.createSession("smtp", sendUser, senPwd);
    		MimeMessage mail = new WithAttachmentMessage().createMessage(session, sendUname, sendUser, headName,
    				receiveUser, bodyParts);
    		sender.sendMail(session, mail,sendUser,senPwd);
    		model.addAttribute("flag", "success");
    	}catch(Exception e){
    		
    		model.addAttribute("flag", "faild");
    	}
    	
		return "pages/email/sendstate";
	}
}
