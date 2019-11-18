package com.yuecheng.workportal.tools;

import java.io.File;

public class StringUtils {
	
	/**
	 * 是否是window系统
	 * @return
	 */
	public static boolean isWindows() {
		String osName = System.getProperties().getProperty("os.name");
		if(!isEmpty(osName)&&osName.toLowerCase().contains("windows")) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 是否是mac系统
	 * @return
	 */
	public static boolean isMac() {
		String osName = System.getProperties().getProperty("os.name");
		if(!isEmpty(osName)&&osName.toLowerCase().contains("mac")) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean isOARequestUrl(String url) {
		if(!isEmpty(url)&&url.toLowerCase().contains("seeyon")) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}
	
	public static File getUserDownloads(String fileName) {
		String userDownloads = System.getProperty("user.home")+File.separator+"Downloads"+File.separator+fileName;
		File file= new File(userDownloads);
		if (!file.getParentFile().exists()) {
		  file.getParentFile().mkdirs();
		}
		return file;
	}
}
