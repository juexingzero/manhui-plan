package com.manhui.util.email;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: WithAttachmentMessage
 * @description: 文本附件处理
 * @author:	RanMaoKun
 * @date Create in 2017年11月6日14:36:58
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class WithAttachmentMessage {

	/**
	 * 根据传入的文件路径创建附件并返回
	 * 
	 * @param fileName
	 *            附件路径
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public static MimeBodyPart createAttachment(String fileName) throws MessagingException, UnsupportedEncodingException {
		MimeBodyPart part = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(fileName);
		part.setDataHandler(new DataHandler(fds));
		part.setFileName(MimeUtility.encodeText(fds.getName(),"utf-8",null));
		return part;
	}

	/**
	 * 根据传入的邮件正文和文件路径构建图文并茂的正文
	 * 
	 * @param body
	 *            邮件正文
	 * @param fileName
	 *            图片路径
	 * @return
	 * @throws MessagingException
	 */
	public static MimeBodyPart createContent(String body, String fileName) throws MessagingException {
		// 用于保存最终正文部分
		MimeBodyPart contentBody = new MimeBodyPart();
		// 用于组合正文与图片，"related"型的MimeMultipart对象
		MimeMultipart contentMulti = new MimeMultipart("related");

		// 正文的文本部分
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(body, "text/html;charset=utf-8");
		contentMulti.addBodyPart(textBody);

		/*// 正文的图片部分
		MimeBodyPart jpgBody = new MimeBodyPart();
		
		FileDataSource fds = new FileDataSource(fileName);
		jpgBody.setDataHandler(new DataHandler(fds));
		jpgBody.setContentID("logo_jpg");*/
		
		// 创建图片“节点”
        MimeBodyPart image = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource(fileName)); // 读取本地文件
        image.setDataHandler(dh);                   // 将图片数据添加到“节点”
        image.setContentID("image_fairy_tail"); 
		
		
        // 创建文本“节点”
        MimeBodyPart text = new MimeBodyPart();
        //    这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        text.setContent("<br/><img src='cid:"+image.getContentID()+"'/>", "text/html;charset=UTF-8");
		
		
		/*contentMulti.addBodyPart(jpgBody);*/
		contentMulti.addBodyPart(text);
		contentMulti.addBodyPart(image);
		// 将上面的"related"型的MimeMultipart对象作为邮件正文
		contentBody.setContent(contentMulti);
		return contentBody;
	}

	/**
	 * 构建正文
	 * 
	 * @param content
	 *            邮件正文
	 * @return
	 * @throws MessagingException
	 */
	public static MimeBodyPart createContent(String content) throws MessagingException {
		// 用于保存最终正文部分
		MimeBodyPart contentBody = new MimeBodyPart();
		// 用于组合正文与图片，"related"型的MimeMultipart对象
		MimeMultipart contentMulti = new MimeMultipart("related");

		// 正文的文本部分
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content, "text/html;charset=utf-8");
		contentMulti.addBodyPart(textBody);

		// 将上面的"related"型的MimeMultipart对象作为邮件正文
		contentBody.setContent(contentMulti);
		return contentBody;
	}

	/**
	 * 根据传入的 Seesion 对象创建混合型的 MIME消息
	 * 
	 * @param session
	 * @param nick
	 *            发件人昵称
	 * @param from
	 *            发件人邮箱地址
	 * @param headName
	 *            邮件标题
	 * @param receiveUser
	 *            收件人
	 * @param bodyParts
	 *            邮件内容 List<MimeBodyPart>
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public MimeMessage createMessage(Session session, String nick, String from, String headName, String receiveUser,
			List<MimeBodyPart> bodyParts) throws AddressException, MessagingException, UnsupportedEncodingException {
		// 校验邮箱的有效性
		if (!CheckEmailValidityUtil.isEmailValid(receiveUser)){
			return null;
		}
			

		MimeMessage msg = new MimeMessage(session);
		// 设置发件人昵称
		String nickStr = "";
		if (StringUtils.isBlank(nick)) {
			nickStr = "未知帐号";
		} else {
			nickStr = MimeUtility.encodeText(nick);
		}
		msg.setFrom(new InternetAddress(nickStr + " <" + from + ">"));// 发件人
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveUser));// 收件人
		msg.setSubject(headName);// 邮件标题

		// 判断邮件内容
		if (bodyParts == null || bodyParts.size() == 0) {
			return null;
		}

		// 设置正文与附件之间的关系
		MimeMultipart allPart = new MimeMultipart();
		for (MimeBodyPart mbp : bodyParts) {
			allPart.addBodyPart(mbp);
		}
		allPart.setSubType("mixed");

		// 将上面混合型的 MimeMultipart 对象作为邮件内容并保存
		msg.setContent(allPart, "text/html;charset=utf-8");
		msg.saveChanges();
		return msg;
	}
}