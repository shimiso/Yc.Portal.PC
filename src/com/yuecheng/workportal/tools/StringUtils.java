package com.yuecheng.workportal.tools;

import java.io.File;

import javax.swing.SwingWorker;

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
	
	public static File getUserDownloads(File mFile) {
		String fileName = mFile.getName();
		
		//如果是windows且是exe安装包，下载到安装所在文件夹
		if(StringUtils.isWindows()&&fileName.endsWith(".exe")) {
			return mFile;
		}
		
		String userDownloads = System.getProperty("user.home")+File.separator+"Downloads"+File.separator+fileName;
		File file= new File(userDownloads);
		//如果文件夹不存在就创建
		if (!file.getParentFile().exists()) {
		  file.getParentFile().mkdirs();
		}
		// 如果文件已经存在并且大小完整，直接打开，不用重复下载
		if (file.exists()) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					file.delete();
					return null;
				}
			}.execute();
		}
//		String[] fileInfo = getFileInfo(fileName);
//		String toPrefix = fileInfo[0];
//		String toSuffix = fileInfo[1];
		// 如果文件已经存在并且大小完整，直接打开，不用重复下载
//		for (int i = 1; file.exists() && i < Integer.MAX_VALUE; i++) {
//			file = new File(System.getProperty("user.home")+File.separator+"Downloads"+File.separator, toPrefix + '(' + i + ')' + toSuffix);
//        }
		return file;
	}
	
	/**
    *
    * @param from
    * fileInfo[0]: toPrefix;
    * fileInfo[1]: toSuffix;
    * @return
    */
   public static String[] getFileInfo(String fileName){
       int index = fileName.lastIndexOf(".");
       String toPrefix="";
       String toSuffix="";
       if(index==-1){
           toPrefix=fileName;
       }else{
           toPrefix=fileName.substring(0,index);
           toSuffix=fileName.substring(index,fileName.length());
       }
       return new String[]{toPrefix,toSuffix};
   }
}
