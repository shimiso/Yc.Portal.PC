/**
 * 类说明：
 * @创建时间 2014-2-13 上午9:28:25
 * @创建人：zdyang
 * @项目名称 capture
 * @类名 Fdsfdfd.java
 */
package com.yuecheng.workportal.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToStringUtil {
	
	/**
	 * 得到如 截图20131024101512.png的字符串
	 * @return
	 */
	public static String getStringByDate() {
		return getStringByDate("截图") ;
	}
	
	/**
	 * 得到如 文件20131024101512.png的字符串
	 * @return
	 */
	public static String getStringByDate(String filename) {
		Date date = new Date(System.currentTimeMillis());
		System.out.println(date);
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = filename;
		str += format.format(date);
		str += ".png";
		return str;
	}
	
	/**
	 * 得到如 文件20131024101512.png的字符串
	 * @return
	 */
	public static String getStringByDate(String filename, String suffix) {
		Date date = new Date(System.currentTimeMillis());
		System.out.println(date);
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = filename;
		str += format.format(date);
		str += "."+suffix;
		return str;
	}
	
	/**
	 * 得到如 截图2013102410153212325_s.png的字符串
	 * @param flag
	 * @return
	 */
	public static String getStringByDateAndRandom(boolean flag) {// flag为true则是发送
																	// 为false则是接收
		Date date = new Date(System.currentTimeMillis());
		System.out.println(date);
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = "截图";
		str += format.format(date);
		for (int i = 0; i < 5; i++) {
			str += (int) (Math.random() * 10);
		}
		if (flag) {
			str += "_s"; // 发送 send
		} else {
			str += "_r";// 接收 receive
		}
		str += ".png";
		return str;
	}

}
