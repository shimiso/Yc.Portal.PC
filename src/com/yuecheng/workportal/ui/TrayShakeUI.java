package com.yuecheng.workportal.ui;

import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class TrayShakeUI extends Thread {
	private TrayIcon trayIcon;// 当前对象的托盘
	private ImageIcon icon = null;
	JFrame mainJFrame;
	Boolean isShaking = true;
	
	public TrayShakeUI(JFrame mainJFrame, TrayIcon trayIcon,ImageIcon icon) {
		this.mainJFrame = mainJFrame;
		this.trayIcon = trayIcon;
		this.icon = icon;
		this.isShaking = true;
	}
	@Override
	public void run() {
		while (isShaking) {
			try {
				// 闪动消息的空白时间
				mainJFrame.setTitle("");
				this.trayIcon.setImage(new ImageIcon("").getImage());
				Thread.sleep(500);
				// 闪动消息的提示图片
				this.trayIcon.setImage(icon.getImage());
				mainJFrame.setTitle("乐成工作台");
				Thread.sleep(500);
			}catch(Exception e) {
	            e.printStackTrace();
	        }
		}
	}
	
	public boolean isShaking() {
		return isShaking;
	}
	
	public void stopShake() {
		// 闪动消息的提示图片
		this.trayIcon.setImage(icon.getImage());
		mainJFrame.setTitle("乐成工作台");
		isShaking = false;
	}

}
