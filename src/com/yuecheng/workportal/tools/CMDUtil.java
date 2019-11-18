package com.yuecheng.workportal.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 执行windows的cmd命令工具类
 * 
 * @author dufei
 *
 */
public class CMDUtil {
	public static void main(String[] args) {
		exec("dir");
	}
	public static void exec(String cmd) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            InputStream is = proc.getInputStream();
            InputStream es = proc.getErrorStream();
            String line;
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
            	System.out.println(">>>"+line);
            }
            br = new BufferedReader(new InputStreamReader(es, "UTF-8"));
            while ((line = br.readLine()) != null) {
            	System.out.println(">>>"+line);
            }
        } catch (Exception e) {
        	System.out.println(">>>异常信息"+e);
        }
    }
	/**
	 * 执行一个cmd命令
	 * 
	 * @param cmdCommand  cmd命令
	 * @return 命令执行结果字符串，如出现异常返回null
	 * @throws Exception
	 */
	public static String excuteCMDCommand(String cmdCommand) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		process = Runtime.getRuntime().exec(cmdCommand);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * 执行bat文件，
	 * 
	 * @param file
	 *            bat文件路径
	 * @param isCloseWindow
	 *            执行完毕后是否关闭cmd窗口
	 * @return bat文件输出log
	 */
	public static String excuteBatFile(String file, boolean isCloseWindow) {
		String cmdCommand = null;
		if (isCloseWindow) {
			cmdCommand = "cmd.exe /c " + file;
		} else {
			cmdCommand = "cmd.exe /k " + file;
		}
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 执行bat文件,新开窗口
	 * 
	 * @param file
	 *            bat文件路径
	 * @param isCloseWindow
	 *            执行完毕后是否关闭cmd窗口
	 * @return bat文件输出log
	 */
	public static String excuteBatFileWithNewWindow(String file, boolean isCloseWindow) {
		String cmdCommand = null;
		if (isCloseWindow) {
			cmdCommand = "cmd.exe /c start " + file;
		} else {
			cmdCommand = "cmd.exe /k start " + file;
		}
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
