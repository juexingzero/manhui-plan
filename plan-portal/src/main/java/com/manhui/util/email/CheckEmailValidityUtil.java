package com.manhui.util.email;

import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * @ClassName: CheckEmailValidityUtil
 * @description: 校验邮箱 格式是否正确
 * @author:	RanMaoKun
 * @date Create in 2017年11月6日14:34:40
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class CheckEmailValidityUtil {

	
	private static final Logger logger = Logger.getLogger(CheckEmailValidityUtil.class);

	/**
	 * @param email
	 *            待校验的邮箱地址
	 * @return
	 */
	public static boolean isEmailValid(String email) {
		if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
			logger.error("邮箱（" + email + "）校验未通过，格式不对!");
			return false;
		}else{
			return true;
			}
		}
}
