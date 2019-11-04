package com.yuecheng.workportal.bridge;

import com.yuecheng.workportal.screen.Capturer;
import com.yuecheng.workportal.tools.DesktopAppUtils;
import com.yuecheng.workportal.ui.CaptureScreenUI;
import com.yuecheng.workportal.ui.LowerRightPromptUI;
import com.yuecheng.workportal.ui.Main;

/**
 * JS和原生桥接入口
 * @author simgsg
 *
 */
public class BrowserBridge {
	//程序主界面
	Main mainFrame;
	//截屏后的回调函数
	public static String shotPhotoCallback ="shotPhotoCallback('%s')";
	
	
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
}
