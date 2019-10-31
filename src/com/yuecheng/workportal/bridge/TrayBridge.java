package com.yuecheng.workportal.bridge;

import com.yuecheng.workportal.Main;

public class TrayBridge {
	Main frame;
	public static String shotPhotoCallback ="shotPhotoCallback('%s')";
	
	
	public TrayBridge(Main frame) {
		this.frame = frame;
	}
	public void shake(boolean isShake) {
		frame.setFlag(isShake);	
	}
}
