package com.yuecheng.workportal.bridge;

import com.yuecheng.workportal.Main;

public class BrowserBridge {
	Main frame;
	public static String shotPhotoCallback ="shotPhotoCallback('%s')";
	
	
	public BrowserBridge(Main frame) {
		this.frame = frame;
	}
	
	public void trayShake(boolean isShake) {
		if(isShake) {
			frame.startShake();	
		}else {
			frame.stopShake();	
		}
	}
	
	public void debugBrowser(boolean isDebug) {
		frame.debugBrowser(isDebug);	
	}
}
