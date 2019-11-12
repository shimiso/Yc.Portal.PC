package com.yuecheng.workportal.screen;

/**
 * 类说明：
 * @创建时间 2014-1-26 下午2:48:12
 * @创建人：zdyang
 * @项目名称 capture
 * @类名 ControlPanel.java
 */

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.yuecheng.workportal.BrowserManager;
import com.yuecheng.workportal.bridge.BrowserBridge;
import com.yuecheng.workportal.tools.Constant;
import com.yuecheng.workportal.tools.GraphicsUtils;
import com.yuecheng.workportal.tools.ImageToBase64;
import com.yuecheng.workportal.ui.Main;


public class ControlPanel extends JPanel {
	
	private ImageIcon ok = new ImageIcon(getClass().getClassLoader().getResource("images/capture_ok.png"));
	private ImageIcon ok_entered = new ImageIcon(getClass().getClassLoader().getResource("images/capture_ok_mouseentered.png"));
	private ImageIcon save = new ImageIcon(getClass().getClassLoader().getResource("images/capture_save.png"));
	private ImageIcon save_entered = new ImageIcon(getClass().getClassLoader().getResource("images/capture_save_mouseentered.png"));
	private ImageIcon cancel = new ImageIcon(getClass().getClassLoader().getResource("images/capture_cancel.png"));
	private ImageIcon cancel_entered = new ImageIcon(getClass().getClassLoader().getResource("images/capture_cancel_mouseentered.png"));

	private static final long serialVersionUID = -2855989557719746956L;
	
	/**
	 * 获得截取的bufferedimage
	 * @param capturer
	 * @return
	 */
	private BufferedImage getCaptureImage(Capturer capturer) {
		capturer.getCaptureWindow().setAlwaysOnTop(false) ;
		Rectangle rectengle = capturer.getSelectedRectangle();
		BufferedImage subimage = capturer.getScreenImage().getSubimage(rectengle.x, rectengle.y, rectengle.width, rectengle.height);
		return subimage ;
	}
	
	public ControlPanel(final Capturer capturer) {
		setBackground(new Color(222, 238, 255));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(78, 153, 210)));
		setBounds(100, 100, 120, 28);
		setVisible(false);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 3)) ;
		
		JButton saveButton = createButton(save, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = getCaptureImage(capturer) ;
				boolean isOk = GraphicsUtils.exportImage(image, capturer);
				if(isOk) {
//					SnapShoot.getInstance(image).showFrame() ;
					capturer.exitCapturer() ;
				}
			} 
		}) ;
		saveButton.setRolloverIcon(save_entered) ;
		saveButton.setToolTipText(Main.RES_BUNDLE.getString(Constant.Save_Capture));//保存截图
		
		add(saveButton);
		
		JButton okButton = createButton(ok, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//保存到剪贴板
				BufferedImage image = getCaptureImage(capturer) ;
				GraphicsUtils.setClipboardImage(image);
//				SnapShoot.getInstance(image).showFrame() ;
	        	String imageBase64 = ImageToBase64.ImageToBase64(image);
	        	System.out.println(imageBase64);
	        	String method = String.format(BrowserBridge.shotPhotoCallback,imageBase64);
	        	BrowserManager.getInstance().getBrowser().executeJavaScript(method);
				capturer.exitCapturer() ;
			}
		}) ;
		okButton.setRolloverIcon(ok_entered) ;
		okButton.setToolTipText(Main.RES_BUNDLE.getString(Constant.OK_Capture));//完成截图

		add(okButton);
		
		JButton cancelButton = createButton(cancel, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				capturer.exitCapturer();
			}
		}) ;
		cancelButton.setRolloverIcon(cancel_entered) ;
		cancelButton.setToolTipText(Main.RES_BUNDLE.getString(Constant.Exit_Capture));//退出截图
		add(cancelButton);
	}
	
	private JButton createButton(Icon icon, ActionListener l) {
		JButton button = new JButton(icon);
		button.setOpaque(false);
		button.setBorderPainted(false) ;
		button.setBorder(null) ;
		button.addActionListener(l);
		return button;
	}

}
