package com.yuecheng.workportal.tools;

import com.registry.RegistryKey;
import com.registry.RegistryValue;

public class DesktopAppUtils {
	
	static	String CMD_OPEN_EAS = "cmd /c cd %1$s & %2$s & start %3$s";
	static	String CMD_OPEN_LETIAN = "cmd /c start %1$s";
	public static void main(String[] args) {
		openLeTian();
	}

	public static boolean openKingDeeEas() {

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
				return false;
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
			return false;
		}
		return true;
	}
	
	
	public static boolean openLeTian() {

		try {
			RegistryKey registryKey = new RegistryKey(
					RegistryKey.getRootKeyForIndex(RegistryKey.HKEY_LOCAL_MACHINE_INDEX),
					"SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Product_Name");
			RegistryValue registryValue = registryKey.getValue("UninstallString");
			System.out.println(registryValue);
			
			if(registryValue == null||registryValue.toString()==null||registryValue.toString().indexOf("Value:")<=-1) {
				return false;
			}else {
				String value = registryValue.toString().trim().replaceAll(" ", "\" \"");
				String openCmd=String.format(CMD_OPEN_LETIAN, "D:\\Program\" \"Files\" \"(x86)\\Rtwyjqwl\\Rtwy_6_KFWL.exe");   
				System.out.println(openCmd);
				CMDUtil.excuteCMDCommand(openCmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
