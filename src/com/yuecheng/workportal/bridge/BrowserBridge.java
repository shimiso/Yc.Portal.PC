package com.yuecheng.workportal.bridge;

import com.yuecheng.workportal.Main;
import com.yuecheng.workportal.tools.DesktopAppUtils;

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
	
	public void openKingDeeEas() {
		DesktopAppUtils.openKingDeeEas();
	}
	
	public void openLeTian() {
		DesktopAppUtils.openLeTian();
	}
}
