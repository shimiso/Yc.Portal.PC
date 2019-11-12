package com.yuecheng.workportal.bridge;

import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.screen.Capturer;
import com.yuecheng.workportal.tools.DesktopAppUtils;
import com.yuecheng.workportal.ui.LowerRightPromptUI;
import com.yuecheng.workportal.ui.Main;

/**
 * JS和原生桥接入口
 * @author simgsg
 *
 */
public class BrowserBridge {
	public static String versionCode = "3";
	public static String versionName = "Beta_1.0.3";
	//程序主界面
	Main mainFrame;
	//截屏后的回调函数
	public static String shotPhotoCallback = "shotPhotoCallback('%s')";
	//打开PC端回调回调函数
	public static String openWinAPPCallback ="openWinAPPCallback('%s')";
	//打开PC端回调回调函数
	public static String getVersionCallback ="getVersionCallback('%1s','%2s')";
	
	public BrowserBridge(Main mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void trayShake(boolean isShake) {
		if(isShake) {
			mainFrame.startShake();	
		}else {
			mainFrame.stopShake();	
		}
	}
	/**
	 * 浏览器调试模式
	 * @param isDebug true开启  false关闭
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
	 * @param title 标题
	 * @param content 内容
	 */
	public void showPrompt(String title,String content) {
		LowerRightPromptUI lowerRightPromptBridge =new LowerRightPromptUI();
		lowerRightPromptBridge.show(title, content);
	}
	/**
	 * 截屏
	 * @param hideFrameBox 是否隐藏本窗口
	 */
	public void beginCapture(boolean hideFrameBox) {
//		CaptureScreenUI.getInstance(mainFrame).shot(hideFrameBox); 
		Capturer.getInstance(mainFrame).beginCapture(hideFrameBox);
	}
	
	/**
	 * 获取当前版本号
	 */
	public void getVersion() {
		BrowserManager.getInstance().getBrowser().
    	executeJavaScript(String.format(BrowserBridge.getVersionCallback,versionCode,versionName));
	}
}
