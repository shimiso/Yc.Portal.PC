package com.yuecheng.workportal.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import sun.misc.BASE64Encoder;

public class ImageToBase64 {

	public static String strNetImageToBase64;

	public static void main(String[] args) {
		// //第一个:把网络图片装换成Base64
		// String netImagePath = "网络图片路径";
		// //下面是网络图片转换Base64的方法
		// NetImageToBase64(netImagePath);

		// 下面是本地图片转换Base64的方法
		String imagePath = "D:\\201910301304014.png";
		ImageToBase64(imagePath);
	}

	/**
	 * 网络图片转换Base64的方法
	 *
	 * @param netImagePath
	 */
	public static void NetImageToBase64(String netImagePath) {
		final ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			// 创建URL
			URL url = new URL(netImagePath);
			final byte[] by = new byte[1024];
			// 创建链接
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream is = conn.getInputStream();
						// 将内容读取内存中
						int len = -1;
						while ((len = is.read(by)) != -1) {
							data.write(by, 0, len);
						}
						// 对字节数组Base64编码
						BASE64Encoder encoder = new BASE64Encoder();
						strNetImageToBase64 = encoder.encode(data.toByteArray());
						System.out.println("网络图片转换Base64:" + strNetImageToBase64);
						// 关闭流
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 本地图片转换Base64的方法
	 *
	 * @param imgPath
	 */

	public static String ImageToBase64(String imgPath) {
		byte[] buffer= null;
		// 读取图片字节数组
		try {
			 File file = new File(imgPath);
		        FileInputStream inputFile = new FileInputStream(file);
		        buffer = new byte[(int)file.length()];
		        inputFile.read(buffer);
		        inputFile.close();
		        
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return new BASE64Encoder().encode(Objects.requireNonNull(buffer)).replaceAll("[\\s*\t\n\r]", "");
		// 返回Base64编码过的字节数组字符串
		// System.out.println("本地图片转换Base64:" +
		// encoder.encode(Objects.requireNonNull(data)));
	}

}
