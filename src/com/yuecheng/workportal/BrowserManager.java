package com.yuecheng.workportal;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class BrowserManager {
	 Browser browser;
	 static BrowserManager browserManager = null;
	 private BrowserManager() {
		  browser = new Browser();
	 }
	 public static BrowserManager getInstance() {
		 if(browserManager==null) {
			 browserManager = new BrowserManager();
		 }
		 return browserManager;
	 }
	public Browser getBrowser() {
		return browser;
	}
	 
	 
}
