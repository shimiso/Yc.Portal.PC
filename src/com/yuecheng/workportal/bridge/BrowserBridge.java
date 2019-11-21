package com.yuecheng.workportal.bridge;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.screen.Capturer;
import com.yuecheng.workportal.tools.CMDUtil;
import com.yuecheng.workportal.tools.Constant;
import com.yuecheng.workportal.tools.DesktopAppUtils;
import com.yuecheng.workportal.tools.StringUtils;
import com.yuecheng.workportal.ui.Main;
import com.yuecheng.workportal.ui.DonwLoadDialog;
import com.yuecheng.workportal.ui.RightCornerPopMessage;

/**
 * JS和原生桥接入口
 * 
 * @author simgsg
 *
 */
public class BrowserBridge {
	// 程序主界面
	Main mainFrame;
	// 截屏后的回调函数
	public static String shotPhotoCallback = "shotPhotoCallback('%s')";
	// 打开PC端回调回调函数
	public static String openWinAPPCallback = "openWinAPPCallback('%s')";
	// 打开PC端回调回调函数
	public static String getVersionCallback = "getVersionCallback('%1s','%2s','%3s')";
	RightCornerPopMessage rightCornerPopMessage;
	public BrowserBridge(Main mainFrame) {
		this.mainFrame = mainFrame;
	}

	public void trayShake(boolean isShake) {
		System.out.println("闪动"+isShake);
		if (isShake) {
			mainFrame.startShake();
		} else {
			mainFrame.stopShake();
		}
	}

	/**
	 * 浏览器调试模式
	 * 
	 * @param isDebug
	 *            true开启 false关闭
	 */
	public void debugBrowser(boolean isDebug) {
		mainFrame.debugBrowser(isDebug);
	}

	/**
	 * 打开金蝶EAS客户端
	 */
	public void openKingDeeEas() {
		DesktopAppUtils.openKingDeeEas();
	}

	/**
	 * 打开乐天客户端
	 */
	public void openLeTian() {
		DesktopAppUtils.openLeTian();
	}

	/**
	 * 打开Outlook邮箱
	 */
	public void openOutLookEmail() {
		DesktopAppUtils.openOutLookEmail();
	}

	/**
	 * 弹出右下角提示框
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 */
	long mLastClickTime = 0;
	public void showRightCornerPopMessage(String title, String content) {
		if(!Main.isOpen&&StringUtils.isMac()) {
			long nowTime = System.currentTimeMillis();
	         if (nowTime - mLastClickTime > 2000L) {
	        	 if(rightCornerPopMessage!=null) {
	        		 rightCornerPopMessage.close();
	        		 rightCornerPopMessage = null;
	        	 }
	        	 rightCornerPopMessage = new RightCornerPopMessage(mainFrame);
	        	 rightCornerPopMessage.open(title,content);
	        	 mLastClickTime = nowTime;
	         }  
		}
	}

	/**
	 * 截屏
	 * 
	 * @param hideFrameBox
	 *            是否隐藏本窗口
	 */
	public void beginCapture(boolean hideFrameBox) {
		// CaptureScreenUI.getInstance(mainFrame).shot(hideFrameBox);
		Capturer.getInstance(mainFrame).beginCapture(hideFrameBox);
	}

	/**
	 * 获取当前版本号
	 */
	public void getVersion() {
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name");
		System.out.println("操作系统的名称：" + props.getProperty("os.name"));
		System.out.println("操作系统的构架：" + props.getProperty("os.arch"));
		System.out.println("操作系统的版本号：" + props.getProperty("os.version"));
		BrowserManager.getInstance().getBrowser()
				.executeJavaScript(String.format(BrowserBridge.getVersionCallback, osName,Constant.VERSION_CODE, Constant.VERSION_NAME));
	}
	
	/**
	 * 打开IE浏览器
	 * @param url
	 */
	public void openIE(String url) {
		try {
			if(StringUtils.isWindows()) {
				//*******下面这样处理是为了避免在打开IE浏览器的时候带&号后面的参数被截掉的问题
				url = url.replaceAll("\"","\'");
				url = "\"" + url + "\"";
				CMDUtil.excuteCMDCommand("cmd /c start iexplore " + url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	
	/**
	 * 打开Safari浏览器
	 * @param url
	 */
	public void openSafari(String url) {
		try {
			if(StringUtils.isMac()) {
				CMDUtil.excuteCMDCommand("/usr/bin/open -a safari "+url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	/**
	 * 启动一个下载任务
	 * @param url
	 */
	public void openDownload(String url,String fileName) {
		try {
			DonwLoadDialog  progressDialog = DonwLoadDialog.createProgressDialog(mainFrame);
			progressDialog.startDownLoad(url,fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	/**
	 * 切换中英文
	 * @param language zh_CN 中文   en_US  英文
	 */
	public void switchLanguage(String language) {
		Locale locale = Locale.getDefault();// 获取地区:默认
		ResourceBundle bundle = ResourceBundle.getBundle("msg_zh_CN",locale);
		if(language!=null&&language.equals("zh_CN")) {
			 bundle = ResourceBundle.getBundle("msg_zh_CN",locale);
		}else if(language!=null&&language.equals("en_US")) {
			 bundle = ResourceBundle.getBundle("msg_en_US",locale);
		}
		System.out.println("switchLanguage方法被调用："+language);
		mainFrame.switchLanguage(bundle);
	}
}
