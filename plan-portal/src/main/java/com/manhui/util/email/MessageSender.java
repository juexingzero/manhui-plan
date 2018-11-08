package com.manhui.util.email;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;



import com.sun.mail.util.MailSSLSocketFactory;

/**
 * @ClassName: MessageSender
 * @description: 邮件发送配置
 * @author:	RanMaoKun
 * @date Create in 2017年11月6日14:34:40
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class MessageSender {
	
	// 设置服务器
	private static String KEY_SMTP = "mail.smtp.host";
	
	private static String VALUE_SMTP = "smtp.163.com";

	// 服务器验证
	private static String KEY_PROPS = "mail.smtp.auth";
	
	private static String VALUE_PROPS = "true";
	// 发件人用户帐号、昵称和密码（邮箱必须开启SMTP服务才能使用）afpqesttlajwbgge
	public static String SEND_USER = "m15223570231@163.com";
	
	public static String SEND_UNAME = "rmk";
	
	private static String SEND_PWD = "19930114r";// 授权码

	/**
	 * 创建session对象，此时需要传输协议，是否认证身份
	 * 
	 * @param protocol
	 * @return
	 */
	public Session createSession(String protocol,String sendUser,String senPwd) {
		Properties propertie = new Properties();
		propertie.setProperty("mail.transport.protocol", protocol);
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		sf.setTrustAllHosts(true);
		// SMTP发送邮件，需要进行身份验证
		propertie.setProperty(KEY_PROPS, VALUE_PROPS);
		propertie.put(KEY_SMTP, VALUE_SMTP);
		propertie.put("mail.smtp.ssl.enable", "true");
		propertie.put("mail.smtp.ssl.socketFactory", sf);

		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sendUser, senPwd);
			}
		};
		// 启动JavaMail调试功能，可以返回与SMTP服务交互的命令信息
		Session session = Session.getInstance(propertie, authenticator);
		session.setDebug(true);
		return session;
	}

	/**
	 * 传入Session、MimeMessage对象，创建 Transport 对象发送邮件
	 * 
	 * @param session
	 * @param msg
	 * @throws Exception
	 */
	public void sendMail(Session session, MimeMessage msg,String sendUser,String senPwd) throws Exception {
		// 由 Session 对象获得 Transport 对象
		Transport transport = session.getTransport();
		// 发送用户名、密码连接到指定的 smtp 服务器
		transport.connect(VALUE_SMTP, sendUser, senPwd);

		// 设置邮件的收件人 TO：表示发件人 CC：表示抄送 BCC：表示暗送
		transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String sendUser="m15223570231@163.com";
		String senPwd="19930114r";
		String sendUname="rmk";
		String receiveUser = "291274627@qq.com";// 收件人邮箱地址
		String headName = "邮箱";
		String body = "<h1>带附件的</h1>";
		String fileName="D:/日报-冉茂坤.txt";
		
		MimeBodyPart part=WithAttachmentMessage.createAttachment(fileName);
		MimeBodyPart content = WithAttachmentMessage.createContent(body);
		MimeBodyPart bodyPart=WithAttachmentMessage.createContent(body, fileName);
		List<MimeBodyPart> bodyParts = new ArrayList<MimeBodyPart>();
		bodyParts.add(bodyPart);
		bodyParts.add(content);
		bodyParts.add(part);
		
		MessageSender sender = new MessageSender();
		Session session = sender.createSession("smtp",sendUser,senPwd);
		MimeMessage mail = new WithAttachmentMessage().createMessage(session, sendUname, sendUser, headName,
				receiveUser, bodyParts);
		sender.sendMail(session, mail,sendUser,senPwd);
	}
}
