package com.yuecheng.workportal.tools;

import com.registry.RegistryKey;
import com.registry.RegistryValue;
import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.bridge.BrowserBridge;

public class DesktopAppUtils {
	
	static	String CMD_OPEN_EAS = "cmd /c cd %1$s & %2$s & start %3$s";
	static	String CMD_OPEN_LETIAN = "cmd /c start %1$s";
	public static void main(String[] args) {
		openLeTian();
	}

	/**
	 * 打开金蝶EAS
	 */
	public static void openKingDeeEas() {
		try {
			RegistryKey registryKey = new RegistryKey(
					RegistryKey.getRootKeyForIndex(RegistryKey.HKEY_LOCAL_MACHINE_INDEX),
					"SOFTWARE\\WOW6432Node\\JavaSoft\\Prefs\\kingdee\\eas");
			RegistryValue registryValue = registryKey.getValue("easclienthome");
			if (registryValue == null) {
				registryKey = new RegistryKey(RegistryKey.getRootKeyForIndex(RegistryKey.HKEY_LOCAL_MACHINE_INDEX),
						"SOFTWARE\\JavaSoft\\Prefs\\kingdee\\eas");
				registryValue = registryKey.getValue("easclienthome");
			}
			
			System.out.println(registryValue);
			
			if(registryValue == null) {
				BrowserManager.getInstance().getBrowser().
		    	executeJavaScript(String.format(BrowserBridge.openWinAPPCallback,"1"));
			}else {
				String value = registryValue.toString();
				int valueIndex = value.lastIndexOf("Value:");
				String path =value.substring(valueIndex + 6, value.length()).trim()+"\\bin";
				String drivePath = path.substring(0,2);
				String openCmd=String.format(CMD_OPEN_EAS, path,drivePath,path+"\\client.bat");   
				System.out.println(openCmd);
				CMDUtil.excuteCMDCommand(openCmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BrowserManager.getInstance().getBrowser().
	    	executeJavaScript(String.format(BrowserBridge.openWinAPPCallback,"1"));
		}
    	
	}
	
	/**
	 * 打开乐天
	 */
	public static void openLeTian() {
		try {
			RegistryKey registryKey = new RegistryKey(
					RegistryKey.getRootKeyForIndex(RegistryKey.HKEY_LOCAL_MACHINE_INDEX),
					"SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Product_Name");
			RegistryValue registryValue = registryKey.getValue("UninstallString");
			System.out.println(registryValue);
			if(registryValue == null||registryValue.toString()==null||registryValue.toString().indexOf("exe \"")<=-1) {
				BrowserManager.getInstance().getBrowser().
		    	executeJavaScript(String.format(BrowserBridge.openWinAPPCallback,"1"));
			}else {
				String value = registryValue.toString();
				int valueIndex = value.lastIndexOf("exe \"");
				int endIndex = value.lastIndexOf("\\");//D:\\Program\" \"Files\" \"(x86)\\Rtwyjqwl\\Rtwy_6_KFWL.exe
				String path =value.substring(valueIndex + 5, endIndex).trim().replaceAll(" ", "\\\" \\\"");
				String openCmd=String.format(CMD_OPEN_LETIAN, path + "\\Rtwy_6_KFWL.exe");   
				System.out.println(openCmd);
				CMDUtil.excuteCMDCommand(openCmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BrowserManager.getInstance().getBrowser().
	    	executeJavaScript(String.format(BrowserBridge.openWinAPPCallback,"1"));
		}
	}
	
	/**
	 * 打开邮箱
	 */
	public static void openOutLookEmail() {
		try {
			// java调用outllook
			Runtime.getRuntime()
					.exec("rundll32 url.dll,FileProtocolHandler mailto:yangfei_luck@163.com?subject=&body=");
		} catch (Exception e) {
			e.printStackTrace();
			BrowserManager.getInstance().getBrowser().
	    	executeJavaScript(String.format(BrowserBridge.openWinAPPCallback,"1"));
		}
	}
}
